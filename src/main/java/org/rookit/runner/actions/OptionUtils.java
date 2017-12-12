package org.rookit.runner.actions;

import org.extendedCLI.argument.ArgumentEnum;
import org.extendedCLI.argument.Arguments;

@SuppressWarnings("javadoc")
public abstract class OptionUtils {

	private OptionUtils() {}
	
	public static Arguments createArguments(ArgumentEnum[] values) {
		final Arguments arguments = Arguments.create();
		arguments.addArguments(values);

		return arguments;
	}
}
