package com.melot.data.change.core.config.po;

public enum DBType {
	PG("pg");
	
	private String type;
	
	private DBType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public static DBType fromString(String typeName)
	{
		DBType dbType = PG;
		
		if(typeName.equalsIgnoreCase("pg"))
		{
			dbType = PG;
		}
		return dbType;
	}
}
