package com.hdekker.moondumpui.dyndb.opps;

import java.time.LocalDateTime;

import com.hdekker.moondumpui.views.admin.core.io.IOEvent;

import reactor.core.publisher.Mono;

public class IOEventProvider extends DatabaseOperation {

	public Mono<IOEvent> getIOEventsBetween(LocalDateTime startDate, LocalDateTime endDate){
		return null;
	}
	
}
