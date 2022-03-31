package com.hdekker.moondumpui.dyndb.opps;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.Marshalling;
import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;

@Component
public class IndicatorSubscriptionRemover extends DatabaseOperation {

	public CompletableFuture<DeleteItemResponse> deleteSubscription(IndicatorSubscription uss) {
		
		return client.deleteItem(b->{
			
			b.tableName(databaseConfig.getTableName());
			b.key(Map.of(
				databaseConfig.getPrimaryKey(),
				AttributeValue.builder()
					.s(PrimaryKeySpec.INDICATOR_SUBSCRIPTION
							.getPrimaryKeyValue())
					.build(),
				databaseConfig.getSortKey(),
				AttributeValue.builder()
					.s(Marshalling.createSortKey().apply(uss))
					.build()
					
			));
			
		});
		
	}
}
