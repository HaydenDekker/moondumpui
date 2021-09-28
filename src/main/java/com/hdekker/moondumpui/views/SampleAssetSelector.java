package com.hdekker.moondumpui.views;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

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
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@Route("sample-asset-selector")
public class SampleAssetSelector extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver{

	Grid<String> assetList;
	
	public SampleAssetSelector(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		assetList = new Grid<String>();
		assetList.addColumn(String::toString).setHeader("Select Indicated Asset");
		
		assetList.addItemClickListener((e)->{
			state.setAssetName(Optional.of(e.getItem()));
			UI.getCurrent().navigate(ApplyIndicator.class);
		});
		
		add(assetList);
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		CompletableFuture<GetItemResponse> item = client.getItem(builder->{
			
			builder.tableName(dbc.getTableName());
			builder.key(Map.of(
					dbc.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.SAMPLER_INTERFACE_KEYS)
						.build(),
					dbc.getSortKey(),
					AttributeValue.builder()
						.s(state.getInterfaceName().get())
						.build())
				);
			
		});
		
		item.thenAccept(resp->{
			
			List<String> assetsForIF = resp.item()
											.get("list")
											.ss();
			assetList.getUI().get().access(()->{
				
				assetList.setItems(assetsForIF);
				assetList.getUI().get().push();
			});
			
		});
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		state.getInterfaceName().ifPresentOrElse((i)->{}, ()-> event.forwardTo(SampleInterfaceSelector.class));

	}

}
