package com.melot.data.change.searcher.domain;
import java.util.*;

import lombok.Data;

@Data
public class ESResult {
	
	private long count;
	
	private List<ESField> fieldList;

	@Data
	public static class ESField {
		private Map<String, Object> field;
		private Map<String, Object> highlightField;
		private double score;
	}
	
}
