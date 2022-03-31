package com.hdekker.moondumpui.views.admin;

import java.time.LocalDateTime;

public class IndicatorStateDTO {

	final String stateJSON;
	final LocalDateTime lastUpdated;
	
	public IndicatorStateDTO(String stateJSON, LocalDateTime lastUpdated) {
		super();
		this.stateJSON = stateJSON;
		this.lastUpdated = lastUpdated;
	}

	public String getStateJSON() {
		return stateJSON;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}
	
}
