package com.hdekker.moondumpui.views.admin;

import java.util.Optional;

import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpec;
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



	Optional<UserSubscriptionSpec> uss = Optional.empty();
	
	public Optional<UserSubscriptionSpec> getUss() {
		return uss;
	}

	public void setUss(Optional<UserSubscriptionSpec> uss) {
		this.uss = uss;
	}
	
}
