package org.rookit.runner.actions;

import java.util.Arrays;

import org.extendedCLI.argument.Argument;
import org.extendedCLI.argument.Requires;

enum ElementType {
	
	ALL,
	ARTIST,
	ALBUM,
	TRACK,
	GENRE;
	
	static final Argument getArgument() {
		return Argument.create("e", 
				Requires.OPTIONAL, 
				"Filters the type of elements taken into account", 
				Arrays.stream(values()).map(ElementType::name).toArray(String[]::new), 
				ALL.name());
	}

}
