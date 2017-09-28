package org.rookit.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Map;

import org.extendedCLI.command.CLIBuilder;
import org.extendedCLI.command.ExtendedCLI;
import org.extendedCLI.exceptions.NoSuchCommandException;
import org.rookit.core.config.Config;
import org.rookit.core.config.DatabaseConfig;
import org.rookit.mongodb.DBManager;
import org.rookit.parser.formatlist.FormatList;
import org.rookit.runner.actions.ImportAction;
import org.rookit.runner.actions.ListAction;
import org.rookit.utils.log.Logs;
import org.rookit.utils.log.Validator;
import org.rookit.utils.resource.Resources;

@SuppressWarnings("javadoc")
public class RookitShell {
	
	private static final Validator VALIDATOR = new Validator(Logs.CORE);
	
	private static final Path FL_PATH = Resources.RESOURCES_MAIN
			.resolve("parser")
			.resolve("formats.txt");
	// TODO [end] move to configs

	private final ExtendedCLI cli;
	private final BufferedReader reader;
	
	private RookitShell(Config configuration, BufferedReader reader) throws IOException {
		this.reader = reader;
		final DBManager db = loadDatabase(configuration.getDatabase());
		final FormatList list = FormatList.readFromPath(FL_PATH);
		VALIDATOR.info("[...] Loading actions");
		final CLIBuilder builder = new CLIBuilder(true);
		builder.setInput(reader);
		builder.setOutput(System.out);
		
		builder.registerCommand("import", new ImportAction(db, list, configuration));
		builder.registerCommand("list", new ListAction());
		cli = builder.build();
		VALIDATOR.info("[ok] Loading actions");
	}
	
	private DBManager loadDatabase(DatabaseConfig config) {
		final Map<String, String> options = config.getOptions();
		final DBManager db = DBManager.open(
				options.get("host"), 
				Integer.valueOf(options.get("port")), 
				options.get("db_name"));
		db.init();
		return db;
	}
	
	private void execute(String input) {
		try {
			cli.execute(input);
		} catch (NoSuchCommandException e) {
			System.out.println("Unkown Command.");
		}
	}
	
	private void start() throws IOException {
		String line;
		System.out.print("Rookit>");
		while((line = reader.readLine()) != null) {
			execute(line);
			System.out.print("Rookit>");
		}
		reader.close();
	}
	
	public static void main(String[] args) throws IOException {
		final Config config = Config.read();
		final RookitShell shell = new RookitShell(config, new BufferedReader(new InputStreamReader(System.in)));
		
		if(args.length > 0) {
			shell.execute(String.join(" ", args));
		}
		else {
			shell.start();
		}
	}

}
