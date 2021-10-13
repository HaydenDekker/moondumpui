package com.hdekker.moondumpui.dyndb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpec;
import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpecStoreSpec;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class Marshalling {

	public static Function<UserSubscriptionSpec, Map<String, AttributeValue>> converter(){
	
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
	
	public static Function<UserSubscriptionSpec, String> createSortKey(){
		
		return (uss) -> uss.getEmail() + "-" + uss.getIndicatorName();
		
	}
	

	public static Function<String, Map<String, Map<String, Double>>> convertIndicatorConfigToMap
	= (config) -> {
		
		ObjectMapper om = new ObjectMapper();
		TypeReference<Map<String, Map<String, Double>>>
			typeRef = new TypeReference<Map<String,Map<String,Double>>>() {};
			Map<String, Map<String, Double>> val = null;
			try {
				val = om.readValue(config, typeRef);
			} catch (JsonProcessingException e) {
				LoggerFactory.getLogger("Indicator Config Converter")
				.error("Failed to convert user defined indicator configuration.");
				e.printStackTrace();
			}
			return val;
		
	};

	/**
	 *  Map dynDb isub item to user sub object.
	 * 
	 */
	public static Function<Map<String, AttributeValue>, UserSubscriptionSpec>
		databaseMapToUserSubscriptionSpec =
		(map) -> {
			
			try {
			String assetName = map.get("assetName").s();
			String email = map.get("email").s();
			String indicatorConfig = map.get("indicatorConfig").s();
			Map<String, Map<String, Double>> indicatorConfigAsMap = convertIndicatorConfigToMap.apply(indicatorConfig);
			String indicatorName = map.get("indicatorName").s();
			List<String> indicatorOrder = map.get("indicatorOrder")
												.l()
												.stream()
												.map(av->av.s())
												.collect(Collectors.toList());
			String interfaceName = map.get("interfaceName").s();
			List<Double> sampleRates = map.get("sampleRates")
											.ns()
											.stream()
											.map(s->Double.valueOf(s))
											.collect(Collectors.toList());
			
			UserSubscriptionSpec uss = new UserSubscriptionSpec(
					assetName, 
					email,
					indicatorConfigAsMap,
					indicatorName, 
					indicatorOrder,
					interfaceName, 
					sampleRates);
			
			return uss;
			
			}catch(NullPointerException e) {
				throw new Error("Database returned a malformed Isub, it must never do this.");
			}
			
		};


	
		
		public static Function<UserSubscriptionSpecStoreSpec, Map<String, AttributeValue>> convertToSubscriptionMap(){
			return (uss) -> {
				
				Map<String, AttributeValue> vals = converter().apply(uss.getUss());
				
				HashMap<String, AttributeValue> withKeys = new HashMap<>(vals);
				withKeys.put(uss.getDbPKRef(),
							AttributeValue.builder()
								.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
								.build());
				withKeys.put(uss.getDbSKRef(),
							AttributeValue.builder()
								.s(createSortKey().apply(uss.getUss()))
								.build());
				
				return withKeys;
				
			};
		}
		
}
