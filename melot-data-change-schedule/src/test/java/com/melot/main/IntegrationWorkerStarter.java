package com.melot.main;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.melot.data.change.core.config.po.FilterType;
import com.melot.data.change.core.config.po.IntegrationConfig;
import com.melot.data.change.core.config.po.WorkType;
import com.melot.data.schedule.DeamonWorker;
import com.melot.data.schedule.registry.WorkerStatusType;
import com.melot.data.schedule.registry.ZkWorkerNodeData;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
		MongoAutoConfiguration.class })
@ComponentScan({ "com.melot" })
@EnableAutoConfiguration
public class IntegrationWorkerStarter {
	
	public static void main(String[] args) {
		
		ConfigurableApplicationContext context = SpringApplication.run(
				PullerWorkerStarter.class, args);
		DeamonWorker deamonWorker = context.getBean(DeamonWorker.class);
		deamonWorker.startWorker(false);
		
		List<IntegrationConfig> configs = new ArrayList<IntegrationConfig>();
		IntegrationConfig config = new IntegrationConfig();
//		IntegrationConfig dbConfig = new IntegrationConfig();
//		
//		dbConfig.setIntegrationClassName("com.melot.data.integration.db.IsomorphismIntegration");
//		dbConfig.setPwd("postgres");
//		dbConfig.setUrl("jdbc:postgresql://10.0.3.157:5432/test");
//		dbConfig.setUser("postgres");
//		dbConfig.setTableUser("postgres");
//		configs.add(dbConfig);
		
		config.setIntegrationClassName("com.melot.data.integration.es.ElasticSearchIntegration");
		config.setUser("elasticsearch");
		config.setUrl("10.0.17.31:9300");
		config.setFilterType(FilterType.ANOLE);
//		config.setFilterPattern("{\"user_id\":\"!user_alias\"}");
		configs.add(config);
		
		ZkWorkerNodeData nodeData = new ZkWorkerNodeData();
		nodeData.setStatus(WorkerStatusType.START);
		nodeData.setWorkerId("2");
		nodeData.setWorkerName("test-worker");
		nodeData.setWorkType(WorkType.INTEGRATION.getType());
		nodeData.setIntegrationConfigs(configs);
		deamonWorker.workerStatusChanged(nodeData);
	}
}
