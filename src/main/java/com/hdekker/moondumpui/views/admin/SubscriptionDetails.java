package com.hdekker.moondumpui.views.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.opps.EmailSHARemover;
import com.hdekker.moondumpui.dyndb.opps.IndicatorSubscriptionRemover;
import com.hdekker.moondumpui.dyndb.opps.IndicatorSubscriptionsProvider;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
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

/**
 * Displays subscriptions by user.
 * 
 * @author Hayden Dekker
 *
 */
@Route("subscription-details")
public class SubscriptionDetails extends AppBaseSinglePageCard implements BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 285999421239778748L;

	@Autowired
	StateAdminWorkflow stateAdminWorkflow;
	
	Grid<IndicatorSubscription> uss = new Grid<>();
	
	private List<IndicatorSubscription> userSubscriptions;

	public List<IndicatorSubscription> getUserSubscriptions() {
		return userSubscriptions;
	}
	
	@Autowired
	IndicatorSubscriptionRemover usr;
	
	@Autowired
	EmailSHARemover esr;
	
	@Autowired
	IndicatorSubscriptionsProvider usp;
	
	public SubscriptionDetails() {
		super();
		
		uss.addColumn(IndicatorSubscription::getAssetName).setHeader("Asset");
		uss.addColumn(IndicatorSubscription::getIndicatorName).setHeader("Indicator Name");
		uss.addColumn(IndicatorSubscription::getSampleRates).setHeader("Sample Rates");
		
		uss.addColumn(new ComponentRenderer<>(Button::new, (b, v)->{
			
			b.setIcon(new Icon(VaadinIcon.CLOSE_CIRCLE_O));
			b.addClickListener((e)-> {
				usr.deleteSubscription(v);
				userSubscriptions.remove(v);
				uss.setItems(userSubscriptions);
				if(userSubscriptions.size()==0) {
					esr.deleteEmailSHA(v.getEmail());
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
		
		usp.getUserSubscriptionsForUser(stateAdminWorkflow.getSortKeyForIndicators().get())
			.subscribe(list->{
				
				userSubscriptions = list;
				
			uss.getUI()
			.get()
			.access(()->{
				
				uss.setItems(list);
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
