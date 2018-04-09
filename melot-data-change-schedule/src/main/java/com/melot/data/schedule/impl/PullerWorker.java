package com.melot.data.schedule.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import com.melot.data.change.api.DataIntegration;
import com.melot.data.change.api.StreamRelay;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.core.config.po.JobConfig;
import com.melot.data.change.core.config.po.RelayType;
import com.melot.data.change.job.PullerJob;

public class PullerWorker extends AbstractQuartzWorker {
	
	@Getter
	private Map<RelayType, StreamRelay> _relayMap = new HashMap<RelayType,StreamRelay>();
	
	@Setter
	@Getter
	private SchemaRegistryService _schemaRegistryService;
	
	@Override
	public JobDetail getJobDetail(JobConfig jobConfig, JobDataMap jobDataMap) {
		return JobBuilder.newJob(PullerJob.class)
				.withIdentity(jobConfig.getJobName(), jobConfig.getGroup())
				.setJobData(jobDataMap).build();
	}

	@Override
	public void prepare() {
	}

	@Override
	public StreamRelay getRelay(RelayType relayType) {
		return _relayMap.get(relayType);
	}

	@Override
	public void addRelay(RelayType relayType, StreamRelay relay) {
		_relayMap.put(relayType, relay);
	}

	@Override
	public void addSchemaRegistry(SchemaRegistryService schemaRegistryService) {
		_schemaRegistryService = schemaRegistryService;
	}

	@Override
	public SchemaRegistryService getSchemaRegistry() {
		return _schemaRegistryService;
	}

	@Override
	public void addDataIntegration(DataIntegration dataIntegration) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<DataIntegration> getDataIntegration() {
		return null;
	}

}
