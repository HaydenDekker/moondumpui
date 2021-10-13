package com.hdekker.moondumpui.views.admin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.dyndb.Marshalling;
import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.hdekker.moondumpui.views.BaseDynamoDBSinglePageCard;
import com.vaadin.flow.component.UI;
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
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Route("subscription-details")
public class SubscriptionDetails extends BaseDynamoDBSinglePageCard implements BeforeEnterObserver{

	@Autowired
	StateAdminWorkflow stateAdminWorkflow;
	
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
	
	public void deleteEmailSHA(String email) {
		
		client.query(b->{
			b.tableName(dbc.getTableName());
			b.indexName("email-index");
			b.expressionAttributeValues(Map.of(
					":" + dbc.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_UNSUBSCRIBE)
						.build(),
					":email",
					AttributeValue.builder()
						.s(email)
						.build())
			);
			b.keyConditionExpression(dbc.getPrimaryKey() + "= :" + dbc.getPrimaryKey() 
									+ " AND " + "email = :email");
			
		}).thenAcceptAsync(c->{
			
			client.deleteItem(b->{
				
				b.tableName(dbc.getTableName());
				Map<String, AttributeValue> item = c.items().get(0);
				b.key(Map.of(
						dbc.getPrimaryKey(),
						item.get(dbc.getPrimaryKey()),
						dbc.getSortKey(),
						item.get(dbc.getSortKey())
						
						
				));
				
			});
			
		});
		
	}
	
	public SubscriptionDetails(DatabaseConfig dbc, SessionState state) {
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
					deleteEmailSHA(v.getEmail());
					// TODO navigate somewhere
				}
			});
			
		})).setHeader("delete");
		
		uss.addItemClickListener(e->{
			
			stateAdminWorkflow.setUss(Optional.of(e.getItem()));
			UI.getCurrent().navigate(IndicatorDetails.class);
			
		});
		
		add(uss);
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		client.query(builder->{
			
			builder.tableName(dbc.getTableName());
			builder.expressionAttributeValues(Map.of(
					":pk",
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
						.build(),
						":skval",
					AttributeValue.builder()
						.s(stateAdminWorkflow.getSortKeyForIndicators().get())
						.build()
					
			));
			builder.keyConditionExpression(dbc.getPrimaryKey() + " = :pk and begins_with(" + dbc.getSortKey() + ", :skval)");
			
		}).thenAccept(resp->{
			
			uss.getUI()
			.get()
			.access(()->{
				
				List<UserSubscriptionSpec> usss = resp.items()
						.stream()
						.map(item->Marshalling.databaseMapToUserSubscriptionSpec.apply(item))
						.collect(Collectors.toList());
				
				userSubscriptions = usss;
				
				uss.setItems(usss);
				uss.getUI()
					.get()
					.push();
				
			});
			
		});
		
	}
	

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		stateAdminWorkflow.getSortKeyForIndicators()
			.ifPresentOrElse((e)->{}, ()-> event.forwardTo(AllSubscribers.class));
		
	}

	
	
}
