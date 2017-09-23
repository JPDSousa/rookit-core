package org.rookit.core.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.rookit.parser.utils.TrackPath;

import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;

@SuppressWarnings("javadoc")
public class TrackPathNormalizer {
	
	private final TrackPath path;
	
	public TrackPathNormalizer(TrackPath path) {
		this.path = path;
	}
	
	public void removeTags() throws NotSupportedException, IOException {
		final String tempName = "aksjdhashdfasgdjasdadfdgf";
		final Mp3File mp3 = path.getMp3();
		mp3.removeId3v1Tag();
		mp3.removeId3v2Tag();
		mp3.removeCustomTag();
		mp3.save(tempName);
		Files.move(Paths.get(tempName), path.getPath(), StandardCopyOption.REPLACE_EXISTING);
	}

}
