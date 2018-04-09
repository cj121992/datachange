package com.melot.data.change.avro.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.io.JsonEncoder;
import org.apache.avro.util.Utf8;
import org.apache.log4j.Logger;

import com.melot.common.melot_utils.StringUtils;
import com.melot.data.change.core.config.po.DBType;
import com.melot.data.change.core.config.po.FieldInfo;

public class SchemaHelper {

	private static final Logger _logger = Logger.getLogger(SchemaHelper.class);

	// BinaryDecoder is threadunsafe. So use threadlocal to wrap it
	private static final ThreadLocal<JsonDecoder> binDecoder = new ThreadLocal<JsonDecoder>();

	public static String serializeEvent(GenericRecord record)
			throws IOException {
		// Get the md5 for the schema
		// SchemaId schemaId = SchemaId.createWithMd5(changeEntry.getSchema());
		// Serialize the row
		String serializedValue;
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			JsonEncoder encoder = EncoderFactory.get().jsonEncoder(record.getSchema(), bos);
			GenericDatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(
					record.getSchema());
			writer.write(record, encoder);
			encoder.flush();
			serializedValue = bos.toString();
			
		} catch (IOException ex) {
			// TODO
			throw new IOException("Failed to serialize the Avro GenericRecord",
					ex);
		} catch (RuntimeException ex) {
			// Avro likes to throw RuntimeExceptions instead of checked
			// exceptions when serialization fails.
			_logger.error("Exception for record: " + record + " with schema: "
					+ record.getSchema().getFullName());
			throw new RuntimeException(
					"Failed to serialize the Avro GenericRecord", ex);
		} finally {
			if (bos != null) {
				bos.close();
			}
		}
		return serializedValue;
	}

	public static GenericRecord deserializeEvent(String value,
			Schema schema) {
		GenericRecord result = null;
		try {
			binDecoder.set(DecoderFactory.get().jsonDecoder(schema, value));
			GenericDatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(
					schema);
			result = reader.read(null, binDecoder.get());
			return result;
		} catch (Exception ex) // IOException, ArrayIndexOutOfBoundsException,
								// ...
		{
			_logger.error("getGenericRecord Avro error: " + ex.getMessage(), ex);
		}
		return result;
	}
	
	public static Map<String, FieldInfo> generateRecord(Schema schema,
			GenericRecord record) {
		Map<String, FieldInfo> valueMap = new HashMap<String, FieldInfo>();
		List<Field> fields = schema.getFields();
		for (Field field : fields) {
			String column = field.name();
			Object value = record.get(column);
			valueMap.put(column, getJavaValue(value, field, DBType.PG));
		}
		return valueMap;
	}
	
	/**
	 * 获取meta中的某个字段值
	 * @param schema
	 * @param metaFieldName
	 * @return
	 */
	public static final String getMetaField(Schema schema, String metaFieldName) {
		if (schema == null) {
			return null;
		}

		String metaValue = schema.getProp(metaFieldName);
		if (null != metaValue) {
			return metaValue;
		}

		String meta = schema.getProp("meta");
		if (meta == null) {
			return null;
		}

		return getFieldName(meta, metaFieldName);
	}
	
	private static final String getFieldName(String meta, String metaFieldName) {
		String metaValue = null;
		String[] metaSplit = meta.split(";");
		for (String s : metaSplit) {
			int eqIdx = s.indexOf('=');
			if (eqIdx > 0) {
				String itemKey = s.substring(0, eqIdx).trim();
				String itemValue = s.substring(eqIdx + 1).trim();
				if (null == metaValue && metaFieldName.equals(itemKey)) {
					metaValue = itemValue;
				}
			}
		}
		return metaValue;
	}
	
	private static FieldInfo getJavaValue(Object value, Field field, DBType dbType) {
		FieldInfo fieldInfo = new FieldInfo();
		Schema schema = field.schema();
		switch (schema.getTypes().get(0).getType()) {
		case STRING:
			if (value != null) {
				value = (String) ((Utf8) value).toString();
			}
			fieldInfo.setFieldValue(value);
			fieldInfo.setSqlString(decorate(value));
			if (dbType.equals(DBType.PG)) {
				fieldInfo.setFieldJdbcType("text");
			}
			break;
		case INT:
			value = (Integer) value;
			fieldInfo.setFieldValue(value);
			fieldInfo.setSqlString(String.valueOf(value));
			if (dbType.equals(DBType.PG)) {
				fieldInfo.setFieldJdbcType("integer");
			}
			break;
		case LONG:
			if (dbType.equals(DBType.PG)) {
				String meta = field.getProp("meta");
				String type = getFieldName(meta, "dbType");
				if (StringUtils.isNotEmpty(type) && type.equals("timestamp")) {
					fieldInfo.setFieldJdbcType("timestamp without time zone");
					if (value != null) {
						value = new Timestamp((Long) value);
					}
					fieldInfo.setSqlString(decorate(value));
				} else {
					fieldInfo.setFieldJdbcType("bigint");
					value = (Long) value;
					fieldInfo.setSqlString(String.valueOf(value));
				}
			}
			fieldInfo.setFieldValue(value);
			break;
		case FLOAT:
			value = (Float) value;
			fieldInfo.setFieldValue(value);
			fieldInfo.setSqlString(String.valueOf(value));
			if (dbType.equals(DBType.PG)) {
				fieldInfo.setFieldJdbcType("float");
			}
			break;
		case DOUBLE:
			value = (Double) value;
			fieldInfo.setFieldValue(value);
			fieldInfo.setSqlString(String.valueOf(value));
			if (dbType.equals(DBType.PG)) {
				fieldInfo.setFieldJdbcType("double");
			}
			break;
		case BOOLEAN:
			value = (Boolean) value;
			fieldInfo.setFieldValue(value);
			fieldInfo.setSqlString(String.valueOf(value));
			if (dbType.equals(DBType.PG)) {
				fieldInfo.setFieldJdbcType("boolean");
			}
			break;
		case BYTES:
			// TODO 有待测试
			value = (ByteBuffer) value;
			fieldInfo.setFieldValue(value);
			if (dbType.equals(DBType.PG)) {
				fieldInfo.setFieldJdbcType("bytea");
			}
			break;
		default:
			fieldInfo = null;
			break;
		}
		return fieldInfo;
	}

	private static String decorate(Object value) {
		if (null == value) {
			return "null";
		}
		return "'" + value + "'";
	}
	
}
