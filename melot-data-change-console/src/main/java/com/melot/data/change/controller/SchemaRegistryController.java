package com.melot.data.change.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@RequestMapping(value = "/get")
	@ResponseBody
	public String getScheamByCriteria() {
		List<SchemaDTO> ret = schemaDAO.getAllSchemas();
		return JSON.toJSONString(ret);
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public ReturnT<String> addSchema(String schemaJson) {
		int ret = 0;
		SchemaDTO schemaConfig = JSON.parseObject(schemaJson, SchemaDTO.class);
		String name = schemaConfig.getSchema_name();
		short version = schemaConfig.getVersion();
		SchemaDTO schema = schemaDAO.getSchemaByNameVersion(name, version);
		if (schema == null) {
			ret = schemaDAO.insert(schemaConfig.getValue(),
					schemaConfig.getSchema_name(), schemaConfig.getVersion());
		} else {
			// 更新?
		}
		return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
	}

	@RequestMapping(value = "/remove")
	@ResponseBody
	public ReturnT<String> deleteNormConfig(String schemaId) {
		int ret = schemaDAO.delete(Integer.parseInt(schemaId));
		return (ret > 0) ? ReturnT.SUCCESS : ReturnT.FAIL;
	}

}
