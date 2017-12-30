package org.rookit.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.rookit.mongodb.DBManager;
import org.rookit.parser.formatlist.FormatList;
import org.rookit.parser.utils.TrackPath;
import org.rookit.utils.resource.Resources;

@SuppressWarnings("javadoc")
public class TestResources {
	
	public static final Path TRACKS = Resources.RESOURCES_TEST.resolve("tracks");
	public static final Path FORMATS = Resources.RESOURCES_TEST.resolve("parser").resolve("formats.txt");

	private static final DBManager DB = DBManager.open("localhost", 27020, "rookit-core");
	
	public static final DBManager getDBConnection() {
		return DB;
	}
	
	public static final FormatList getFormatList() throws IOException {
		return FormatList.readFromPath(FORMATS);
	}
	
	public static final List<TrackPath> getTracks() throws IOException {
		return Files.list(TRACKS)
				.map(p -> TrackPath.create(p))
				.collect(Collectors.toList());
	}

}
