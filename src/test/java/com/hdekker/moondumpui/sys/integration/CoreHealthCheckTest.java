package com.hdekker.moondumpui.sys.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.moondumpui.event.process.ProcessEvent;
import com.hdekker.moondumpui.system.health.CoreHealthStatusSupplier;
import com.hdekker.moondumpui.system.health.FunctionStatus;
import com.hdekker.moondumpui.system.health.SystemHealthConfiguration;

/**
 * Test requires core module online
 * 
 * TODO move to dedicated integration package.
 * 
 * 
 * @author Hayden Dekker
 *
 */
@SpringBootTest
public class CoreHealthCheckTest {
	
	@Autowired
	CoreHealthStatusSupplier healthStatusSupplier;
	
	@Autowired
	SystemHealthConfiguration systemConfiguration;

	@Test
	public void startsHealthStatusProcessOnBoot() {
		
		FunctionStatus evt = healthStatusSupplier.getHealthStatusEvent()
			.block();
		
		assertThat(evt, equalTo(FunctionStatus.OK));
		
	}
	
}
