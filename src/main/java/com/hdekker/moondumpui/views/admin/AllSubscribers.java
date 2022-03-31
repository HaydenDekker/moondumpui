package com.hdekker.moondumpui.views.admin;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.UserSubscriptions;
import com.hdekker.moondumpui.dyndb.opps.IndicatorSubscriptionsSortKeysSupplier;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

@Route("all-subscribers")
public class AllSubscribers extends AppBaseSinglePageCard{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5747965835841827246L;
	
	Grid<UserSubscriptions> us = new Grid<UserSubscriptions>();
	
	@Autowired
	SessionState state;
	
	@Autowired
	StateAdminWorkflow stateAdminWorkflow;
	
	@Autowired
	IndicatorSubscriptionsSortKeysSupplier uss;
	
	public AllSubscribers() {
		super();
		
		us.addColumn(UserSubscriptions::getEmail).setHeader("Email");
		us.addColumn(us->us.getKeys().size()).setHeader("Number of Subscriptions");
		add(us);
		
		us.addItemClickListener(e->{
			
			stateAdminWorkflow.setSortKeyForIndicators(Optional.of(e.getItem().getEmail()));
			UI.getCurrent().navigate(SubscriptionDetails.class);
			
		});
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		
		uss.getUserSubscriptionsByEmail()
			.subscribe(userSubscriptions->{
				
			us.getUI().get().access(()->{
				
				us.setItems(userSubscriptions);
				us.getUI().get().push();

			});
				
		});
		
	}

}
