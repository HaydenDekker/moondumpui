package com.hdekker.moondumpui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.codec.digest.DigestUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.github.mvysny.kaributesting.v10.LocatorJ;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.github.mvysny.kaributools.RouterUtilsKt;
import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.DynDBKeysAndAttributeNamesSpec;
import com.hdekker.moondumpui.dyndb.Marshalling;
import com.hdekker.moondumpui.dyndb.types.UserSubscriptionSpec;
import com.hdekker.moondumpui.views.onboard.SampleInterfaceSelector;
import com.hdekker.moondumpui.views.unsubscribe.Unsubscribe;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.QueryParameters;
import com.vaadin.flow.spring.SpringServlet;

import kotlin.jvm.functions.Function0;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.ses.SesAsyncClient;

@SpringBootTest
public class SubscriptionLinkTests {

	@Autowired
	DatabaseConfig db;
	
	static Routes routes;
	
	@BeforeAll
    public static void createRoutes() {
        routes = new Routes().autoDiscoverViews("com.hdekker.moondumpui");
    }

	@Autowired
	ApplicationContext ctx;
	
    @BeforeEach
    public void setupVaadin() {
    	final Function0<UI> uiFactory = UI::new;
        final SpringServlet servlet = new MockSpringServlet(routes, ctx, uiFactory);
        MockVaadin.setup(uiFactory, servlet);
    }
    
    @Test
    public void simpleUITest() {
    	RouterUtilsKt.navigateTo("sample-interface-selector");
        final SampleInterfaceSelector main =  (SampleInterfaceSelector) UI.getCurrent().getChildren().findFirst().get();
        assertThat(main.getChildren().count(), Matchers.equalTo(1L));
    }

	/**
	 *  DynamoDB to inspect correct function of UI link
	 *  components.
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public void subscriptionConfirmationLinkUpdatesDatabase() throws InterruptedException {
		
		DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
				.region(Region.of(db.getRegion()))
				.build();
	
		// add temp test subscription
		UserSubscriptionSpec uss = new UserSubscriptionSpec(
				"test-asset-sub-conf", 
				db.getAppAdminEmail(), 
				Map.of("ind", Map.of("prop1", 0.34)), 
				"testIndicatorSubscriptionLinkTests", 
				List.of("ind"), 
				"test-interface", List.of(34.00));
		
		Map<String, AttributeValue> attr = Marshalling.converter()
			.apply(uss);
		
		UUID uuid = UUID.randomUUID();
		
		HashMap<String, AttributeValue> withKeys = new HashMap<>(attr);
		withKeys.put(db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION_TEMP)
						.build());
		withKeys.put(db.getSortKey(),
					AttributeValue.builder()
						.s(uuid.toString())
						.build());
		
		CompletableFuture<PutItemResponse> tempSubscriptionAddFuture = client.putItem(b->{
			
			b.tableName(db.getTableName());
			b.item(withKeys);
			
		});
		
		assertThat(tempSubscriptionAddFuture.join().sdkHttpResponse().statusCode(), Matchers.equalTo(200));
	
		// TODO create system int test to ensure email can be sent
		// and the link can be manually clicked to confirm subscription
		// for an account.
		
		// call link - offline call to check view function
		UI.getCurrent().navigate("confirm/" + uuid, QueryParameters.empty());		
		
		Thread.sleep(6000);
		
		// inspect database to see that subscription has been updated.
		// temp should be removed.
		GetItemResponse tempGetResp = client.getItem(b->{
			b.tableName(db.getTableName());
			b.key(Map.of(db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION_TEMP)
						.build(),
					db.getSortKey(),
					AttributeValue.builder()
						.s(uuid.toString())
						.build()));
			
		}).join();
		assertThat(tempGetResp.hasItem(), Matchers.equalTo(false));	
		
		// actual should exist.
		GetItemResponse actualGetResp = client.getItem(b->{
			b.tableName(db.getTableName());
			b.key(Map.of(db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
						.build(),
					db.getSortKey(),
					AttributeValue.builder()
						.s(Marshalling.createSortKey().apply(uss))
						.build()));
			
		}).join();
		assertThat(actualGetResp.hasItem(), Matchers.equalTo(true));
		
		// clean up
		client.deleteItem(b->{
			
			b.tableName(db.getTableName());
			b.key(Map.of(db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
						.build(),
					db.getSortKey(),
					AttributeValue.builder()
						.s(Marshalling.createSortKey().apply(uss))
						.build()));
			
			
		}).join();
		
		client.deleteItem(b->{
			
			b.tableName(db.getTableName());
			b.key(Map.of(db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION_TEMP)
						.build(),
					db.getSortKey(),
					AttributeValue.builder()
						.s(uuid.toString())
						.build()));
			
			
		}).join();
		
		
	}
	
	/**
	 *  This test should not invoke and email
	 * @throws InterruptedException 
	 * 
	 */
	@Test
	public void itCanUnsubscribe() throws InterruptedException {
		
		// STUB data in DB for test
		
		// stores un-subscribe key to user
		DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
				.region(Region.of(db.getRegion()))
				.build();
	
		// add temp test subscription
		UserSubscriptionSpec uss = new UserSubscriptionSpec(
				"test-asset-unsubscribe", 
				"unsubunittest@testemail.com", 
				Map.of("ind", Map.of("prop1", 0.34)), 
				"testIndicatorSubscriptionLinkTests", 
				List.of("ind"), 
				"test-interface", List.of(34.00));
		
		Map<String, AttributeValue> attr = Marshalling.converter()
				.apply(uss);
		
		HashMap<String, AttributeValue> withKeys = new HashMap<>(attr);
		withKeys.put(db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
						.build());
		withKeys.put(db.getSortKey(),
					AttributeValue.builder()
						.s(Marshalling.createSortKey().apply(uss))
						.build());
		
		// add test subscription
		CompletableFuture<PutItemResponse> tempSubscriptionAddFuture = client.putItem(b->{
			
			b.tableName(db.getTableName());
			b.item(withKeys);
			
		});
		
		assertThat(tempSubscriptionAddFuture.join().sdkHttpResponse().statusCode(), Matchers.equalTo(200));
		
		// at this point UI flow should have added an unsubscribe key for email.
		String sha = DigestUtils.sha256Hex(db.getAppAdminEmail());
		
		client.putItem(p->{
			
			p.tableName(db.getTableName());
			p.item(Map.of(
					db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_UNSUBSCRIBE)
						.build(),
					db.getSortKey(),
					AttributeValue.builder()
						.s(sha)
						.build(),
					"email",
					AttributeValue.builder()
						.s("unsubunittest@testemail.com")
						.build()
					
			));
		}).join();
		
		// check that sha can be queried by email
		// so that the core service can create 
		// the subscription link when sending
		QueryResponse shaByEmailLookup = client.query(b->{
			b.tableName(db.getTableName());
			b.indexName("email-index");
			b.expressionAttributeValues(Map.of(
					":" + db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_UNSUBSCRIBE)
						.build(),
					":email",
					AttributeValue.builder()
						.s("unsubunittest@testemail.com")
						.build())
			);
			b.keyConditionExpression(db.getPrimaryKey() + "= :" + db.getPrimaryKey() 
									+ " AND " + "email = :email");
			
		}).join();
		
		assertThat("An LSI should allow access by email.", shaByEmailLookup.hasItems(), Matchers.equalTo(true));
		assertThat("Should only be a single key available for a given email.", shaByEmailLookup.items().size(), Matchers.equalTo(1));
		
		// calls view with an un-subscribe key
		UI.getCurrent().navigate("unsubscribe/" + shaByEmailLookup.items().get(0).get(db.getSortKey()).s());
		
		// need to allow view to access database
		Thread.sleep(4000);
		
		// get a subscription
		Unsubscribe unsubClass = LocatorJ._get(Unsubscribe.class);
		
		List<UserSubscriptionSpec> usss = unsubClass.getUserSubscriptions();
		assertThat("View should have only found a single subscription for unsubunittest@testemail.com.", usss.size(), Matchers.equalTo(1));
		
		// mocks user interaction - remove subscription
		unsubClass.deleteSubscription(usss.get(0));
		unsubClass.deleteEmailSHA(sha);
		
		Thread.sleep(2000); // let view do it thing.
		
		// checks
		GetItemResponse shaRef = client.getItem(b->{
			
			b.tableName(db.getTableName());
			b.key(Map.of(
					db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_UNSUBSCRIBE)
						.build(),
					db.getSortKey(),
					AttributeValue.builder()
						.s(sha)
						.build()
					
			));
		}).join();
		
		assertThat("As no more subscriptions exist, the sha reference should be removed.", shaRef.hasItem(), Matchers.equalTo(false));
		
		GetItemResponse subRef = client.getItem(b->{
			
			b.tableName(db.getTableName());
			b.key(Map.of(
					db.getPrimaryKey(),
					AttributeValue.builder()
						.s(DynDBKeysAndAttributeNamesSpec.INDICATOR_SUBSCRIPTION)
						.build(),
					db.getSortKey(),
					AttributeValue.builder()
						.s(Marshalling.createSortKey().apply(uss))
						.build()
					
			));
		}).join();
		
		assertThat("The subscription must be removed when unsubscribe is called.", subRef.hasItem(), Matchers.equalTo(false));
		
	}
	
}
