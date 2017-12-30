package org.rookit.runner.actions;

import org.extendedCLI.argument.Argument;
import org.extendedCLI.argument.ArgumentEnum;
import org.extendedCLI.argument.Requires;

enum PlaylistOptions implements ArgumentEnum {
	LIST(Argument.create("ls", Requires.FALSE, "Lists all the playlists stored"), 1),
	CREATE(Argument.create("c", Requires.TRUE, "Creates a new dynamic playlist with the "
			+ "specified name"), 1),
	SELECT(Argument.create("sel", Requires.TRUE, "Selects a playlist to act upon"), 1),
	AUDIO_PROPS(Argument.create("audio_props", Requires.FALSE, "Edits the audio properties of the"
			+ " selected playlist"), 1),
	PLAY(Argument.create("play", Requires.FALSE, "Plays the selected playlist"), 1),
	NEXT(Argument.create("next", Requires.FALSE, "Plays the next track in the playlist"), 1);
	
	private final Argument argument;
	private final int groupId;
	
	private PlaylistOptions(Argument argument, int groupId) {
		this.argument = argument;
		this.groupId = groupId;
	}

	@Override
	public Argument getArgument() {
		return argument;
	}

	@Override
	public int getGroupID() {
		return groupId;
	}

}
