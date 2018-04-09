package com.melot.change.example;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.melot.data.change.searcher.domain.ESResult;
import com.melot.data.change.searcher.es.ElasticClient;

@RestController
@RequestMapping({ "/search" })
@Slf4j
public class UserController {
	
	{
		client = new ElasticClient("10.0.3.157:9300",
				 "elasticsearch");
				 client.init();
	}
	
	private static ElasticClient client;

	@RequestMapping(value = "/es")
	@ResponseBody
	public String user(
			@RequestParam(value = "fuzzyString", required = false, defaultValue = "") String fuzzyString,
			@RequestParam(value = "from", required = false, defaultValue = "0") int from,
			@RequestParam(value = "size", required = false, defaultValue = "10") int size,
			@RequestParam(value = "index", required = false, defaultValue = "public") String index,
			@RequestParam(value = "type", required = false, defaultValue = "base_user_info") String type) {
		
		log.info("aa");
		
		Map<String, String> fuzzyMap = new HashMap<String, String>();
		if (StringUtils.isNotEmpty(fuzzyString)) {
			String str[] = fuzzyString.split(",");
			for (String temp : str) {
				try {
					String t[] = temp.split(":");
					fuzzyMap.put(t[0], t[1]);
				} catch (Exception e) {
				}
				
			}
		}
		
		ESResult result = client.fuzzyFind(index, type, fuzzyMap, from, size);  
		JSONObject ret  = new JSONObject();
		ret.put("totalCount", result.getCount());
		ret.put("result", result.getFieldList());
		return ret.toString();
	}

}