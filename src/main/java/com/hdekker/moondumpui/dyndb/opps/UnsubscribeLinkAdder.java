package com.hdekker.moondumpui.dyndb.opps;

import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * Add a sha based on users email
 * to add some complexity to the unautohorised unsub view
 * 
 * TODO potentially add a new one for every email.
 * 
 * @author Hayden Dekker
 *
 */
@Component
public class UnsubscribeLinkAdder extends DatabaseOperation{

	public void add(String email) {
		
		String sha = DigestUtils.sha256Hex(email);
		
		client.putItem(p->{
			
			p.tableName(databaseConfig.getTableName());
			p.item(Map.of(
					databaseConfig.getPrimaryKey(),
					AttributeValue.builder()
						.s(PrimaryKeySpec.INDICATOR_UNSUBSCRIBE.getPrimaryKeyValue())
						.build(),
					databaseConfig.getSortKey(),
					AttributeValue.builder()
						.s(sha)
						.build(),
					"email",
					AttributeValue.builder()
						.s(email)
						.build()
					
			));
		});
		
	}
	
}
