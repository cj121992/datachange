package com.melot.data.schedule.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.common.message.MessageExt;

import com.melot.common.transaction.history.ActionHistory;
import com.melot.data.change.api.ChangeData;
import com.melot.data.change.api.DataIntegration;
import com.melot.data.change.avro.SchemaRegistryService;

public class DataIntegrationWorker extends AbstractMqConsumerWorker {

	private SchemaRegistryService _schemaRegistryService;

	private List<DataIntegration> _dataIntegrations = new ArrayList<DataIntegration>();

	@Override
	public void addSchemaRegistry(SchemaRegistryService schemaRegistryService) {
		_schemaRegistryService = schemaRegistryService;
	}

	@Override
	public SchemaRegistryService getSchemaRegistry() {
		return _schemaRegistryService;
	}

	@Override
	public boolean process(MessageExt msg, ActionHistory actionHistory) {
		String dataStr = actionHistory.getActionData("data").toString();
		Integer schemaId = (Integer) actionHistory.getActionData("schemaId");
		String schemaName = (String) actionHistory.getActionData("schemaName");
		String schemaNamespace = (String) actionHistory.getActionData("schemaNamespace");
		
		ChangeData data = new ChangeData();
		data.setData(dataStr);
		if (null != schemaId) {
			data.setSchemaId(schemaId);
		}
		if (!StringUtils.isEmpty(schemaName)) {
			data.setSchemaName(schemaName);
		}
		if (!StringUtils.isEmpty(schemaNamespace)) {
			data.setNamespace(schemaNamespace);
		}
		boolean flag = false;
		for (DataIntegration dataIntegration : _dataIntegrations) {
			// TODO
			flag = dataIntegration.integration(data);
		}
		return flag;
	}

	@Override
	public void addDataIntegration(DataIntegration dataIntegration) {
		_dataIntegrations.add(dataIntegration);
	}

	@Override
	public List<DataIntegration> getDataIntegration() {
		return _dataIntegrations;
	}

}
