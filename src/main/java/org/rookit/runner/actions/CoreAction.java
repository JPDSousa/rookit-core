package org.rookit.runner.actions;

import org.extendedCLI.command.Command;

@SuppressWarnings("javadoc")
public enum CoreAction {
	
	//HELP, -> inherited by Extended CLI
	IMPORT(new ImportAction()),
	LIST(new ListAction());
	
	private final Command action;
	
	private CoreAction(Command action) {
		this.action = action;
	}

	public Command getAction() {
		return action;
	}

}
