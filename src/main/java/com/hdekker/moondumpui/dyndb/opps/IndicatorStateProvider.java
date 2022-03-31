package com.hdekker.moondumpui.dyndb.opps;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.moondumpui.dyndb.IndicatorStateAttributeNames;
import com.hdekker.moondumpui.views.admin.IndicatorStateDTO;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;

@Service
public class IndicatorStateProvider extends DatabaseOperation{

public Mono<List<IndicatorStateDTO>> getIndicatorDetails(List<String> itemSortKeys) {
		
		List<Map<String, AttributeValue>> keys = itemSortKeys.stream()
				.map(k->
					Map.of(
							"PK", 
							AttributeValue
								.builder()
								.s("IAttr")
								.build(),
							"SK", 
							AttributeValue
								.builder()
								.s(k)
								.build()
							)
		
				).collect(Collectors.toList());
			
			CompletableFuture<BatchGetItemResponse> res = client.batchGetItem(builder->{
				
				builder.requestItems(Map.of(databaseConfig.getTableName(),
							KeysAndAttributes.builder()
								.keys(keys)
								.build()
				));
				
			});
			
			ObjectMapper om = new ObjectMapper();
			om.registerModule(new JavaTimeModule());
			
			// recieve db state
			return Mono.fromCompletionStage(res.thenApply(c->{
	
						return c.responses().get(databaseConfig.getTableName())
						.stream()
						.map(item->{

							LocalDateTime val = null;
							try {
								val = om.readValue(item.get(IndicatorStateAttributeNames.DATE.getAttributeName()).s(), LocalDateTime.class);
							} catch (JsonProcessingException e) {
								e.printStackTrace();
							}
							return new IndicatorStateDTO(item.get(IndicatorStateAttributeNames.STATE.getAttributeName()).s(), val);
				
						})
						.collect(Collectors.toList());
						
						
					}));
		
	}
	
}
