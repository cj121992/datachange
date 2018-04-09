package com.melot.data.change.api;


import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.IntegrationConfig;

public interface DataIntegration {
	
	boolean integration(ChangeData data);
	
	void initIntegration(IntegrationConfig integrationConfig, SchemaRegistryService schemaRegistryService) throws Exception;
	
}
