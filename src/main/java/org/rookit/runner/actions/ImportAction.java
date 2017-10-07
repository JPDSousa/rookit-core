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
import org.rookit.core.config.ParsingConfig;
import org.rookit.core.config.ParsingConfig.OnSuccess.Remove;
import org.rookit.core.stream.TPGResult;
import org.rookit.core.stream.TrackParserGenerator;
import org.rookit.core.utils.CoreValidator;
import org.rookit.core.utils.TrackPathNormalizer;
import org.rookit.mongodb.DBManager;
import org.rookit.dm.parser.IgnoreField;
import org.rookit.dm.parser.TrackFormat;
import org.rookit.dm.utils.PrintUtils;
import org.rookit.parser.result.SingleTrackAlbumBuilder;
import org.rookit.parser.utils.TrackPath;

@SuppressWarnings("javadoc")
public class ImportAction extends AbstractCommand implements Command {
	
	private final CoreValidator validator;
	private final TrackParserGenerator parser;
	private final DBManager db;
	private final Config config;
	
	public ImportAction(DBManager db, Config config) {
		super(ImportOptions.createArguments());
		validator = CoreValidator.getDefault();
		this.db = db;
		this.config = config;
		final ParsingConfig parsingConfig = config.getParsing();
		parser = new TrackParserGenerator(db, parsingConfig);
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
			output.println("Parsing: " + source);
			index = 1;
			for(SingleTrackAlbumBuilder subRes : results){
				printResult(index++, subRes);
			}
			if(!results.isEmpty()) {
				choice = Character.getNumericValue(input.readLine().charAt(0));
				if(choice > 0){
					finalResult = results.get(choice-1);
					new TrackPathNormalizer(source).removeTags();
					db.addAlbum(finalResult.build());
					updateHits(finalResult);
					askForRemoval(source);
				}
			}
			else {
				output.println("No suitable format found! :(");
			}
			output.println("\n");
		} catch(IOException e) {
			validator.handleIOException(e);
		}
	}
	
	private void updateHits(SingleTrackAlbumBuilder result) {
		result.getIgnored().forEach(i -> db.updateIgnored(IgnoreField.create(i)));
		db.updateTrackFormat(TrackFormat.create(result.getFormat().toString()));
	}

	private void askForRemoval(TrackPath source) throws IOException {
		final Remove removeConfig = config.getParsing().getOnSuccess().getRemove();
		String answer;
		if(removeConfig == Remove.ALWAYS) {
			Files.delete(source.getPath());
		}
		else if(removeConfig == Remove.ASK) {
			do {
				output.println("Remove the file (y/n)?");
				answer = input.readLine();
				if(answer.equals("y")) {
					Files.delete(source.getPath());
				}
			} while (!answer.equals("y") && !answer.equals("n"));
		}
	}

	private void printResult(int index, SingleTrackAlbumBuilder result) throws IOException {
		final StringBuilder builder = new StringBuilder().append(index)
				.append(" :: ").append(result.getFormat())
				.append(": [").append(result.getScore()).append("]\n")
				.append(PrintUtils.track(result.getTrack()));
		output.println(builder.toString());
	}

	@Override
	public void undo() {
		validator.info("Imports cannot be undone.");
	}

}
