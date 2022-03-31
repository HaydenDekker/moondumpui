package com.hdekker.moondumpui.dyndb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@Component
public class DatabaseClientConfig {

	@Autowired
	DatabaseConfig dbc;
	
	DynamoDbAsyncClient client;
	
	public DatabaseClientConfig(DatabaseConfig dbc) {
		
		client = DynamoDbAsyncClient.builder()
				.region(Region.of(dbc.getRegion()))
				.build();
		
	}
	
	@Bean
	DynamoDbAsyncClient getDynamoDBClient() {
		return client;
	}
	
}
