package com.hdekker.moondumpui.views.unsubscribe;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.opps.EmailSHARemover;
import com.hdekker.moondumpui.dyndb.opps.IndicatorSubscriptionRemover;
import com.hdekker.moondumpui.dyndb.opps.UnsubscribeSubscriptionsProvider;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;


@Route("unsubscribe/:sha")
public class Unsubscribe extends AppBaseSinglePageCard implements BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = -9154203511996995740L;

	String sha;
	
	Grid<IndicatorSubscription> uss = new Grid<>();
	
	private List<IndicatorSubscription> userSubscriptions;

	public List<IndicatorSubscription> getUserSubscriptions() {
		return userSubscriptions;
	}
	
	@Autowired
	IndicatorSubscriptionRemover isr;

	@Autowired
	EmailSHARemover esr;

	public Unsubscribe() {
		super();
		
		uss.addColumn(IndicatorSubscription::getAssetName).setHeader("Asset");
		uss.addColumn(IndicatorSubscription::getIndicatorName).setHeader("Indicator Name");
		uss.addColumn(IndicatorSubscription::getSampleRates).setHeader("Sample Rates");
		
		uss.addColumn(new ComponentRenderer<>(Button::new, (b, v)->{
			
			b.setIcon(new Icon(VaadinIcon.CLOSE_CIRCLE_O));
			b.addClickListener((e)-> {
				isr.deleteSubscription(v);
				userSubscriptions.remove(v);
				uss.setItems(userSubscriptions);
				if(userSubscriptions.size()==0) {
					esr.deleteEmailSHA(sha);
				}
			});
			
		})).setHeader("delete");
		
		add(uss);
		
	}
	
	@Autowired
	UnsubscribeSubscriptionsProvider usp;

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
	
		usp.getSubscriptionsForUnsubSHA(sha)
		.subscribe(list->{
			
			uss.getUI()
			.get()
			.access(()->{

				uss.setItems(list);
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
