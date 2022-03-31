package com.hdekker.moondumpui.dyndb.opps;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;

@Component
public class TemporaryIndicatorSubscriptionRemover extends DatabaseOperation {

	public CompletableFuture<DeleteItemResponse> remove(String uuid){
		
		return client.deleteItem(b->{
			
			b.tableName(databaseConfig.getTableName());
			b.key(Map.of(
					databaseConfig.getPrimaryKey(),
					AttributeValue.builder()
						.s(PrimaryKeySpec.INDICATOR_SUBSCRIPTION_TEMP
								.getPrimaryKeyValue())
						.build(),
					databaseConfig.getSortKey(),
					AttributeValue.builder()
						.s(uuid)
						.build()));
			
		});
		
	}
	
}
