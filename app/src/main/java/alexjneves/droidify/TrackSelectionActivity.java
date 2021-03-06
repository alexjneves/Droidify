package alexjneves.droidify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import alexjneves.droidify.service.DroidifyPlayerService;
import alexjneves.droidify.service.IDroidifyPlayer;
import alexjneves.droidify.service.IDroidifyPlayerOnErrorListener;

public final class TrackSelectionActivity extends AppCompatActivity implements IRunOnUiThread, IDroidifyPlayerRetrievedListener, ITrackListRetrievedListener, IDroidifyPlayerOnErrorListener {
    private final TrackListViewAdapterFactory trackListViewAdapterFactory;

    private IDroidifyPlayer droidifyPlayer;
    private DroidifyPlayerServiceConnection droidifyPlayerServiceConnection;
    private TrackPlayPauseButton trackPlayPauseButton;
    private TrackShuffleButton trackShuffleButton;
    private TrackListView trackListView;
    private DroidifyPreferencesEditor droidifyPreferencesEditor;
    private TrackChangedBroadcastReceiver trackChangedBroadcastReceiver;
    private Toast errorMessageToast;

    public TrackSelectionActivity() {
        trackListViewAdapterFactory = new TrackListViewAdapterFactory();

        droidifyPlayer = null;
        trackPlayPauseButton = null;
        trackShuffleButton = null;
        trackListView = null;
        droidifyPreferencesEditor = null;
        trackChangedBroadcastReceiver = null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_track_selection);

        errorMessageToast = Toast.makeText(this, "", Toast.LENGTH_LONG);

        droidifyPlayerServiceConnection = new DroidifyPlayerServiceConnection(this);
        droidifyPreferencesEditor = new DroidifyPreferencesEditor(getPreferences(MODE_PRIVATE));

        final Intent startDroidifyPlayerServiceIntent = new Intent(this, DroidifyPlayerService.class);
        this.bindService(startDroidifyPlayerServiceIntent, droidifyPlayerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (droidifyPlayer != null) {
            final String lastPlayedTrack = droidifyPlayer.getCurrentTrack();
            droidifyPreferencesEditor.writeLastPlayedTrack(lastPlayedTrack);

            final boolean shuffleOn = droidifyPlayer.isShuffleOn();
            droidifyPreferencesEditor.writeShuffleOn(shuffleOn);

            this.unbindService(droidifyPlayerServiceConnection);
        }

        if (trackChangedBroadcastReceiver != null) {
            trackChangedBroadcastReceiver.unregister();
        }
    }

    @Override
    public void executeOnUiThread(final Runnable toRun) {
        this.runOnUiThread(toRun);
    }

    @Override
    public void onDroidifyPlayerRetrieved(final IDroidifyPlayer droidifyPlayer) {
        this.droidifyPlayer = droidifyPlayer;
        this.droidifyPlayer.registerOnErrorListener(this);

        final Button playPauseButton = (Button) this.findViewById(R.id.playPauseButton);
        trackPlayPauseButton = TrackPlayPauseButton.create(this.droidifyPlayer, playPauseButton, this);

        final RetrieveTrackListTask retrieveTrackListTask = new RetrieveTrackListTask(getContentResolver(), this);
        retrieveTrackListTask.execute();
    }

    @Override
    public void onTrackListRetrieved(final List<Track> tracks) {
        trackListView = createTrackListView(tracks);
        trackChangedBroadcastReceiver = new TrackChangedBroadcastReceiver(this, this, trackListView);

        final List<String> resourcePaths = getTrackResourceIds(tracks);
        droidifyPlayer.changePlaylist(resourcePaths);

        trackShuffleButton = createTrackShuffleButton();

        trySetPreviouslyPlayedTrack(resourcePaths);
    }

    private TrackListView createTrackListView(final List<Track> tracks) {
        final ListView trackListViewUi = (ListView) this.findViewById(R.id.trackList);
        final ListAdapter trackListViewAdapter = trackListViewAdapterFactory.createAdapter(this, tracks);
        final OnTrackClickListener onTrackClickListener = new OnTrackClickListener(tracks, droidifyPlayer);

        return new TrackListView(trackListViewUi, tracks, trackListViewAdapter, onTrackClickListener, this);
    }

    private TrackShuffleButton createTrackShuffleButton() {
        final Button shuffleButton = (Button) this.findViewById(R.id.shuffleButton);
        final boolean shuffleOn = droidifyPreferencesEditor.readShuffleOn();

        return TrackShuffleButton.create(shuffleButton, droidifyPlayer, shuffleOn);
    }

    private List<String> getTrackResourceIds(final List<Track> tracks) {
        final List<String> resourcePaths = new ArrayList<>();

        for (int i = 0; i < tracks.size(); ++i) {
            final String resourcePath = tracks.get(i).getResourcePath();

            resourcePaths.add(resourcePath);
        }

        return resourcePaths;
    }

    private void trySetPreviouslyPlayedTrack(final List<String> resourcePaths) {
        final String lastPlayedTrack = droidifyPreferencesEditor.readLastPlayedTrack();

        boolean trackExists = false;

        for (final String resourcePath : resourcePaths) {
            if (resourcePath.equals(lastPlayedTrack)) {
                trackExists = true;
                break;
            }
        }

        if (trackExists) {
            droidifyPlayer.changeTrack(lastPlayedTrack);
        } else if (!resourcePaths.isEmpty()) {
            droidifyPlayer.changeTrack(resourcePaths.get(0));
        }
    }

    @Override
    public void onDroidifyPlayerError(final String errorMessage) {
        errorMessageToast.setText(errorMessage);
        errorMessageToast.show();
    }

    public void onBackwardButtonClick(final View view) {
        if (droidifyPlayer != null) {
            droidifyPlayer.skipBackward();
        }
    }

    public void onForwardButtonClick(final View view) {
        if (droidifyPlayer != null) {
            droidifyPlayer.skipForward();
        }
    }
}
