package org.rookit.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

import org.extendedCLI.command.CLIBuilder;
import org.extendedCLI.command.ExtendedCLI;
import org.extendedCLI.exceptions.NoSuchCommandException;
import org.rookit.core.config.Config;
import org.rookit.core.config.DatabaseConfig;
import org.rookit.crawler.RookitCrawler;
import org.rookit.crawler.config.MusicServiceConfig;
import org.rookit.mongodb.DBManager;
import org.rookit.player.RookitPlayer;
import org.rookit.runner.actions.CrawlAction;
import org.rookit.runner.actions.ImportAction;
import org.rookit.runner.actions.ListAction;
import org.rookit.runner.actions.PlayerAction;
import org.rookit.runner.actions.PlaylistAction;
import org.rookit.utils.log.Logs;
import org.rookit.utils.log.Validator;

import com.google.common.io.Closer;

@SuppressWarnings("javadoc")
public class RookitShell {

	private static final Validator VALIDATOR = new Validator(Logs.CORE);

	private final ExtendedCLI cli;
	private final BufferedReader reader;
	private final Optional<RookitCrawler> crawler;
	private final Closer toClose;

	private RookitShell(Config configuration, BufferedReader reader) {
		this.toClose = Closer.create();
		this.reader = reader;
		toClose.register(this.reader);
		this.crawler = loadCrawler(configuration.getCrawler());
		this.cli = buildCLI(configuration);
		VALIDATOR.info("[ok] Loading actions");
	}

	private ExtendedCLI buildCLI(Config config) {
		VALIDATOR.info("[...] Loading actions");
		final CLIBuilder builder = new CLIBuilder(true);
		final DBManager db = loadDatabase(config.getDatabase());
		final RookitPlayer player = new RookitPlayer();
		builder.setInput(reader);
		builder.setOutput(System.out);
		builder.registerCommand("import", new ImportAction(db, config));
		builder.registerCommand("list", new ListAction(db));
		builder.registerCommand("playlist", new PlaylistAction(player, db));
		builder.registerCommand("play", new PlayerAction(player, db));
		if(crawler.isPresent()) {
			builder.registerCommand("crawler", new CrawlAction(db, crawler.get()));
		}
		else {
			VALIDATOR.info("[crawler] command cannot be registered, as the RookitCrawler is not unavailable.");
		}
		return builder.build();

	}

	private Optional<RookitCrawler> loadCrawler(MusicServiceConfig config) {
		assert toClose != null;
		try {
			final Optional<RookitCrawler> crawler = Optional.of(new RookitCrawler(config));
			if(crawler.isPresent()) {
				toClose.register(crawler.get());
			}
			return crawler;
		} catch (RuntimeException e) {
			final Throwable cause = e.getCause();
			if(cause instanceof IOException) {
				VALIDATOR.info("Cannot connect crawler, check connection to internet.");
				return Optional.empty();
			}
			throw e;
		}
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
		try {
			// "exit" overrides ExtendedCLI EXIT command, which abruptly exits the application
			// without closing resources.
			while((line = reader.readLine()) != null && !line.equals("equals")) {
				execute(line);
				System.out.print("Rookit>");
			}
		} catch (Throwable e) {
			toClose.rethrow(e);
		} finally {
			toClose.close();
		}
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
