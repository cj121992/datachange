package com.melot.data.schedule.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.melot.common.melot_utils.NetUtils;

import lombok.Data;

@Data
@Component
public class WorkerConfig {
	
	@Value("${data.change.worker.name}")
	private String name;
	
	@Value("${data.change.worker.host}")
	private String host;
	
	@PostConstruct
	public void init()
	{
		this.host = NetUtils.fixIpWithWildChar(host);
	}
	
}
