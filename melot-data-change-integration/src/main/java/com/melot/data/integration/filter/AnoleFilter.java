package com.melot.data.integration.filter;

import java.util.Map;
import java.util.Set;

import com.melot.data.change.api.ChangeData;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.FieldInfo;
import com.melot.data.integration.DefaultFilter;

public class AnoleFilter extends DefaultFilter {
	
	private Set<Anoler> anolerSet;
	
	public AnoleFilter(SchemaRegistryService schemaRegistryService, String anolePattern) {
		super(schemaRegistryService);
		anolerSet = AnoleHelper.getRuleMap(anolePattern);
	}

	@Override
	public Map<String, FieldInfo> enrich(ChangeData changeData) {
		Map<String, FieldInfo> map = super.enrich(changeData);
		if (anolerSet != null && anolerSet.size() > 0) {
			for (Anoler anoler : anolerSet) {
				anoler.anole(map);
			}
		}
		return map;
	}

}
