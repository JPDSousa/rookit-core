package org.rookit.runner.actions;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.extendedCLI.command.AbstractCommand;
import org.extendedCLI.command.Command;
import org.extendedCLI.command.ExtendedCommandLine;
import org.rookit.core.config.Config;
import org.rookit.core.stream.TPGResult;
import org.rookit.core.stream.TrackParserGenerator;
import org.rookit.core.utils.CoreValidator;
import org.rookit.core.utils.TrackPathNormalizer;
import org.rookit.mongodb.DBManager;
import org.rookit.dm.utils.PrintUtils;
import org.rookit.parser.formatlist.FormatList;
import org.rookit.parser.result.SingleTrackAlbumBuilder;
import org.rookit.parser.utils.TrackPath;

@SuppressWarnings("javadoc")
public class ImportAction extends AbstractCommand implements Command {
	
	private final CoreValidator validator;
	private final TrackParserGenerator parser;
	private final DBManager db;
	
	public ImportAction(DBManager db, FormatList list, Config config) {
		super(ImportOptions.createArguments());
		this.db = db;
		validator = CoreValidator.getDefault();
		parser = new TrackParserGenerator(db, list, config.getParsing());
	}

	@Override
	protected void execute(ExtendedCommandLine line) {
		final Path path = Paths.get(line.getValue(ImportOptions.PATH));
		if(Files.isDirectory(path)) {
			importDirectory(path);
		}
		else {
			importTrack(path);
		}
	}

	private void importDirectory(Path path) {
		parser.generate(path).forEach(this::handleResult);
	}
	
	private void importTrack(Path path) {
		final TrackPath source = TrackPath.create(path);
		handleResult(new TPGResult(source, parser.parseAll(source)));
	}
	
	private void handleResult(TPGResult result) {
		int index;
		int choice;
		final TrackPath source = result.getSource();
		final List<SingleTrackAlbumBuilder> results = result.getResults();
		final SingleTrackAlbumBuilder finalResult;
		try {
			System.out.println("Parsing: " + source);
			index = 1;
			for(SingleTrackAlbumBuilder subRes : results){
				printResult(index++, subRes);
			}
			choice = Character.getNumericValue(input.readLine().charAt(0));
			if(choice > 0){
				finalResult = results.get(choice-1);
				new TrackPathNormalizer(source).removeTags();
				db.addAlbum(finalResult.build());
			}
			System.out.println("\n");
		} catch(IOException e) {
			validator.handleIOException(e);
		}
	}

	private void printResult(int index, SingleTrackAlbumBuilder result) {
		final StringBuilder builder = new StringBuilder().append(index)
				.append(" :: ").append(result.getFormat())
				.append(": [").append(result.getScore()).append("]\n")
				.append(PrintUtils.track(result.getTrack()));
		System.out.println(builder);
	}

	@Override
	public void undo() {
		validator.info("Imports cannot be undone.");
	}

}
