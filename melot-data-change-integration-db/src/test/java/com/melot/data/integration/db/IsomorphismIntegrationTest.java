//package com.melot.data.integration.db;
//
//import java.util.List;
//
//import org.junit.Before;
//import org.junit.Test;
//
//import com.alibaba.fastjson.JSON;
//import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonAnyFormatVisitor;
//import com.melot.data.change.api.ChangeData;
//import com.melot.data.change.avro.VersionedSchema;
//import com.melot.data.change.avro.impl.SchemaRefresher;
//import com.melot.data.change.avro.impl.VersionSchemaRegistryService;
//import com.melot.data.integration.db.util.DatasourceUtils;
//
//public class IsomorphismIntegrationTest {
//
//	private IsomorphismIntegration integration;
//
//	@Before
//	public void setUp() throws Exception {
//		VersionSchemaRegistryService schemaRegistryService = new VersionSchemaRegistryService();
//		SchemaRefresher refresher = new SchemaRefresher(
//				"http://localhost:10021");
//		List<VersionedSchema> schemas = refresher.refreshSchema();
//		for (VersionedSchema schema : schemas) {
//			schemaRegistryService.registerSchema(schema);
//		}
//
//		integration = new IsomorphismIntegration();
//		integration.set_dataSource(DatasourceUtils.createDataSource(
//				"jdbc:postgresql://10.0.3.157:5432/test", "postgres",
//				"postgres"));
//		integration.set_schemaRegistryService(schemaRegistryService);
//		integration.set_user("postgres");
//	}
//
//	@Test
//	public void testUpdate() {
//		ChangeData data = new ChangeData();
//		byte[] b = { 0, -96, -100, 1, 0, -76, -14, -96, 1, 0, -38, -53, -125,
//				-80, -29, 81, 0, 12, 117, 112, 100, 97, 116, 101 };
//		data.setData(b);
//		data.setSchemaId(1);
//		integration.integration(data);
//	}
//
//	@Test
//	public void testInsert() {
//		ChangeData data = new ChangeData();
//		byte[] b = { 0, -52, -99, 1, 0, -64, -102, 12, 2, 0, 12, 105, 110, 115,
//				101, 114, 116 };
//		data.setData(b);
//		data.setSchemaId(1);
//		integration.integration(data);
//	}
//
//	@Test
//	public void testDelete() {
//		ChangeData data = new ChangeData();
//		byte[] b = { 0, -52, -99, 1, 0, -64, -102, 12, 2, 0, 12, 100, 101, 108,
//				101, 116, 101 };
//		data.setSchemaId(1);
//		data.setData(b);
//		integration.integration(data);
//	}
//
//	@Test
//	public void testDecode() {
//		ChangeData data = new ChangeData();
//		byte[] b = { 0, -96, -100, 1, 0, -76, -14, -96, 1, 0, -38, -53, -125,
//				-80, -29, 81, 0, 12, 117, 112, 100, 97, 116, 101 };
//		data.setSchemaId(1);
//		data.setData(b);
//		String a = JSON.toJSONString(data);
//		System.out.println(a);
//		data = JSON.parseObject(a, ChangeData.class);
//		System.out.println(data);
//	}
//
//}
