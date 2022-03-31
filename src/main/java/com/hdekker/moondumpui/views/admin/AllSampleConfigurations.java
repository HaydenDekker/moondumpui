package com.hdekker.moondumpui.views.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.mobileapp.templates.SinglePageCard;
import com.hdekker.moondumpui.dyndb.opps.SampleConfigurationsProvider;
import com.hdekker.moondumpui.sample.SampleConfiguration;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;

@Route("all-sample-configs")
public class AllSampleConfigurations extends SinglePageCard implements AfterNavigationObserver{

	List<SampleConfiguration> displayedItems = new ArrayList<SampleConfiguration>();
	
	Grid<SampleConfiguration> sampleConfigurations = new Grid<SampleConfiguration>();
	
	public AllSampleConfigurations() {
		
		sampleConfigurations.addColumn(SampleConfiguration::getAssetId)
			.setHeader("Asset Id");
		sampleConfigurations.addColumn(SampleConfiguration::getInterfaceName)
			.setHeader("Interface Name");
		sampleConfigurations.addColumn((sc)->{
			return sc.getSampleRates().stream()
					.map(d->Long.valueOf(d.toSeconds()).toString())
					.reduce((p,n)-> p + ", " + n)
					.get();
		}).setHeader("Durations (seconds)");
		
		add(sampleConfigurations);
		
	}

	@Autowired
	SampleConfigurationsProvider sampleConfigurationsProvider;
	
	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		displayedItems = null;
		
	}

	public List<SampleConfiguration> getDisplayedItems() {
		return displayedItems;
	}
	
	
}
