package com.hdekker.moondumpui.dyndb.opps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.opps.marshalers.ItemToProcessEventMarshaler;
import com.hdekker.moondumpui.event.process.CoreProcesses;
import com.hdekker.moondumpui.event.process.ProcessEvent;

import reactor.core.publisher.Mono;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Component
public class ProcessEventProvider extends DatabaseOperation{
	
	@Autowired
	ItemToProcessEventMarshaler marshaler;

	// TODO these events require life time property in DB
	public Mono<List<ProcessEvent>> getProcessEventsFor(CoreProcesses process){
		
		CompletableFuture<QueryResponse> allProcessEvents = client.query(b->{
			b.tableName(databaseConfig.getTableName());
			b.expressionAttributeValues(Map.of("primaryKeyVal", AttributeValue.builder()
					.s(process.toString())
					.build())
			);
			b.keyConditionExpression("PK = :primaryKeyVal");
		});
		
		return Mono.fromCompletionStage(allProcessEvents)
			.map(query->{
				
				return query.items()
					.stream()
					.map(item->
						marshaler.convert(item)
					).collect(Collectors.toList());
			});
		
	}
	
}
