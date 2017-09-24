package org.rookit.core.utils;

import org.rookit.parser.utils.TrackPath;

import com.mpatric.mp3agic.Mp3File;

@SuppressWarnings("javadoc")
public class TrackPathNormalizer {
	
	private final TrackPath path;
	
	public TrackPathNormalizer(TrackPath path) {
		this.path = path;
	}
	
	public void removeTags() {
		final Mp3File mp3 = path.getMp3();
		mp3.removeId3v1Tag();
		mp3.removeId3v2Tag();
		mp3.removeCustomTag();
		path.updateMP3(mp3);
	}

}
