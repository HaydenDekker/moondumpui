package com.hdekker.moondumpui.views;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@Route("select-alert-properties")
public class SelectAlertProperties extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver {

	VerticalLayout holder = new VerticalLayout();
	
	public SelectAlertProperties(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		add(new H3("Configure Alert Properties"));
		add(holder);
		holder.getStyle().set("margin", "0");
		holder.setPadding(false);
		holder.setSpacing(false);
		
		Button nextButton = new Button("Next");
		
		nextButton.addClickListener((e)->{
			
			Map<String, Double> props = numberFields.
				stream()
				.collect(Collectors.toMap(
					nf->nf.getLabel(), nf->nf.getValue()));
				
			state.addProperties(state.getAlertName().get(), props);
			UI.getCurrent().navigate(SampleRates.class);

		});
		
		add(nextButton);
		
	}
		
	List<NumberField> numberFields;

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getAlertName()
		.ifPresentOrElse((e)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		CompletableFuture<GetItemResponse> itemF = client.getItem(builder->{
			
			builder.tableName(dbc.getTableName());
			builder.key(Map.of(
					dbc.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_DISCRIPTOR)
						.build(),
					dbc.getSortKey(),
					AttributeValue.builder()
						.s(state.getAlertName().get())
						.build())
				);
			builder.projectionExpression(DynDBKeysAndAttributeNamesSpec.INDICATOR_CONFIGURABLE_PROPERTIES_AND_DEFAULTS);
			
		});
		
		itemF.thenAccept(item->{
			
			numberFields = item.item()
				.get(DynDBKeysAndAttributeNamesSpec.INDICATOR_CONFIGURABLE_PROPERTIES_AND_DEFAULTS)
				.m()
				.entrySet()
				.stream()
				.map(prop-> {
					
					NumberField nf = new NumberField(prop.getKey());
					nf.setValue(Double.valueOf(prop.getValue().n()));
					return nf;
					
				})
				.collect(Collectors.toList());
			holder.getUI().get()
				.access(()->{
					
					holder.add(numberFields.toArray(new NumberField[numberFields.size()]));
					holder.getUI().get().push();
					
				});
			
		});
		
	}

}
