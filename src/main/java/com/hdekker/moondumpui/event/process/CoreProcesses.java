package com.hdekker.moondumpui.event.process;

/**
 * Lists all application process names
 * 
 * Each process creates ProcessEvents
 * as health checks
 * 
 * 
 * @author Hayden Dekker
 *
 */
public enum CoreProcesses {

	/**
	 * Core module's system's overall health
	 * 
	 */
	CORE_HEALTH_STATUS("Core Health Status"),
	
	/**
	 * Tests configured database on
	 * startup
	 * 
	 */
	STARTUP_DB_LOG("Startup DB log");
	
	String name;
	
	private CoreProcesses(String name) {
		this.name = name;
	}
	
}
