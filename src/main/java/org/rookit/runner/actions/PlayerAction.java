package org.rookit.runner.actions;

import static org.rookit.runner.actions.PlayerOptions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.extendedCLI.command.AbstractCommand;
import org.extendedCLI.command.ExtendedCommandLine;
import org.rookit.dm.track.Track;
import org.rookit.mongodb.DBManager;
import org.rookit.player.RookitPlayer;

@SuppressWarnings("javadoc")
public class PlayerAction extends AbstractCommand {

	private final DBManager database;
	private final RookitPlayer player;
	
	public PlayerAction(RookitPlayer player, DBManager database) {
		super(OptionUtils.createArguments(values()));
		this.player = player;
		this.database = database;
	}

	@Override
	protected void execute(ExtendedCommandLine line) {
		if(line.hasArg(ID)) {
			final ObjectId id = new ObjectId(line.getValue(ID));
			final Track track = database.getTracks()
					.withId(id)
					.first();
			if(track != null) {
				player.load(track);
				play();
			}
			else {
				println("No track found with id: " + id);
			}
		}
		else if(line.hasArg(PAUSE)) {
			player.pause();
		}
		else if(line.hasArg(PLAY)) {
			play();
		}
		else if(line.hasArg(STOP)) {
			player.stop();
		}
		else if(line.hasArg(CLEAR_CACHE)) {
			player.clearCache();
		}
	}

	private void play() {
		player.play();
		final Optional<Track> current = player.getCurrent();
		if(current.isPresent()) {
			database.updateTrack().play().setLastPlayed(LocalDate.now())
			.where().withId(current.get().getId())
			.execute();
		}
	}
	
	private void println(String str) {
		try {
			output.println(str);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void undo() {
		// TODO Auto-generated method stub

	}
}
