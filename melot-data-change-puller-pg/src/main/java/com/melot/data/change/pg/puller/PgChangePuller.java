package com.melot.data.change.pg.puller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import lombok.Setter;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.melot.common.melot_utils.StringUtils;
import com.melot.data.change.api.ChangeData;
import com.melot.data.change.api.ChangePuller;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.avro.VersionedSchema;
import com.melot.data.change.avro.util.SchemaHelper;
import com.melot.data.change.core.config.po.JobConfig;
import com.melot.data.change.pg.event.Cell;
import com.melot.data.change.pg.event.DmlEvent;
import com.melot.data.change.pg.event.Event;
import com.melot.data.change.pg.event.TxEvent;
import com.melot.data.change.pg.parser.AntlrBasedParser;
import com.melot.data.change.pg.parser.PgParser;
import com.melot.data.change.pg.pojo.ChangeSetDTO;
import com.melot.data.change.pg.util.PgJarUtils;
import com.melot.data.change.rdb.FileReaderWriter;

public class PgChangePuller implements ChangePuller {

	private final PgParser _parser = new AntlrBasedParser();;

	private final Logger _logger = Logger.getLogger(getClass());

	private DataSource _dataSource;

	private Connection _eventSelectConnection;

	private FileReaderWriter _readerWriter = new FileReaderWriter();

	@Setter
	private String _url;

	@Setter
	private int _generateRange = DEFAULT_RANGE;

	@Setter
	private String _regressionSlot = DEFAULT_REGRESSION_SLOT;

	@Setter
	private SchemaRegistryService _schemaRegistryService;

	private static final String DEFAULT_REGRESSION_SLOT = "regression_slot";

	private static final int DEFAULT_RANGE = 1000;

	private static final String LOGICAL_DECODING_CONSUME = "SELECT * from pg_logical_slot_get_changes(?, NULL, ?, 'include-timestamp', 'on')";

	private static final String LOGICAL_DECODING_PEEK = "SELECT * from pg_logical_slot_peek_changes(?, NULL, ?, 'include-timestamp', 'on')";

	private static final String PG_SLOT_OUTPUT_XID = "xid";

	private static final String PG_SLOT_OUTPUT_LOCATION = "location";

	private static final String PG_SLOT_OUTPUT_DATA = "data";

	@Override
	public void initPuller(JobConfig jobConfig,
			SchemaRegistryService schemaRegistryService) throws Exception {
		HashMap<String, Object> pullerMap = jobConfig.getPullerMap();
		String url = (String) pullerMap.get("url");
		this.set_url(url);
		if (pullerMap.containsKey("generateRange")) {
			this.set_generateRange((Integer) pullerMap.get("generateRange"));
		}
		if (pullerMap.containsKey("regressionSlot")) {
			this.set_regressionSlot((String) pullerMap.get("regressionSlot"));
		}
		_schemaRegistryService = schemaRegistryService;
		_dataSource = PgJarUtils.createPgDataSource(_url);
	}

	@PostConstruct()
	public void init() throws Exception {
	}

	@Override
	public List<ChangeData> readEvent() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int generateSize = 0;
		Collection<ChangeSetDTO> changes = new ArrayList<ChangeSetDTO>();
		try {
			initPg();
			pstmt = _eventSelectConnection
					.prepareStatement(LOGICAL_DECODING_PEEK);
			pstmt.setString(1, _regressionSlot);
			pstmt.setInt(2, _generateRange);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				generateSize++;
				ChangeSetDTO changeset = new ChangeSetDTO();
				changeset.setData(rs.getString(PG_SLOT_OUTPUT_DATA));
				changeset.setLocation(rs.getString(PG_SLOT_OUTPUT_LOCATION));
				changeset.setTransactionId(rs.getLong(PG_SLOT_OUTPUT_XID));
				changes.add(changeset);
			}
		} catch (Exception e) {
			_logger.error("pg puller select event error", e);
		}
		Collection<DmlEvent> events = new ArrayList<DmlEvent>();
		Map<Long, Long> transactionCommitTimes = new HashMap<Long, Long>();
		for (ChangeSetDTO change : changes) {
			Event event = null;
			try {
				event = _parser.parseLogLine(change.getData());
			} catch (Exception e) {
				_logger.error("antlr4 parser unsupport event type, msg : "
						+ change.getData());
				continue;
			}
			event.setTransactionId(change.getTransactionId());
			if (event instanceof DmlEvent) {
				events.add((DmlEvent) event);
			} else if (event instanceof TxEvent) {
				if (event.getCommitTime() > 0) {
					transactionCommitTimes.put(event.getTransactionId(),
							event.getCommitTime());
				}
			}
		}

		// add transaction time to dmlevent
		for (DmlEvent event : events) {
			event.setCommitTime(transactionCommitTimes.get(event
					.getTransactionId()));
		}

		Predicate<DmlEvent> predicate = new Predicate<DmlEvent>() {
			@Override
			public boolean apply(DmlEvent input) {
				return _schemaRegistryService.containsKey(input.getSchemaName()
						+ "." + input.getTableName());
			}
		};
		Collection<DmlEvent> relevantEvents = Collections2.filter(events,
				predicate);

		List<ChangeData> ret = new ArrayList<ChangeData>();
		for (DmlEvent event : relevantEvents) {
			try {
				// int schemaId = _schemaRegistryService
				// .fetchLatestVersionedSchemaBySourceName(
				// event.getSchemaName() + "."
				// + event.getTableName()).getSchemaId();

				ChangeData changeData = serializeDmlEvent(event);
				ret.add(changeData);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				_logger.error(
						"fail to serialize event, event is : "
								+ event.toString(), e);
			}
		}

		if (ret != null && ret.size() > 0) {
			// 写到文件,若宕机则从文件恢复。
			try {
				_readerWriter.save(JSON.toJSONString(ret));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		slotCommit(generateSize);
		_logger.info("this time generater over!!!");
		return ret;
	}

	private ChangeData serializeDmlEvent(DmlEvent dmlEvent) throws IOException {
		String uri = dmlEvent.getSchemaName() + "." + dmlEvent.getTableName();
		VersionedSchema vs = _schemaRegistryService
				.fetchLatestVersionedSchemaBySourceName(uri);
		Schema schema = vs.getSchema();
		GenericRecord gr = new GenericData.Record(schema);
		Map<String, Object> keyMap = generateAvroEvent(gr, dmlEvent, schema);
		Integer orderParam = null;
		if (keyMap != null && keyMap.size() > 0) {
			// 去主键hash值作为唯一判断
			orderParam = keyMap.hashCode();
		}
		String value = SchemaHelper.serializeEvent(gr);
		ChangeData changeData = new ChangeData(value, vs.getSchemaId(),
				orderParam);
		return changeData;
	}

	private Map<String, Object> generateAvroEvent(GenericRecord gr,
			DmlEvent dmlEvent, Schema schema) {
		Map<String, Object> keyValue = new HashMap<String, Object>();
		String primaryKey = SchemaHelper.getMetaField(schema, "pk");
		List<String> keys = null;
		if (StringUtils.isNotEmpty(primaryKey)) {
			keys = Arrays.asList(primaryKey.split(","));
		}
		List<Cell> cellList = null;
		gr.put("op_type", dmlEvent.getType().name());
		if (dmlEvent.getType().equals(DmlEvent.Type.delete)) {
			cellList = dmlEvent.getOldValues();
		} else {
			cellList = dmlEvent.getNewValues();
		}
		for (Cell cell : cellList) {
			// 未在schema定义的字段不予处理。
			if (cell.getTypeValue() != null
					&& gr.getSchema().getField(cell.getName()) != null) {
				gr.put(cell.getName(), cell.getTypeValue());
			}
			if (keys.contains(cell.getName())
					&& gr.getSchema().getField(cell.getName()) != null) {
				keyValue.put(cell.getName(), cell.getTypeValue());
			}
		}
		return keyValue;
	}

	void initPg() throws SQLException {
		if (_eventSelectConnection == null || _eventSelectConnection.isClosed()) {
			resetConnections();
		}
	}

	private void resetConnections() throws SQLException {
		_eventSelectConnection = _dataSource.getConnection();
	}

	/**
	 * 逻辑槽提交
	 * 
	 * @param size
	 */
	private void slotCommit(int size) {
		PreparedStatement pstmt = null;
		// int count = 0;
		try {
			pstmt = _eventSelectConnection
					.prepareStatement(LOGICAL_DECODING_CONSUME);
			pstmt.setString(1, _regressionSlot);
			pstmt.setInt(2, _generateRange);
			pstmt.executeQuery();
			// TODO check
		} catch (Exception e) {
			_logger.error("slotCommit error", e);
		} finally {
			if (_eventSelectConnection != null) {
				try {
					_eventSelectConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
