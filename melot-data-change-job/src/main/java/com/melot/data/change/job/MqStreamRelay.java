package com.melot.data.change.job;

import java.util.List;
import java.util.Map;

import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.melot.common.transaction.KKHistory;
import com.melot.common.transaction.history.ActionHistory;
import com.melot.data.change.api.ChangeData;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.avro.VersionedSchema;

@Component
public class MqStreamRelay extends AbstractAsyncStreamRelay {

	private static final String DATA_CHANGE = "data_change_";

	@Autowired
	private KKHistory _kkHistory;
	
	@Autowired
	private SchemaRegistryService _schemaRegistryService;

	@Override
	public void stream(ChangeData data) {
		
		int schemaId = data.getSchemaId();
		VersionedSchema versionedSchema = _schemaRegistryService
				.fetchLatestVersionedSchemaBySchemaId(schemaId);
		Map<String, Object> actionDataMap = Maps.newLinkedHashMap();
		String name = versionedSchema.getSchemaBaseName();
//		Iterable<String> it = Splitter.on(".").split(name);
//		String schema = it.iterator().next();
//		String table = it.iterator().next();
		Integer orderParam = data.getOrderParam();
		ActionHistory action;
		if (orderParam == null) {
			action = _kkHistory.newActionHistory(getTopic(name));
		} else {
			final int order = orderParam;
			action = _kkHistory.newActionHistory(getTopic(name),
					new MessageQueueSelector(){
				@Override
				public MessageQueue select(List<MessageQueue> mqs, Message msg,
						Object arg) {
					int index = order % mqs.size();;
					if (index < 0) {
						index = -index;
					}
	    	        return mqs.get(index);
				}
	    	});
		}
		actionDataMap.put("schemaId", schemaId);
		actionDataMap.put("data", data.getData());
		action.addActionDataMap(actionDataMap);
//		action.setMsgTag(table);
		action.complete();
	}

	private String getTopic(String schema) {
		return DATA_CHANGE + schema.replaceAll("\\.", "_");
	}
}
