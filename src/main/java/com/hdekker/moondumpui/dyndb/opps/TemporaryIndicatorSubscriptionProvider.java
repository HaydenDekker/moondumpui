package com.hdekker.moondumpui.dyndb.opps;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@Component
public class TemporaryIndicatorSubscriptionProvider extends DatabaseOperation{

	public Mono<IndicatorSubscription> getSubscription(String uuid){
		
		CompletableFuture<GetItemResponse> respOfCheckForPendingConfirmation = client.getItem(b->{
			b.tableName(databaseConfig.getTableName());
			b.key(Map.of(
					databaseConfig.getPrimaryKey(),
					AttributeValue.builder()
						.s(PrimaryKeySpec.INDICATOR_SUBSCRIPTION_TEMP.getPrimaryKeyValue())
						.build(),
					databaseConfig.getSortKey(),
					AttributeValue.builder()
						.s(uuid)
						.build()
			));
			
		});
		
		return Mono.fromCompletionStage(respOfCheckForPendingConfirmation)
			.map(resp->{
				IndicatorSubscription uss = IndicatorSubscriptionsProvider.databaseMapToUserSubscriptionSpec
						.apply(resp.item());
				return uss;
			});
		
	}
	
}
