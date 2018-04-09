package com.melot.data.change.api;

import java.util.Map;

import com.melot.data.change.core.config.po.FieldInfo;

public interface OutputPlugin {
	
	boolean output(Map<String, FieldInfo> map, int schemaId);
	
}
