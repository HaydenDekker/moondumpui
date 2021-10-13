package com.hdekker.moondumpui.views;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.mobileapp.templates.SinglePageCard;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.state.SessionState;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.AfterNavigationObserver;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@StyleSheet("./shared-styles.css")
@Push
public abstract class BaseDynamoDBSinglePageCard extends SinglePageCard implements AfterNavigationObserver{

	@Autowired
	protected DatabaseConfig dbc;
	
	public final DynamoDbAsyncClient client;
	
	@Autowired
	protected SessionState state;
	
	public BaseDynamoDBSinglePageCard(DatabaseConfig dbc, SessionState state) {
		
		this.dbc = dbc;
		this.state = state;
		
		client = DynamoDbAsyncClient.builder()
				.region(Region.of(dbc.getRegion()))
				.build();
		
	}
	
	
}
