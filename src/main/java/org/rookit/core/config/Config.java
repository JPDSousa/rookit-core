package org.rookit.core.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.rookit.utils.resource.Resources;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import static org.rookit.core.config.ConfigUtils.*;

@SuppressWarnings("javadoc")
public class Config {
	
	private static final Path CONFIG_PATH = Resources.RESOURCES_MAIN.resolve("config.json");
	
	public static final Config read() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		return read(CONFIG_PATH);
	}
	
	public static final Config read(Path path) throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		return fromPath(path);
	}
	
	public static final void update(Config config) throws IOException {
		update(config, CONFIG_PATH);
	}
	
	public static final void update(Config config, Path path) throws IOException {
		toPath(config, path);
	}
	
	private DatabaseConfig database;
	
	private ParsingConfig parser;
	
	private Config() {}
	
	public DatabaseConfig getDatabase() {
		return getOrDefault(database, new DatabaseConfig());
	}

	public ParsingConfig getParsing() {
		return getOrDefault(parser, new ParsingConfig());
	}

}
