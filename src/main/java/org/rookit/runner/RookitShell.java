package org.rookit.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.extendedCLI.command.CLIBuilder;
import org.extendedCLI.command.ExtendedCLI;
import org.extendedCLI.exceptions.NoSuchCommandException;
import org.rookit.core.Config;
import org.rookit.runner.actions.CoreAction;
import org.rookit.utils.log.Logs;
import org.rookit.utils.log.Validator;

@SuppressWarnings("javadoc")
public class RookitShell {
	
	private static final Validator VALIDATOR = new Validator(Logs.CORE);

	private final ExtendedCLI cli;
	
	private RookitShell(Config configuration) {
		VALIDATOR.info("[...] Loading actions");
		final CLIBuilder builder = new CLIBuilder(false);
		for(CoreAction action : CoreAction.values()) {
			builder.registerCommand(action.name(), action.getAction());
		}
		cli = builder.build();
		VALIDATOR.info("[ok] Loading actions");
	}
	
	private void execute(String input) throws NoSuchCommandException {
		cli.execute(input);
	}
	
	private void start(BufferedReader reader) throws IOException, NoSuchCommandException {
		String line;
		System.out.print("Rookit>");
		while((line = reader.readLine()) != null) {
			execute(line);
			System.out.print("Rookit>");
		}
		reader.close();
	}
	
	public static void main(String[] args) throws NoSuchCommandException, IOException {
		final Config config = Config.read();
		final RookitShell shell = new RookitShell(config);
		
		if(args.length > 0) {
			shell.execute(String.join(" ", args));
		}
		else {
			shell.start(new BufferedReader(new InputStreamReader(System.in)));
		}
	}

}
