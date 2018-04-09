package com.melot.data.change.avro.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.avro.VersionedSchema;
import com.melot.data.change.avro.VersionedSchemaSet;

@Component
public class VersionSchemaRegistryService implements SchemaRegistryService {

	@Autowired
	private SchemaRefresher _schemaRefresher;

	private final Logger _logger = Logger.getLogger(getClass());

	private VersionedSchemaSet _versionedSchemaSet = new VersionedSchemaSet();

	private static final long INITIAL_DELAY = 0;

	private static final long UPDATE_PERIOD = 60;

	private ScheduledExecutorService _executor = Executors
			.newSingleThreadScheduledExecutor(new ThreadFactory() {
				private AtomicInteger idx = new AtomicInteger(0);

				@Override
				public Thread newThread(Runnable r) {
					String groupName = "SchemaRefresher_";
					Thread newThread = new Thread(r);
					newThread.setDaemon(true);
					newThread.setName(groupName + idx.getAndAdd(1));
					return newThread;
				}
			});

	@Override
	public void registerSchema(VersionedSchema schema) {
		_versionedSchemaSet.add(schema);
	}

	public void updateSchema() {
		// TODO
	}

	@Override
	public VersionedSchema fetchLatestVersionedSchemaBySourceName(
			String sourceName) {
		return _versionedSchemaSet.getLatestVersionByName(sourceName);
	}

	@Override
	public VersionedSchema fetchLatestVersionedSchemaBySchemaId(int schemaId) {
		return _versionedSchemaSet.getById(schemaId);
	}

	@PostConstruct
	private void init() {

		_executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					List<VersionedSchema> schemaList = _schemaRefresher
							.refreshSchema();
					for (VersionedSchema versionedSchema : schemaList) {
						_versionedSchemaSet.add(versionedSchema);
					}
				} catch (Exception e) {
					_logger.error("add new schema for failed", e);
				}
			}

		}, INITIAL_DELAY, UPDATE_PERIOD, TimeUnit.SECONDS);
	}

	@Override
	public boolean containsKey(String sourceName) {
		return _versionedSchemaSet.getAllVersionsByName(sourceName) != null;
	}

}
