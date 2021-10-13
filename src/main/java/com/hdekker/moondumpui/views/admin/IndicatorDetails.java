package com.hdekker.moondumpui.views.admin;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.hdekker.moondumpui.views.BaseDynamoDBSinglePageCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.BatchGetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.KeysAndAttributes;


@Route("indicator-details")
public class IndicatorDetails extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver{

	@Autowired
	StateAdminWorkflow stateAdminWorkflow;
	
	VerticalLayout holder = new VerticalLayout();
	
	public IndicatorDetails(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		add(holder);
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		stateAdminWorkflow.getUss()
			.ifPresentOrElse(e->{}, ()-> event.forwardTo(AllSubscribers.class));
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		UserSubscriptionSpec uss = stateAdminWorkflow.getUss().get();
		
		holder.add(getItem("asset", uss.getAssetName()));
		holder.add(getItem("interface", uss.getInterfaceName()));
		holder.add(getItem("indicator name", uss.getIndicatorName()));
		holder.add(getItem("email", uss.getEmail()));
		holder.add(getItem("sample rates", uss.getSampleRates().toString()));
		holder.add(getItem("indicator structure", uss.getIndicatorOrder().toString()));
		holder.add(getItem("indicator config", uss.getIndicatorConfig().toString()));
		
		// sample rate will require unique keys
		List<String> itemKeys = uss.getSampleRates()
										.stream()
										.map(d-> Duration.ofMinutes(d.longValue()).toString())
										.map(dur-> uss.getAssetName() + "-" + dur + "-" + uss.getIndicatorName())
										.collect(Collectors.toList());
		
		List<Map<String, AttributeValue>> keys = itemKeys.stream()
			.map(k->
				Map.of(
						"PK", 
						AttributeValue
							.builder()
							.s("IAttr")
							.build(),
						"SK", 
						AttributeValue
							.builder()
							.s(k)
							.build()
						)
	
			).collect(Collectors.toList());
		
		CompletableFuture<BatchGetItemResponse> res = client.batchGetItem(builder->{
			
			builder.requestItems(Map.of(dbc.getTableName(),
						KeysAndAttributes.builder()
							.keys(keys)
							.build()
			));
			
		});
		
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		
		// recieve db state
		res.thenAccept(c->{
			
			holder.getUI().get()
				.access(()->{
					
					c.responses().get(dbc.getTableName())
					.stream()
					.forEach(item->{
						
						holder.add(getItem("state", item.get("indResultState").s()));
						
						LocalDateTime val = null;
						try {
							val = om.readValue(item.get("inputSampleDate").s(), LocalDateTime.class);
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						holder.add(getItem("last state update", val.format(DateTimeFormatter.ofPattern("dd MMM uuuu"))));
			
					});
					holder.getUI().get().push();
					
				});
			
		});
				
	}
	
	VerticalLayout getItem(String label, String value) {
		
		Label item = new Label(label);
		H4 val = new H4(value);
		VerticalLayout h = new VerticalLayout(item, val);
		h.setAlignItems(Alignment.BASELINE);
		h.setMargin(false);
		val.getStyle().set("margin-top", "0");
		
		return h;
		
	}

}
