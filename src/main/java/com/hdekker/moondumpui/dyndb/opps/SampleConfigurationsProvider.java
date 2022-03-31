package com.hdekker.moondumpui.dyndb.opps;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.DyndbIndex;
import com.hdekker.moondumpui.sample.SampleConfiguration;

import reactor.core.publisher.Mono;

@Component
public class SampleConfigurationsProvider extends DatabaseOperation{

	Mono<List<SampleConfiguration>> getSampleConfigurations(){
		return Mono.fromCompletionStage(
					client.scan(b->{
			
						b.tableName(this.databaseConfig.getTableName());
						b.indexName(DyndbIndex.SampleConfig.toString());
					
					}))
				.map(resp->{
					
					return resp.items()
							.stream()
							.map(item->{
								
								String assetName = item.get("assetName")
											.s();
								String interfaceName = item.get("interfaceName")
										.s();
								Set<Duration> durations = item.get("sampleRates")
										.ns()
										.stream()
										.map(s->Duration.ofSeconds(Long.valueOf(s)))
										.collect(Collectors.toSet()
										);
								return new SampleConfiguration(assetName, durations, interfaceName);
							})
							.collect(Collectors.toList());
					
				});
	}
	
}
