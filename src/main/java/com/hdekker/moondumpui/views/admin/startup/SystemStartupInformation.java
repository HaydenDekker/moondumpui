package com.hdekker.moondumpui.views.admin.startup;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.system.health.CoreHealthStatusSupplier;
import com.hdekker.moondumpui.system.startup.LastDayDBHealthCheckSupplier;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

@Route("system-status")
public class SystemStartupInformation extends AppBaseSinglePageCard{
	
	TextField dbConfigHealthCheck;
	TextField coreSystemHealthStatus;
	
	
	public SystemStartupInformation(){
		
		dbConfigHealthCheck = new TextField("Database Status");
		dbConfigHealthCheck.setId("database-status");
		dbConfigHealthCheck.setTitle("Database Status");
		dbConfigHealthCheck.setReadOnly(true);
		add(dbConfigHealthCheck);
		
		coreSystemHealthStatus = new TextField("Core System Status");
		coreSystemHealthStatus.setId("core-system-status");
		coreSystemHealthStatus.setTitle("Core System Status");
		coreSystemHealthStatus.setReadOnly(true);
		add(coreSystemHealthStatus);
		
	}
	
	// TODO data should join all monitoring data together
	// for single db trip
	@Autowired
	LastDayDBHealthCheckSupplier dbHealth;
	
	@Autowired
	CoreHealthStatusSupplier statusSupplier;

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		
		dbHealth.getHealthCheckEvent()
			.subscribe(status->{
				dbConfigHealthCheck.getUI()
					.get()
					.access(()->{
						
						dbConfigHealthCheck.setValue(status.getStatus());
						dbConfigHealthCheck.getUI()
							.get()
							.push();
						
					});
			});
		
		statusSupplier.getHealthStatusEvent()
			.subscribe( status->{
				coreSystemHealthStatus.getUI()
					.get()
					.access(()->{
						coreSystemHealthStatus.setValue(status.getStatus());
						coreSystemHealthStatus.getUI()
							.get()
							.push();
						
					});
			});
		
		
	}

}
