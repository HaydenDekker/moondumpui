package com.hdekker.moondumpui.dyndb.opps;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.opps.marshalers.ProcessEventToItemMarshaller;
import com.hdekker.moondumpui.event.process.ProcessEvent;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@Component
public class ProcessEventConsumer extends DatabaseOperation{

	@Autowired
	ProcessEventToItemMarshaller marshaler;
	
	public Mono<ProcessEvent> logStartupEvent(ProcessEvent event){
		
		CompletableFuture<PutItemResponse> fut = client.putItem(b->{
			b.tableName(this.databaseConfig.getTableName());
			b.item(marshaler.convert(event));
		});
		
		return Mono.fromFuture(fut)
				.map(f-> event);
		
	}
}
