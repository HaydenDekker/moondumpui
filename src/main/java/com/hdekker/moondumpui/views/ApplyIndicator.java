package com.hdekker.moondumpui.views;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Route("apply-indicator")
public class ApplyIndicator extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver {

	Grid<Map<String, AttributeValue>> selectIndicator;
	
	HorizontalLayout h = new HorizontalLayout();
	
	public ApplyIndicator(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		// style
		VerticalLayout w = new VerticalLayout();
		h = new HorizontalLayout();
		h.setAlignItems(Alignment.BASELINE);
		w.add(h);
		
		selectIndicator = new Grid<>();
		selectIndicator.addColumn(m->m.get(DynDBKeysAndAttributeNamesSpec.INDICATOR_DESCRIPTOR_FNNAME).s()).setHeader(w);
		selectIndicator.addItemClickListener((e)->{
			
			state.setTransformName(Optional.of(e.getItem().get(dbc.getSortKey()).s()));
			UI.getCurrent().navigate(SelectIndicatorProperties.class);
			
		});
		
		
		add(selectIndicator);
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		h.add(new Text("Apply an Indicator on " + state.getAssetName().get()));
		
		CompletableFuture<QueryResponse> itemFuture = client.query(builder->{
			
			builder.tableName(dbc.getTableName());
			builder.expressionAttributeValues(Map.of(":pk", AttributeValue.builder()
					.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_DISCRIPTOR)
					.build(),
					":skval",
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_DISCRIPTOR_TRANSFORMS)
						.build()
					));
			builder.projectionExpression(
					dbc.getSortKey() + ", " +
					DynDBKeysAndAttributeNamesSpec.INDICATOR_DESCRIPTOR_FNNAME);
			builder.keyConditionExpression(dbc.getPrimaryKey() + " = :pk and begins_with(" + dbc.getSortKey() + ", :skval)");
			
		});
		
		itemFuture.thenAccept(list->{
			
			selectIndicator.getUI().get()
				.access(()->{
					
					selectIndicator.setItems(list.items());	
					selectIndicator.getUI().get().push();
					
				});
			
			
		});
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getAssetName()
			.ifPresentOrElse((e)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));
		
	}

}
