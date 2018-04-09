package com.melot.data.change.avro;


public interface SchemaRegistryService {

	/**
	 * Name of metadata schema 'source'.
	 */
	String DEFAULT_METADATA_SCHEMA_SOURCE = "metadata-source";

	/**
	 * Register event schema. Schema can be extracted from event class.
	 * 
	 * @param schema
	 *            the versioned event schema
	 */
	void registerSchema(VersionedSchema schema);

	VersionedSchema fetchLatestVersionedSchemaBySourceName(String sourceName);
	
	VersionedSchema fetchLatestVersionedSchemaBySchemaId(int schemaId);
	
	boolean containsKey(String sourceName);
}
