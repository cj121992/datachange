package com.melot.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.melot.data.change.core.config.po.JobConfig;
import com.melot.data.change.core.config.po.RelayType;
import com.melot.data.change.core.config.po.TriggerConfig;
import com.melot.data.change.core.config.po.WorkType;
import com.melot.data.schedule.DeamonWorker;
import com.melot.data.schedule.registry.WorkerStatusType;
import com.melot.data.schedule.registry.ZkWorkerNodeData;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
		MongoAutoConfiguration.class })
@ComponentScan({ "com.melot" })
@EnableAutoConfiguration
public class PullerWorkerStarter {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(
				PullerWorkerStarter.class, args);
		DeamonWorker deamonWorker = context.getBean(DeamonWorker.class);
		deamonWorker.startWorker(false);
		ZkWorkerNodeData nodeData = new ZkWorkerNodeData();

		List<JobConfig> jobConfigs = new ArrayList<JobConfig>();
		JobConfig jobConfig = new JobConfig();
		jobConfig.setPullerClassName("com.melot.data.change.pg.puller.PgChangePuller");
		HashMap<String, Object> pullerMap = new HashMap<String, Object>();
		pullerMap.put("generateRange", 1000);
		pullerMap.put("regressionSlot", "regression_slot_appstable");
		pullerMap.put("url", "jdbc:postgresql://pg44.kktv2.com:5432/appstable");
		jobConfig.setPullerMap(pullerMap);
		jobConfigs.add(jobConfig);
		jobConfig.setJobName("pulljob");
		jobConfig.setGroup("pg");
		TriggerConfig triggerConfig = new TriggerConfig();
		triggerConfig.setCron("*/5 * * * * ?");
		jobConfig.setTriggerConfig(triggerConfig);
		jobConfig.setRelayType(RelayType.MQ);
		
		nodeData.setJobConfigs(jobConfigs);
		nodeData.setStatus(WorkerStatusType.START);
		nodeData.setWorkerId("1");
		nodeData.setWorkerName("test-worker");
		nodeData.setWorkType(WorkType.PULLER.getType());
		
		deamonWorker.workerStatusChanged(nodeData);
	}
}