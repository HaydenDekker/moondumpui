package com.hdekker.moondumpui.views.onboard.transform;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.opps.TransformDescriptorSupplier;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.onboard.SampleInterfaceSelector;
import com.hdekker.moondumpui.views.onboard.SelectIndicatorProperties;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("apply-indicator")
public class ApplyIndicator extends AppBaseSinglePageCard implements BeforeEnterObserver {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2238635809416237114L;

	Grid<TransformDescriptor> selectIndicator;
	
	HorizontalLayout h = new HorizontalLayout();
	
	@Autowired
	SessionState state;
	
	@Autowired
	DatabaseConfig dbc;
	
	public ApplyIndicator() {
		super();
		
		// style
		VerticalLayout w = new VerticalLayout();
		h = new HorizontalLayout();
		h.setAlignItems(Alignment.BASELINE);
		w.add(h);
		
		selectIndicator = new Grid<>();
		selectIndicator.addColumn(m->
			m.getName())
			.setHeader(w);
		selectIndicator.addItemClickListener((e)->{
			
			state.setTransformDescriptor(Optional.of(e.getItem()));
			UI.getCurrent().navigate(SelectIndicatorProperties.class);
			
		});
		
		
		add(selectIndicator);
		
	}
	
	@Autowired
	TransformDescriptorSupplier tdp;

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		h.add(new Text("Apply an Indicator on " + state.getAssetName().get()));
		
		tdp.getAllTransformDescriptors()
			.subscribe(l->{
			selectIndicator.getUI().get()
				.access(()->{
					
					selectIndicator.setItems(l);	
					selectIndicator.getUI().get().push();
					
				});
			
			
		});
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getAssetName()
			.ifPresentOrElse((e)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));
		
	}

}
