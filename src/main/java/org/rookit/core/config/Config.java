package org.rookit.core.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

@SuppressWarnings("javadoc")
public class Config {
	
	private static final Path CONFIG_PATH = Paths.get("src", "main", "resources", "config.json");

	private static Gson gson;
	
	public static final Config read() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		return read(CONFIG_PATH);
	}
	
	public static final Config read(Path path) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		if(gson == null) {
			gson = buildGson();
		}
		return gson.fromJson(new FileReader(path.toFile()), Config.class);
		
	}
	
	public static final void update(Config config) throws IOException {
		update(config, CONFIG_PATH);
	}
	
	public static final void update(Config config, Path path) throws IOException {
		if(gson == null) {
			gson = buildGson();
		}
		Files.write(path, gson.toJson(config).getBytes(), StandardOpenOption.WRITE);
	}
	
	private static final Gson buildGson() {
		final GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		return builder.create();
	}
	
	private DatabaseConfig database;
	
	private ParsingConfig parser;
	
	private Config() {}
	
	public DatabaseConfig getDatabase() {
		return database;
	}

	public ParsingConfig getParsing() {
		return parser;
	}

	public class DatabaseConfig {
		
		private String driverName;
		private Map<String, String> options;
		
		public String getDriverName() {
			return driverName;
		}
		public void setDriverName(String driverName) {
			this.driverName = driverName;
		}
		public Map<String, String> getOptions() {
			return options;
		}
		public void setOptions(Map<String, String> options) {
			this.options = options;
		}
		
	}

	public class ParsingConfig {
		
		private int resultsLimit;
	
		public int getParserLimit() {
			return resultsLimit;
		}
	
		public void setParserLimit(int parserLimit) {
			this.resultsLimit = parserLimit;
		}
		
		
	}

}
