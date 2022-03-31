package com.hdekker.moondumpui.event.process;

import java.time.LocalDateTime;

/**
 * 
 * Emits multiple times during 
 * process life, it is up to down stream
 * system to convert into metrics.
 * 
 * @author Hayden Dekker
 *
 */
public class ProcessEvent {

	final CoreProcesses processName;
	final ProcessState state;
	final LocalDateTime eventTime;
	final String eventDescription;
	
	
	public ProcessEvent(CoreProcesses processName, LocalDateTime eventTime, String eventDescription,
			ProcessState state) {
		super();
		this.processName = processName;
		this.eventTime = eventTime;
		this.eventDescription = eventDescription;
		this.state = state;
	}
	public CoreProcesses getProcessName() {
		return processName;
	}
	public LocalDateTime getEventTime() {
		return eventTime;
	}
	public String getEventDescription() {
		return eventDescription;
	}
	public ProcessState getState() {
		return state;
	}
	
}
