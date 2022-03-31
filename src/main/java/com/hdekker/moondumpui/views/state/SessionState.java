package com.hdekker.moondumpui.views.state;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.context.annotation.SessionScope;

import com.hdekker.moondumpui.views.onboard.alerts.AlertDescriptor;
import com.hdekker.moondumpui.views.onboard.transform.TransformDescriptor;
import com.vaadin.flow.spring.annotation.SpringComponent;

@SpringComponent
@SessionScope
public class SessionState {

	Optional<String> interfaceName = Optional.empty();
	
	public Optional<String> getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(Optional<String> interfaceName) {
		this.interfaceName = interfaceName;
	}

	Optional<String> assetName = Optional.empty();
	
	public Optional<String> getAssetName() {
		return assetName;
	}
	public void setAssetName(Optional<String> assetName) {
		this.assetName = assetName;
	}
	
	Optional<Map<String, Map<String, Double>>> properties = Optional.empty();
	
	
	public Optional<Map<String, Map<String, Double>>> getProperties() {
		return properties;
	}
	public void addProperties(String indicatorName,Map<String, Double> props) {
		
		properties.ifPresentOrElse(allProp->{
			allProp.put(indicatorName, props);
		}, ()->{
			HashMap<String, Map<String, Double>> map = new HashMap<>();
			map.put(indicatorName, props);
			properties = Optional.of(map);
		});
		
		
	}
	Optional<List<String>> indicatorApplicationOrder = Optional.empty();
	
	
	public Optional<List<String>> getIndicatorApplicationOrder() {
		return indicatorApplicationOrder;
	}
	public void setIndicatorApplicationOrder(Optional<List<String>> indicatorApplicationOrder) {
		this.indicatorApplicationOrder = indicatorApplicationOrder;
	}

	Optional<String> indicatorName = Optional.empty();

	public Optional<String> getIndicatorName() {
		return indicatorName;
	}
	public void setIndicatorName(Optional<String> indicatorName) {
		this.indicatorName = indicatorName;
	}
	
	Optional<AlertDescriptor> alertDescriptor = Optional.empty();
	
	public Optional<AlertDescriptor> getAlertDescriptor() {
		return alertDescriptor;
	}
	public void setAlertDescriptor(Optional<AlertDescriptor> alertDescriptor) {
		this.alertDescriptor = alertDescriptor;
	}
	
	Optional<TransformDescriptor> transformDescriptor = Optional.empty();

	public Optional<TransformDescriptor> getTransformDescriptor() {
		return transformDescriptor;
	}
	public void setTransformDescriptor(Optional<TransformDescriptor> transformDescriptor) {
		this.transformDescriptor = transformDescriptor;
	}
	
	Optional<String> email = Optional.empty();

	public Optional<String> getEmail() {
		return email;
	}
	public void setEmail(Optional<String> email) {
		this.email = email;
	}
	
	Optional<List<Integer>> assetSampleRateMinutes = Optional.empty();

	public Optional<List<Integer>> getAssetSampleRateMinutes() {
		return assetSampleRateMinutes;
	}
	public void setAssetSampleRateMinutes(Optional<List<Integer>> assetSampleRateMinutes) {
		this.assetSampleRateMinutes = assetSampleRateMinutes;
	}
	
	
	
}
