package com.melot.data.change.rdb;

import java.io.IOException;

public interface RdbReaderWriter {
	
	void save(String data) throws IOException;
	
	String read();
	
	void clean() throws IOException;
}
