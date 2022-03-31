package com.hdekker.moondumpui.event.process;

public enum ProcessState {

	RUNNING("Running"), // working on a task
	IDLE("Idle"), // awaiting input
	COMPLETED("Completed"), // not accepting new inputs, last run was successful
	FAILED("Failed"), // not accepting new inputs, last run failed.
	ERROR("Error"), // process completed but encounter emitting an error
	UNKNOWN("Unknown"); // it is not possible to determine the process state
	
	final String statusName;
	
	private ProcessState(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusName() {
		return statusName;
	}
	
}
