package com.hdekker.moondumpui.subscription;

import java.util.List;
import java.util.Map;

/**
 * The required information for
 * one sample type with many streams
 * to map into 1 indicator 
 * with one associated email.
 * 
 * 
 * @author Hayden Dekker
 *
 */
public class IndicatorSubscription {

	final String assetName;
	final String email;
	final Map<String, Map<String, Double>> indicatorConfig;
	final String indicatorName;
	final List<String> indicatorOrder;
	final String interfaceName;
	final List<Double> sampleRates;
	
	public IndicatorSubscription(String assetName, String email, Map<String, Map<String, Double>> indicatorConfig, String indicatorName,
			List<String> indicatorOrder, String interfaceName, List<Double> sampleRates) {
		super();
		this.assetName = assetName;
		this.email = email;
		this.indicatorConfig = indicatorConfig;
		this.indicatorName = indicatorName;
		this.indicatorOrder = indicatorOrder;
		this.interfaceName = interfaceName;
		this.sampleRates = sampleRates;
	}
	public String getAssetName() {
		return assetName;
	}
	public String getEmail() {
		return email;
	}
	
	

	public Map<String, Map<String, Double>> getIndicatorConfig() {
		return indicatorConfig;
	}
	public String getIndicatorName() {
		return indicatorName;
	}
	public List<String> getIndicatorOrder() {
		return indicatorOrder;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public List<Double> getSampleRates() {
		return sampleRates;
	}
	
}
