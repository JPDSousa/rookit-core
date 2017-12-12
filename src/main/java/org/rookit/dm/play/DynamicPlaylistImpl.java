package org.rookit.dm.play;

import static org.rookit.dm.play.DatabaseFields.*;

import java.util.stream.Stream;

import org.rookit.dm.track.Track;
import org.rookit.dm.track.audio.TrackKey;
import org.rookit.dm.track.audio.TrackMode;
import org.smof.annnotations.SmofBoolean;
import org.smof.annnotations.SmofNumber;
import org.smof.annnotations.SmofString;

class DynamicPlaylistImpl extends AbstractPlaylist implements DynamicPlaylist {

	// Audio features
	@SmofNumber(name = BPM)
	private short bpm;

	@SmofString(name = KEY)
	private TrackKey trackKey;

	@SmofString(name = MODE)
	private TrackMode trackMode;

	@SmofBoolean(name = INSTRUMENTAL)
	private boolean isInstrumental;

	@SmofBoolean(name = LIVE)
	private boolean isLive;

	@SmofBoolean(name = ACOUSTIC)
	private boolean isAcoustic;

	@SmofNumber(name = DANCEABILITY)
	private double danceability;

	@SmofNumber(name = ENERGY)
	private double energy;

	@SmofNumber(name = VALENCE)
	private double valence;

	DynamicPlaylistImpl(String name) {
		super(name);
	}

	@Override
	public StaticPlaylist freeze() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StaticPlaylist freeze(int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public short getBPM() {
		return bpm;
	}

	@Override
	public Void setBPM(short bpm) {
		this.bpm = bpm;
		return null;
	}

	@Override
	public TrackKey getTrackKey() {
		return trackKey;
	}

	@Override
	public Void setTrackKey(TrackKey trackKey) {
		this.trackKey = trackKey;
		return null;
	}

	@Override
	public TrackMode getTrackMode() {
		return trackMode;
	}

	@Override
	public Void setTrackMode(TrackMode trackMode) {
		this.trackMode = trackMode;
		return null;
	}

	@Override
	public boolean isInstrumental() {
		return isInstrumental;
	}

	@Override
	public Void setInstrumental(boolean isInstrumental) {
		this.isInstrumental = isInstrumental;
		return null;
	}

	@Override
	public boolean isLive() {
		return isLive;
	}

	@Override
	public Void setLive(boolean isLive) {
		this.isLive = isLive;
		return null;
	}

	@Override
	public boolean isAcoustic() {
		return isAcoustic;
	}

	@Override
	public Void setAcoustic(boolean isAcoustic) {
		this.isAcoustic = isAcoustic;
		return null;
	}

	@Override
	public double getDanceability() {
		return danceability;
	}

	@Override
	public Void setDanceability(double danceability) {
		this.danceability = danceability;
		return null;
	}

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public Void setEnergy(double energy) {
		this.energy = energy;
		return null;
	}

	@Override
	public double getValence() {
		return valence;
	}

	@Override
	public Void setValence(double valence) {
		this.valence = valence;
		return null;
	}

	@Override
	public Stream<Track> streamTracks() {
		// TODO Auto-generated method stub
		return null;
	}

}
