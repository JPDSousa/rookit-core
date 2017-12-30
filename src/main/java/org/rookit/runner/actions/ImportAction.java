package org.rookit.runner.actions;

import static org.rookit.utils.print.TypeFormat.TITLE;
import static org.rookit.dm.utils.PrintUtils.*;

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
import org.rookit.parser.result.SingleTrackAlbumBuilder;
import org.rookit.parser.utils.TrackPath;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

@SuppressWarnings("javadoc")
public class ImportAction extends AbstractCommand implements Command {

	private final CoreValidator validator;
	private final TrackParserGenerator parser;
	private final DBManager db;
	private final Config config;

	public ImportAction(DBManager db, Config config) {
		super(OptionUtils.createArguments(ImportOptions.values()));
		validator = CoreValidator.getDefault();
		this.db = db;
		this.config = config;
		final ParsingConfig parsingConfig = config.getParsing();
		parser = new TrackParserGenerator(db, parsingConfig);
	}

	@Override
	protected synchronized void execute(ExtendedCommandLine line) {
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
		try {
			output.println("Parsing: " + source);
			index = 1;
			for(SingleTrackAlbumBuilder subRes : results){
				printResult(index++, subRes);
			}
			if(!results.isEmpty()) {
				choice = Character.getNumericValue(input.readLine().charAt(0));
				if(choice > 0){
					choose(source, results.get(choice-1));
					for(int i = 0; i < results.size(); i++) {
						if(i != choice-1) {
							results.get(i);
						}
					}
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

	private void choose(TrackPath source, SingleTrackAlbumBuilder result) {
		final Single<Boolean> dbOp = Single.fromCallable(() -> {
			new TrackPathNormalizer(source).removeTags();
			db.addAlbum(result.build());
			updateHits(result);
			return true;
		}).observeOn(Schedulers.io());

		Single.fromCallable(() -> {
			return askForRemoval(source);
		}).zipWith(dbOp, (one, another) -> one && another)
		.subscribe(toDelete -> {
			if(toDelete) {
				Files.delete(source.getPath());
			}
		});
	}

	private void updateHits(SingleTrackAlbumBuilder result) {
		result.getIgnored().forEach(i -> db.updateIgnored(IgnoreField.create(i)));
		db.updateTrackFormat(TrackFormat.create(result.getFormat().toString()));
	}

	private boolean askForRemoval(TrackPath source) throws IOException {
		final Remove removeConfig = config.getParsing().getOnSuccess().getRemove();
		String answer;
		if(removeConfig == Remove.ALWAYS) {
			return true;
		}
		else if(removeConfig == Remove.ASK) {
			boolean toDelete = false;
			do {
				output.println("Remove " + source.getPath() + " (y/n)?");
				answer = input.readLine();
				toDelete = answer.equals("y");
			} while (!answer.equals("y") && !answer.equals("n"));
			return toDelete;
		}
		else {
			return false;
		}
	}

	private void printResult(int index, SingleTrackAlbumBuilder result) throws IOException {
		final StringBuilder builder = new StringBuilder().append(index)
				.append(" :: ").append(result.getFormat())
				.append(": [").append(result.getScore()).append("]\n")
				.append(toString(result));
		output.println(builder.toString());
	}

	private String toString(SingleTrackAlbumBuilder result) {
		final StringBuilder builder = new StringBuilder()
				.append('[').append(result.getType()).append(':').append(result.getId()).append("] ")
				.append(result.getTitle()).append('\n')
				.append("Content: ").append(result.getPath() != null ? "Yes" : "No").append('\n')
				.append("Main Artists: ").append(
						getIterableAsString(result.getMainArtists(), TITLE, "none"))
				.append('\n')
				.append("Features: ").append(
						getIterableAsString(result.getFeatures(), TITLE, "none"))
				.append('\n')
				.append("Producers: ").append(
						getIterableAsString(result.getProducers(), TITLE, "none"))
				.append('\n')
				.append("Duration: ").append(duration(result.getDuration())).append('\n');
		if(result.isVersion()) {
			builder.append("Version [").append(result.getTypeVersion())
			.append("](").append(result.getVersionToken()).append("): ")
			.append(getIterableAsString(result.getVersionArtists(), TITLE, "none"))
			.append('\n');
		}
		return builder.toString();
	}

	@Override
	public void undo() {
		validator.info("Imports cannot be undone.");
	}

}
