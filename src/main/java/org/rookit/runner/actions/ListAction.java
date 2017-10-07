package org.rookit.runner.actions;

import org.extendedCLI.command.AbstractCommand;
import org.extendedCLI.command.Command;
import org.extendedCLI.command.ExtendedCommandLine;
import org.rookit.core.utils.CoreValidator;
import org.rookit.dm.album.Album;
import org.rookit.dm.artist.Artist;
import org.rookit.dm.genre.Genre;
import org.rookit.dm.utils.PrintUtils;
import org.rookit.mongodb.DBManager;

import static org.rookit.runner.actions.ElementType.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

@SuppressWarnings("javadoc")
public class ListAction extends AbstractCommand implements Command {
	
	private static Pattern fromQuery(String query) {
		final String quoted = Pattern.quote(query);
		return Pattern.compile(".*"+quoted, Pattern.CASE_INSENSITIVE);
	}
	
	private final CoreValidator validator;
	private final DBManager db;

	public ListAction(DBManager db) {
		super(ListOptions.createArguments());
		this.validator = CoreValidator.getDefault();
		this.db = db;
	}

	@Override
	protected void execute(ExtendedCommandLine input) {
		final String query = input.getValue(ListOptions.QUERY);
		final ElementType element = ElementType.valueOf(input.getValue(ListOptions.ELEMENT));
		if(element == ALL) {
			Arrays.stream(values())
			.filter(e -> e != ALL)
			.forEach(e -> search(query, e));
		}
		else {
			search(query, element);
		}
	}
	
	@SuppressWarnings("incomplete-switch")
	private void search(String query, ElementType e) {
		try {
			switch(e) {
			case ALBUM:
				searchAlbum(query);
				break;
			case ARTIST:
				searchArtist(query);
				break;
			case GENRE:
				searchGenre(query);
				break;
			case TRACK:
				searchTrack(query);
				break;
			}			
		} catch(IOException ex) {
			validator.handleIOException(ex);
		}
	}
	
	private void searchTrack(String query) throws IOException {
		final Pattern pattern = fromQuery(query);
		output.println("Tracks: " + query);
		db.getTracks().withTitle(pattern)
		.stream()
		.forEach(t -> PrintUtils.track(t));
	}
	
	private void searchGenre(String query) throws IOException {
		output.println("Genres: " + query);
		db.getGenres()
		.withName(fromQuery(query))
		.stream()
		.forEach(this::printGenre);
	}
	
	private void printGenre(Genre genre) {
		try {
			output.println(genre.toString());
		} catch (IOException e) {
			validator.handleIOException(e);
		}
	}
	
	private void searchArtist(String query) throws IOException {
		output.println("Artists: " + query);
		db.getArtists()
		.withName(fromQuery(query))
		.stream()
		.forEach(this::printArtist);
	}
	
	private void printArtist(Artist artist) {
		try {
			output.println(PrintUtils.artist(artist));
		} catch (IOException e) {
			validator.handleIOException(e);
		}
	}
	
	private void searchAlbum(String query) throws IOException {
		output.println("Albums: " + query);
		db.getAlbums()
		.withTitle(fromQuery(query))
		.stream()
		.forEach(this::printAlbum);
	}
	
	private void printAlbum(Album album) {
		try {
			output.println(PrintUtils.album(album));
		} catch (IOException e) {
			validator.handleIOException(e);
		}
	}

	@Override
	public void undo() {
		validator.info("Why undo a listing?");
	}

}
