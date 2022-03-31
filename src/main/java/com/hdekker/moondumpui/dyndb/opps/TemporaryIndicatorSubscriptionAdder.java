package com.hdekker.moondumpui.dyndb.opps;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
public class TemporaryIndicatorSubscriptionAdder extends DatabaseOperation{

	public void add(String uuid, IndicatorSubscription is) {
		
		client.putItem(builder->{
			
			ObjectMapper om = new ObjectMapper();
			
			String propStr = "";
			try {
				propStr = om.writeValueAsString(is.getIndicatorConfig());
			} catch (JsonProcessingException er) {
				er.printStackTrace();
			}
			
			builder.tableName(databaseConfig.getTableName());
			builder.item(Map.of(
				databaseConfig.getPrimaryKey(),
				AttributeValue.builder()
					.s(PrimaryKeySpec.INDICATOR_SUBSCRIPTION_TEMP.getPrimaryKeyValue())
					.build(),
				databaseConfig.getSortKey(),
				AttributeValue.builder()
					.s(uuid)
					.build(),
				"interfaceName",
				AttributeValue.builder()
					.s(is.getInterfaceName())
					.build(),
				"assetName",
				AttributeValue.builder()
					.s(is.getAssetName())
					.build(),
				"indicatorConfig",
				AttributeValue.builder()
					.s(propStr)
					.build(),
				"indicatorOrder",
				AttributeValue.builder()
					.l(
						is.getIndicatorOrder()
							.stream()
							.map(ind->AttributeValue.builder()
									.s(ind)
									.build())
						.collect(Collectors.toList()))
					.build(),	
				"indicatorName",
				AttributeValue.builder()
					.s(is.getIndicatorName())
					.build(),
			    "email",
					AttributeValue.builder()
						.s(is.getEmail())
						.build(),
				"sampleRates",
				AttributeValue.builder()
					.ns(is.getSampleRates()
							.stream()
							.map(n->n.toString())
							.collect(Collectors.toList()))
					.build()
				
			));

		});
		
	}
	
}
