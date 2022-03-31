package com.hdekker.moondumpui.system.startup;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.hdekker.moondumpui.dyndb.opps.ProcessEventConsumer;
import com.hdekker.moondumpui.event.process.CoreProcesses;
import com.hdekker.moondumpui.event.process.ProcessEvent;
import com.hdekker.moondumpui.event.process.ProcessState;

@Configuration
public class StartupDBTest {

	public final static String eventDescription = "UI Service has started.";
	
	private final LocalDateTime systemStartupTime;
	
	@Autowired
	ProcessEventConsumer eventConsumer;
	
	public StartupDBTest(ProcessEventConsumer eventConsumer) {
	
		this.eventConsumer = eventConsumer;
		
		systemStartupTime = LocalDateTime.now();
		
		eventConsumer.logStartupEvent(new ProcessEvent(CoreProcesses.STARTUP_DB_LOG, systemStartupTime, eventDescription, ProcessState.COMPLETED))
			.subscribe((p)->{}, (e)-> {throw new Error("Database must be reachable on startup. " + e.getMessage());});
		
	}

	public LocalDateTime getSystemStartupTime() {
		return systemStartupTime;
	}	
	
}
