package com.melot.data.change.api;

import java.util.List;

import com.melot.common.transaction.processor.HistoryConsumerConfig;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.JobConfig;
import com.melot.data.change.core.config.po.RelayType;

public interface Worker {

	void init();

	String getWorkerId();

	
	boolean isDone();

	boolean isAlive();

	void start();

	void stop();

	public void addAll(List<JobConfig> jobConfigs);
	
	void add(JobConfig jobConfig);
	
	void remove(JobConfig jobConfig);

	void startOne(JobConfig jobConfig);

	void stopOne(JobConfig jobConfig);

	StreamRelay getRelay(RelayType relayType);

	void addRelay(RelayType relayType, StreamRelay relay);

	void addSchemaRegistry(SchemaRegistryService schemaRegistryService);
	
	SchemaRegistryService getSchemaRegistry();

	void addDataIntegration(DataIntegration dataIntegration);
	
	List<DataIntegration> getDataIntegration();
	
	void addCosumers(HistoryConsumerConfig[] configs);
}
