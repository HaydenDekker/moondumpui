package com.hdekker.moondumpui.sysadmin;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.moondumpui.dyndb.opps.StartupEventSupplier;
import com.hdekker.moondumpui.event.process.ProcessEvent;
import com.hdekker.moondumpui.event.process.ProcessState;
import com.hdekker.moondumpui.system.startup.StartupDBTest;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

@SpringBootTest
public class OnStartDatabaseLogTest {

	@Autowired
	StartupEventSupplier events;
	
	@Test
	public void onStartupShouldLogDBEvent() {
		
		ProcessEvent lastEvent = events.getAllStartupEvents()
			.block()
			.stream()
			.sorted((f,s)->f.getEventTime().compareTo(s.getEventTime()))
			.collect(Collectors.toList())
			.get(0);
		
		assertThat(lastEvent.getState(), equalTo(ProcessState.COMPLETED));
		assertThat(lastEvent.getEventDescription(), equalTo(StartupDBTest.eventDescription));
		
	}
	
}
