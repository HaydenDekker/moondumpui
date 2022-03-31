package com.hdekker.moondumpui.dyndb.opps.marshalers;

import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdekker.moondumpui.event.process.ProcessEvent;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
public class ProcessEventToItemMarshaller extends DatabaseMarshaler{

	public Map<String, AttributeValue> convert(ProcessEvent event){
		
		String eventDateTime = "";
		
		try {
			eventDateTime = om.writeValueAsString(event.getEventTime());
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return Map.of(
				databaseConfig.getPrimaryKey(),
				AttributeValue.builder()
					.s(event.getProcessName().toString())
					.build(),
				databaseConfig.getSortKey(),
				AttributeValue.builder()
					.s("" + event.getEventTime().toEpochSecond(ZoneOffset.MIN))
					.build(),
					"name",
					AttributeValue.builder()
						.s(event.getProcessName().name())
						.build(),
					"date-time",
					AttributeValue.builder()
						.s(eventDateTime)
						.build(),
					"message",
					AttributeValue.builder()
						.s(event.getEventDescription())
						.build(),
					"state",
					AttributeValue.builder()
						.s(event.getState().toString())
						.build()
		);
		
	}
	
}
