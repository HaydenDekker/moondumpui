package com.hdekker.moondumpui.views.admin;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.hdekker.moondumpui.dyndb.opps.IndicatorStateProvider;
import com.hdekker.moondumpui.subscription.IndicatorSubscription;
import com.hdekker.moondumpui.views.AppBaseSinglePageCard;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;


@Route("indicator-details")
public class IndicatorDetails extends AppBaseSinglePageCard implements BeforeEnterObserver{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5806665591718811452L;

	@Autowired
	StateAdminWorkflow stateAdminWorkflow;
	
	VerticalLayout holder = new VerticalLayout();
	
	@Autowired
	IndicatorStateProvider isp;
	
	public IndicatorDetails() {
		super();
		
		add(holder);
		
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		
		stateAdminWorkflow.getUss()
			.ifPresentOrElse(e->{}, ()-> event.forwardTo(AllSubscribers.class));
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		
		IndicatorSubscription uss = stateAdminWorkflow.getUss().get();
		
		holder.add(getItem("asset", uss.getAssetName()));
		holder.add(getItem("interface", uss.getInterfaceName()));
		holder.add(getItem("indicator name", uss.getIndicatorName()));
		holder.add(getItem("email", uss.getEmail()));
		holder.add(getItem("sample rates", uss.getSampleRates().toString()));
		holder.add(getItem("indicator structure", uss.getIndicatorOrder().toString()));
		holder.add(getItem("indicator config", uss.getIndicatorConfig().toString()));
		
		// sample rate will require unique keys
		List<String> itemSortKeys = uss.getSampleRates()
										.stream()
										.map(d-> Duration.ofMinutes(d.longValue()).toString())
										.map(dur-> uss.getAssetName() + "-" + dur + "-" + uss.getIndicatorName())
										.collect(Collectors.toList());
		
		isp.getIndicatorDetails(itemSortKeys)
			.subscribe(l->{
				
				List<VerticalLayout> items = l.stream()
					.flatMap(ind->{
						VerticalLayout state = getItem("state", ind.getStateJSON());
						VerticalLayout date = getItem("last state update", ind.getLastUpdated()
								.format(DateTimeFormatter.ofPattern("dd MMM uuuu")));
						return Arrays.asList(state, date)
								.stream();
					})
					.collect(Collectors.toList());
				
				holder.getUI().get()
					.access(()->{
						items.forEach(i-> holder.add(i));
						holder.getUI().get().push();
					});
				
			});
				
	}
	
	VerticalLayout getItem(String label, String value) {
		
		Label item = new Label(label);
		H4 val = new H4(value);
		VerticalLayout h = new VerticalLayout(item, val);
		h.setAlignItems(Alignment.BASELINE);
		h.setMargin(false);
		val.getStyle().set("margin-top", "0");
		
		return h;
		
	}

}
