package com.hdekker.moondumpui.dyndb.types;

import java.time.LocalDateTime;
import java.util.List;

public class SampleInterfaceConfiguration {

	final String interfaceName;
	final List<String> interfaceKeys;
	final LocalDateTime interfaceValidAt;
	
	
	public SampleInterfaceConfiguration(String interfaceName, List<String> interfaceKeys,
			LocalDateTime interfaceValidAt) {
		super();
		this.interfaceName = interfaceName;
		this.interfaceKeys = interfaceKeys;
		this.interfaceValidAt = interfaceValidAt;
	}
	
	public LocalDateTime getInterfaceValidAt() {
		return interfaceValidAt;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public List<String> getInterfaceKeys() {
		return interfaceKeys;
	}
	
}
