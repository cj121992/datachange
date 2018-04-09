package com.melot.data.change.core.config.po;

import java.util.HashMap;

import lombok.Data;

@Data
public class JobConfig {
	
	//puller 实例类型
	private String pullerClassName;
	
	//puller自定义参数(url, range, slot等)
	private HashMap<String, Object> pullerMap;
	
	//relay 类型
	private RelayType relayType;
	
	//job名称
	private String jobName;
	
	//每个周期是否自动更新puller和relay中的配置
	private boolean autoUpdate;
	
	//optional
	private String group;
	
	//optional
	private TriggerConfig triggerConfig;
}
