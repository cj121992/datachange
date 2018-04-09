package com.melot.data.integration.es;

import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.FilterType;
import com.melot.data.change.core.config.po.IntegrationConfig;
import com.melot.data.integration.AbstractIntegration;
import com.melot.data.integration.DefaultFilter;
import com.melot.data.integration.filter.AnoleFilter;
import com.melot.data.integration.filter.GrokFilter;

	
public class ElasticSearchIntegration extends AbstractIntegration {
	@Override
	public void initIntegration(IntegrationConfig integrationConfig,
			SchemaRegistryService schemaRegistryService) throws Exception {
		FilterType type = integrationConfig.getFilterType();
		switch (type) {
		case DEFAULT :
			super.filter = new DefaultFilter(schemaRegistryService);
			break;
		case ANOLE :
			super.filter = new AnoleFilter(schemaRegistryService, integrationConfig.getFilterPattern());
			break;
		case GROK :
			super.filter = new GrokFilter(schemaRegistryService, integrationConfig.getFilterPattern());
			break;
		}
		super.output = new ElasticOutput(schemaRegistryService, integrationConfig.getUrl(), integrationConfig.getUser());
	}
	
}
