package com.hdekker.moondumpui.dyndb;

public enum SortKeySearchConstants {

	// the SK begins with search constants
	INDICATOR_DISCRIPTOR_TRANSFORMS("Transform"),
	INDICATOR_DISCRIPTOR_ALERTS("Alert");
	
	private final String searchConstant;
	
	private SortKeySearchConstants(String searchConstant) {
		this.searchConstant = searchConstant;
	}

	public String getSearchConstant() {
		return searchConstant;
	}
	
	
}
