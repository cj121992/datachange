package com.melot.data.integration.filter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.melot.common.melot_utils.StringUtils;
import com.melot.data.change.api.ChangeData;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.FieldInfo;
import com.melot.data.integration.DefaultFilter;
import com.nflabs.grok.GrokException;

@Slf4j
public class GrokFilter extends DefaultFilter {
	
	private GrokHandler grokHandler;
	
	public GrokFilter(SchemaRegistryService schemaRegistryService, String pattern) {
		super(schemaRegistryService);
		try {
			grokHandler = GrokHelper.getRule(pattern);
		} catch (GrokException e) {
			log.warn("no valid grok pattern, is : " + pattern);
		}
	}

	@Override
	public Map<String, FieldInfo> enrich(ChangeData changeData) {
		Map<String, FieldInfo> map = super.enrich(changeData);
		if (grokHandler != null && map.containsKey(grokHandler.getTarget())) {
			FieldInfo field = map.get(grokHandler.getTarget());
			if (field.getFieldValue() instanceof String) {
				String retJson = grokHandler.match((String) field.getFieldValue());
				if (StringUtils.isNotEmpty(retJson)) {
					JSONObject json = JSON.parseObject(retJson);
					Set<Entry<String, Object>> set = json.entrySet();
					for (Entry<String, Object> entry : set) {
						String key = entry.getKey();
						String value = (String) entry.getValue();
						FieldInfo fieldInfo = new FieldInfo(value);
						map.put(key, fieldInfo);
					}
					//grok执行成功则移除原字段
					map.remove(grokHandler.getTarget());
				}
			}
		}
		
		return map;
	}
	
	 
	
}
