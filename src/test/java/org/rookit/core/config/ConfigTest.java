package org.rookit.core.config;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.rookit.core.config.Config;
import org.rookit.utils.resource.Resources;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

@SuppressWarnings("javadoc")
public class ConfigTest {
	
	private static final Path CONFIG_DIR = Resources.RESOURCES_TEST.resolve("config");
	private static final Path CONFIG_DUMMY_PATH = CONFIG_DIR.resolve("config.json");
	private static final Path CONFIG_EMPTY_PATH = CONFIG_DIR.resolve("empty.json");
	
	
	private static final String DRIVER_NAME = "rookit-monogdb";
	private static final int PARSER_LIMIT = 5;
	
	@Before
	public final void beforeTest() throws IOException {
		final FileWriter fileWriter = new FileWriter(CONFIG_DUMMY_PATH.toFile());
		final JsonWriter writer = new JsonWriter(fileWriter);
		writer.beginObject()
		.name("database").beginObject()
			.name("driverName").value(DRIVER_NAME)
			.endObject()
		.name("parser").beginObject()
			.name("resultsLimit").value(PARSER_LIMIT)
			.endObject()
		.endObject();
		writer.close();
		fileWriter.close();
	}

	@Test
	public final void testReadPath() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		final Config config = Config.read(CONFIG_DUMMY_PATH);
		assertEquals(DRIVER_NAME, config.getDatabase().getDriverName());
		assertEquals(PARSER_LIMIT, config.getParsing().getParserLimit());
	}

	@Test
	public final void testUpdateConfigPath() throws JsonSyntaxException, JsonIOException, IOException {
		final Config config = Config.read(CONFIG_DUMMY_PATH);
		final int expected = 10;
		config.getParsing().setParserLimit(expected);
		Config.update(config, CONFIG_DUMMY_PATH);
		final int actual = Config.read(CONFIG_DUMMY_PATH).getParsing().getParserLimit();
		assertEquals(expected, actual);
	}
	
	@Test
	public final void testDefaults() throws JsonSyntaxException, JsonIOException, FileNotFoundException {
		final Config config = Config.read(CONFIG_EMPTY_PATH);
		assertNotNull(config);
		final DatabaseConfig dbConfig = config.getDatabase();
		final ParsingConfig parseConfig = config.getParsing();
		assertNotNull(dbConfig);
		assertNotNull(parseConfig);
		assertNotNull(dbConfig.getDriverName());
		assertNotNull(dbConfig.getOptions());
		assertTrue(parseConfig.getParserLimit() > 0);
	}

}
