package alexjneves.droidify;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;

import alexjneves.droidify.service.DroidifyPlayerService;
import alexjneves.droidify.service.IDroidifyPlayer;

public final class TrackSelectionActivity extends AppCompatActivity implements IDroidifyPlayerRetrievedListener, ITrackListRetrievedListener, IRunOnUiThread {
    private final TrackListViewAdapterFactory trackListViewAdapterFactory;

    private String musicDirectory;
    private ListView trackListView;
    private IDroidifyPlayer droidifyPlayer;
    private DroidifyPlayerServiceConnection droidifyPlayerServiceConnection;
    private TrackPlayPauseButton trackPlayPauseButton;

    public TrackSelectionActivity() {
        trackListViewAdapterFactory = new TrackListViewAdapterFactory();

        musicDirectory = null;
        trackListView = null;
        droidifyPlayer = null;
        trackPlayPauseButton = null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_track_selection);

        trackListView = (ListView) this.findViewById(R.id.trackList);

        final Intent startDroidifyPlayerServiceIntent = new Intent(this, DroidifyPlayerService.class);
        // TODO: Investigate different bind constants
        // TODO: Make foreground service
        droidifyPlayerServiceConnection = new DroidifyPlayerServiceConnection(this);
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
        final SimpleAdapter trackListViewAdapter = trackListViewAdapterFactory.createAdapter(this, tracks);
        trackListView.setAdapter(trackListViewAdapter);

        final OnTrackClickListener onTrackClickListener = new OnTrackClickListener(tracks, droidifyPlayer);
        trackListView.setOnItemClickListener(onTrackClickListener);
    }

    private void retrieveTrackList() {
        final RetrieveTrackListTask retrieveTrackListTask = new RetrieveTrackListTask(getContentResolver(), this);
        retrieveTrackListTask.execute(musicDirectory);
    }

    @Override
    public void run(final Runnable toRun) {
        this.runOnUiThread(toRun);
    }
}
