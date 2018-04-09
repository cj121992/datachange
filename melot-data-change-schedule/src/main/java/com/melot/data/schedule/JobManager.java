package com.melot.data.schedule;

import com.melot.data.change.api.Worker;
import com.melot.data.schedule.registry.ZkWorkerNodeData;

public interface JobManager {
	public Worker createNewJob(ZkWorkerNodeData zkWorkerNodeData);
}