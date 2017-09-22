package org.rookit.core;

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
		return builder.create();
	}
	
	private DatabaseConfig database;
	
	private ParsingConfig parsing;
	
	private Config() {}
	
	public DatabaseConfig getDatabase() {
		return database;
	}

	public void setDatabase(DatabaseConfig database) {
		this.database = database;
	}

	public ParsingConfig getParsing() {
		return parsing;
	}

	public void setParsing(ParsingConfig parsing) {
		this.parsing = parsing;
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
		
		private int parserLimit;
	
		public int getParserLimit() {
			return parserLimit;
		}
	
		public void setParserLimit(int parserLimit) {
			this.parserLimit = parserLimit;
		}
		
		
	}

}
