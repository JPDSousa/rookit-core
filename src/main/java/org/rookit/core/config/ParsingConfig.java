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
	private boolean filterNegatives;
	private OnSuccess onSuccess;

	public int getParserLimit() {
		return resultsLimit > 0 ? resultsLimit : DEFAULT_LIMIT;
	}

	public void setParserLimit(int parserLimit) {
		this.resultsLimit = parserLimit;
	}

	public Path getFormatsPath() {
		return getOrDefault(formatsPath, DEFAULT_FORMATS, formatsPath -> Paths.get(formatsPath));
	}

	public void setFormatsPath(Path formatsPath) {
		this.formatsPath = formatsPath.toString();
	}
	
	public boolean isFilterNegatives() {
		return filterNegatives;
	}

	public void setFilterNegatives(boolean filterNegatives) {
		this.filterNegatives = filterNegatives;
	}

	public OnSuccess getOnSuccess() {
		return getOrDefault(onSuccess, new OnSuccess());
	}

	public void setOnSuccess(OnSuccess onSuccess) {
		this.onSuccess = onSuccess;
	}

	public static class OnSuccess {
		
		private String remove;

		public String getRemove() {
			return getOrDefault(remove, "ASK");
		}

		public void setRemove(String remove) {
			this.remove = remove;
		}
		
		
	}
	
}