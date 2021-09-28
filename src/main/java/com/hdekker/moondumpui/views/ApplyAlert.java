package com.hdekker.moondumpui.views;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Route("apply-alert")
public class ApplyAlert extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver{

	Grid<Map<String, AttributeValue>> selectAlert;
	
	public ApplyAlert(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		

		selectAlert = new Grid<>();
		selectAlert.addColumn(m->m.get(DynDBKeysAndAttributeNamesSpec.INDICATOR_DESCRIPTOR_FNNAME).s()).setHeader("Apply Alert");
		selectAlert.addItemClickListener((e)->{
			state.setAlertName(Optional.of(e.getItem().get(dbc.getSortKey()).s()));
			UI.getCurrent().navigate(SelectAlertProperties.class);
		});
		
		add(selectAlert);
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getProperties().ifPresentOrElse((e)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		CompletableFuture<QueryResponse> itemFuture = client.query(builder->{
			
			builder.tableName(dbc.getTableName());
			builder.expressionAttributeValues(Map.of(":pk", AttributeValue.builder()
					.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_DISCRIPTOR)
					.build(),
					":skval",
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_DISCRIPTOR_ALERTS)
						.build()
					));
			builder.projectionExpression(
					dbc.getSortKey() + ", " +
					DynDBKeysAndAttributeNamesSpec.INDICATOR_DESCRIPTOR_FNNAME);
			builder.keyConditionExpression(dbc.getPrimaryKey() + " = :pk and begins_with(" + dbc.getSortKey() + ", :skval)");
			
		});
		
		itemFuture.thenAccept(list->{
			
			selectAlert.getUI().get()
				.access(()->{
					
					selectAlert.setItems(list.items());	
					selectAlert.getUI().get().push();
					
				});
			
			
		});
		
	}

	
	
}
