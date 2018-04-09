package com.melot.data.integration.filter;

import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nflabs.grok.GrokException;

public class GrokHelper {
	
	public static GrokHandler getRule(String pattern) throws GrokException {
		GrokHandler gh = null;
		if (StringUtils.isEmpty(pattern)) {
			return null;
		}
		
		JSONObject json = JSON.parseObject(pattern);
		for (Entry<String, Object> entry : json.entrySet()) {
			String grokPattern = (String) entry.getValue();
			gh = new GrokHandler(pattern);
			gh.loadParren(grokPattern);
			//TODO 暂时只支持单个
			break;
		}
		
		return gh;
	}
	
}
