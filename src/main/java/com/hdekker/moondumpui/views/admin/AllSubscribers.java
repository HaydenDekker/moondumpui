package com.hdekker.moondumpui.views.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.hdekker.moondumpui.views.BaseDynamoDBSinglePageCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Route("all-subscribers")
public class AllSubscribers extends BaseDynamoDBSinglePageCard{

	public class UserSubscriptions{
		
		final String email;
		final List<String> keys;
		public UserSubscriptions(String email, List<String> keys) {
			super();
			this.email = email;
			this.keys = keys;
		}
		public String getEmail() {
			return email;
		}
		public List<String> getKeys() {
			return keys;
		}
		
	}
	
	Grid<UserSubscriptions> us = new Grid<UserSubscriptions>();
	
	@Autowired
	DatabaseConfig dbc;
	
	@Autowired
	SessionState state;
	
	@Autowired
	StateAdminWorkflow stateAdminWorkflow;
	
	public AllSubscribers(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		this.dbc = dbc;
		this.state = state;
		
		us.addColumn(UserSubscriptions::getEmail).setHeader("Email");
		us.addColumn(us->us.getKeys().size()).setHeader("Number of Subscriptions");
		add(us);
		
		us.addItemClickListener(e->{
			
			stateAdminWorkflow.setSortKeyForIndicators(Optional.of(e.getItem().getEmail()));
			UI.getCurrent().navigate(SubscriptionDetails.class);
			
		});
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		CompletableFuture<QueryResponse> subscriptionKeys = client.query(builder->{
			
			builder.tableName(dbc.getTableName());
			builder.expressionAttributeValues(Map.of(":pk", AttributeValue.builder()
					.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
					.build()
					));
			builder.projectionExpression(
					dbc.getSortKey());
			builder.keyConditionExpression(dbc.getPrimaryKey() + " = :pk");
	
			
		});
		
		subscriptionKeys.thenAccept(keys->{
			
			List<UserSubscriptions> userSubscriptions = keys.items()
				.stream()
				.map(k->k.get(dbc.getSortKey())
							.s())
				.collect(Collectors.toMap(s-> s.split("-")[0], 
						  v->List.of(v),
						  (p,n)->{
							  List<String> ln = new ArrayList<>(p);
							  ln.addAll(n);
							  return ln;
						  }))
				.entrySet()
				.stream()
				.map(es-> new UserSubscriptions(es.getKey(), es.getValue()))
				.collect(Collectors.toList());
			
			us.getUI().get().access(()->{
				
				us.setItems(userSubscriptions);
				us.getUI().get().push();
				
				
			});
			
		});
		
	}

}
