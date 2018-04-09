package com.melot.data.change.console;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;


@SpringBootApplication(exclude = { MongoAutoConfiguration.class,
		DataSourceAutoConfiguration.class }, scanBasePackages = { "com.melot.data.change.console" })
@MapperScan(basePackages = { "com.melot.data.change.console.dao" }, sqlSessionFactoryRef = "sqlSessionFactory_appstableMysqlDs")
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
}