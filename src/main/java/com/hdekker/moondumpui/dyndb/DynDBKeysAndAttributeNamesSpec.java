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
public interface DynDBKeysAndAttributeNamesSpec {

	public static final String SAMPLER_INTERFACE_KEYS = "SIFKeys";
	public static final String INDICATOR_DISCRIPTOR = "Idisc";
	// the SK begins with search constants
	public static final String INDICATOR_DISCRIPTOR_TRANSFORMS = "Transform";
	public static final String INDICATOR_DISCRIPTOR_ALERTS = "Alert";
	public static final String INDICATOR_DESCRIPTOR_FNNAME = "indicatorName";
	
	/**
	 * Key for storage of configured indicators
	 * 
	 */
	public static final String ASSET_INDICATOR_CONFIGURATION = "AIC";
	public static final String INDICATOR_CONFIGURABLE_PROPERTIES_AND_DEFAULTS = "indicatorConfigurablePropertiesAndDefaults";
	public static final String INDICATOR_SUBSCRIPTION = "isub";
	
}
