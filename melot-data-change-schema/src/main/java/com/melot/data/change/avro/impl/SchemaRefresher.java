package com.melot.data.change.avro.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.melot.common.melot_utils.StringUtils;
import com.melot.data.change.avro.VersionedSchema;
import com.melot.data.change.core.config.po.SchemaDTO;

@Component
public class SchemaRefresher {
	
	public SchemaRefresher(){
		
	}
	
	public SchemaRefresher(String host) {
		this._fetchServer = host;
	}
	
	@Value("${melot.data.change.server}")
	private String _fetchServer;

	private final Logger _logger = Logger.getLogger(getClass());

	// TODO
	// @Value("${melot.data.change.server}")
	// private String backupServer;

	private static final String _refreshUrl = "/schema/get";

	public List<VersionedSchema> refreshSchema() {
		List<VersionedSchema> newValue = null;
		String schemaStr = refreshUrl();
		if (StringUtils.isEmpty(schemaStr)) {
			_logger.warn("this cycle refresh schema get null, skip refresh");
		} else {
			newValue = trans2VersionedSchema(schemaStr);
		}
		return newValue;
	}

	private String refreshUrl() {
		String schemaStr = "";

		final String fullUrl = _fetchServer + _refreshUrl;

		RestTemplate restTemplate = new RestTemplate();
		try {
			schemaStr = restTemplate.getForObject(fullUrl, String.class);
		} catch (Exception e) {
			_logger.warn("fail to refresh schema this time, url is : "
					+ fullUrl, e);

		}
		return schemaStr;
	}

	private List<VersionedSchema> trans2VersionedSchema(String schemaStr) {
		List<VersionedSchema> newList = new ArrayList<VersionedSchema>();
		List<SchemaDTO> schemaList = JSON.parseArray(schemaStr, SchemaDTO.class);
		for (SchemaDTO schemaDTO : schemaList) {
			VersionedSchema versionSchema = new VersionedSchema(schemaDTO.getSchema_name(), schemaDTO.getVersion(), Schema.parse(schemaDTO.getValue()), schemaDTO.getSchema_id());
			newList.add(versionSchema);
		}
		return newList;
	}
	
	
}
