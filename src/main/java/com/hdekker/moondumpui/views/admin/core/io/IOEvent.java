package com.hdekker.moondumpui.views.admin.core.io;

import java.time.LocalDateTime;
import java.util.Optional;

public class IOEvent {
	
	final String eventAPI;
	final IOEventType eventType;
	final Optional<String> optionalMessage;
	final LocalDateTime startTime;

	public IOEvent(String eventAPI, String optionalMessageForMaintainer, LocalDateTime startTime,
			IOEventType eventType) {
		super();
		this.eventAPI = eventAPI;
		this.eventType = eventType;
		this.optionalMessage = Optional.ofNullable(optionalMessageForMaintainer);
		this.startTime = startTime;
	}

	public String getEventAPI() {
		return eventAPI;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public IOEventType getEventType() {
		return eventType;
	}

	public Optional<String> getOptionalMessage() {
		return optionalMessage;
	}

}
