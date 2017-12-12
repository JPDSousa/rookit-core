package org.rookit.runner.actions;

import org.extendedCLI.argument.Argument;
import org.extendedCLI.argument.ArgumentEnum;
import org.extendedCLI.argument.Requires;

@SuppressWarnings("javadoc")
public enum ImportOptions implements ArgumentEnum {
	PATH(Argument.create("p", Requires.TRUE, "The content path to be imported."), 1),
	AUTO(Argument.create("a", Requires.FALSE, "Automatically chooses the first option in every parsing"), 2);

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

}
