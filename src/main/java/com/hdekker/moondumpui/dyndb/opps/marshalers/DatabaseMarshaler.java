package com.hdekker.moondumpui.dyndb.opps.marshalers;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;

public class DatabaseMarshaler {

	@Autowired
	DatabaseConfig databaseConfig;
	
	ObjectMapper om = new ObjectMapper();
	
	DatabaseMarshaler(){
		om.registerModule(new JavaTimeModule());
	}
	
}
