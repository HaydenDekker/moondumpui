package com.hdekker.moondumpui.dyndb;

public enum IndicatorStateAttributeNames {

	STATE("indResultState"),
	DATE("inputSampleDate");
	
	private final String attributeName;
	
	private IndicatorStateAttributeNames(String name) {
		this.attributeName = name;
	}

	public String getAttributeName() {
		return attributeName;
	}
	
}
