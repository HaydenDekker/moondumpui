package com.hdekker.moondumpui;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.moondumpui.dyndb.opps.DatabaseOperation;
import com.hdekker.moondumpui.dyndb.opps.IOEventProvider;
import com.hdekker.moondumpui.dyndb.opps.IndicatorSubscriptionAdder;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

@SpringBootTest
public class ScenarioUserAddsIndicatorSubscription {
	
	@Autowired
	DatabaseOperation db;
	
	@Autowired
	IndicatorSubscriptionAdder isa;
	
	public static final String testEmailAddress = "hayden.d@hotmail.com";
	public static final String assetForSubscription1 = "bitcoin";
	
	/*
	 *  1 minute sample rate to better show what's happening.
	 * 
	 */
	public IndicatorSubscription subscription1() {
		
		return new IndicatorSubscription(
				assetForSubscription1, 
				testEmailAddress, 
				Map.of("Alert-ALERT_THRESHOLD_BELOW", Map.of("value",30.0),"Transform-TRANSFORM_RSI", Map.of("steps",14.0)),
				"rsi drops below", 
				List.of("Transform-TRANSFORM_RSI", "Alert-ALERT_THRESHOLD_BELOW"), 
				"coin-geko", 
				List.of(1.0));
		
	}
	
	public void addUserSubscriptionToDB() {
			
		isa.addUserSubscription(subscription1());
		
	}
	
	@Autowired
	IOEventProvider ioEventProvider;
	
	@Test
	public void userAddsSubscription() {
		
		// add subscription to database
		addUserSubscriptionToDB();
		
		// observe IO on database stream
		ioEventProvider.getIOEventsBetween(LocalDateTime.now().minusSeconds(20), null);
		
		// observe inclusion of state to sampler config
		
		// observe state for indicators
		
		// observe state for emails
		
		// observe historical value retrieval
		
		// observe initial outputs
		
	}
	
}
