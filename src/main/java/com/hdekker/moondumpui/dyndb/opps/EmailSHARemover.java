package com.hdekker.moondumpui.dyndb.opps;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.hdekker.moondumpui.dyndb.PrimaryKeySpec;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
public class EmailSHARemover extends DatabaseOperation {

	public void deleteEmailSHA(String email) {
		
		client.query(b->{
			b.tableName(databaseConfig.getTableName());
			b.indexName("email-index");
			b.expressionAttributeValues(Map.of(
					":" + databaseConfig.getPrimaryKey(),
					AttributeValue.builder()
						.s(PrimaryKeySpec.INDICATOR_UNSUBSCRIBE
								.getPrimaryKeyValue())
						.build(),
					":email",
					AttributeValue.builder()
						.s(email)
						.build())
			);
			b.keyConditionExpression(databaseConfig.getPrimaryKey() + "= :" + databaseConfig.getPrimaryKey() 
									+ " AND " + "email = :email");
			
		}).thenAcceptAsync(c->{
			
			client.deleteItem(b->{
				
				b.tableName(databaseConfig.getTableName());
				Map<String, AttributeValue> item = c.items().get(0);
				b.key(Map.of(
						databaseConfig.getPrimaryKey(),
						item.get(databaseConfig.getPrimaryKey()),
						databaseConfig.getSortKey(),
						item.get(databaseConfig.getSortKey())
						
						
				));
				
			});
			
		});
		
	}
	
}
