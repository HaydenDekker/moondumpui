package com.hdekker.moondumpui.dyndb.opps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Component
public class InterfaceNamesSupplier extends DatabaseOperation{

	public Mono<List<String>> getNames(){
		
		CompletableFuture<QueryResponse> res = client.query(builder->{
			
			builder.tableName(databaseConfig.getTableName());
			builder.expressionAttributeValues(Map.of(":pk", AttributeValue.builder()
															.s(PrimaryKeySpec.SAMPLER_INTERFACE_KEYS.getPrimaryKeyValue())
															.build()));
			builder.projectionExpression(databaseConfig.getSortKey());
			builder.keyConditionExpression(databaseConfig.getPrimaryKey() + " = :pk");
			
		});
		
		return Mono.fromCompletionStage(res.thenApply(resp->{ 
			
			List<String> interfaceNamesList = resp.items()
				.stream()
				.map(m-> m.get("SK").s())
				.collect(Collectors.toList());
			
			return interfaceNamesList;
			
		}));
		
	}
	
}
