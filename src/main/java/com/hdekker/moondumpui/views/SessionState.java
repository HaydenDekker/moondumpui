package com.hdekker.moondumpui.views;

import java.util.Optional;

import org.springframework.web.context.annotation.SessionScope;

import com.hdekker.moondumpui.dyndb.types.ui.events.SelectedInterfaceAsset;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
@SessionScope
public class SessionState {

	/**
	 *  Core may implement as many sample interfaces
	 *  as it pleases and at each interface a select number
	 *  of options could be allowed for the purposes
	 *  of indications. 
	 * 
	 */
	Optional<String> selectedSamplerInterface = Optional.empty();

	public Optional<String> getSelectedSamplerInterface() {
		return selectedSamplerInterface;
	}

	public void setSelectedSamplerInterface(Optional<String> selectedSamplerInterface) {
		this.selectedSamplerInterface = selectedSamplerInterface;
	}
	
	/**
	 *  Each sampler interface can have many assets.
	 *  This represents the selected asset for an interface.
	 * 
	 * 
	 */
	Optional<SelectedInterfaceAsset> selectedSamplerInterfaceAsset = Optional.empty();

	public Optional<SelectedInterfaceAsset> getSelectedSamplerInterfaceAsset() {
		return selectedSamplerInterfaceAsset;
	}

	public void setSelectedSamplerInterfaceAsset(Optional<SelectedInterfaceAsset> selectedSamplerInterfaceAsset) {
		this.selectedSamplerInterfaceAsset = selectedSamplerInterfaceAsset;
	}
	
	
	
}
