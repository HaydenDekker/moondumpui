package com.hdekker.moondumpui.dyndb.opps.marshalers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hdekker.moondumpui.event.process.CoreProcesses;
import com.hdekker.moondumpui.event.process.ProcessEvent;
import com.hdekker.moondumpui.event.process.ProcessState;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
public class ItemToProcessEventMarshaler extends DatabaseMarshaler{

	public ProcessEvent convert(Map<String, AttributeValue> item){
		
		String processEnumString = item.get(databaseConfig.getPrimaryKey())
				.s();
		CoreProcesses processEnum = CoreProcesses.valueOf(processEnumString);
		
		String processName = item.get("name")
					.s();
		
		Optional.ofNullable(processEnum)
		.filter(pe->pe.name().equals(processName))
		.orElseThrow(()->new Error("Process Key must be associated with name attribute."));
		
		String message = item.get("message").s();
		String dateTime = item.get("date-time").s();
		LocalDateTime ldt = null;
		try {
		ldt = om.readValue(dateTime, LocalDateTime.class);
		} catch (JsonProcessingException e) {
		throw new Error("Error - Database inconsistancy - Process event attribute date-time must always be marshalable to LocalDateTime");
		}
		String state = item.get("state").s();
		ProcessState ps = ProcessState.valueOf(state);
		ProcessEvent pe = new ProcessEvent(processEnum, ldt, message, ps);
		return pe;
		
	}
	
}
