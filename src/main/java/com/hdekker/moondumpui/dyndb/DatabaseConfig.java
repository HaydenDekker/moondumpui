package com.hdekker.moondumpui.dyndb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration("uidb")
@ConfigurationProperties(prefix = "moondump.db")
public class DatabaseConfig {

	public String tableName;
	public String primaryKey;
	public String sortKey;
	public String region;
	public String appAdminEmail;
	public String subscriptionConfmEndpoint;
	public String emailRegion;
	

	public String getEmailRegion() {
		return emailRegion;
	}

	public void setEmailRegion(String emailRegion) {
		this.emailRegion = emailRegion;
	}

	public String getSubscriptionConfmEndpoint() {
		return subscriptionConfmEndpoint;
	}

	public void setSubscriptionConfmEndpoint(String subscriptionConfmEndpoint) {
		this.subscriptionConfmEndpoint = subscriptionConfmEndpoint;
	}

	public String getAppAdminEmail() {
		return appAdminEmail;
	}

	public void setAppAdminEmail(String appAdminEmail) {
		this.appAdminEmail = appAdminEmail;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(String primaryKey) {
		this.primaryKey = primaryKey;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}
	
}
