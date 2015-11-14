package alexjneves.droidify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import alexjneves.droidify.service.DroidifyPlayerService;
import alexjneves.droidify.service.IDroidifyPlayer;

public final class TrackSelectionActivity extends AppCompatActivity implements IDroidifyPlayerRetrievedListener, ITrackListRetrievedListener, IRunOnUiThread {
    private final TrackListViewAdapterFactory trackListViewAdapterFactory;

    private String musicDirectory;
    private IDroidifyPlayer droidifyPlayer;
    private DroidifyPlayerServiceConnection droidifyPlayerServiceConnection;
    private TrackPlayPauseButton trackPlayPauseButton;
    private TrackListView trackListView;

    public TrackSelectionActivity() {
        trackListViewAdapterFactory = new TrackListViewAdapterFactory();

        musicDirectory = null;
        droidifyPlayer = null;
        trackPlayPauseButton = null;
        trackListView = null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_track_selection);

        droidifyPlayerServiceConnection = new DroidifyPlayerServiceConnection(this);

        // TODO: Investigate different bind constants
        // TODO: Make foreground service
        final Intent startDroidifyPlayerServiceIntent = new Intent(this, DroidifyPlayerService.class);
        this.bindService(startDroidifyPlayerServiceIntent, droidifyPlayerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (droidifyPlayer != null) {
            this.unbindService(droidifyPlayerServiceConnection);
        }
    }

    @Override
    public void onDroidifyPlayerRetrieved(final IDroidifyPlayer droidifyPlayer) {
        this.droidifyPlayer = droidifyPlayer;

        final Button playPauseButton = (Button) this.findViewById(R.id.playPauseButton);
        trackPlayPauseButton = TrackPlayPauseButton.Create(this.droidifyPlayer, playPauseButton, this);

        retrieveTrackList();
    }

    @Override
    public void onTrackListRetrieved(final List<Track> tracks) {
        final ListView trackListViewUi = (ListView) this.findViewById(R.id.trackList);
        final ListAdapter trackListViewAdapter = trackListViewAdapterFactory.createAdapter(this, tracks);
        final OnTrackClickListener onTrackClickListener = new OnTrackClickListener(tracks, droidifyPlayer);

        this.trackListView = new TrackListView(trackListViewUi, tracks, trackListViewAdapter, onTrackClickListener, this);

        this.trackListView.changeSelection(0);
        droidifyPlayer.changeTrack(tracks.get(0).getResourcePath());
    }

    private void retrieveTrackList() {
        final RetrieveTrackListTask retrieveTrackListTask = new RetrieveTrackListTask(getContentResolver(), this);
        retrieveTrackListTask.execute(musicDirectory);
    }

    @Override
    public void executeOnUiThread(final Runnable toRun) {
        this.runOnUiThread(toRun);
    }
}
