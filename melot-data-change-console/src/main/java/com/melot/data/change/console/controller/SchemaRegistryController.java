package com.melot.data.change.console.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.alibaba.fastjson.JSON;
import com.melot.data.change.console.core.consistant.ReturnT;
import com.melot.data.change.console.dao.SchemaDAO;
import com.melot.data.change.core.config.po.SchemaDTO;

@Controller
@RequestMapping("/schema")
public class SchemaRegistryController {

	@Autowired
	private SchemaDAO schemaDAO;

	@RequestMapping
	public String index() {
		return "schema/schema.list";
	}

	@RequestMapping(value = "/update")
	@ResponseBody
	public void update(HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam("file") CommonsMultipartFile file) {
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(value = "/list")
	@ResponseBody
	public Map<String, Object> getScheamByCriteria(
			@RequestParam(required = false, defaultValue = "0") int start,
			@RequestParam(required = false, defaultValue = "10") int length,
			HttpServletRequest request) {
		String schemaName = request.getParameter("schemaName");
		List<SchemaDTO> list = schemaDAO.getSchemasByCriteria(start, length,
				schemaName);
		int totalCount = schemaDAO.getSchemasCountByCriteria(schemaName);
		// package result
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("recordsTotal", totalCount); // 总记录数
		maps.put("recordsFiltered", totalCount); // 过滤后的总记录数
		maps.put("data", list); // 分页列表
		return maps;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@ResponseBody
	public ReturnT<String> addSchema(String schema_name, String version,
			String value) {
		int ret = 0;
		SchemaDTO schema = schemaDAO.getSchemaByNameVersion(schema_name,
				Short.valueOf(version));
		if (schema == null) {
			ret = schemaDAO.insert(value, schema_name, Short.valueOf(version));
		} else {
			// 更新?
		}
		return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
	}

	@RequestMapping(value = "/remove", method = RequestMethod.DELETE)
	@ResponseBody
	public ReturnT<String> deleteNormConfig(String schemaId) {
		int ret = schemaDAO.delete(Integer.parseInt(schemaId));
		return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
	}

	@RequestMapping(value = "/get")
	@ResponseBody
	public String getScheamByCriteria() {
		List<SchemaDTO> ret = schemaDAO.getAllSchemas();
		return JSON.toJSONString(ret);
	}

}
