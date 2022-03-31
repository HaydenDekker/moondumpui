package com.hdekker.moondumpui.system.health;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "moondump.sys.health")
public class SystemHealthConfiguration {

	/**
	 * The period which this module should
	 * log events relating to its overall
	 * health status.
	 * 
	 */
	public Integer loggingPeriodSeconds;

	public Integer getLoggingPeriodSeconds() {
		return loggingPeriodSeconds;
	}

	public void setLoggingPeriodSeconds(Integer loggingPeriodSeconds) {
		this.loggingPeriodSeconds = loggingPeriodSeconds;
	}
	
}
