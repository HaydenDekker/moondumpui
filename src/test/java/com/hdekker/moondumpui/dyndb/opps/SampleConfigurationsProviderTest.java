package com.hdekker.moondumpui.dyndb.opps;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hdekker.moondumpui.sample.SampleConfiguration;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import reactor.core.publisher.Mono;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
public class SampleConfigurationsProviderTest {

	@Autowired
	SampleConfigurationsProvider sampleConfigurationsProvider;
	
	@Autowired
	IndicatorSubscriptionAdder isa;
	
	@Test
	public void canGetConfigUsingGSI() {
		
		IndicatorSubscription is = new IndicatorSubscription("Unit test for GSI", 
				"hayden@hotmail.com", 
				Map.of("param1", Map.of("yippe", 2.0)), 
				"Test Ind" + LocalDateTime.now().toEpochSecond(ZoneOffset.MIN), 
				List.of("yes"),
				"if1", 
				List.of(3.0));
		
		isa.addUserSubscription(is)
			.block();

		List<SampleConfiguration> s = sampleConfigurationsProvider.getSampleConfigurations()
				.block();
		List<SampleConfiguration> testAssets = s.stream()
			.filter(sc->sc.getAssetId().equals("Unit test for GSI"))
			.collect(Collectors.toList());
		
		assertThat(testAssets.size(), greaterThan(1));
		
	}
	
}
