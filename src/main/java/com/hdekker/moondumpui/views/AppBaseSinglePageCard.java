package com.hdekker.moondumpui.views;

import com.hdekker.mobileapp.templates.SinglePageCard;
import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.AfterNavigationObserver;


@SuppressWarnings("serial")
@StyleSheet("./shared-styles.css")
@Push
public abstract class AppBaseSinglePageCard extends SinglePageCard implements AfterNavigationObserver{
	
}
