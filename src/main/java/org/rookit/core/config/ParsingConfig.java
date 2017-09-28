package org.rookit.core.config;

import static org.rookit.core.config.ConfigUtils.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.rookit.utils.resource.Resources;

@SuppressWarnings("javadoc")
public class ParsingConfig {
	
	public static final int DEFAULT_LIMIT = 5;
	public static final Path DEFAULT_FORMATS = Resources.RESOURCES_MAIN
			.resolve("parser")
			.resolve("formats.txt");
	
	private int resultsLimit;
	private String formatsPath;

	public int getParserLimit() {
		return getOrDefault(resultsLimit, DEFAULT_LIMIT);
	}

	public void setParserLimit(int parserLimit) {
		this.resultsLimit = parserLimit;
	}

	public Path getFormatsPath() {
		return getOrDefault(Paths.get(formatsPath), DEFAULT_FORMATS);
	}

	public void setFormatsPath(Path formatsPath) {
		this.formatsPath = formatsPath.toString();
	}
	
}