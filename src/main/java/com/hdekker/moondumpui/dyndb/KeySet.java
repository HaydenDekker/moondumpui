package com.hdekker.moondumpui.dyndb;

public class KeySet {

	final public String PK;
	final public String SK;
	
	public KeySet(String pK, String sK) {
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
