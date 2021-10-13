package com.hdekker.moondumpui.dyndb;

public class DynDBItemKey {

	final public String PK;
	final public String SK;
	
	public DynDBItemKey(String pK, String sK) {
		super();
		PK = pK;
		SK = sK;
	}
	public String getPK() {
		return PK;
	}
	public String getSK() {
		return SK;
	}
	
	
	
}
