package com.melot.data.integration.filter;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class AnoleHelper {
	
	public static Set<Anoler> getRuleMap(String pattern) {
		if (StringUtils.isEmpty(pattern)) {
			return null;
		}
		Set<Anoler> set = new HashSet<Anoler>();
		JSONObject json = JSON.parseObject(pattern);
		for (Entry<String, Object> entry : json.entrySet()) {
			String target = (String) entry.getValue();
			if (target.startsWith("%")) {
				
			} else if (target.startsWith("!")) {
				String alias = target.substring(1);
				Anoler anoler = new AliasAnoler(entry.getKey(), alias);
				set.add(anoler);
			}
		}
		return set;
	}
	
}
