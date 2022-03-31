package com.hdekker.moondumpui.dyndb.opps;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
public class IndicatorSubscriptionsProvider extends DatabaseOperation{

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
		
		public Mono<List<IndicatorSubscription>> getUserSubscriptionsForUser(String user){
			return Mono.fromCompletionStage(client.query(builder->{
				
				builder.tableName(databaseConfig.getTableName());
				builder.expressionAttributeValues(Map.of(
						":pk",
						AttributeValue.builder()
							.s(PrimaryKeySpec.INDICATOR_SUBSCRIPTION
									.getPrimaryKeyValue())
							.build(),
							":skval",
						AttributeValue.builder()
							.s(user)
							.build()
						
				));
				builder.keyConditionExpression(databaseConfig.getPrimaryKey() + " = :pk and begins_with(" + databaseConfig.getSortKey() + ", :skval)");
				
			}).thenApply(resp->{
		
					List<IndicatorSubscription> usss = resp.items()
							.stream()
							.map(item-> databaseMapToUserSubscriptionSpec.apply(item))
							.collect(Collectors.toList());
					return usss;
					
			}));

		}
		
	
}
