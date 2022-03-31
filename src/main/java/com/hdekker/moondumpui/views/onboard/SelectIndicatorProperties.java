package com.hdekker.moondumpui.views.onboard;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.onboard.alerts.ApplyAlert;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;


@Route("select-indicator-properties")
public class SelectIndicatorProperties extends AppBaseSinglePageCard implements BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2156232337697048435L;
	Div holder = new Div();
	
	@Autowired
	SessionState state;
	
	public SelectIndicatorProperties() {
		super();
		
		add(new H3("Select Indicator Properties"));
		add(holder);
		holder.getStyle().set("margin", "0");
		
		Button nextButton = new Button("Next");
		
		nextButton.addClickListener((e)->{
			
			Map<String, Double> props = numberFields.
				stream()
				.collect(Collectors.toMap(
					nf->nf.getLabel(), nf->nf.getValue()));
				
			state.addProperties(state.getTransformDescriptor()
					.get()
					.getName(), 
					props);
			UI.getCurrent().navigate(ApplyAlert.class); 

		});
		
		
		add(nextButton);
		
	}
	
	List<NumberField> numberFields;

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
			
			numberFields = state.getTransformDescriptor().get()
					.getProperties()
				.entrySet()
				.stream()
				.map(prop-> {
					
					NumberField nf = new NumberField(prop.getKey());
					nf.setValue(prop.getValue());
					return nf;
					
				})
				.collect(Collectors.toList());
			holder.getUI().get()
				.access(()->{
					
					holder.add(numberFields.toArray(new NumberField[numberFields.size()]));
					holder.getUI().get().push();
					
				});

	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getTransformDescriptor()
			.ifPresentOrElse((e)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));
		
	}

}
