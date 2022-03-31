package com.hdekker.moondumpui.dyndb.opps;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
public class UnsubscribeSubscriptionsProvider extends DatabaseOperation {

	@Autowired
	IndicatorSubscriptionsProvider usp;
	
	public Mono<List<IndicatorSubscription>> getSubscriptionsForUnsubSHA(String sha){
		
		return Mono.fromCompletionStage(client.getItem(b->{

			b.tableName(databaseConfig.getTableName());
			b.key(Map.of(
					databaseConfig.getPrimaryKey(),
					AttributeValue.builder()
						.s(PrimaryKeySpec.INDICATOR_UNSUBSCRIBE.getPrimaryKeyValue())
						.build(),
					databaseConfig.getSortKey(),
					AttributeValue.builder()
						.s(sha)
						.build()
					
			));
		})
		.thenApply(item->{
			String email = item.item().get("email").s();
			return email;
		}))
		.flatMap(email->usp.getUserSubscriptionsForUser(email));
		
	}
	
}
