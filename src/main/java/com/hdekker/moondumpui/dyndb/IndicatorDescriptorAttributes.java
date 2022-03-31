package com.hdekker.moondumpui.dyndb;

public enum IndicatorDescriptorAttributes {
	
	INDICATOR_DESCRIPTOR_FNNAME("indicatorName"),
	INDICATOR_DESCRIPTOR_CONFIGURABLE_PROPERTIES_AND_DEFAULTS("indicatorConfigurablePropertiesAndDefaults");
	
	private final String attr;
	
	private IndicatorDescriptorAttributes(String attr) {
		this.attr = attr;
	}

	public String getAttr() {
		return attr;
	}
	
}
