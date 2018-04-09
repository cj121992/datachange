package com.melot.data.change.core.config.po;

import lombok.Data;

@Data
public class IntegrationConfig {

	// private IntegrationType type;

	// integration 实例类型
	private String integrationClassName;

	private String url;

	private String user;

	private String pwd;
	
	private FilterType filterType = FilterType.DEFAULT;

	private String filterPattern;

	/**
	 * pg create table need user
	 */
	private String tableUser;

}
