package com.melot.data.change.searcher.es;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.melot.data.change.searcher.domain.ESResult;
import com.melot.data.change.searcher.domain.ESResult.ESField;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElasticClient {
	
	
	private TransportClient client;

	private String servers;

	private String clusterName;

	public ElasticClient(String servers, String cluterName) {
		this.servers = servers;
		this.clusterName = cluterName;
	}

	public void init() {
		/* 1.4.4 */
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName)// 指定集群名称
				.put("client.transport.sniff", true)// 启动嗅探功能
				.build();

		client = new TransportClient(settings);

		String[] sers = servers.split(",");
		if (sers.length > 1) {
			client.addTransportAddresses(getESserverAddress(servers));
		} else {
			client.addTransportAddress(getESserverAddress(servers)[0]);
		}
	}

	private TransportAddress[] getESserverAddress(String serveres) {
		String[] ip_ports = serveres.split(",");
		List<TransportAddress> addressList = new ArrayList<TransportAddress>();
		for (String ip_port : ip_ports) {
			String[] pp = ip_port.split(":");
			try {
				addressList.add(new InetSocketTransportAddress(InetAddress
						.getByName(pp[0]), Integer.parseInt(pp[1])));

			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		TransportAddress[] addressArray = new TransportAddress[addressList
				.size()];
		addressList.toArray(addressArray);
		return addressArray;
	}

	public Client getClient() {
		return client;
	}

	/**
	 * 模糊查询方法
	 * 
	 * @param fuzzeyMap
	 *            key: 字段名 value:匹配的表达式
	 */
	public ESResult fuzzyFind(String index, String type,
			Map<String, String> fuzzeyMap, int from, int size) {
		ESResult esResult = new ESResult();

		BoolQueryBuilder requestBuilder = QueryBuilders.boolQuery();
		for (Entry<String, String> entry : fuzzeyMap.entrySet()) {
			String in = entry.getKey();
			// map.keySet()返回的是所有key的值
			String str = fuzzeyMap.get(in);// 得到每个key多对用value的值
			requestBuilder.must(QueryBuilders.matchQuery(in, str));
		}
		SearchResponse request = client.prepareSearch(index)
				.setQuery(QueryBuilders.matchAllQuery())
				.setQuery(requestBuilder)
				.setSearchType(SearchType.QUERY_THEN_FETCH).setFrom(from)
				.setSize(size).get(TimeValue.timeValueMillis(1000));

		long count = request.getHits().getTotalHits();
		esResult.setCount(count);
		if (request.getHits().getTotalHits() > 0) {
			List<ESField> fieldList = new ArrayList<ESField>();
			SearchHit[] searchHits = request.getHits().getHits();
			for (SearchHit searchHist : searchHits) {
				String json = searchHist.getSourceAsString();
				if (json != null && json.length() > 0) {
					JSONObject jsonObject = JSON.parseObject(json);
					Map<String, Object> map = new HashMap<String, Object>();
					for (Entry<String, Object> entry : jsonObject.entrySet()) {
						map.put(entry.getKey(), entry.getValue());
					}
					ESField field = new ESField();
					field.setField(map);
					field.setScore(searchHist.getScore());
					fieldList.add(field);
				}
			}
			esResult.setFieldList(fieldList);
		}

		return esResult;
	}
	
	/**
	 * 创建索引
	 * 
	 * @param index
	 */
	public void create(String index, String type, Map<String, Object> esMap) {
		try {
			IndexResponse indexResponse = client
					// 这个索引库的名称还必须不包含大写字母
					.prepareIndex(index, type).setSource(esMap).execute()
					.actionGet();
			System.out.println(indexResponse.getVersion());
			log.info("------------------>  server's" + index + ", source is : "
					+ esMap.toString() + "  created!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更新数据
	 * 
	 * @param index
	 * @param type
	 * @param id
	 * @param esMap
	 */
	public void update(String index, String type, String id,
			Map<String, Object> esMap) {
		try {
			UpdateResponse updateResponse = client
					.prepareUpdate(index, type, id).setDoc(esMap).execute()
					.actionGet();
			System.out.println(updateResponse.getVersion());
			log.info("------------------>  server's" + index + ", source is : "
					+ esMap.toString() + "  update!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据
	 * 
	 * @param index
	 * @param type
	 */
	public String select(String index, String type,
			BoolQueryBuilder boolQueryBuilder) {
		try {
			SearchResponse request = client
					.prepareSearch(index)
					.setQuery(QueryBuilders.matchAllQuery())
					// 查询所有
					.setQuery(boolQueryBuilder)
					// 查询省份为江苏的
					// .setQuery(QueryBuilders.matchQuery("uquestion",
					// "12599").operator(Operator.AND)) //根据tom分词查询name,默认or
					// .setQuery(QueryBuilders.matchQuery("province",
					// "江苏").operator(Operator.AND)) //根据tom分词查询name,默认or
					// .setQuery(QueryBuilders.multiMatchQuery("tom", "name",
					// "age")) //指定查询的字段
					// .setQuery(QueryBuilders.queryString("name:to* AND age:[0 TO 19]"))
					// //根据条件查询,支持通配符大于等于0小于等于19
					// .setQuery(QueryBuilders.termQuery("name", "tom"))//查询时不分词
					.setSearchType(SearchType.QUERY_THEN_FETCH).setFrom(0)
					.setSize(1)// 分页
					// .addSort("age", SortOrder.DESC)//排序
					.get();
			if (request.getHits().getTotalHits() > 0) {
				SearchHit[] searchHits = request.getHits().getHits();
				for (SearchHit searchHist : searchHits) {
					return searchHist.getId();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//TODO

		return null;

	}
	
	// TODO test
	// public static void main(String[] args) throws InterruptedException {
	// ElasticClient client = new ElasticClient("10.0.17.31:9300",
	// "elasticsearch");
	// client.init();
	// Map<String, String> map = new HashMap<String, String>();
	// map.put("nickname", "吃火cj");
	// map.put("login_name", "qiye");
	// client.fuzzyFind("public", "base_user_info", map, 0, 10);
	//
	// }
}
