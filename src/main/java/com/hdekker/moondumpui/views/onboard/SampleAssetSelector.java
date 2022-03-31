package com.hdekker.moondumpui.views.onboard;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.opps.AvailableAssetsSupplier;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.onboard.transform.ApplyIndicator;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;


@Route("sample-asset-selector")
public class SampleAssetSelector extends AppBaseSinglePageCard implements BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4666820260335764946L;
	Grid<String> assetList;
	
	@Autowired
	SessionState state;
	
	public SampleAssetSelector() {
		super();
		
		assetList = new Grid<String>();
		assetList.addColumn(String::toString).setHeader("Select Indicated Asset");
		
		assetList.addItemClickListener((e)->{
			state.setAssetName(Optional.of(e.getItem()));
			UI.getCurrent().navigate(ApplyIndicator.class);
		});
		
		add(assetList);
		
	}
	
	@Autowired
	AvailableAssetsSupplier aas;

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		aas.getAllAssets(state.getInterfaceName().get())
			.subscribe(l->
		
				assetList.getUI().get().access(()->{
					
					assetList.setItems(l);
					assetList.getUI().get().push();
			}));

	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getInterfaceName().ifPresentOrElse((i)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));

	}

}
