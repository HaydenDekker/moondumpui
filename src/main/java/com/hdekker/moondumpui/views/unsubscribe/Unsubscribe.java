package com.hdekker.moondumpui.views.unsubscribe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.atmosphere.interceptor.AtmosphereResourceStateRecovery.B;
import org.slf4j.LoggerFactory;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.dyndb.Marshalling;
import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.hdekker.moondumpui.views.BaseDynamoDBSinglePageCard;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Route("unsubscribe/:sha")
public class Unsubscribe extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver{

	String sha;
	
	Grid<UserSubscriptionSpec> uss = new Grid<>();
	
	private List<UserSubscriptionSpec> userSubscriptions;

	public List<UserSubscriptionSpec> getUserSubscriptions() {
		return userSubscriptions;
	}

	public void deleteSubscription(UserSubscriptionSpec uss) {
		
		client.deleteItem(b->{
			
			b.tableName(dbc.getTableName());
			b.key(Map.of(
				dbc.getPrimaryKey(),
				AttributeValue.builder()
					.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
					.build(),
				dbc.getSortKey(),
				AttributeValue.builder()
					.s(Marshalling.createSortKey().apply(uss))
					.build()
					
			));
			
		});
		
	}
	
	public void deleteEmailSHA(String sha) {
		
		client.deleteItem(b->{
			
			b.tableName(dbc.getTableName());
			b.key(Map.of(
				dbc.getPrimaryKey(),
				AttributeValue.builder()
					.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_UNSUBSCRIBE)
					.build(),
				dbc.getSortKey(),
				AttributeValue.builder()
					.s(sha)
					.build()
					
			));
			
		});
	}

	public Unsubscribe(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		uss.addColumn(UserSubscriptionSpec::getAssetName).setHeader("Asset");
		uss.addColumn(UserSubscriptionSpec::getIndicatorName).setHeader("Indicator Name");
		uss.addColumn(UserSubscriptionSpec::getSampleRates).setHeader("Sample Rates");
		
		uss.addColumn(new ComponentRenderer<>(Button::new, (b, v)->{
			
			b.setIcon(new Icon(VaadinIcon.CLOSE_CIRCLE_O));
			b.addClickListener((e)-> {
				deleteSubscription(v);
				userSubscriptions.remove(v);
				uss.setItems(userSubscriptions);
				if(userSubscriptions.size()==0) {
					deleteEmailSHA(sha);
					// TODO navigate somewhere
				}
			});
			
		})).setHeader("delete");
		
		add(uss);
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		// get email
		client.getItem(b->{
			
			LoggerFactory.getLogger(Unsubscribe.class)
				.debug("Unsubscribe called for " + sha);
			
			b.tableName(dbc.getTableName());
			b.key(Map.of(
					dbc.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_UNSUBSCRIBE)
						.build(),
					dbc.getSortKey(),
					AttributeValue.builder()
						.s(sha)
						.build()
					
			));
		})
		
		// get subscriptions
		.thenComposeAsync(resp->{
			
			String email = resp.item().get("email").s();
			
			LoggerFactory.getLogger(Unsubscribe.class)
				.debug(sha + " is associated with " + email);
			
			return client.query(builder->{
				builder.tableName(dbc.getTableName());
				builder.expressionAttributeValues(Map.of(
					":pk",
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
						.build(),
						":skval",
					AttributeValue.builder()
						.s(email)
						.build()
					
					));
				builder.keyConditionExpression(dbc.getPrimaryKey() + " = :pk and begins_with(" + dbc.getSortKey() + ", :skval)");
			});
			
		})
		.thenAccept(resp->{
			
			uss.getUI()
			.get()
			.access(()->{
				
				List<UserSubscriptionSpec> usss = resp.items()
					.stream()
					.map(item->Marshalling.databaseMapToUserSubscriptionSpec.apply(item))
					.collect(Collectors.toList());
				
				LoggerFactory.getLogger(Unsubscribe.class)
					.debug(" and has " + usss.size() + " subscriptions associated with it.");
				
				// for observations
				userSubscriptions = usss;
				
				uss.setItems(usss);
				uss.getUI()
					.get()
					.push();
				
			});
			
		});;
		
		
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		sha = event.getRouteParameters().get("sha").get();
		
	}

}
