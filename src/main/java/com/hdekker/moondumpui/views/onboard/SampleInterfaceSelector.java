package com.hdekker.moondumpui.views.onboard;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.opps.InterfaceNamesSupplier;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;


/**
 * Displays list of samples for user to
 * select.
 * 
 * @author HDekker
 *
 */

@Route("sample-interface-selector")
public class SampleInterfaceSelector extends AppBaseSinglePageCard{

	/**
	 * 
	 */
	private static final long serialVersionUID = -452952343439957278L;
	Grid<String> interfaceNames;
	
	@Autowired
	SessionState state;
	
	public SampleInterfaceSelector() {
		super();
		
		interfaceNames = new Grid<>();
		interfaceNames.addColumn(String::toString).setHeader("Sample Source");
		interfaceNames.addItemClickListener((event)->{
			
			state.setInterfaceName(Optional.of(event.getItem()));
			UI.getCurrent().navigate(SampleAssetSelector.class);

		});
		
		add(interfaceNames);
		
	}
	
	@Autowired
	InterfaceNamesSupplier ins;

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
				
		ins.getNames()
			.subscribe(n->{
			
				interfaceNames.getUI().get().access(()->{
					
					interfaceNames.setItems(n);
					interfaceNames.getUI().get().push();
					
				});
	
		});
		
	}
	
}
