package com.hdekker.moondumpui.views.onboard;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.dyndb.Marshalling;
import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpec;
import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpecStoreSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.hdekker.moondumpui.views.BaseDynamoDBSinglePageCard;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

@Route("confirm/:uuid")
public class Confirm extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver{

	String uuid;
	
	Div holder = new Div();
	
	public Confirm(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		holder.add(new H2("confirming subscription..."));
		add(holder);
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		CompletableFuture<GetItemResponse> respOfCheckForPendingConfirmation = client.getItem(b->{
			b.tableName(dbc.getTableName());
			b.key(Map.of(
					dbc.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION_TEMP)
						.build(),
					dbc.getSortKey(),
					AttributeValue.builder()
						.s(uuid)
						.build()
			));
			
		});
		
		respOfCheckForPendingConfirmation.thenAccept(resp->{
			
			if(resp.hasItem()==false) return;
			
			UserSubscriptionSpec uss = Marshalling.databaseMapToUserSubscriptionSpec
				.apply(resp.item());
			
			UserSubscriptionSpecStoreSpec usss = new UserSubscriptionSpecStoreSpec(
						uss, 
						dbc.getPrimaryKey(), 
						dbc.getSortKey());

			client.putItem(b->{
				
				b.tableName(dbc.getTableName());
				b.item(Marshalling.convertToSubscriptionMap()
							.apply(usss));
				
			});
			
			client.deleteItem(b->{
				
				b.tableName(dbc.getTableName());
				b.key(Map.of(
						dbc.getPrimaryKey(),
						AttributeValue.builder()
							.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION_TEMP)
							.build(),
						dbc.getSortKey(),
						AttributeValue.builder()
							.s(uuid)
							.build()));
				
			});
			
			holder.getUI().get()
				.access(()->{
					
					holder.removeAll();
					holder.add(new H2("Success, your indicator is added and will message your email from time to time. Make sure you set to vibrate so you don't miss out."));
					holder.getUI().get().push();
					
			});
			
			
		});
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {

		uuid = event.getRouteParameters().get("uuid").get();
		
		
	}

}
