package org.rookit.core.stream;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rookit.core.config.ParsingConfig;
import org.rookit.core.utils.TestResources;
import org.rookit.mongodb.DBManager;
import org.rookit.parser.utils.TrackPath;
import org.rookit.utils.resource.Resources;

@SuppressWarnings("javadoc")
public class TrackParserGeneratorTests {

	private static final Path BASE_DIR = Resources.RESOURCES_TEST.resolve("tpg");

	private static DBManager db;
	private static ParsingConfig pConfig;

	@BeforeClass
	public void setUpBeforeClass() {
		db = TestResources.getDBConnection();
	}

	@Before
	public void setUp() throws IOException {
		pConfig = new ParsingConfig();
		pConfig.setFormatsPath(TestResources.FORMATS);
		Files.createDirectories(BASE_DIR);
		for(TrackPath path : TestResources.getTracks()) {
			Files.copy(path.getPath(), BASE_DIR.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	@After
	public void tearDown() throws IOException {
		for(Path path : Files.list(BASE_DIR).collect(Collectors.toList())) {
			Files.delete(path);
		}
		Files.delete(BASE_DIR);
	}

	@Test
	public final void testTrackParserGenerator() {
		assertNotNull(new TrackParserGenerator(db, pConfig));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTrackParserNullDB() {
		assertNotNull(new TrackParserGenerator(null, pConfig));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTrackParserNullConfig() {
		assertNotNull(new TrackParserGenerator(db, null));
	}

	@Test
	public final void testGenerate() throws IOException {
		final int parserLimit = 5;
		// tests directories
		try(TrackParserGenerator generator = new TrackParserGenerator(db, pConfig)) {
			assertTrue(generator.generate(BASE_DIR).count() > 0);
		}
		// tests parser limit
		pConfig.setParserLimit(parserLimit);
		try(TrackParserGenerator generator = new TrackParserGenerator(db, pConfig)) {
			generator.generate(BASE_DIR).forEach(result -> assertTrue(result.getResults().size()<=parserLimit));
		}
		// tests paths
		try(TrackParserGenerator generator = new TrackParserGenerator(db, pConfig)) {
			final Path path = Files.list(BASE_DIR).findFirst().get();
			assertEquals(1, generator.generate(path).count());
		}
	}

	@Test(expected = IOException.class)
	public final void testClose() {
		final TrackParserGenerator generator = new TrackParserGenerator(db, pConfig);
		generator.close();
		generator.generate(BASE_DIR).count();
		fail("A closed stream cannot be operated upon.");
	}

}
