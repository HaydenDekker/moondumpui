package com.hdekker.moondumpui.dyndb.opps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.UserSubscriptions;
import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Component
public class IndicatorSubscriptionsSortKeysSupplier extends DatabaseOperation{

	private CompletableFuture<QueryResponse> getAllSubscriptionKeys(){
		return client.query(builder->{
			builder.tableName(databaseConfig.getTableName());
			builder.expressionAttributeValues(Map.of(":pk", AttributeValue.builder()
				.s(PrimaryKeySpec.INDICATOR_SUBSCRIPTION.getPrimaryKeyValue())
				.build()
				));
			builder.projectionExpression(
				databaseConfig.getSortKey());
			builder.keyConditionExpression(databaseConfig.getPrimaryKey() + " = :pk");
		});
	}

	/**
	 * User email is encoded in the sort key.
	 * 
	 * @return
	 */
	public Mono<List<UserSubscriptions>> getUserSubscriptionsByEmail() {
		
		return Mono.fromCompletionStage(
					getAllSubscriptionKeys().thenApplyAsync(keys-> {
			
			List<UserSubscriptions> userSubscriptions = keys.items()
				.stream()
				.map(k->k.get(databaseConfig.getSortKey())
							.s())
				.collect(Collectors.toMap(s-> s.split("-")[0], 
						  v->List.of(v),
						  (p,n)->{
							  List<String> ln = new ArrayList<>(p);
							  ln.addAll(n);
							  return ln;
						  }))
				.entrySet()
				.stream()
				.map(es-> new UserSubscriptions(es.getKey(), es.getValue()))
				.collect(Collectors.toList());
			
			return userSubscriptions;
			
		}));
		
	}
	
}
