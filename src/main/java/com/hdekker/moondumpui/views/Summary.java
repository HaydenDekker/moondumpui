package com.hdekker.moondumpui.views;

import java.util.Optional;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.state.SessionState;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("summary")
public class Summary extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver{

	public Summary(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		add(new H2("Name Indicator Alert"));
		
		//TODO create nice summary
//		
//		add(new Label("An " + state.getTransformName().get() + " indicator alert will be created for " + state.getAssetName().get() + " from " + state.getInterfaceName().get()));
//		
//		add(new Label("The alert " + state.getAlertName().get() + " will be configured with " 
//				+ state.getProperties()
//				.get()
//				.get(state.getAlertName().get())
//				.entrySet()
//				.stream()
//				.map(prop-> "" + prop.getKey() + ": " + prop.getValue())
//				.reduce((p,n) -> p.concat(". ").concat(n)).get()
//				
//		));
		
		TextField name = new TextField("Indicator Name");
		// VerticalLayout properties = new VerticalLayout();
		add(name);
		
//		state.getAssetIndicatorProperties()
//			.get()
//			.getProperties()
//			.entrySet()
//			.stream()
//			.map(prop-> "" + prop.getKey() + ": " + prop.getValue())
//			.map(propString -> new Label(propString))
//			.forEach(properties::add);
		
		Button next = new Button("next");
		add(next);
		next.addClickListener((e)-> {
			
			state.setIndicatorName(Optional.of(name.getValue()));
			UI.getCurrent().navigate(Emailer.class);
		});
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getAssetSampleRateMinutes().ifPresentOrElse(e->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));
		
	}
	
	
	
}
