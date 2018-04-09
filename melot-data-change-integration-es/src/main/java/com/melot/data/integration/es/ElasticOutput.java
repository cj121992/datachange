package com.melot.data.integration.es;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.avro.Schema;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.melot.common.melot_utils.StringUtils;
import com.melot.data.change.api.OutputPlugin;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.avro.util.SchemaHelper;
import com.melot.data.change.core.config.po.FieldInfo;
import com.melot.data.change.searcher.es.ElasticClient;

public class ElasticOutput implements OutputPlugin {

	private ElasticClient client;

	private SchemaRegistryService _schemaRegistryService;

	public ElasticOutput(SchemaRegistryService schemaRegistryService,
			String servers, String clusterName) {
		_schemaRegistryService = schemaRegistryService;
		client = new ElasticClient(servers, clusterName);
		client.init();
	}

	@Override
	public boolean output(Map<String, FieldInfo> map, int schemaId) {
		Map<String, Object> esMap = new HashMap<String, Object>();
		Schema schema = _schemaRegistryService
				.fetchLatestVersionedSchemaBySchemaId(schemaId).getSchema();
		String primaryKey = SchemaHelper.getMetaField(schema, "pk");
		String[] pKeys = primaryKey.split(",");

		String tableName = _schemaRegistryService
				.fetchLatestVersionedSchemaBySchemaId(schemaId)
				.getSchemaBaseName();
		String[] names = tableName.split("\\.");
		String index = names[0];
		String type = names[1];

		for (Entry<String, FieldInfo> entry : map.entrySet()) {
			String key = entry.getKey();
			FieldInfo fieldInfo = entry.getValue();
			esMap.put(key, fieldInfo.getFieldValue());
		}

		BoolQueryBuilder requestBuilder = QueryBuilders.boolQuery();
		for (String key : pKeys) {
			requestBuilder.must(QueryBuilders.matchQuery(key, esMap.get(key)));
		}

		createIndex(index, type, esMap, requestBuilder);
		return true;
	}

	public void createIndex(String index, String type,
			Map<String, Object> esMap, BoolQueryBuilder requestBuilder) {
		String id = client.select(index, type, requestBuilder);
		if (StringUtils.isNotEmpty(id)) {
			client.update(index, type, id, esMap);
		} else{
			client.create(index, type, esMap);
		}
	}

}
