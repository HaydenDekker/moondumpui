package com.hdekker.moondumpui.dyndb.opps;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;

import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

public class DatabaseOperation {
	
	@Autowired
	DynamoDbAsyncClient client;
	
	@Autowired
	DatabaseConfig databaseConfig;
	
	public DatabaseOperation() {
	}
	
}
