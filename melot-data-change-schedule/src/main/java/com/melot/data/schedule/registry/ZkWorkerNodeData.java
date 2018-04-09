package com.melot.data.schedule.registry;

import java.util.List;

import com.melot.data.change.core.config.po.IntegrationConfig;
import com.melot.data.change.core.config.po.JobConfig;

import lombok.Data;

@Data
public class ZkWorkerNodeData {
	private String workerName;
	private String workerId;
	private String workType;
	private WorkerStatusType status; 

	/**
	 * puller的属性
	 */
	private List<JobConfig> jobConfigs;
	
	private List<IntegrationConfig> integrationConfigs;
}
