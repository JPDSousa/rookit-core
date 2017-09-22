package org.rookit.runner.actions;

import org.extendedCLI.argument.Argument;
import org.extendedCLI.argument.ArgumentEnum;
import org.extendedCLI.argument.Arguments;

@SuppressWarnings("javadoc")
public enum ListOptions implements ArgumentEnum{
	;
	
	private final Argument argument;
	private final int groupId;
	
	private ListOptions(Argument argument, int groupId) {
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
	
	public static Arguments createArguments() {
		final Arguments arguments = Arguments.create();
		arguments.addArguments(values());

		return arguments;
	}

}
