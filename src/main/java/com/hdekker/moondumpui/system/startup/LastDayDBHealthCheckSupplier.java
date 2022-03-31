package com.hdekker.moondumpui.system.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.opps.StartupEventSupplier;
import com.hdekker.moondumpui.system.health.FunctionStatus;

import reactor.core.publisher.Mono;

@Component
public class LastDayDBHealthCheckSupplier {

	@Autowired
	StartupEventSupplier events;
	
	@Autowired
	StartupDBTest dbTest;
	
	public Mono<FunctionStatus> getHealthCheckEvent(){
		
		return events.getAllStartupEvents()
		.map(e-> {
			FunctionStatus status = e.stream()
				.filter(p->p.getEventTime().isEqual(dbTest.getSystemStartupTime()))
				.findAny()
				.map(p-> FunctionStatus.OK)
				.orElse(FunctionStatus.ERROR);
			return status;
			
		});
	}
	
}
