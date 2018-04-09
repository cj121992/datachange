package com.melot.data.schedule.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.melot.common.transaction.processor.HistoryConsumerConfig;

@Data
@Component
@ConfigurationProperties(prefix = "data.change")
public class ConsumeConfig {
	
	
	private HistoryConsumerConfig[] consumer;
	
}
