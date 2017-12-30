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

	public CrawlAction(DBManager db, RookitCrawler crawler) {
		super(Arguments.create());
		validator = CoreValidator.getDefault();
		this.crawler = crawler;
		this.db = db;
	}

	@Override
	protected void execute(ExtendedCommandLine line) {
		db.getTracks().stream().forEach(track -> {
			validator.info("Validating: " + track.getLongFullTitle());
			System.out.println("==== Before ====\n"
					+ track.getExternalMetadata() + "\n"
					+ PrintUtils.track(track));
			crawler.fillTrack(track).subscribe(() -> {
				System.out.println("==== Another one ====\n"
						+ track.getExternalMetadata() + "\n"
						+ PrintUtils.track(track)
						+ "Danceability: " + track.getDanceability()
						+ "\nValence: " + track.getValence()
						+ "\nEnergy: " + track.getEnergy()
						+ "\nBPM: " + track.getBPM()
						+ "\nKey: " + track.getTrackKey()
						+ "\nMode: " + track.getTrackMode()
						//+ "\nIntrumental: " + track.isInstrumental()
						+ "\nAcoustic: " + track.isAcoustic()
						+ "\nLive: " + track.isLive());
				db.replaceTrack(track);
			});
			
		});
	}

	@Override
	public void undo() {
		throw new RuntimeException("Not implemented yet!");
	}

}
