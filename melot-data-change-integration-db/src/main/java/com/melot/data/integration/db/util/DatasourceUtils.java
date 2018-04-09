package com.melot.data.integration.db.util;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import org.postgresql.ds.PGPoolingDataSource;

@Slf4j
public class DatasourceUtils {

	public static DataSource createDataSource(String uri, String user,
			String pwd) throws Exception {
		if (uri.startsWith("jdbc:oracle:")) {
			//TODO
			
		} else if (uri.startsWith("jdbc:postgresql:")) {
			PGPoolingDataSource ds = new PGPoolingDataSource();
			ds.setUrl(uri);
			ds.setUser(user);
			ds.setPassword(pwd);
			log.info("create data for uri : " + uri + ", user : " + user
					+ ", pwd : " + pwd);
			return ds;
		} else if (uri.startsWith("mysql:")) {
			//TODO
		
		} else {
			log.error("");
		}
		return null;

	}

}
