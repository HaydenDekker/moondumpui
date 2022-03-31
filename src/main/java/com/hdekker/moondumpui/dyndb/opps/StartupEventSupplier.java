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
public class StartupEventSupplier extends DatabaseOperation {
	
	@Autowired
	ItemToProcessEventMarshaler marshaler;

	public Mono<List<ProcessEvent>> getAllStartupEvents(){
		
		CompletableFuture<QueryResponse> query = client.query(b->{
			
			b.tableName(databaseConfig.getTableName());
			b.expressionAttributeValues(Map.of(":apn", AttributeValue.builder()
								.s(CoreProcesses.STARTUP_DB_LOG.toString())
								.build()));
			b.keyConditionExpression("PK = :apn");

		});
		
		return Mono.fromFuture(query)
			.map(q->
				q.items()
					.stream()
					.map(item-> marshaler.convert(item))
					.collect(Collectors.toList())
			);
	}
	
}
