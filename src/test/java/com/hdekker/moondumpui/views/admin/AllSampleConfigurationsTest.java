package com.hdekker.moondumpui.views.admin;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.github.mvysny.kaributesting.v10.LocatorJ;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.hdekker.moondumpui.sample.SampleConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.spring.SpringServlet;

import kotlin.jvm.functions.Function0;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

@SpringBootTest
public class AllSampleConfigurationsTest {
	
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
	public void displaysListOfConfiguredInterfacesAndAssets() {
		
    	UI.getCurrent().navigate(AllSampleConfigurations.class);
    	AllSampleConfigurations baseClass = LocatorJ._get(AllSampleConfigurations.class);
    	List<SampleConfiguration> items = baseClass.getDisplayedItems();
    	
    	Optional<SampleConfiguration> asset = items.stream()
    		.filter(item->item.getAssetId().equals("Test Asset"))
    		.findFirst();
    	assertThat(asset.isPresent(), equalTo(true));
	}
	
}
