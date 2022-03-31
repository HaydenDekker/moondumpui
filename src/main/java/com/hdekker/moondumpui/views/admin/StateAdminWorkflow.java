package com.hdekker.moondumpui.views.admin;

import java.util.Optional;

import com.hdekker.moondumpui.subscription.IndicatorSubscription;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
@SpringComponent
public class StateAdminWorkflow {

	Optional<String> sortKeyForIndicators = Optional.empty();

	public Optional<String> getSortKeyForIndicators() {
		return sortKeyForIndicators;
	}

	public void setSortKeyForIndicators(Optional<String> sortKeyForIndicators) {
		this.sortKeyForIndicators = sortKeyForIndicators;
	}



	Optional<IndicatorSubscription> uss = Optional.empty();
	
	public Optional<IndicatorSubscription> getUss() {
		return uss;
	}

	public void setUss(Optional<IndicatorSubscription> uss) {
		this.uss = uss;
	}
	
}
