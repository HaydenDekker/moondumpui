package com.hdekker.moondumpui.views.onboard;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("summary")
public class Summary extends AppBaseSinglePageCard implements BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6670224329043595734L;

	@Autowired
	SessionState state;
	
	public Summary() {
		super();
		
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
			if(name.isEmpty()) {
				name.setErrorMessage("requires a name");
				name.setInvalid(true);
				return;
			}
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
