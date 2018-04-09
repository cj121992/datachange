package com.melot.data.schedule.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.melot.common.transaction.processor.HistoryConsumerConfig;
import com.melot.data.change.api.DataIntegration;
import com.melot.data.change.api.Worker;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.IntegrationConfig;
import com.melot.data.change.core.config.po.RelayType;
import com.melot.data.change.core.config.po.WorkType;
import com.melot.data.change.job.MqStreamRelay;
import com.melot.data.schedule.JobManager;
import com.melot.data.schedule.config.ConsumeConfig;
import com.melot.data.schedule.registry.ZkWorkerNodeData;

@Component
@Slf4j
public class DefaultJobManager implements JobManager {
	
	@Autowired
	private MqStreamRelay _mqStreamRelay;
	
	@Autowired
	private SchemaRegistryService _schemaRegistryService;
	
	@Autowired
	private ConsumeConfig _consumeConfig;
	
	@PostConstruct
	public void init() {
		
	}
	
	@Override
	public Worker createNewJob(ZkWorkerNodeData zkWorkerNodeData) {
		Worker worker = null;
		//根据类型创建对应worker
		if (zkWorkerNodeData.getWorkType().equals(WorkType.PULLER.getType())) {
			worker = new PullerWorker();
			worker.addRelay(RelayType.MQ, _mqStreamRelay);
			worker.addSchemaRegistry(_schemaRegistryService);
			log.debug("jobManager creater new worker------>type is puller");
		} else if (zkWorkerNodeData.getWorkType().equals(WorkType.INTEGRATION.getType())) {
			worker = new DataIntegrationWorker();
			List<IntegrationConfig> integrationConfigs = zkWorkerNodeData.getIntegrationConfigs();
			for (IntegrationConfig config : integrationConfigs) {
				String className = config.getIntegrationClassName();
				try {
					DataIntegration dataIntegration = (DataIntegration) Class.forName(className).newInstance();
					dataIntegration.initIntegration(config, _schemaRegistryService);
					worker.addDataIntegration(dataIntegration);
				} catch (Exception e) {
					log.error("fail to reflect worker class : " +  className, e);
				}
			}
			HistoryConsumerConfig[] consumers = _consumeConfig.getConsumer();
			worker.addCosumers(consumers);
		}
		worker.addAll(zkWorkerNodeData.getJobConfigs());
		worker.init();
		return worker;
	}
	
	
}
