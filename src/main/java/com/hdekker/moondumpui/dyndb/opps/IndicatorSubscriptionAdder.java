package com.hdekker.moondumpui.dyndb.opps;

import org.springframework.stereotype.Service;

import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpecConverters;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;

import reactor.core.publisher.Mono;

@Service
public class IndicatorSubscriptionAdder extends DatabaseOperation{

	public Mono<IndicatorSubscription> addUserSubscription(IndicatorSubscription spec){
		
		  return Mono.fromCompletionStage(client
			.putItem(b->{	
				b.tableName(databaseConfig.getTableName());
				b.item(UserSubscriptionSpecConverters.convertToAttributeValues()
						.apply(databaseConfig.getPrimaryKey())
						.apply(databaseConfig.getSortKey())
						.apply(spec));
			}))
			.map(resp->{
			
				if(resp.sdkHttpResponse().statusCode()!=200) throw new Error("DB add subs failed.");
				return spec;
				
			});
			
	}
	
}
