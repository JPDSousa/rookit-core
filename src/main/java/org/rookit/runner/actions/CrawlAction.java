package org.rookit.runner.actions;

import org.extendedCLI.argument.Arguments;
import org.extendedCLI.command.AbstractCommand;
import org.extendedCLI.command.Command;
import org.extendedCLI.command.ExtendedCommandLine;
import org.rookit.core.utils.CoreValidator;
import org.rookit.crawler.RookitCrawler;
import org.rookit.dm.utils.PrintUtils;
import org.rookit.mongodb.DBManager;

@SuppressWarnings("javadoc")
public class CrawlAction extends AbstractCommand implements Command {
	
	private final CoreValidator validator;
	private final RookitCrawler crawler;
	private final DBManager db;
	
	public CrawlAction(DBManager db) {
		super(Arguments.create());
		validator = CoreValidator.getDefault();
		crawler = new RookitCrawler();
		this.db = db;
	}

	@Override
	protected void execute(ExtendedCommandLine line) {
		db.getTracks().stream().forEach(track -> {
			validator.info("Validating: " + track.getLongFullTitle());
			crawler.fillTrack(track);
			System.err.println(PrintUtils.track(track));
		});
	}

	@Override
	public void undo() {
		throw new RuntimeException("Not implemented yet!");
	}
	
}
