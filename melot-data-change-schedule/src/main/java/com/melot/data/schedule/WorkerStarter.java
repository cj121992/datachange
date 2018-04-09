package com.melot.data.schedule;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import com.melot.main.PullerWorkerStarter;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, MongoAutoConfiguration.class},
scanBasePackages={"com.melot.data"})
public class WorkerStarter {

	public static void main(String[] args)
	{
		ConfigurableApplicationContext context = SpringApplication.run(
				PullerWorkerStarter.class, args);
		DeamonWorker deamonWorker = context.getBean(DeamonWorker.class);
		deamonWorker.startWorker();
	}
}
