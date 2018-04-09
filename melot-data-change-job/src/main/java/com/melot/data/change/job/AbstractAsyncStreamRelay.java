package com.melot.data.change.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.melot.data.change.api.ChangeData;
import com.melot.data.change.api.StreamRelay;
import com.melot.data.change.job.util.AsyncHelper;
import com.melot.data.change.rdb.FileReaderWriter;

//import com.melot.data.change.rdb.FileReaderWriter;

public abstract class AbstractAsyncStreamRelay implements StreamRelay {

	private FileReaderWriter _readerWriter = new FileReaderWriter();

	@Override
	public void streamEvent(List<ChangeData> events) {

		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		// String diffData = _readerWriter.read();

		// List<ChangeData> diffEvents = JSON.parseArray(diffData,
		// ChangeData.class);

		for (ChangeData data : events) {
			Future<Boolean> future = AsyncHelper.addToThread(data, this);
			futures.add(future);
		}

		//TODO 考虑把结果处理也做成异步
		for (Future<Boolean> future : futures) {
			try {
				future.get(1000, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		try {
			_readerWriter.clean();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public abstract void stream(ChangeData data);

}
