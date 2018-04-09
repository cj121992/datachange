package com.melot.data.change.core.config.po;

import lombok.Data;

@Data
public class SchemaDTO {
	
	private int schema_id;
	
	private String schema_name;
	
	private short version;
	
	private String value;
	
}
