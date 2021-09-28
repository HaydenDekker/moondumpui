package com.hdekker.moondumpui.views;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.state.SessionState;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

@Route("emailer-auth-sent")
public class EmailerAuthSent extends BaseDynamoDBSinglePageCard{

	public EmailerAuthSent(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		add(new Label("Check email"));
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		// TODO Auto-generated method stub
		
	}

}
