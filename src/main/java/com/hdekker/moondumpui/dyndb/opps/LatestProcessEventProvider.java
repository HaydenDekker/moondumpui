package com.hdekker.moondumpui.dyndb.opps;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.opps.marshalers.ItemToProcessEventMarshaler;
import com.hdekker.moondumpui.event.process.CoreProcesses;
import com.hdekker.moondumpui.event.process.ProcessEvent;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@Component
public class LatestProcessEventProvider extends DatabaseOperation{

	@Autowired
	ItemToProcessEventMarshaler marshaler;
	
	protected Mono<ProcessEvent> getLatestProcessEventFor(CoreProcesses process){
		
		CompletableFuture<GetItemResponse> item = client.getItem(b->{
			
			b.tableName(databaseConfig.getTableName());
			b.key(Map.of(
				databaseConfig.getPrimaryKey(),
				AttributeValue.builder()
					.s(process.toString())
					.build(),
					databaseConfig.getSortKey(),
				AttributeValue.builder()
					.s("latest")
					.build() 
			));
			//b.attributesToGet("name", "message", "date-time", "state", dbc.getPrimaryKey());
			
			
		});
		
		return Mono.fromCompletionStage(item)
			.doOnNext(resp-> {
				if(!resp.hasItem()) throw new RuntimeException("Could not find latest.");
			})
			.map(resp-> marshaler.convert(resp.item()));
		
	}
	
}
