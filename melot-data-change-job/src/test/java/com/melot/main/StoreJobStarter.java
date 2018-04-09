package com.melot.main;

import java.util.HashMap;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.melot.common.transaction.KKHistory;
import com.melot.data.change.api.ChangeData;
import com.melot.data.change.api.ChangePuller;
import com.melot.data.change.api.StreamRelay;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.avro.impl.VersionSchemaRegistryService;
import com.melot.data.change.core.config.po.JobConfig;
import com.melot.data.change.pg.puller.PgChangePuller;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
		MongoAutoConfiguration.class })
@ComponentScan({ "com.melot" })
@EnableAutoConfiguration
public class StoreJobStarter {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(
				StoreJobStarter.class, args);
		ChangePuller puller = new PgChangePuller();
		JobConfig jobConfig = new JobConfig();
		jobConfig.setPullerClassName("com.melot.data.change.pg.puller.PgChangePuller");
		HashMap<String, Object> pullerMap = new HashMap<String, Object>();
		pullerMap.put("generateRange", 1000);
		pullerMap.put("regressionSlot", "regression_slot");
		pullerMap.put("url", "jdbc:postgresql://pg44.kktv2.com:5432/appstable");
		jobConfig.setPullerMap(pullerMap);
		Object t = context.getBean(KKHistory.class);
		puller.initPuller(jobConfig, (SchemaRegistryService) context.getBean(VersionSchemaRegistryService.class));
		StreamRelay streamRelay = (StreamRelay) context
				.getBean("mqStreamRelay");
		List<ChangeData> changeStream = puller.readEvent();
		streamRelay.streamEvent(changeStream);
	}
}