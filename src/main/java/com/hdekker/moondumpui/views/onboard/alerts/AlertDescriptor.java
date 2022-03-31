package com.hdekker.moondumpui.views.onboard.alerts;

import java.util.Map;

public class AlertDescriptor {

	final String alertName;
	final Map<String, Double> properties;
	
	public AlertDescriptor(String alertName, Map<String, Double> properties) {
		super();
		this.alertName = alertName;
		this.properties = properties;
	}
	public String getAlertName() {
		return alertName;
	}
	public Map<String, Double> getProperties() {
		return properties;
	}
	
	
	
}
