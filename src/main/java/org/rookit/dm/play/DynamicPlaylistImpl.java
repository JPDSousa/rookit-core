package org.rookit.dm.play;

import static org.rookit.dm.play.TypePlaylist.DYNAMIC;

import java.util.stream.Stream;

import org.mongodb.morphia.annotations.Entity;
import org.rookit.dm.track.Track;
import org.rookit.dm.track.audio.TrackKey;
import org.rookit.dm.track.audio.TrackMode;
import org.rookit.mongodb.DBManager;
import org.rookit.mongodb.queries.TrackQuery;
import org.rookit.mongodb.utils.Order;
import org.rookit.mongodb.utils.Order.TypeOrder;

@SuppressWarnings("javadoc")
@Entity("Playlist")
public class DynamicPlaylistImpl extends AbstractPlaylist implements DynamicPlaylist {

	private static final int LIMIT = 50;

	// Audio features
	private short bpm;
	private short bpmGap;

	private TrackKey trackKey;

	private TrackMode trackMode;

	private Boolean isInstrumental;

	private Boolean isLive;

	private Boolean isAcoustic;

	private double danceability;
	private float danceabilityGap;

	private double energy;
	private float energyGap;

	private double valence;
	private float valenceGap;
	
	private transient DBManager db;
	
	@SuppressWarnings("unused")
	private DynamicPlaylistImpl() {
		this(null);
	}

	public DynamicPlaylistImpl(String name) {
		super(DYNAMIC, name);
		bpm = -1;
		bpmGap = 10;
		danceability = -1;
		danceabilityGap = 0.2f;
		energy = -1;
		energyGap = 0.1f;
		valence = -1;
		valenceGap = 0.1f;
	}
	
	public void setDatabase(DBManager db) {
		this.db = db;
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
	public Boolean isInstrumental() {
		return isInstrumental;
	}

	@Override
	public Void setInstrumental(Boolean isInstrumental) {
		this.isInstrumental = isInstrumental;
		return null;
	}

	@Override
	public Boolean isLive() {
		return isLive;
	}

	@Override
	public Void setLive(Boolean isLive) {
		this.isLive = isLive;
		return null;
	}

	@Override
	public Boolean isAcoustic() {
		return isAcoustic;
	}

	@Override
	public Void setAcoustic(Boolean isAcoustic) {
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
		final TrackQuery query = db.getTracks();
		final Order order = Order.create();
		order.addField(PLAYS, TypeOrder.DSC);
		setBpm(query);
		setTrackKey(query);
		setTrackMode(query);
		setInstrumental(query);
		setLive(query);
		setAcoustic(query);
		setDanceability(query);
		setEnergy(query);
		setValence(query);
		
		// TODO use other props
		// TODO order by plays[DSC], release data[DSC]
		return query.order(order).stream();
	}

	private void setValence(TrackQuery query) {
		if(valence > 0) {
			query.withValence(valence-(valenceGap/2), 
					valence+(valenceGap/2));
		}
	}

	private void setDanceability(TrackQuery query) {
		if(danceability > 0) {
			query.withDanceability(danceability-(danceabilityGap/2), 
					danceability+(danceabilityGap/2));
		}
	}

	private void setEnergy(TrackQuery query) {
		if(energy > 0) {
			query.withEnergy(energy-(energyGap/2), energy+(energyGap/2));
		}
	}

	private void setAcoustic(TrackQuery query) {
		if(isAcoustic != null) {
			query.withAcoustic(isAcoustic);
		}
	}

	private void setLive(TrackQuery query) {
		if(isLive != null) {
			query.withLive(isLive);
		}
	}

	private void setInstrumental(TrackQuery query) {
		if(isInstrumental != null) {
			query.withInstrumental(isInstrumental);
		}
	}

	private void setTrackMode(TrackQuery query) {
		if(trackMode != null) {
			query.withTrackMode(trackMode);
		}
	}

	private void setTrackKey(TrackQuery query) {
		if(trackKey != null) {
			query.withTrackKey(trackKey);
		}
	}

	private void setBpm(TrackQuery query) {
		if(bpm > 0) {
			query.withBPM((short) (bpm-(bpmGap/2)), (short) (bpm+(bpmGap/2)));
		}
	}

	@Override
	public StaticPlaylist freeze() {
		return freeze(LIMIT);
	}

	@Override
	public StaticPlaylist freeze(int limit) {
		
		return null;
	}

}
