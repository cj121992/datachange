package com.melot.data.integration.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.avro.Schema;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.util.StringUtils;

import com.melot.data.change.api.OutputPlugin;
import com.melot.data.change.avro.SchemaRegistryService;
import com.melot.data.change.avro.util.SchemaHelper;
import com.melot.data.change.core.config.po.FieldInfo;

@Slf4j
public class DefaultDBOutput implements OutputPlugin {
	
	@Setter
	private DataSource _dataSource;
	
	private SchemaRegistryService _schemaRegistryService;
	
	private String _user;
	
	private static final String PG_CHECK_TB_EXIST_SQL = "SELECT count(*) FROM pg_tables where tablename = ?";
	
	public DefaultDBOutput(DataSource datasource, SchemaRegistryService schemaRegistryService, String user) {
		_dataSource = datasource;
		_schemaRegistryService = schemaRegistryService;
		_user = user;
	}
	
	@Override
	public boolean output(Map<String, FieldInfo> map, int schemaId) {
		Connection connection;
		if (_dataSource == null) {
			log.error("this datasource is null, please check your config !!!");
			return false;
		} else {
			try {
				connection = _dataSource.getConnection();
			} catch (SQLException e) {
				log.error("fail to get connection", e);
				return false;
			}
		}
		
		String tableName = _schemaRegistryService
				.fetchLatestVersionedSchemaBySchemaId(schemaId)
				.getSchemaBaseName();
		String singleTableName = getTBName(tableName);
		Schema schema = _schemaRegistryService
				.fetchLatestVersionedSchemaBySchemaId(schemaId).getSchema();
		String primaryKey = SchemaHelper.getMetaField(schema, "pk");
		if (_dataSource instanceof PGPoolingDataSource) {
			boolean isTableExist = checkExist(singleTableName, connection);
			int length = map.size();
			// 表不存则创建表
			if (!isTableExist) {
				String sql = "CREATE TABLE " + tableName + "(";
				int i = 0;
				if (map != null && map.size() > 0) {
					for (Entry<String, FieldInfo> entry : map.entrySet()) {
						if (entry.getKey().equals("op_type")) {
							i++;
							continue;
						}
						String columnName = entry.getKey();
						sql += columnName;
						FieldInfo fieldInfo = entry.getValue();
						String jdbcType = fieldInfo.getFieldJdbcType();
						sql += " " + jdbcType;
						if (i++ < length - 1) {
							sql += ",";
						}
					}
				}

				// 添加主键语句
				if (!StringUtils.isEmpty(primaryKey)) {
					sql += ", CONSTRAINT " + singleTableName
							+ "_pkey PRIMARY KEY (" + primaryKey + ")";
				}

				// 拼凑完 建表语句 设置默认字符集
				sql += ") WITH (OIDS=FALSE); ALTER TABLE " + tableName
						+ " OWNER TO " + _user + ";";
				log.info("建表语句是：" + sql);
				PreparedStatement ps = null;
				try {
					ps = connection.prepareStatement(sql);
					ps.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (ps != null) {
							ps.close();
						}
					} catch (SQLException e) {
					}
				}
			}

			// 操作变更
			if (map.containsKey("op_type")) {
				String op_type = (String) map.get("op_type").getFieldValue();
				String sql = "";
				// TODO
				if (op_type.equals("delete")) {
					sql += "DELETE FROM " + tableName + " where ";
					String[] keys = primaryKey.split(",");
					int j = 0;
					for (String key : keys) {
						sql += key + " = " + map.get(key).getFieldValue();
						if (j++ < keys.length - 1) {
							sql += " and ";
						}
					}
				} else if (op_type.equals("update")) {
					sql += "UPDATE " + tableName + " set  ";
					int i = 0;
					for (Entry<String, FieldInfo> entry : map.entrySet()) {
						if (entry.getKey().equals("op_type")) {
							i++;
							continue;
						}
						String columnName = entry.getKey();
						sql += columnName;
						FieldInfo fieldInfo = entry.getValue();
						
						sql += " = " + fieldInfo.getSqlString();
						if (i++ < length - 1) {
							sql += ",";
						}
					}
					
					// 处理联合主键
					sql += " where ";
					String[] keys = primaryKey.split(",");
					int j = 0;
					for (String key : keys) {
						sql += key + " = " + map.get(key).getSqlString();
						if (j++ < keys.length - 1) {
							sql += " and ";
						}
					}
				} else if (op_type.equals("insert")) {
					sql += "INSERT into " + tableName + "(";
					String valueSql = " values(";
					int i = 0;
					for (Entry<String, FieldInfo> entry : map.entrySet()) {
						if (entry.getKey().equals("op_type")) {
							i++;
							continue;
						}
						String columnName = entry.getKey();
						sql += columnName;
						FieldInfo fieldInfo = entry.getValue();
						valueSql += fieldInfo.getSqlString();
						if (i++ < length - 1) {
							sql += ", ";
							valueSql += ", ";
						} else {
							sql += ")";
							valueSql += ")";
						}
					}
					sql += valueSql;
				} else {
					log.error("upsupport sql op_type");
				}

				log.info("操作语句是：" + sql);
				PreparedStatement ps = null;
				try {
					ps = connection.prepareStatement(sql);
					ps.executeUpdate();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					try {
						if (ps != null) {
							ps.close();
						}
					} catch (SQLException e) {
					}
				}
			}

		}

		return true;
	}
	
	private String getTBName(String schemaBaseName) {
		return schemaBaseName.substring(schemaBaseName.indexOf(".") + 1);
	}

	private boolean checkExist(String tbName, Connection connection) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection
					.prepareStatement(PG_CHECK_TB_EXIST_SQL);
			pstmt.setString(1, tbName);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				int count = rs.getInt("count");
				return count > 0;
			}
		} catch (Exception e) {
			log.error("pg puller select event error", e);
		}
		return false;
	}

//	void initDB() throws SQLException {
//		if (_eventSelectConnection == null || _eventSelectConnection.isClosed()) {
//			resetConnections();
//		}
//	}
//
//	void resetConnections() throws SQLException {
//		_eventSelectConnection = _dataSource.getConnection();
//	}
	
}
