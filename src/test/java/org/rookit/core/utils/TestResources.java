package org.rookit.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.rookit.parser.utils.TrackPath;
import org.rookit.utils.resource.Resources;

@SuppressWarnings("javadoc")
public class TestResources {
	
	public static final Path TRACKS = Resources.RESOURCES_TEST.resolve("tracks");
	
	public static final List<TrackPath> getTracks() throws IOException {
		return Files.list(TRACKS)
				.map(p -> TrackPath.create(p))
				.collect(Collectors.toList());
	}

}
