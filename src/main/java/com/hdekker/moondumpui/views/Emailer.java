package com.hdekker.moondumpui.views;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@Route("emailer")
public class Emailer extends BaseDynamoDBSinglePageCard{
	
	public Emailer(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		EmailField emailField = new EmailField("Enter email");
		add(emailField);
		Button submit = new Button("Subscribe to Indicator");
		add(submit);
		
		submit.addClickListener((e)->{
			
			state.setEmail(Optional.of(emailField.getValue()));
			
			state.setIndicatorApplicationOrder(
					Optional.of(
						List.of(state.getTransformName().get(),
						state.getAlertName().get()))
			);
			
			CompletableFuture<PutItemResponse> resp = client.putItem(builder->{
				
				ObjectMapper om = new ObjectMapper();
				
				String propStr = "";
				try {
					propStr = om.writeValueAsString(state.getProperties().get());
				} catch (JsonProcessingException er) {
					er.printStackTrace();
				}
				
				builder.tableName(dbc.getTableName());
				builder.item(Map.of(
					dbc.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
						.build(),
					dbc.getSortKey(),
					AttributeValue.builder()
						.s(state.getEmail().get() + "-" + state.getIndicatorName().get())
						.build(),
					"interfaceName",
					AttributeValue.builder()
						.s(state.getIndicatorName().get())
						.build(),
					"assetName",
					AttributeValue.builder()
						.s(state.getAssetName().get())
						.build(),
					"indicatorConfig",
					AttributeValue.builder()
						.s(propStr)
						.build(),
					"indicatorOrder",
					AttributeValue.builder()
						.l(
							state.getIndicatorApplicationOrder()
								.get()
								.stream()
								.map(ind->AttributeValue.builder()
										.s(ind)
										.build())
							.collect(Collectors.toList()))
						.build(),	
					"indicatorName",
					AttributeValue.builder()
						.s(state.getIndicatorName().get())
						.build(),
				    "email",
						AttributeValue.builder()
							.s(state.getEmail().get())
							.build(),
					"sampleRates",
					AttributeValue.builder()
						.ns(state.getAssetSampleRateMinutes()
								.get()
								.stream()
								.map(n->n.toString())
								.collect(Collectors.toList()))
						.build()
					
				));
	
			});
			
			UI.getCurrent().navigate(EmailerAuthSent.class);
			
		});
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		
	}

}
