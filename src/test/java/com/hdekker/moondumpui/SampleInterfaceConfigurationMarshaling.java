package com.hdekker.moondumpui;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@SpringBootTest
public class SampleInterfaceConfigurationMarshaling {

	public DynamoDbAsyncClient client;
	public static final String SAMPLER_INTERFACE_KEYS = "SIFKeys";
	
	@Autowired
	DatabaseConfig dbc;
	
	@BeforeEach
	public void setClient() {
		
		client = DynamoDbAsyncClient.builder()
				.region(Region.of(dbc.getRegion()))
				.build();
		
		
	}
	
	@Test
	public void  canMarshalDataTake2() {
		
		CompletableFuture<QueryResponse> res = client.query(builder->{
			
			builder.tableName(dbc.getTableName());
			builder.expressionAttributeValues(Map.of(":pk", AttributeValue.builder()
															.s(SAMPLER_INTERFACE_KEYS)
															.build()));
			builder.projectionExpression("SK");
			builder.keyConditionExpression("PK = :pk");
			
		});
		
		QueryResponse qr = res.join();
		assertThat(qr.items().size(), equalTo(2));
		assertThat(qr.items().get(0).keySet().size(), equalTo(1));
		
	}
	
	
	
}
