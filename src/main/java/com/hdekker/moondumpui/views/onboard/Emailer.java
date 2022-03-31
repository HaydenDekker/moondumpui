package com.hdekker.moondumpui.views.onboard;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.dyndb.opps.TemporaryIndicatorSubscriptionAdder;
import com.hdekker.moondumpui.dyndb.opps.UnsubscribeLinkAdder;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.hdekker.moondumpui.views.state.SessionState;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesAsyncClient;

@Route("emailer")
public class Emailer extends AppBaseSinglePageCard{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5959940866416029847L;
	SesAsyncClient emailer;
	
	@Autowired
	DatabaseConfig dbc;
	
	@Autowired
	SessionState state;
	
	@Autowired
	TemporaryIndicatorSubscriptionAdder tisa;
	
	@Autowired
	UnsubscribeLinkAdder usla;
	
	public Emailer() {
		super();
		
		// TODO move email logic
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
				emailField.setErrorMessage("requires a valid email.");
				emailField.setInvalid(true);
				return;
			}
			
			state.setEmail(Optional.of(emailField.getValue()));
			
			UUID uuid = UUID.randomUUID();
			
			IndicatorSubscription is = new IndicatorSubscription(
					state.getAssetName().get(), 
					state.getEmail().get(), 
					state.getProperties().get(), 
					state.getIndicatorName().get(), 
					state.getIndicatorApplicationOrder()
					.get(), 
					state.getInterfaceName().get(), 
					state.getAssetSampleRateMinutes()
					.get()
					.stream()
					.map(i->i.doubleValue())
					.collect(Collectors.toList()));
			
			tisa.add(uuid.toString(), is);
			
			usla.add(state.getEmail().get());
			
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

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		
	}

}
