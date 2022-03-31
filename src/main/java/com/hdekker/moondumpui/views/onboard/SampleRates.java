package com.hdekker.moondumpui.views.onboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("sample-rates")
public class SampleRates extends AppBaseSinglePageCard implements BeforeEnterObserver {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1622352547024541246L;
	static final Map<String, Integer> textMap = Map.of(
			"1 hour", 1*60,
			"2 hour", 2*60,
			"4 hour", 4*60,
			"Daily", 24*60
			);
			
	@Autowired
	SessionState state;
	
	public SampleRates() {
		super();
		
		add(new H2("Select Samples Rates"));
		
		CheckboxGroup<String> groupCB = new CheckboxGroup<String>();
		add(groupCB);
		groupCB.setItems(List.of(
				"1 hour",
				"2 hour",
				"4 hour",
				"Daily"));
		
		Button next = new Button("next");
		
		next.addClickListener(e->{
			
			state.setAssetSampleRateMinutes(Optional.of(
					groupCB.getSelectedItems()
						.stream()
						.map(item->textMap.get(item))
						.collect(Collectors.toList())
			));
			
			// save
			
			
			UI.getCurrent().navigate(Summary.class);
			
		});
		
		add(next);
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getProperties().ifPresentOrElse((e)->{}, ()->event.forwardTo(SampleInterfaceSelector.class));
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
	}

}
