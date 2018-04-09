package com.melot.data.integration.es;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ElasticOutputTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConcurrent() throws InterruptedException {
		/**
		 * 验证transportClient是否thread safe
		 */
		
		final ElasticOutput output = new ElasticOutput(null, "10.0.17.31:9300", "elasticsearch");
		int i = 0;
		final int n = 100;
		final CountDownLatch latch = new CountDownLatch(10);
		for (; i < 10 ; i++) {
			final int j = i;
			new Thread(new Runnable(){
				@Override
				public void run() {
					int t = 0;
					while (t ++ < n) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("j", j);
						map.put("t", t);
						BoolQueryBuilder requestBuilder = QueryBuilders.boolQuery();
						requestBuilder.must(QueryBuilders.matchQuery("j", j));
						requestBuilder.must(QueryBuilders.matchQuery("t", t));
						output.createIndex("cj" + j, "a" + j, map, requestBuilder);
					}
					latch.countDown();
				}
			}).start();
		}
		latch.await();
	}
	
	@Test
	public void testSelect() {
		final ElasticOutput output = new ElasticOutput(null, "10.0.17.31:9300", "elasticsearch");
		BoolQueryBuilder requestBuilder = QueryBuilders.boolQuery();
		requestBuilder.must(QueryBuilders.matchQuery("user_id", 10001809));
		Map<String, Object> esMap = new HashMap<String, Object>();
		esMap.put("nickname", "富豪275364");
		output.createIndex("public", "base_user_info", esMap, requestBuilder);
	}
	
}
