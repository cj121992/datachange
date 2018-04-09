package com.melot.data.schedule.registry;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
@Component
@Slf4j
public class ZkStoreManager {
	private static final String ROOT_PATH = "/datachange/workers";
	private static final String SEPERATOR = "/";
	
	@Value("${data.change.zk.zkAddr}")
	private String zkAddr;
	@Value("${data.change.zk.connect.timeout}")
	private int CONN_TIMEOUT = 10000;
	@Value("${data.change.zk.session.timeout}")
	private int SESS_TIMEOUT = 30000;

	private CuratorZkClient _zkClient; 
	
	private ConcurrentHashMap<String, ZkWorkerStatusChangedListener> registeredWorkers = 
			new ConcurrentHashMap<String, ZkWorkerStatusChangedListener>();
	
	private ConcurrentHashMap<String, WorkerDataChangedWatcher> workerWatcherMap = 
			new ConcurrentHashMap<String, WorkerDataChangedWatcher>();
	
	private WorkerDirectoryChangedWatcher workerDirectoryWatcher = new WorkerDirectoryChangedWatcher();	
	private ConcurrentHashMap<String, ZkWorkerNodeData> workerDataMap = new ConcurrentHashMap<>();
		
	
	@PostConstruct
	public void init(){
		_zkClient = new CuratorZkClient(this.zkAddr, CONN_TIMEOUT, SESS_TIMEOUT);
		_zkClient.addConnLostListener(new CuratorZkClient.ZkConnLostListener() {
			
			@Override
			public void sessionExpired() {
				try {
					initStore();
				} catch (Exception e) {
					log.error("Recover from session expired failed.", e);
				}
			}
		});
		initStore();
	}
	
	public synchronized void recoverRegisteredWorkers()
	{
		if(registeredWorkers != null && registeredWorkers.size() > 0)
		{
			for(String workerName : registeredWorkers.keySet())
			{
				ZkWorkerNodeData workerNodeDataParam = workerDataMap.get(workerName);
				this.registerNewWorker(workerNodeDataParam, registeredWorkers.get(workerName));
			}
		}
	}
	
	/**
	 * 从map根据节点路径获取对应watcher,添加到zk上并从zk获取jobData设置入map
	 */
	public synchronized void initStore(){
		workerDataMap.clear();
		List<String> workers = null;
		try {
			workers = _zkClient.getChildren(ROOT_PATH, workerDirectoryWatcher);
		} catch (Exception e) {
			log.error("Get all workers path failed.", e);
		}
		if(workers != null && workers.size() > 0)
		{
			log.debug("Get and watch wroklist for services: " + Arrays.toString(workers.toArray()));
			for(String workerName : workers)
			{
				String workerPath = ROOT_PATH  + SEPERATOR + workerName;
				String workerData = getWorkerDataWithWatch(workerPath, 
						getWorkerNodeWatcher(workerPath));
				if(StringUtils.isNotBlank(workerData))
				{
					workerDataMap.put(workerPath, JSON.parseObject(workerData, ZkWorkerNodeData.class));
				}
			}
		}
	}
	
	/**
	 * 从map中获取watcher
	 * @param workerPath
	 * @return
	 */
	private WorkerDataChangedWatcher getWorkerNodeWatcher(String workerPath)
	{
		WorkerDataChangedWatcher watcher = workerWatcherMap.get(workerPath);
		if(watcher == null)
		{
			workerWatcherMap.putIfAbsent(workerPath, new WorkerDataChangedWatcher(workerPath));
		}
		watcher = workerWatcherMap.get(workerPath);
		return watcher;
	}
	
	
	private synchronized void handleWorkDataChanged(String workerPath, String workData, CuratorWatcher watcher)
	{
		if(!StringUtils.isEmpty(workerPath))
		{
			ZkWorkerNodeData newWorkerData = JSON.parseObject(workData, ZkWorkerNodeData.class);
			ZkWorkerNodeData oldWorkerData = workerDataMap.get(newWorkerData.getWorkerName());
			log.debug("handleWorkDataChanged newWorkerData: " + workData +
					"oldWorkerData: " + JSON.toJSONString(oldWorkerData));
			workerDataMap.put(workerPath, newWorkerData);
			ZkWorkerStatusChangedListener workerStatusChangedListener = registeredWorkers.get(newWorkerData.getWorkerName());
			if(workerStatusChangedListener != null)
			{
				workerStatusChangedListener.workerStatusChanged(newWorkerData);
			}
		}
	}
	
	public void registerNewWorker(ZkWorkerNodeData workerNodeDataParam, ZkWorkerStatusChangedListener listener){
		this.registeredWorkers.put(workerNodeDataParam.getWorkerName(), listener);
		String workerPath = ROOT_PATH  + SEPERATOR + workerNodeDataParam.getWorkerName();
		try {
			if(_zkClient.checkExist(workerPath)){
				_zkClient.delete(workerPath);
			}
			WorkerDataChangedWatcher workerWatcher = getWorkerNodeWatcher(workerPath);
			_zkClient.createEphemeral(workerPath, JSON.toJSONString(workerNodeDataParam));
			getWorkerDataWithWatch(workerPath,workerWatcher);

			workerDataMap.put(workerPath, workerNodeDataParam);
		} catch (Exception e) {
			log.error("create worker node failed.", e);
		}
	}
	
	public synchronized boolean updateWorkerStatus(ZkWorkerNodeData workerNodeDataParam){
		boolean bRet = false;
		String workerPath = ROOT_PATH  + SEPERATOR + workerNodeDataParam.getWorkerName();
		try {
			_zkClient.setData(workerPath, JSON.toJSONString(workerNodeDataParam));
			bRet = true;
		} catch (Exception e) {
			log.error("create worker node failed.", e);
		}
		return bRet;
	}
	
	public  synchronized List<ZkWorkerNodeData> getAllWorkerNodes(){
		List<ZkWorkerNodeData> workingNodes = Lists.newArrayList(workerDataMap.values());
		return workingNodes;
	}
	
	
	public synchronized ZkWorkerNodeData getWorkerNodeData(String workerName){
		ZkWorkerNodeData retWorkerNodeData = workerDataMap.get(workerName);
		return retWorkerNodeData;
	}
	
	private String getWorkerDataWithWatch(String path, WorkerDataChangedWatcher watcher)
	{
		String workerData = null;
		try {
			if(_zkClient.checkExist(path))
			{
				workerData = _zkClient.getData(path, watcher);
			}
		} catch (Exception e) {
			log.error("Get workerData failed for " + path , e);
		}
		
		if(workerData != null)
		{
			log.debug("Got new workerData for " + path + " :" + workerData);
		}
		return workerData;
	}
	
	class WorkerDataChangedWatcher implements CuratorWatcher
	{
		private final String workerPath;
		
		public WorkerDataChangedWatcher(String path)
		{
			this.workerPath = path;
		}
		
		@Override
		public void process(WatchedEvent event) throws Exception {
			if(event.getType() == Watcher.Event.EventType.NodeDataChanged)
			{
				String workData = getWorkerDataWithWatch(workerPath, this);
				handleWorkDataChanged(workerPath, workData, this);
			}
		}
		
	}
	
	class WorkerDirectoryChangedWatcher implements CuratorWatcher
	{

		@Override
		public void process(WatchedEvent event) throws Exception {
			initStore();
		}
		
	}
	
}
