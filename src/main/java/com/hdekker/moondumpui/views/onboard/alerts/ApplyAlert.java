package com.hdekker.moondumpui.views.onboard.alerts;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.opps.AlertDescriptorSupplier;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.onboard.SampleInterfaceSelector;
import com.hdekker.moondumpui.views.onboard.SelectAlertProperties;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;


@Route("apply-alert")
public class ApplyAlert extends AppBaseSinglePageCard implements BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9017929394139740202L;
	Grid<AlertDescriptor> selectAlert;
	
	@Autowired
	SessionState state;
	
	public ApplyAlert() {
		super();

		selectAlert = new Grid<>();
		selectAlert.addColumn(m->m.getAlertName())
		.setHeader("Apply Alert");
		selectAlert.addItemClickListener((e)->{
			state.setAlertDescriptor(Optional.of(e.getItem()));
			UI.getCurrent().navigate(SelectAlertProperties.class);
		});
		
		add(selectAlert);
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getProperties().ifPresentOrElse((e)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));
		
	}
	
	@Autowired
	AlertDescriptorSupplier adp;

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		adp.getAllAlertDescriptors()
			.subscribe(list->{
				selectAlert.getUI().get()
					.access(()->{
						selectAlert.setItems(list);	
						selectAlert.getUI().get().push();
						
					});
		});
		
	}

	
	
}
