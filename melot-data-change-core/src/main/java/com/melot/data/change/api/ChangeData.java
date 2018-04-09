package com.melot.data.change.api;

import lombok.Data;

@Data
public class ChangeData {
	
	private String data;
	
	/**
	 * 顺序性值, 该值用来保证顺序性变更流间的顺序性, null则不保证
	 */
	private Integer orderParam;
	
	private int schemaId;
	
	private String schemaName;
	
	private String namespace;
	
	public ChangeData() {
		
	}
	
	public ChangeData(String data, int schemaId, Integer orderParam) {
		this.data = data;
		this.schemaId = schemaId;
		this.orderParam = orderParam;
	}
	
}
