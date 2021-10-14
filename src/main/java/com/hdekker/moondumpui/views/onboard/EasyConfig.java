package com.hdekker.moondumpui.views.onboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hdekker.moondumpui.dyndb.DatabaseConfig;
import com.hdekker.moondumpui.state.SessionState;
import com.hdekker.moondumpui.views.BaseDynamoDBSinglePageCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.Route;

@Route("easy-config")
public class EasyConfig extends BaseDynamoDBSinglePageCard {

	public EasyConfig(DatabaseConfig dbc, SessionState state) {
		super(dbc, state);
		
		Label getStarted = new Label("Please choose from the following two options. You can quickly and easily sign up to a bitcoin buy indicator or get your hands dirty and create a customised indicator yourself."); 
		
		Button easyIndicator = new Button("Easy Indicator");
		easyIndicator.addClickListener(e->{
			
			// TODO how do I make this resiliant with core component
			// 4.0 should allow core to define pre configured indicators for selection.
			state.setAlertName(Optional.of("Alert-ALERT_THRESHOLD_BELOW"));
			state.setAssetName(Optional.of("bitcoin"));
			state.setAssetSampleRateMinutes(Optional.of(List.of(24*60)));
			
			//{"Alert-ALERT_THRESHOLD_BELOW":{"value":30.0},"Transform-TRANSFORM_RSI":{"steps":14.0}}
			state.addProperties("Alert-ALERT_THRESHOLD_BELOW", Map.of("value", 30.0));
			state.addProperties("Transform-TRANSFORM_RSI", Map.of("steps", 14.0));
			state.setIndicatorApplicationOrder(Optional.of(List.of("Transform-TRANSFORM_RSI", "Alert-ALERT_THRESHOLD_BELOW")));
			
			state.setIndicatorName(Optional.of("bitcoin daily"));
			state.setInterfaceName(Optional.of("coin-geko"));
			state.setTransformName(Optional.of("Transform-TRANSFORM_RSI"));
			
			UI.getCurrent().navigate(Emailer.class);
			
			
			
		});
		
		
		H2 orOtherOption = new H2("OR");
		
		Button configureYourOwn = new Button("Configure Your Own");
		configureYourOwn.addClickListener((e)->{
			UI.getCurrent().navigate(SampleInterfaceSelector.class);
		});
		
		VerticalLayout vl = new VerticalLayout();
		vl.setAlignItems(Alignment.CENTER);
		vl.add(getStarted, easyIndicator, orOtherOption, configureYourOwn);
		add(vl);
		
	}

	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		// TODO Auto-generated method stub
		
	}

}
