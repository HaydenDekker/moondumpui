package com.hdekker.moondumpui.dyndb.opps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@Component
public class AvailableAssetsSupplier extends DatabaseOperation{

	public Mono<List<String>> getAllAssets(String interfaceName){
		
		CompletableFuture<GetItemResponse> item = client.getItem(builder->{
			
			builder.tableName(databaseConfig.getTableName());
			builder.key(Map.of(
					databaseConfig.getPrimaryKey(),
					AttributeValue.builder()
						.s(PrimaryKeySpec.SAMPLER_INTERFACE_KEYS.getPrimaryKeyValue())
						.build(),
					databaseConfig.getSortKey(),
					AttributeValue.builder()
						.s(interfaceName)
						.build())
				);
			
		});
		
		return Mono.fromCompletionStage(item)
			.map(resp->resp.item()
				.get("list")
				.ss());
		
	}
	
}
