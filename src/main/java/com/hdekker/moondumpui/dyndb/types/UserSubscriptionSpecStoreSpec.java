package com.hdekker.moondumpui.dyndb.types;

public class UserSubscriptionSpecStoreSpec {

	final UserSubscriptionSpec uss;
	final String dbPKRef;
	final String dbSKRef;
	
	public UserSubscriptionSpecStoreSpec(UserSubscriptionSpec uss, String PKref, String SKref) {
		super();
		this.uss = uss;
		this.dbPKRef = PKref;
		this.dbSKRef = SKref;
	}
	
	
	public String getDbSKRef() {
		return dbSKRef;
	}


	public String getDbPKRef() {
		return dbPKRef;
	}


	public UserSubscriptionSpec getUss() {
		return uss;
	}
	
	
}
