package com.melot.data.change.pg.util;

import javax.sql.DataSource;

import org.postgresql.ds.PGSimpleDataSource;

public class PgJarUtils {

	/**
	 * 
	 * @param set
	 *            URI after loading the class OracleDataSource, and
	 *            instantiating an object
	 * @return
	 * @throws DatabusException
	 */
	public static DataSource createPgDataSource(String uri)
			throws Exception {
		// Create the OracleDataSource used to get DB connection(s)
		PGSimpleDataSource ds = new PGSimpleDataSource();
		ds.setUrl(uri);
		ds.setUser("repl");
		return ds;
	}
}
