package com.melot.data.schedule.impl;

import java.util.Arrays;
import java.util.List;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import com.melot.common.transaction.processor.ActionHistoryProcessor;
import com.melot.common.transaction.processor.HistoryConsumer;
import com.melot.common.transaction.processor.HistoryConsumerConfig;
import com.melot.common.transaction.processor.KKHistoryProcessor;
import com.melot.data.change.api.StreamRelay;
import com.melot.data.change.api.Worker;
import com.melot.data.change.core.config.po.JobConfig;
import com.melot.data.change.core.config.po.RelayType;

@Slf4j
public abstract class AbstractMqConsumerWorker implements Worker,
		ActionHistoryProcessor {

	@Setter
	private HistoryConsumerConfig[] _consumers;

	private KKHistoryProcessor mqMsgConsumer;

	private volatile boolean _isAlive = false;

	private volatile boolean _isDone = false;

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getWorkerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAlive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		if (!_isAlive) {
			if (_consumers != null && _consumers.length > 0) {
				mqMsgConsumer = new KKHistoryProcessor(Arrays.asList(_consumers), this);
			} else {
				log.warn("mq worker start, but no consumer config");
			}
			_isAlive = true;
		}

	}

	@Override
	public void stop() {
		if (_isAlive) {
			List<HistoryConsumer> consumerList = mqMsgConsumer
					.getConsumerList();
			if (consumerList != null && consumerList.size() > 0) {
				for (HistoryConsumer consumer : consumerList) {
					consumer.shutdown();
				}
			}
			_isAlive = false;
		}
		_isDone = true;
	}

	@Override
	public void addAll(List<JobConfig> jobConfigs) {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(JobConfig jobConfig) {
		// TODO Auto-generated method stub

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
	public StreamRelay getRelay(RelayType relayType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRelay(RelayType relayType, StreamRelay relay) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addCosumers(HistoryConsumerConfig[] configs) {
		_consumers = configs;
	}
	
}
