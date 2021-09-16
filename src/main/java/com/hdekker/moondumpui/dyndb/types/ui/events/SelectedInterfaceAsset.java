package com.hdekker.moondumpui.dyndb.types.ui.events;

public class SelectedInterfaceAsset {

	final String interfaceName;
	final String assetName;
	
	public String getInterfaceName() {
		return interfaceName;
	}
	public String getAssetName() {
		return assetName;
	}
	
	public SelectedInterfaceAsset(String interfaceName, String assetName) {
		super();
		this.interfaceName = interfaceName;
		this.assetName = assetName;
	}
	
	
	
}
