package com.melot.data.change.rdb;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileReaderWriter implements RdbReaderWriter {
	
	public static void main(String[] args) throws IOException {
		FileReaderWriter rw = new FileReaderWriter();
		rw.clean();
	}
	
	@Override
	public void save(String data) throws IOException {
		String path = System.getProperty("user.dir");
		System.out.println(path);
		File dir = new File("rdb");
		if (!dir.exists() && !dir.mkdirs()) {
			throw new IOException("unable to create SCN file parent:"
					+ dir.getAbsolutePath());
		}
		
		
		File file = new File(dir + "/data");

	    if (!file.createNewFile())
	    {
	    }
		
		FileWriter writer = new FileWriter(file);
		writer.write(data);
		writer.flush();
		writer.close();
		log.debug("save rdb data" + data);
	}

	@Override
	public String read() {
		return null;
	}

	@Override
	public void clean() throws IOException {
		File dir = new File("rdb/data");
		if (!dir.exists()) {
			return;
		}
		
		FileWriter writer = new FileWriter(dir);
		writer.write("");
		writer.flush();
		writer.close();
		log.debug("clean rdb data");
		
	}

}
