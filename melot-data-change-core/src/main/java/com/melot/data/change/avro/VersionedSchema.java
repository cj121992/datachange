package com.melot.data.change.avro;

import org.apache.avro.Schema;

public class VersionedSchema {
	private final Schema _schema;
	private final VersionedSchemaId _id;
	private final Integer _schemaId;
	
	public VersionedSchema(VersionedSchemaId id, Schema s, Integer schemaId) {
		_schema = s;
		_id = id;
		_schemaId = schemaId;
	}

	public VersionedSchema(String baseName, short id, Schema s, Integer schemaId) {
		this(new VersionedSchemaId(baseName, id), s, schemaId);
	}

	public int getVersion() {
		return this._id.getVersion();
	}

	public Schema getSchema() {
		return _schema;
	}
	
	public Integer getSchemaId() {
		return _schemaId;
	}
	
	@Override
	public String toString() {
		return "(" + getSchemaBaseName() + "," + getVersion() + "," + _schema
				+ ")";
	}

	/**
	 * @return The source name (table name)
	 */
	public String getSchemaBaseName() {
		return _id.getBaseSchemaName();
	}

	@Override
	public boolean equals(Object o) {
		if (null == o || !(o instanceof VersionedSchema))
			return false;
		VersionedSchema other = (VersionedSchema) o;
		return _id.equals(other._id);
	}

	@Override
	public int hashCode() {
		return _id.hashCode();
	}

	public VersionedSchemaId getId() {
		return _id;
	}

}