package com.hdekker.moondumpui;

import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


@SpringBootTest
public class AssetIndicatorConfigurationTest {

	public DynamoDbAsyncClient client;
	
	@Autowired
	DatabaseConfig dbc;
	
	@BeforeEach
	public void setClient() {
		
		client = DynamoDbAsyncClient.builder()
				.region(Region.of(dbc.getRegion()))
				.build();
		
		
	}
	
	@Test
	public void canStoreConfiguredIndicatorData() {
		
		String ifName = "UI-testIF";
		String assName = "UI-testAsset";
		Map<String, Map<String, Double>> properties = Map.of(
				"UI-Test-Indicator",
				Map.of("steps", 14.0),
				"UI-Test-Alert",
				Map.of("thresh", 23.0)
		);
		
		ObjectMapper om = new ObjectMapper();
		
		String email = "test.Email@hotmail.com";
		String indicatorName = "UI-test-indicator-rises";
		
		PutItemResponse resp = client.putItem(builder->{
			
			String propStr = "";
			try {
				propStr = om.writeValueAsString(properties);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			builder.tableName(dbc.getTableName());
			builder.item(Map.of(
				dbc.getPrimaryKey(),
				AttributeValue.builder()
					.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
					.build(),
				dbc.getSortKey(),
				AttributeValue.builder()
					.s(email + "-" + indicatorName)
					.build(),
				"interfaceName",
				AttributeValue.builder()
					.s(ifName)
					.build(),
				"assetName",
				AttributeValue.builder()
					.s(assName)
					.build(),
				"indicatorConfig",
				AttributeValue.builder()
					.s(propStr)
					.build(),
				"indicatorName",
				AttributeValue.builder()
					.s(indicatorName)
					.build(),
			    "email",
					AttributeValue.builder()
						.s(email)
						.build()
				
			));
			
			
		}).join();
		
		assertThat(resp.sdkHttpResponse().statusCode(), equalTo(200));
		
	}
}
