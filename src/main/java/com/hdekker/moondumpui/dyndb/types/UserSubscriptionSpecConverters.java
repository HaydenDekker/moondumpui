package com.hdekker.moondumpui.dyndb.types;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class UserSubscriptionSpecConverters {
	
	
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
	public static Function<Map<String, AttributeValue>, IndicatorSubscription>
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
			
			IndicatorSubscription uss = new IndicatorSubscription(
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
		
		private static Function<IndicatorSubscription, String> createUSSSortKey(){
			
			return (uss) -> uss.getEmail() + "-" + uss.getIndicatorName();
			
		}
		
		public static Function<String,
		Function<String,
		Function<IndicatorSubscription, Map<String, AttributeValue>>>> convertToAttributeValues(){
			
			return (PKName)-> (SKName)-> (spec) -> {
				
				ObjectMapper om = new ObjectMapper();
				
				String propStr = "";
				try {
					propStr = om.writeValueAsString(spec.getIndicatorConfig());
				} catch (JsonProcessingException er) {
					er.printStackTrace();
				}
				
				return Map.of(
						PKName,
						AttributeValue.builder()
							.s(PrimaryKeySpec.INDICATOR_SUBSCRIPTION.getPrimaryKeyValue())
							.build(),
						SKName,
						AttributeValue.builder()
							.s(createUSSSortKey().apply(spec))
							.build(),
						"interfaceName",
						AttributeValue.builder()
							.s(spec.getInterfaceName())
							.build(),
						"assetName",
						AttributeValue.builder()
							.s(spec.getAssetName())
							.build(),
						"indicatorConfig",
						AttributeValue.builder()
							.s(propStr)
							.build(),
						"indicatorOrder",
						AttributeValue.builder()
							.l(
								spec.getIndicatorOrder()
									.stream()
									.map(ind->AttributeValue.builder()
											.s(ind)
											.build())
								.collect(Collectors.toList()))
							.build(),	
						"indicatorName",
						AttributeValue.builder()
							.s(spec.getIndicatorName())
							.build(),
					    "email",
							AttributeValue.builder()
								.s(spec.getEmail())
								.build(),
						"sampleRates",
						AttributeValue.builder()
							.ns(spec.getSampleRates()
									.stream()
									.map(n->n.toString())
									.collect(Collectors.toList()))
							.build()
						
					);
				
				
			};
			
			
		}
	
}
