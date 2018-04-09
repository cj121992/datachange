package com.melot.data.schedule;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.melot.data.change.api.Worker;
import com.melot.data.schedule.config.WorkerConfig;
import com.melot.data.schedule.registry.WorkerStatusType;
import com.melot.data.schedule.registry.ZkStoreManager;
import com.melot.data.schedule.registry.ZkWorkerNodeData;
import com.melot.data.schedule.registry.ZkWorkerStatusChangedListener;

@Component
public class DeamonWorker implements ZkWorkerStatusChangedListener {

	@Autowired
	private ZkStoreManager _zkStoreManager;
	
	@Autowired
	private JobManager jobManager;
	
	@Autowired
	private WorkerConfig _workConfig;
	
	private ZkWorkerNodeData _workerNodeData = new ZkWorkerNodeData();

	private ScheduledExecutorService _executor = Executors
			.newSingleThreadScheduledExecutor();

	private final int WORKER_INIT_DELAY = 3;
	private final int WORKER_POLL_PERIOD = 10;

	private Worker _currentWorker;

	/**
	 * 启动定时刷新zk状态线程
	 */
	@PostConstruct
	private void init() {
		_executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				updateWorkerStatus();
			}

		}, WORKER_INIT_DELAY, WORKER_POLL_PERIOD, TimeUnit.SECONDS);
	}

	public void updateWorkerStatus() {
		if (_currentWorker != null) {
			WorkerStatusType status = WorkerStatusType.WAITING;
			if (!_currentWorker.isAlive()) {
				if (_currentWorker.isDone()) {
					status = WorkerStatusType.STOP;
				}
			} else {
				status = WorkerStatusType.RUNNING;
			}
			_workerNodeData.setStatus(status);
			_workerNodeData.setWorkerName(_workConfig.getName());
			_zkStoreManager.updateWorkerStatus(_workerNodeData);
		} else {
			WorkerStatusType status = WorkerStatusType.WAITING;
			_workerNodeData.setStatus(status);
			_workerNodeData.setWorkerName(_workConfig.getName());
			_zkStoreManager.updateWorkerStatus(_workerNodeData);
		}

	}

	public void startWorker(boolean live) {
		_workerNodeData.setStatus(WorkerStatusType.WAITING);
		_workerNodeData.setWorkerName(_workConfig.getName());
		_zkStoreManager.registerNewWorker(_workerNodeData, this);
		if (live) {
			try {
				Thread.currentThread().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void startWorker() {
		startWorker(true);
	}
	
	@Override
	public void workerStatusChanged(ZkWorkerNodeData newZkWorkerNodeData) {
		if (Objects.equals(newZkWorkerNodeData.getWorkerName(),
				_workConfig.getName())) {
			_workerNodeData = newZkWorkerNodeData;
			if (StringUtils.isBlank(newZkWorkerNodeData.getWorkerId())) {
				if (_currentWorker != null) {
					_currentWorker.stop();
				}
				_currentWorker = null;
			} else {
				switch (newZkWorkerNodeData.getStatus()) {
				case STOP:
					if (_currentWorker != null) {
						_currentWorker.stop();
					}
					break;
				case START:
					if (_currentWorker != null
							&& !_currentWorker.getWorkerId().equals(
									_workerNodeData.getWorkerId())) {
						_currentWorker = null;
					}
					if (_currentWorker != null && !_currentWorker.isAlive()) {
						_currentWorker.start();
					} else if (_currentWorker == null) {
						_currentWorker = jobManager
								.createNewJob(newZkWorkerNodeData);
						if (_currentWorker != null) {
							_currentWorker.start();
						}
					}
					break;
				default:
					break;
				}
			}
		}
	}
}
