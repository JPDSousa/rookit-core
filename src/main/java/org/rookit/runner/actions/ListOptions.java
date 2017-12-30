package org.rookit.runner.actions;

import org.extendedCLI.argument.Argument;
import org.extendedCLI.argument.ArgumentEnum;
import org.extendedCLI.argument.Requires;

enum ListOptions implements ArgumentEnum {

	QUERY(Argument.create("q", Requires.TRUE, "Performs a search over the specified query"), 1),
	ELEMENT(ElementType.getArgument(), 2);

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

}
