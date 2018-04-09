package com.melot.data.schedule.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.melot.common.transaction.processor.HistoryConsumerConfig;
import com.melot.data.change.api.Worker;
import com.melot.data.change.core.config.po.JobConfig;

@Slf4j
public abstract class AbstractQuartzWorker implements Worker {

	private Scheduler _jobScheduler;
	private Map<JobDetail, Trigger> _jobTriggerMap = new ConcurrentHashMap<JobDetail, Trigger>();
	
	@Setter
	private List<JobConfig> _jobConfigs = new ArrayList<JobConfig>();
	
	private volatile boolean _isAlive = false;

	private volatile boolean _isDone = false;

	@Override
	public void init() {
		
		prepare();
		// TODO 5?
		Properties props = new Properties();
		props.put("org.quartz.threadPool.class",
				"org.quartz.simpl.SimpleThreadPool");
		props.put("org.quartz.threadPool.threadCount", "5");
		props.put("org.quartz.threadPool.threadPriority", "5");
		props.put(
				"org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread",
				"true");

		try {
			SchedulerFactory schedulerFactory = new StdSchedulerFactory(props);
			_jobScheduler = schedulerFactory.getScheduler();

			if (_jobConfigs != null && _jobConfigs.size() > 0) {
				for (JobConfig jobConfig : _jobConfigs) {
					JobDataMap jobDataMap = new JobDataMap();
					jobDataMap.put("jobConfig", jobConfig);
					jobDataMap.put("worker", this);
					JobDetail jobDetail = getJobDetail(jobConfig, jobDataMap);
					Trigger trigger = getTrigger(jobConfig);
					_jobTriggerMap.put(jobDetail, trigger);
				}
			}
		} catch (SchedulerException e) {
			log.error("Create SchedulerFactory failed.", e);
		}

	}

	public abstract void prepare();

	public abstract JobDetail getJobDetail(JobConfig jobConfig,
			JobDataMap jobDataMap);

	private Trigger getTrigger(JobConfig jobConfig) {
		// TODO 增加simpleTrigger
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.withIdentity(jobConfig.getJobName(), jobConfig.getGroup())
				.withSchedule(
						CronScheduleBuilder.cronSchedule(jobConfig
								.getTriggerConfig().getCron())).build();
		return trigger;
	}

	@Override
	public String getWorkerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDone() {
		return _isDone;
	}

	@Override
	public boolean isAlive() {
		return _isAlive;
	}

	@Override
	public void start() {
		if (!_isAlive) {
			try {
				if (_jobTriggerMap != null && _jobTriggerMap.size() > 0) {
					for (Map.Entry<JobDetail, Trigger> entry : _jobTriggerMap
							.entrySet()) {
						_jobScheduler.scheduleJob(entry.getKey(),
								entry.getValue());
					}
					_jobScheduler.start();
					_isAlive = true;
				}
			} catch (SchedulerException e) {
				log.error("Start update job scheduler failed.", e);
			}
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addAll(List<JobConfig> jobConfigs) {
		_jobConfigs.addAll(jobConfigs);
	}
	
	@Override
	public void add(JobConfig jobConfig) {
		_jobConfigs.add(jobConfig);
	}
	
	@Override
	public void remove(JobConfig jobConfig) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startOne(JobConfig jobConfig) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopOne(JobConfig jobConfig) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addCosumers(HistoryConsumerConfig[] configs) {
	}
}
