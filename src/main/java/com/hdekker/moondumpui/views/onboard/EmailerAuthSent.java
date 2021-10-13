package com.hdekker.moondumpui.views.onboard;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.state.SessionState;
import com.hdekker.moondumpui.views.BaseDynamoDBSinglePageCard;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

@Route("emailer-auth-sent")
public class EmailerAuthSent extends BaseDynamoDBSinglePageCard{

	public EmailerAuthSent(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		add(new Label("Thankyou, Please check your email to finish your subscription. "
				+ "Once your email is confirmed you will be subscribed to your alert. "
				+ "It may take a few minutes for the subscription email to arrive."));
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		// TODO Auto-generated method stub
		
	}

}
