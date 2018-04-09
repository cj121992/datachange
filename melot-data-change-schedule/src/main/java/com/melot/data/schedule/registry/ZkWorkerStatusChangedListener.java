package com.melot.data.schedule.registry;


public interface ZkWorkerStatusChangedListener {
	void workerStatusChanged(ZkWorkerNodeData newZkWorkerNodeData);
}
