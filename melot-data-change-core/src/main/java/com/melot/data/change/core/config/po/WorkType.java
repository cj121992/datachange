package com.melot.data.change.core.config.po;

public enum WorkType {
	PULLER("puller"),
	INTEGRATION("integration");
	
	private String type;
	
	private WorkType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public static WorkType fromString(String typeName)
	{
		WorkType workType = PULLER;
		
		if(typeName.equalsIgnoreCase("puller"))
		{
			workType = PULLER;
		} else if (typeName.equalsIgnoreCase("integration")) {
			workType = INTEGRATION;
		}
		return workType;
	}
}
