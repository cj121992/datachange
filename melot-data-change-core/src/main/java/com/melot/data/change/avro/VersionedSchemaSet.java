package com.melot.data.change.avro;

/*
 *
 * Copyright 2013 LinkedIn Corp. All rights reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.avro.Schema;

public class VersionedSchemaSet {

	private final ReadWriteLock _lock;
	// mappings for payload schema
	private final Map<Integer, VersionedSchema> _idToSchema;
	private final Map<String, SortedMap<VersionedSchemaId, VersionedSchema>> _nameToSchemas;

	public VersionedSchemaSet() {
		this(false);
	}

	/**
	 * @param rehash
	 *            apply the MD5 hash to re-parsed schemas as a guard against
	 *            schemas that change order of fields in the schema
	 */
	public VersionedSchemaSet(boolean rehash) {
		this._lock = new ReentrantReadWriteLock(true);
		this._idToSchema = new HashMap<Integer, VersionedSchema>();
		this._nameToSchemas = new HashMap<String, SortedMap<VersionedSchemaId, VersionedSchema>>();
	}

	/**
	 * 
	 * @return size of unique payload available
	 */
	public int size() {
		int sz = 0;
		for (Map.Entry<String, SortedMap<VersionedSchemaId, VersionedSchema>> e : _nameToSchemas
				.entrySet()) {
			sz += e.getValue().size();
		}
		return sz;
	}

	/**
	 * 
	 * @param id
	 *            : SchemaId ; 128 bit byte array
	 * @return true if a schema with a given SchemaId exists
	 */
	public boolean has(Integer id) {
		Lock readLock = _lock.readLock();
		readLock.lock();
		try {
			return _idToSchema.containsKey(id);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 
	 * @param id
	 *            ; SchemaId
	 * @return VersionedSchema (Payload Avro schema, version, sourceName ) for
	 *         given SchemaId or null if none exists
	 */

	public VersionedSchema getById(Integer id) {
		Lock readLock = _lock.readLock();
		readLock.lock();
		try {
			return _idToSchema.get(id);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 
	 * @param schemaBaseName
	 *            : it's the sourceName (e.g. com.linkedin.example.Person)
	 * @return VersionedSchema (Payload Avro schema, version, sourcename) or
	 *         null for a given sourceName
	 */

	public VersionedSchema getLatestVersionByName(String schemaBaseName) {
		Lock readLock = _lock.readLock();
		readLock.lock();
		try {
			SortedMap<VersionedSchemaId, VersionedSchema> versions = _nameToSchemas
					.get(schemaBaseName);
			if (versions == null || versions.size() == 0)
				return null;
			else
				return versions.get(versions.lastKey());
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Returns all schema versions for a given source name. Neither
	 * VersionedSchemaId nor VersionedSchema have the SchemaId in them so if
	 * SchemaId is needed, use the getAllVersionsWithSchemaId() API.
	 * 
	 * @param schemaBaseName
	 *            : it's the sourceName (e.g. com.linkedin.example.Person)
	 * @return all payload VersionedSchema objects sorted by version number
	 */
	public SortedMap<VersionedSchemaId, VersionedSchema> getAllVersionsByName(
			String schemaBaseName) {
		Lock readLock = _lock.readLock();
		readLock.lock();
		try {
			return _nameToSchemas.get(schemaBaseName);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Return all schema versions for a given source as a map of their SchemaId
	 * and VersionedSchema.
	 * 
	 * @param sourceName
	 *            name of the source (table).
	 */
	public Map<Integer, VersionedSchema> getAllVersionsWithSchemaId(
			String sourceName) {
		Map<Integer, VersionedSchema> schemaMap = new HashMap<Integer, VersionedSchema>(
				4);
		Lock readLock = _lock.readLock();
		readLock.lock();
		try {
			for (Integer id : _idToSchema.keySet()) {
				schemaMap.put(id, _idToSchema.get(id));
			}
			return schemaMap;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 
	 * @param baseName
	 *            : sourceName; e.g. com.linkedin.example.Person
	 * @param version
	 *            : version number
	 * @return - return VersionedSchema object for given sourceName and version
	 *         or null otherwise
	 */
	public VersionedSchema getSchemaByNameVersion(String baseName, short version) {
		VersionedSchemaId lookupKey = new VersionedSchemaId(baseName, version);
		return getSchema(lookupKey);
	}

	/**
	 * 
	 * @param versionedSchemaId
	 *            : pair of sourceName, version
	 * @return VersionedSchemaObject: that has payload Avro schema
	 */

	public VersionedSchema getSchema(VersionedSchemaId versionedSchemaId) {
		Lock readLock = _lock.readLock();
		readLock.lock();
		try {
			SortedMap<VersionedSchemaId, VersionedSchema> versions = getAllVersionsByName(versionedSchemaId
					.getBaseSchemaName());
			VersionedSchema vs = null != versions ? versions
					.get(versionedSchemaId) : null;
			return vs;
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Adds a schema with a given name and version if it does not already exist
	 * 
	 * @return true if the schema was added, false if a schema already exists
	 */
	public boolean add(String name, short version, String schemaStr) {
		return add(name, version, null, schemaStr);
	}

	/**
	 * Adds a schema with a given name , version and schemaId if it does not
	 * already exist. If keepOrigStr is set to true, then the original schema
	 * string is preserved in the VersionedSchema object.
	 * 
	 * @return true if the schema was added, false if a schema already exists
	 */
	public boolean add(String name, short version, Integer id,
			String schemaStr, boolean override) {
		if (id == null) {
			return false;
		}
		Lock writeLock = _lock.writeLock();
		writeLock.lock();
		try {
			// first check if the schema is already there
			if (!override) {
				if (null != getSchemaByNameVersion(name, version))
					return false; // schema is already there
			}
			Schema avroSchema = Schema.parse(schemaStr);
			VersionedSchema schema = new VersionedSchema(name, version,
					avroSchema, id);
			addSchemaInternal(schema, id);
			return true;
		} finally {
			writeLock.unlock();
		}
	}

	public boolean add(String name, short version, Integer id, String schemaStr) {
		return add(name, version, id, schemaStr, true);
	}

	/**
	 * Adds a versioned schema if it does not already exist
	 * 
	 * @return true if the schema was added, false if a schema already exists
	 */
	public boolean add(VersionedSchema schema) {
		Lock writeLock = _lock.writeLock();
		writeLock.lock();
		try {
			// first check if the schema is already there
			if (null != getSchema(schema.getId()))
				return false; // schema is already there
			addSchemaInternal(schema, schema.getSchemaId());
			return true;
		} finally {
			writeLock.unlock();
		}
	}

	/** Assumes there is already a write lock */
	private void addSchemaInternal(VersionedSchema schema, Integer id) {
		SortedMap<VersionedSchemaId, VersionedSchema> versions = _nameToSchemas
				.get(schema.getSchemaBaseName());
		if (versions == null) {
			versions = new TreeMap<VersionedSchemaId, VersionedSchema>(
					new Comparator<VersionedSchemaId>() {
						@Override
						public int compare(VersionedSchemaId s1,
								VersionedSchemaId s2) {
							return s1.getVersion() - s2.getVersion();
						}
					});
			_nameToSchemas.put(schema.getSchemaBaseName(), versions);
		}
		// 根据版本号添加
		versions.put(schema.getId(), schema);
		_idToSchema.put(id, schema);
	}

	@Override
	public String toString() {
		Lock readLock = _lock.readLock();
		readLock.lock();
		try {
			StringBuilder builder = new StringBuilder("SchemaSet(");
			for (Map.Entry<String, SortedMap<VersionedSchemaId, VersionedSchema>> entry : _nameToSchemas
					.entrySet()) {
				builder.append(entry.getKey());
				builder.append(" -> ");
				builder.append(entry.getValue());
				builder.append(", ");
			}
			builder.append(")");
			return builder.toString();
		} finally {
			readLock.unlock();
		}
	}

	public Set<String> getSchemaBaseNames() {
		return _nameToSchemas.keySet();
	}

	/**
	 * Not for external use. For testing ONLY
	 */
	public void clear() {
		_nameToSchemas.clear();
		_idToSchema.clear();
	}

	/**
	 * @return the idToSchema
	 */
	public Map<Integer, VersionedSchema> getIdToSchema() {
		return _idToSchema;
	}

	/**
	 * For testing
	 */
	Map<String, SortedMap<VersionedSchemaId, VersionedSchema>> getNameToSchemas() {
		return _nameToSchemas;
	}

}
