package org.rookit.runner.actions;

import org.extendedCLI.argument.Argument;
import org.extendedCLI.argument.ArgumentEnum;
import org.extendedCLI.argument.Arguments;
import org.extendedCLI.argument.Requires;

@SuppressWarnings("javadoc")
public enum ImportOptions implements ArgumentEnum {
	PATH(Argument.create("p", Requires.TRUE, "The content path to be imported."), 1);

	private final Argument argument;
	private final int groupId;
	
	private ImportOptions(Argument argument, int groupId) {
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
