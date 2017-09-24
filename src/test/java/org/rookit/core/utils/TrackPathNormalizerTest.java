package org.rookit.core.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rookit.parser.utils.TrackPath;
import org.rookit.utils.resource.Resources;

import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;

@SuppressWarnings("javadoc")
public class TrackPathNormalizerTest {

	private static final Path TEST_DIR = Resources.RESOURCES_TEST.resolve("_TrackPathNormalizer");
	private static TrackPath testTrack;
	private static TrackPath originalTrack;
	private static TrackPathNormalizer labRat;
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		originalTrack = TestResources.getTracks().get(0);
		Files.createDirectory(TEST_DIR);
	}

	@AfterClass
	public static void tearDownAfterClass() throws IOException {
		Files.delete(TEST_DIR);
	}

	@Before
	public void setUp() throws IOException {
		final Path path = Files.copy(originalTrack.getPath(), TEST_DIR.resolve(originalTrack.getFileName()));
		testTrack = TrackPath.create(path);
		labRat = new TrackPathNormalizer(testTrack);
	}

	@After
	public void tearDown() throws Exception {
		Files.delete(testTrack.getPath());
	}

	@Test
	public final void testRemoveTags() throws NotSupportedException, IOException {
		labRat.removeTags();
		assertNotEquals(originalTrack, testTrack);
		final Mp3File mp3 = testTrack.getMp3();
		assertFalse(mp3.hasCustomTag());
		assertFalse(mp3.hasId3v1Tag());
		assertFalse(mp3.hasId3v2Tag());
	}

}
