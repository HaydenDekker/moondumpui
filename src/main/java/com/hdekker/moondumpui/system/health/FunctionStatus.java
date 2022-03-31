package com.hdekker.moondumpui.system.health;

/**
 * Consistancy in displaying service
 * function status.
 * 
 * @author Hayden Dekker
 *
 */
public enum FunctionStatus {

	OK("OK"),
	ERROR("Error"),
	NO_Events_Found("No Events Found");
	
	private String status;
	
	private FunctionStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
}
