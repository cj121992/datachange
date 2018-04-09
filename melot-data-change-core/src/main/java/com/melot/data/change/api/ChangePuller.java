package com.melot.data.change.api;

import java.util.List;

import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.JobConfig;

public interface ChangePuller {
	
	List<ChangeData> readEvent();
	
	void initPuller(JobConfig jobConfig, SchemaRegistryService schemaRegistryService) throws Exception;
}
