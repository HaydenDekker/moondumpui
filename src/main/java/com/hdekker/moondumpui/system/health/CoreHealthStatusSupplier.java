package com.hdekker.moondumpui.system.health;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.opps.LatestProcessEventProvider;
import com.hdekker.moondumpui.event.process.CoreProcesses;

import reactor.core.publisher.Mono;

@Component
public class CoreHealthStatusSupplier extends LatestProcessEventProvider {

	@Autowired
	SystemHealthConfiguration systemConfiguration;
	
	public Mono<FunctionStatus> getHealthStatusEvent() {

		return getLatestProcessEventFor(CoreProcesses.CORE_HEALTH_STATUS)
		 .map(pe->{
					
			LocalDateTime now = LocalDateTime.now().minusSeconds(systemConfiguration.getLoggingPeriodSeconds());
			return (pe.getEventTime().isAfter(now)) ? FunctionStatus.OK : FunctionStatus.ERROR;	
			
		})
		.onErrorResume(e-> Mono.just(FunctionStatus.NO_Events_Found));
		
	}
	
}
