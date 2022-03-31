package com.hdekker.moondumpui.sample;

import java.time.Duration;
import java.util.Set;

public class SampleConfiguration{

	final String interfaceName;
	final String assetId;
	final Set<Duration> sampleRates;
	
	public SampleConfiguration(String assetId, Set<Duration> sampleRates, String interfaceName) {
		super();
		this.interfaceName = interfaceName;
		this.assetId = assetId;
		this.sampleRates = sampleRates;
		
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public Set<Duration> getSampleRates() {
		return this.sampleRates;
	}

	
	public String getAssetId() {
		return this.assetId;
	}


}
