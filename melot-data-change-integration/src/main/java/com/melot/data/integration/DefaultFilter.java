package com.melot.data.integration;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import com.melot.data.change.api.ChangeData;
import com.melot.data.change.api.Filter;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.avro.util.SchemaHelper;
import com.melot.data.change.core.config.po.FieldInfo;

@Slf4j
public class DefaultFilter implements Filter{
	
	public DefaultFilter(SchemaRegistryService schemaRegistryService) {
		_schemaRegistryService = schemaRegistryService;
	}
	
	private SchemaRegistryService _schemaRegistryService;
	
	@Override
	public Map<String, FieldInfo> enrich(ChangeData data) {
		int schemaId = data.getSchemaId();
		String bytes = data.getData();
		Schema schema = _schemaRegistryService
				.fetchLatestVersionedSchemaBySchemaId(schemaId).getSchema();
		if (schema == null) {
			log.error("can't find schema, check is your schema has been remove by accident?");
		}
		GenericRecord record = SchemaHelper.deserializeEvent(bytes, schema);

		Map<String, FieldInfo> map = SchemaHelper
				.generateRecord(schema, record);
		
		return map;
	}

}
