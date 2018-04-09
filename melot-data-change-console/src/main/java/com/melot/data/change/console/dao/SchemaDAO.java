package com.melot.data.change.console.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import com.melot.data.change.core.config.po.SchemaDTO;

@Component
public interface SchemaDAO {
	
	@Select("select * from registry_schema where schema_name like CONCAT(CONCAT('%', #{schemaName,jdbcType=VARCHAR}), '%') order by schema_id limit   #{pagesize} offset  #{offset} ")
	List<SchemaDTO> getSchemasByCriteria(@Param("offset") int offset,@Param("pagesize") int pagesize,@Param("schemaName") String schemaName);
	
	@Select("select count(1) from registry_schema where schema_name like CONCAT(CONCAT('%', #{schemaName,jdbcType=VARCHAR}), '%')")
	int getSchemasCountByCriteria(@Param("schemaName") String schemaName);
	
	@Select("select * from registry_schema")
	List<SchemaDTO> getAllSchemas();
	
	@Select("select * from registry_schema where schema_name = #{schemaName} and version = #{version}")
	SchemaDTO getSchemaByNameVersion(@Param("schemaName") String schemaName, @Param("version") short version);
	
	@Insert("INSERT INTO registry_schema(value, schema_name, version) VALUES(#{value}, #{schemaName}, #{version})")
	int insert(@Param("value") String value, @Param("schemaName") String schemaName, @Param("version") short version);
	
	@Delete("delete from registry_schema where schema_id = #{schemaId}")
	int delete(@Param("schemaId") int schemaId);

	
}
