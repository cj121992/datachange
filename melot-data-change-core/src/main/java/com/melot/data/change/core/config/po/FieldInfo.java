package com.melot.data.change.core.config.po;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class FieldInfo {
	private Object fieldValue;
	private String sqlString;
	private String fieldJavaType;
	private String fieldJdbcType;
	
	public FieldInfo() {
		
	}
	
	public FieldInfo(String fieldValue) {
		this.fieldValue = fieldValue;
	}
}
