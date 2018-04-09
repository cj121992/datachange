package com.melot.data.integration.filter;

import java.util.Map;

import com.melot.data.change.core.config.po.FieldInfo;

public class AliasAnoler implements Anoler {
	
	private String alias;
	
	private String target;
	
	public AliasAnoler(String target, String alias) {
		this.alias = alias;
		this.target = target;
	}
	
	@Override
	public void anole(Map<String, FieldInfo> map) {
		if (map.containsKey(target)) {
			FieldInfo field = map.remove(target);
			map.put(alias, field);
		}
	}
}
