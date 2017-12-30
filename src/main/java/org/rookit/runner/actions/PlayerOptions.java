package org.rookit.runner.actions;

import org.extendedCLI.argument.Argument;
import org.extendedCLI.argument.ArgumentEnum;
import org.extendedCLI.argument.Requires;

@SuppressWarnings("javadoc")
public enum PlayerOptions implements ArgumentEnum {
	
	ID(Argument.create("id", Requires.TRUE, "plays the track with the correspondent id"), 1),
	PLAY(Argument.create("play", Requires.FALSE, "plays the current track"), 1),
	PAUSE(Argument.create("pause", Requires.FALSE, "pauses the current track"), 1),
	STOP(Argument.create("stop", Requires.FALSE, "stops the current track"), 1),
	CLEAR_CACHE(Argument.create("cc", Requires.FALSE, "clears the cache of audio files"), 1);
	
	private final Argument argument;
	private final int groupId;
	
	private PlayerOptions(Argument argument, int groupId) {
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
