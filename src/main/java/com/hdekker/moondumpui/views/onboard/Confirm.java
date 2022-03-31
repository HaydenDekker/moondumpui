package com.hdekker.moondumpui.views.onboard;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.opps.IndicatorSubscriptionAdder;
import com.hdekker.moondumpui.dyndb.opps.TemporaryIndicatorSubscriptionProvider;
import com.hdekker.moondumpui.dyndb.opps.TemporaryIndicatorSubscriptionRemover;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;


@Route("confirm/:uuid")
public class Confirm extends AppBaseSinglePageCard implements BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5484909023889547625L;

	String uuid;
	
	Div holder = new Div();
	
	public Confirm() {
		super();
		
		holder.add(new H2("confirming subscription..."));
		add(holder);
		
	}
	
	@Autowired
	TemporaryIndicatorSubscriptionProvider tisp;
	
	@Autowired
	IndicatorSubscriptionAdder usa;
	
	@Autowired
	TemporaryIndicatorSubscriptionRemover tisr;

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
		tisp.getSubscription(uuid)
			.map(is->usa.addUserSubscription(is))
			.map(is->tisr.remove(uuid))
			.subscribe(r->{
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
