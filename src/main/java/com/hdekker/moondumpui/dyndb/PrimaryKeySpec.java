package com.hdekker.moondumpui.dyndb;

/**
 * Required that the core system maintains
 * Consistency with these values
 * 
 * TODO move to app config.
 * 
 * @author Hayden Dekker
 *
 */
public enum PrimaryKeySpec {

	/**
	 *  Startup
	 * 
	 */
	SAMPLER_INTERFACE_KEYS("SIFKeys"),
	INDICATOR_DISCRIPTOR("Idisc"),
	
	/**
	 * Key for storage of configured indicators
	 * 
	 */
	ASSET_INDICATOR_CONFIGURATION("AIC"),
	// TODO simplifiy name
	INDICATOR_SUBSCRIPTION_TEMP("isub-temp"),
	INDICATOR_SUBSCRIPTION("isub"),
	INDICATOR_UNSUBSCRIBE("usub"),	
	
	INDICATOR_ALERT_EVENT("IAlrt"),
	INDICATOR_ATTRIBUTE_EVENT("IAttr"),
	EMAIL_EVENT("E");
	
	private final String primaryKeyValue;
	
	PrimaryKeySpec(String primaryKeyValue){
		this.primaryKeyValue = primaryKeyValue;
	}

	public String getPrimaryKeyValue() {
		return primaryKeyValue;
	}
	
	
}
