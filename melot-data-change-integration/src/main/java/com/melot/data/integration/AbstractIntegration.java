package com.melot.data.integration;

import java.util.Map;

import lombok.Setter;

import com.melot.data.change.api.ChangeData;
import com.melot.data.change.api.DataIntegration;
import com.melot.data.change.api.Filter;
import com.melot.data.change.api.OutputPlugin;
import com.melot.data.change.core.config.po.FieldInfo;

public abstract class AbstractIntegration implements DataIntegration {
	
	@Setter
	protected Filter filter;
	
	@Setter
	protected OutputPlugin output;
	
	@Override 
	public boolean integration(ChangeData data) {
		Map<String, FieldInfo> map = filter.enrich(data);
		return output.output(map, data.getSchemaId());
	}
	
}
