package com.melot.data.change.job;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.melot.data.change.api.ChangeData;
import com.melot.data.change.api.ChangePuller;
import com.melot.data.change.api.StreamRelay;
import com.melot.data.change.api.Worker;
import com.melot.data.change.core.config.po.JobConfig;

@Slf4j
public class PullerJob implements Job {

	private ChangePuller _puller;

	private StreamRelay _streamRelay;

	// 反射用
	public PullerJob() {
	}

	public PullerJob(ChangePuller puller, StreamRelay streamRelay) {
		_puller = puller;
		_streamRelay = streamRelay;
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		beforePull(jobDataMap);
		List<ChangeData> changeStream = _puller.readEvent();
		_streamRelay.streamEvent(changeStream);
	}

	private void beforePull(JobDataMap jobDataMap) {
		JobConfig jobConfig = (JobConfig) jobDataMap.get("jobConfig");
		Worker worker = (Worker) jobDataMap.get("worker");
		if (jobConfig.isAutoUpdate()
				|| ((_puller == null) && (_streamRelay == null))) {
			String pullerClass = jobConfig.getPullerClassName();
			try {
				_puller = (ChangePuller) Class.forName(pullerClass)
						.newInstance();
				_puller.initPuller(jobConfig, worker.getSchemaRegistry());
				_streamRelay = worker.getRelay(jobConfig.getRelayType());
			} catch (Exception e) {
				log.error("invoke new class instance error, className:"
						+ pullerClass, e);
			}
		}
	}
}
