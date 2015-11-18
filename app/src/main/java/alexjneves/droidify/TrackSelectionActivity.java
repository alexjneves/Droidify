package alexjneves.droidify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import alexjneves.droidify.service.DroidifyPlayerService;
import alexjneves.droidify.service.IDroidifyPlayer;

// TODO: Handle Service Error State

public final class TrackSelectionActivity extends AppCompatActivity implements IDroidifyPlayerRetrievedListener, ITrackListRetrievedListener, IRunOnUiThread {
    private final TrackListViewAdapterFactory trackListViewAdapterFactory;

    private String musicDirectory;
    private IDroidifyPlayer droidifyPlayer;
    private DroidifyPlayerServiceConnection droidifyPlayerServiceConnection;
    private TrackPlayPauseButton trackPlayPauseButton;
    private TrackShuffleButton trackShuffleButton;
    private TrackListView trackListView;
    private DroidifyPreferencesEditor droidifyPreferencesEditor;

    public TrackSelectionActivity() {
        trackListViewAdapterFactory = new TrackListViewAdapterFactory();

        musicDirectory = null;
        droidifyPlayer = null;
        trackPlayPauseButton = null;
        trackShuffleButton = null;
        trackListView = null;
        droidifyPreferencesEditor = null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_track_selection);

        droidifyPlayerServiceConnection = new DroidifyPlayerServiceConnection(this);
        droidifyPreferencesEditor = new DroidifyPreferencesEditor(getPreferences(MODE_PRIVATE));

        // TODO: Investigate different bind constants
        final Intent startDroidifyPlayerServiceIntent = new Intent(this, DroidifyPlayerService.class);
        this.bindService(startDroidifyPlayerServiceIntent, droidifyPlayerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        final String lastPlayedTrack = droidifyPlayer.getCurrentTrack();
        droidifyPreferencesEditor.writeLastPlayedTrack(lastPlayedTrack);

        final boolean shuffleOn = droidifyPlayer.isShuffleOn();
        droidifyPreferencesEditor.writeShuffleOn(shuffleOn);

        if (droidifyPlayer != null) {
            this.unbindService(droidifyPlayerServiceConnection);
        }
    }

    @Override
    public void onDroidifyPlayerRetrieved(final IDroidifyPlayer droidifyPlayer) {
        this.droidifyPlayer = droidifyPlayer;

        final Button playPauseButton = (Button) this.findViewById(R.id.playPauseButton);
        trackPlayPauseButton = TrackPlayPauseButton.create(this.droidifyPlayer, playPauseButton, this);

        final RetrieveTrackListTask retrieveTrackListTask = new RetrieveTrackListTask(getContentResolver(), this);
        retrieveTrackListTask.execute(musicDirectory);
    }

    @Override
    public void onTrackListRetrieved(final List<Track> tracks) {
        final ListView trackListViewUi = (ListView) this.findViewById(R.id.trackList);
        trackListViewUi.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final ListAdapter trackListViewAdapter = trackListViewAdapterFactory.createAdapter(this, tracks);
        final OnTrackClickListener onTrackClickListener = new OnTrackClickListener(tracks, droidifyPlayer);

        this.trackListView = TrackListView.create(trackListViewUi, tracks, trackListViewAdapter, onTrackClickListener, this, droidifyPlayer);

        final String lastPlayedTrack = droidifyPreferencesEditor.readLastPlayedTrack();
        int currentSelection = 0;

        final List<String> resourcePaths = new ArrayList<>();
        for (int i = 0; i < tracks.size(); ++i) {
            final String resourcePath = tracks.get(i).getResourcePath();

            resourcePaths.add(resourcePath);

            if (resourcePath.equals(lastPlayedTrack)) {
                currentSelection = i;
            }
        }

        droidifyPlayer.changePlaylist(resourcePaths);

        final Button shuffleButton = (Button) this.findViewById(R.id.shuffleButton);
        final boolean shuffleOn = droidifyPreferencesEditor.readShuffleOn();
        this.trackShuffleButton = TrackShuffleButton.create(shuffleButton, droidifyPlayer, shuffleOn);

        this.droidifyPlayer.changeTrack(tracks.get(currentSelection).getResourcePath());
    }

    @Override
    public void executeOnUiThread(final Runnable toRun) {
        this.runOnUiThread(toRun);
    }

    public void onBackwardButtonClick(final View view) {
        droidifyPlayer.skipBackward();
    }

    public void onForwardButtonClick(final View view) {
        droidifyPlayer.skipForward();
    }
}
