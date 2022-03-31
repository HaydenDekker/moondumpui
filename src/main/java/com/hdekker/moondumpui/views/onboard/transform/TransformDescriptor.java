package com.hdekker.moondumpui.views.onboard.transform;

import java.util.Map;

public class TransformDescriptor {

	final String name;
	final Map<String, Double> properties;
	
	public TransformDescriptor(String name, Map<String, Double> properties) {
		super();
		this.name = name;
		this.properties = properties;
	}
	
	public String getName() {
		return name;
	}
	public Map<String, Double> getProperties() {
		return properties;
	}
	
	
	
}
