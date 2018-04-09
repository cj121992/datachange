package com.melot.data.schedule.registry;


public enum WorkerStatusType {
	RUNNING(1),
	STOP(-1),
	WAITING(0),
	START(2),
	UNKNOWN(3);
	
	private final int status;
	
	private WorkerStatusType(int status)
	{
		this.status = status;
	}
	
	public int key()
	{
		return this.status;
	}
}
