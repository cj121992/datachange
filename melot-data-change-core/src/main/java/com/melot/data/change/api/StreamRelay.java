package com.melot.data.change.api;

import java.util.List;

public interface StreamRelay {
	
	void streamEvent(List<ChangeData> events);
	
}
