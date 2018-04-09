package com.melot.data.integration.db;

import javax.sql.DataSource;

import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.IntegrationConfig;
import com.melot.data.integration.AbstractIntegration;
import com.melot.data.integration.DefaultFilter;
import com.melot.data.integration.db.util.DatasourceUtils;

/**
 * 同构数据库变更流整合器
 * 
 * @author admin
 * 
 */
public class IsomorphismIntegration extends AbstractIntegration {

	/**
	 * pg 建表需要
	 */

	@Override
	public void initIntegration(IntegrationConfig integrationConfig, SchemaRegistryService schemaRegistryService) throws Exception {
		DataSource ds = DatasourceUtils.createDataSource(integrationConfig.getUrl(), integrationConfig.getUser(), integrationConfig.getPwd());
		super.filter = new DefaultFilter(schemaRegistryService);
		super.output = new DefaultDBOutput(ds, schemaRegistryService, integrationConfig.getUser());
	}
	

}