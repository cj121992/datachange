package com.melot.data.change.core.config.po;

public enum FilterType {
	DEFAULT("default"),
	ANOLE("anole"),
	GROK("grok");
	
	private String type;
	
	private FilterType(String type)
	{
		this.type = type;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public static FilterType fromString(String typeName)
	{
		FilterType type = DEFAULT;
		
		if(typeName.equalsIgnoreCase("default"))
		{
			type = DEFAULT;
		} else if (typeName.equalsIgnoreCase("anole")) {
			type = ANOLE;
		} else if (typeName.equalsIgnoreCase("grok")) {
			type = GROK;
		}
		return type;
	}
}
