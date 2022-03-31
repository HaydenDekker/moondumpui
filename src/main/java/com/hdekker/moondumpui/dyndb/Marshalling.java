package com.hdekker.moondumpui.dyndb;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Marshalling {

	public static Function<IndicatorSubscription, Map<String, AttributeValue>> converter(){
	
			ObjectMapper om = new ObjectMapper();

			return (uss) -> {
				
				String propStr = "";
				try {
					propStr = om.writeValueAsString(uss.getIndicatorConfig());
				} catch (JsonProcessingException er) {
					er.printStackTrace();
				}
			
				return Map.of(
//						dbc.getPrimaryKey(),
//						AttributeValue.builder()
//							.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
//							.build(),
//						dbc.getSortKey(),
//						AttributeValue.builder()
//							.s(state.getEmail().get() + "-" + state.getIndicatorName().get())
//							.build(),
						"interfaceName",
						AttributeValue.builder()
							.s(uss.getInterfaceName())
							.build(),
						"assetName",
						AttributeValue.builder()
							.s(uss.getAssetName())
							.build(),
						"indicatorConfig",
						AttributeValue.builder()
							.s(propStr)
							.build(),
						"indicatorOrder",
						AttributeValue.builder()
							.l(
								uss.getIndicatorOrder()
									.stream()
									.map(ind->AttributeValue.builder()
											.s(ind)
											.build())
								.collect(Collectors.toList()))
							.build(),	
						"indicatorName",
						AttributeValue.builder()
							.s(uss.getIndicatorName())
							.build(),
					    "email",
							AttributeValue.builder()
								.s(uss.getEmail())
								.build(),
						"sampleRates",
						AttributeValue.builder()
							.ns(uss.getSampleRates()
									.stream()
									.map(n->n.toString())
									.collect(Collectors.toList()))
							.build());
				
			};
	}
	
	public static Function<IndicatorSubscription, String> createSortKey(){
		
		return (uss) -> uss.getEmail() + "-" + uss.getIndicatorName();
		
	}

	
}
