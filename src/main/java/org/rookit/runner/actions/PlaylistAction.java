package org.rookit.runner.actions;

import static org.rookit.runner.actions.PlaylistOptions.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import org.extendedCLI.command.AbstractCommand;
import org.extendedCLI.command.ExtendedCommandLine;
import org.rookit.dm.play.DynamicPlaylist;
import org.rookit.dm.play.DynamicPlaylistImpl;
import org.rookit.dm.play.Playlist;
import org.rookit.dm.track.Track;
import org.rookit.mongodb.DBManager;
import org.rookit.player.RookitPlayer;

@SuppressWarnings("javadoc")
public class PlaylistAction extends AbstractCommand {

	private final DBManager database;
	private Playlist current;
	private final RookitPlayer player;
	private int trackIndex;

	public PlaylistAction(RookitPlayer player, DBManager database) {
		super(OptionUtils.createArguments(values()), 
				"Use this command to manage playlists");
		this.database = database;
		this.player = player;
		this.player.onEnd(this::next);
		current = null;
		trackIndex = 0;
	}

	@Override
	protected synchronized void execute(ExtendedCommandLine line) {
		if(line.hasArg(LIST)) {
			list();
		}
		else if(line.hasArg(CREATE)) {
			create(line.getValue(CREATE));
		}
		else if(line.hasArg(SELECT)) {
			select(line.getValue(SELECT));
		}
		else if(line.hasArg(AUDIO_PROPS)) {
			editAudioProps();
		}
		else if(line.hasArg(PLAY)) {
			play();
		}
		else if(line.hasArg(NEXT)) {
			next();
		}
	}
	
	private void next() {
		player.stop();
		trackIndex++;
		play();
	}

	private void play() {
		if(current != null) {
			final Optional<Track> track = current.streamTracks()
					.filter(t -> !t.getPath().isEmpty())
					.skip(trackIndex)
					.findFirst();
			if(track.isPresent()) {
				player.load(track.get());
				player.play();
				database.updateTrack().play().setLastPlayed(LocalDate.now())
				.where().withId(track.get().getId())
				.execute();;
			}
			else if(trackIndex != 0) {
				trackIndex = 0;
				play();
			}
			else {
				println("Playlist is empty");
			}
		}
		else {
			println("No playlist selected");
		}
	}

	private void editAudioProps() {
		if(current != null) {
			if(current instanceof DynamicPlaylist) {
				final DynamicPlaylist currentDynamic = (DynamicPlaylist) current;
				try {
					askBPM(currentDynamic);
					askEnergy(currentDynamic);
					askValence(currentDynamic);
					askDanceability(currentDynamic);
					askAcoustic(currentDynamic);
					askInstrumental(currentDynamic);
					askLive(currentDynamic);
					// done!
					database.replacePlaylist(currentDynamic);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			else {
				println("Current is not dynamic");
			}
		}
		else {
			println("No playlist selected");
		}
	}

	private void askLive(final DynamicPlaylist currentDynamic) throws IOException {
		// live
		println("Live (true/false): ");
		final String live = input.readLine();
		if(!live.isEmpty()) {
			currentDynamic.setLive(Boolean.valueOf(live));
		}
	}

	private void askInstrumental(final DynamicPlaylist currentDynamic) throws IOException {
		// instrumental
		println("Instrumental (true/false): ");
		final String instrumental = input.readLine();
		if(!instrumental.isEmpty()) {
			currentDynamic.setInstrumental(Boolean.valueOf(instrumental));
		}
	}

	private void askAcoustic(final DynamicPlaylist currentDynamic) throws IOException {
		// acoustic
		println("Acoustic (true/false): ");
		final String acoustic = input.readLine();
		if(!acoustic.isEmpty()) {
			currentDynamic.setAcoustic(Boolean.valueOf(acoustic));
		}
	}

	private void askDanceability(final DynamicPlaylist currentDynamic) throws IOException {
		// danceability
		println("Danceability [0..1]: ");
		final String danceability = input.readLine();
		if(!danceability.isEmpty()) {
			currentDynamic.setDanceability(Double.valueOf(danceability));
		}
	}

	private void askValence(final DynamicPlaylist currentDynamic) throws IOException {
		// valence
		println("Valence [0..1]: ");
		final String valence = input.readLine();
		if(!valence.isEmpty()) {
			currentDynamic.setValence(Double.valueOf(valence));
		}
	}

	private void askEnergy(final DynamicPlaylist currentDynamic) throws IOException {
		// energy
		println("Energy [0..1]: ");
		final String energy = input.readLine();
		if(!energy.isEmpty()) {
			currentDynamic.setEnergy(Double.valueOf(energy));
		}
	}

	private void askBPM(final DynamicPlaylist currentDynamic) throws IOException {
		// bpm
		println("BPM [50..300]: ");
		final String bpm = input.readLine();
		if(!bpm.isEmpty()) {
			currentDynamic.setBPM(Short.valueOf(bpm));
		}
	}

	private void select(String value) {
		final Playlist playlist = database.getPlaylists()
				.withName(value)
				.first();
		if(playlist == null) {
			println("No playlist found with name: " + value);
		}
		else {
			current = playlist;
			if(current instanceof DynamicPlaylistImpl) {
				((DynamicPlaylistImpl) current).setDatabase(database);
			}
		}
	}

	private void create(String value) {
		// TODO this is ugly
		final DynamicPlaylistImpl playlist = new DynamicPlaylistImpl(value);
		playlist.setDatabase(database);
		database.addPlaylist(playlist);
		current = playlist;
	}

	private void list() {
		database.getPlaylists().stream()
		.map(Playlist::toString)
		.forEach(this::println);
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
		throw new UnsupportedOperationException("Undo is not supported for this operation");
	}
}
