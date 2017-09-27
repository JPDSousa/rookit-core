package org.rookit.core.config;

import static org.rookit.core.config.ConfigUtils.*;

@SuppressWarnings("javadoc")
public class ParsingConfig {
	
	public static final int DEFAULT_LIMIT = 5;
	
	private int resultsLimit;

	public int getParserLimit() {
		return getOrDefault(resultsLimit, DEFAULT_LIMIT);
	}

	public void setParserLimit(int parserLimit) {
		this.resultsLimit = parserLimit;
	}
	
	
}