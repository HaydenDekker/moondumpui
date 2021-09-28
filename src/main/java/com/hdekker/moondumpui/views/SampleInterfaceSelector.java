package com.hdekker.moondumpui.views;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.mobileapp.templates.CardList;
import com.hdekker.mobileapp.templates.SinglePageCard;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.dyndb.types.SampleInterfaceConfiguration;
import com.hdekker.moondumpui.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

/**
 * Displays list of samples for user to
 * select.
 * 
 * @author HDekker
 *
 */

@Route("sample-interface-selector")
public class SampleInterfaceSelector extends BaseDynamoDBSinglePageCard{

	Grid<String> interfaceNames;
	
	public SampleInterfaceSelector(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		interfaceNames = new Grid<>();
		interfaceNames.addColumn(String::toString).setHeader("Sample Source");
		interfaceNames.addItemClickListener((event)->{
			
			state.setInterfaceName(Optional.of(event.getItem()));
			UI.getCurrent().navigate(SampleAssetSelector.class);

		});
		
		add(interfaceNames);
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		CompletableFuture<QueryResponse> res = client.query(builder->{
			
			builder.tableName(dbc.getTableName());
			builder.expressionAttributeValues(Map.of(":pk", AttributeValue.builder()
															.s(DynDBKeysAndAttributeNamesSpec.SAMPLER_INTERFACE_KEYS)
															.build()));
			builder.projectionExpression(dbc.getSortKey());
			builder.keyConditionExpression(dbc.getPrimaryKey() + " = :pk");
			
		});
		
		res.thenAccept(resp->{ 
			
			List<String> interfaceNamesList = resp.items()
				.stream()
				.map(m-> m.get("SK").s())
				.collect(Collectors.toList());
				
			interfaceNames.getUI().get().access(()->{
				
				interfaceNames.setItems(interfaceNamesList);
				interfaceNames.getUI().get().push();
				
			});
			
		});
		
	}
	
}
