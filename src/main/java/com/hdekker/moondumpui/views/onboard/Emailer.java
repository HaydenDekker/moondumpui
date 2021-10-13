package com.hdekker.moondumpui.views.onboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.state.SessionState;
import com.hdekker.moondumpui.views.BaseDynamoDBSinglePageCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.ses.SesAsyncClient;

@Route("emailer")
public class Emailer extends BaseDynamoDBSinglePageCard{
	
	SesAsyncClient emailer;
	
	public Emailer(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		emailer = SesAsyncClient.builder()
		.region(Region.of(dbc.getEmailRegion()))
		.build();
		
		add(new H2("Subscribe to Indicator Alert"));
		EmailField emailField = new EmailField("Enter email");
		add(emailField);
		Button submit = new Button("Subscribe to Indicator");
		add(submit);
		
		submit.addClickListener((e)->{
			
			if(emailField.isInvalid()) {
				emailField.setErrorMessage("requires a valid email, ay.");
				emailField.setInvalid(true);
				return;
			}
			
			state.setEmail(Optional.of(emailField.getValue()));
			
			state.setIndicatorApplicationOrder(
					Optional.of(
						List.of(state.getTransformName().get(),
						state.getAlertName().get()))
			);
			
			UUID uuid = UUID.randomUUID();
			
			CompletableFuture<PutItemResponse> resp = client.putItem(builder->{
				
				ObjectMapper om = new ObjectMapper();
				
				String propStr = "";
				try {
					propStr = om.writeValueAsString(state.getProperties().get());
				} catch (JsonProcessingException er) {
					er.printStackTrace();
				}
				
				builder.tableName(dbc.getTableName());
				builder.item(Map.of(
					dbc.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION_TEMP)
						.build(),
					dbc.getSortKey(),
					AttributeValue.builder()
						.s(uuid.toString())
						.build(),
					"interfaceName",
					AttributeValue.builder()
						.s(state.getInterfaceName().get())
						.build(),
					"assetName",
					AttributeValue.builder()
						.s(state.getAssetName().get())
						.build(),
					"indicatorConfig",
					AttributeValue.builder()
						.s(propStr)
						.build(),
					"indicatorOrder",
					AttributeValue.builder()
						.l(
							state.getIndicatorApplicationOrder()
								.get()
								.stream()
								.map(ind->AttributeValue.builder()
										.s(ind)
										.build())
							.collect(Collectors.toList()))
						.build(),	
					"indicatorName",
					AttributeValue.builder()
						.s(state.getIndicatorName().get())
						.build(),
				    "email",
						AttributeValue.builder()
							.s(state.getEmail().get())
							.build(),
					"sampleRates",
					AttributeValue.builder()
						.ns(state.getAssetSampleRateMinutes()
								.get()
								.stream()
								.map(n->n.toString())
								.collect(Collectors.toList()))
						.build()
					
				));
	
			});
			
			addUnsublinkForEmail(state.getEmail().get());
			
			emailer.sendEmail(b->{
				
				b.destination(d->{
					d.toAddresses(state.getEmail().get());
				});
				b.source(dbc.appAdminEmail);
				b.message(m->{
					m.subject(s->{
						s.data("Please Confirm Subscription.");
					});
					m.body(bod->{
						bod.html(html->{
							html.data("<a href=\""+ dbc.getSubscriptionConfmEndpoint() + "/confirm/" + uuid.toString() +"\">click to confirm subscription.</a>");
						});
					});
					
				});
				
				
			});
			
			
			UI.getCurrent().navigate(EmailerAuthSent.class);
			
		});
		
	}
	
	public void addUnsublinkForEmail(String email) {
		
		String sha = DigestUtils.sha256Hex(dbc.getAppAdminEmail());
		
		client.putItem(p->{
			
			p.tableName(dbc.getTableName());
			p.item(Map.of(
					dbc.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_UNSUBSCRIBE)
						.build(),
					dbc.getSortKey(),
					AttributeValue.builder()
						.s(sha)
						.build(),
					"email",
					AttributeValue.builder()
						.s(email)
						.build()
					
			));
		});
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		
	}

}