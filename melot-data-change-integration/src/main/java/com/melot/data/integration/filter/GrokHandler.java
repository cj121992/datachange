package com.melot.data.integration.filter;

import lombok.Getter;

import com.nflabs.grok.Grok;
import com.nflabs.grok.GrokException;
import com.nflabs.grok.Match;

public class GrokHandler {
	
	@Getter
	private String target;
	
	private Grok grok = new Grok();
	
	public GrokHandler(String target) {
		this.target = target;
	}
	
	void loadParren(String filterPattern) throws GrokException {
		String path = System.getProperty("user.dir");
		grok.addPatternFromFile(path + "/patterns/grok-patterns");
		grok.compile(filterPattern);
	}
	
	public String match(String target) {
		Match gm = grok.match(target);
		gm.captures();
		return gm.toJson();
	}
	
	public static void main(String[] args) throws GrokException {
		GrokHandler grokHandler = new GrokHandler("a");
		grokHandler.loadParren("%{COMBINEDAPACHELOG}");
		String log = "112.169.19.192 - - [06/Mar/2013:01:36:30 +0900] \"GET / HTTP/1.1\" 200 44346 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_2) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.152 Safari/537.22\"";
		System.out.println(grokHandler.match(log));

		log = "112.169.19.121 sagaws";
		grokHandler.loadParren("%{IPV4}");
		System.out.println(grokHandler.match(log));

	}
}
