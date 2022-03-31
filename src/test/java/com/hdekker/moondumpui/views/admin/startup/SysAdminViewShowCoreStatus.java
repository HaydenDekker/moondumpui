package com.hdekker.moondumpui.views.admin.startup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.time.Duration;

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
import com.hdekker.moondumpui.system.health.FunctionStatus;
import com.hdekker.moondumpui.views.admin.startup.SystemStartupInformation;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.spring.SpringServlet;

import kotlin.jvm.functions.Function0;
import reactor.core.publisher.Mono;

@SpringBootTest
public class SysAdminViewShowCoreStatus {
	
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
	public void displaysCoreStatusInAdminView() {
		
		UI.getCurrent().navigate(SystemStartupInformation.class);
		Mono.delay(Duration.ofSeconds(2)).block(); // give some time to populate
		Label label = LocatorJ._get(Label.class, (s)-> s.withId("core-system-status"));
		assertThat(label.getText(), equalTo(FunctionStatus.OK.getStatus()));
	}
	
	
}
