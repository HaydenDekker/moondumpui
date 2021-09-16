package com.hdekker.moondumpui.views;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

@Route("apply-indicator")
public class ApplyIndicator extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver {

	Label interfaceName;
	H2 assetName;
	Grid<String> selectIndicator;
	
	public ApplyIndicator(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		interfaceName = new Label();
		assetName = new H2();
		
		selectIndicator = new Grid<String>();
		selectIndicator.addColumn(String::toString).setHeader("Apply Indicator");
		
		add(interfaceName);
		add(assetName);
		add(selectIndicator);
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		interfaceName.setText("Source: " + state.getSelectedSamplerInterfaceAsset().get().getInterfaceName());
		assetName.setText(state.getSelectedSamplerInterfaceAsset().get().getAssetName());
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getSelectedSamplerInterfaceAsset().ifPresentOrElse((e)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));
		
	}

}
