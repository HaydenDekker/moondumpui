package com.hdekker.moondumpui.dyndb.opps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.IndicatorDescriptorAttributes;
import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;
import com.hdekker.moondumpui.dyndb.SortKeySearchConstants;
import com.hdekker.moondumpui.views.onboard.alerts.AlertDescriptor;
import com.hdekker.moondumpui.views.onboard.transform.TransformDescriptor;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
public class TransformDescriptorSupplier extends DatabaseOperation{

	public Mono<List<TransformDescriptor>> getAllTransformDescriptors(){
		
		CompletableFuture<List<TransformDescriptor>> itemFuture = client.query(builder->{
			
			builder.tableName(databaseConfig.getTableName());
			builder.expressionAttributeValues(Map.of(":pk", AttributeValue.builder()
					.s(PrimaryKeySpec.INDICATOR_DISCRIPTOR.getPrimaryKeyValue())
					.build(),
					":skval",
					AttributeValue.builder()
						.s(SortKeySearchConstants.INDICATOR_DISCRIPTOR_TRANSFORMS.getSearchConstant())
						.build()
					));
//			builder.projectionExpression(
//					databaseConfig.getSortKey() + ", " +
//					IndicatorDescriptorAttributes.INDICATOR_DESCRIPTOR_FNNAME);
			builder.keyConditionExpression(databaseConfig.getPrimaryKey() + " = :pk and begins_with(" + databaseConfig.getSortKey() + ", :skval)");
			
		})
		.thenApply(resp->{
			
			return resp.items()
				.stream()
				.map(item->{
					
					return new TransformDescriptor(
								item.get(IndicatorDescriptorAttributes.INDICATOR_DESCRIPTOR_FNNAME.getAttr())
									.s(), 
								item.get(IndicatorDescriptorAttributes.INDICATOR_DESCRIPTOR_CONFIGURABLE_PROPERTIES_AND_DEFAULTS.getAttr())
									.m()
									.entrySet()
									.stream()
									.collect(Collectors.toMap(e->e.getKey(), e->Double.valueOf(e.getValue().n()))));
				})
				.collect(Collectors.toList());
			
		});	
		
		return Mono.fromCompletionStage(itemFuture);
		
	}
	
}
