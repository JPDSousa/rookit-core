package org.rookit.runner.actions;

import org.extendedCLI.command.AbstractCommand;
import org.extendedCLI.command.Command;
import org.extendedCLI.command.ExtendedCommandLine;

@SuppressWarnings("javadoc")
public class ListAction extends AbstractCommand implements Command {

	public ListAction() {
		super(ListOptions.createArguments());
	}

	@Override
	protected void execute(ExtendedCommandLine arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}

}
