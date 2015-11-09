package alexjneves.droidify;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alexjneves.droidify.service.DroidifyPlayerService;
import alexjneves.droidify.service.IDroidifyPlayer;

public final class TrackSelectionActivity extends AppCompatActivity implements ITrackListRetrievedListener, IDroidifyPlayerRetrievedListener {
    private String musicDirectory;
    private ListView trackListView;
    private IDroidifyPlayer droidifyPlayer;
    private DroidifyPlayerServiceConnection droidifyPlayerServiceConnection;
    private final TrackListViewAdapterFactory trackListViewAdapterFactory;

    public TrackSelectionActivity() {
        musicDirectory = null;
        trackListView = null;
        droidifyPlayer = null;
        droidifyPlayerServiceConnection = new DroidifyPlayerServiceConnection(this);
        trackListViewAdapterFactory = new TrackListViewAdapterFactory();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_track_selection);

        trackListView = (ListView) this.findViewById(R.id.trackList);

        setMusicDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());

        final Intent startDroidifyPlayerServiceIntent = new Intent(this, DroidifyPlayerService.class);
        // TODO: Investigate different bind constants
        // TODO: Make foreground service
        this.bindService(startDroidifyPlayerServiceIntent, droidifyPlayerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!droidifyPlayer.equals(null)) {
            this.unbindService(droidifyPlayerServiceConnection);
        }
    }

    private void setMusicDirectory(final String directory) {
        musicDirectory = directory;

        final TextView musicDirectoryTextView = (TextView) findViewById(R.id.musicDirectory);
        musicDirectoryTextView.setText(musicDirectory);
    }

    private void retrieveTrackList() {
        if (!isExternalStorageReadable()) {
            throw new RuntimeException("Unable to read external storage");
        }

        final RetrieveTrackListTask retrieveTrackListTask = new RetrieveTrackListTask(this);
        retrieveTrackListTask.execute(musicDirectory);
    }

    private boolean isExternalStorageReadable() {
        final String currentState = Environment.getExternalStorageState();

        return currentState.equals(Environment.MEDIA_MOUNTED) ||
                currentState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

    @Override
    public void onTrackListRetrieved(final List<Track> tracks) {
        final SimpleAdapter trackListViewAdapter = trackListViewAdapterFactory.createAdapter(this, tracks);
        trackListView.setAdapter(trackListViewAdapter);

        final OnTrackClickListener onTrackClickListener = new OnTrackClickListener(tracks, droidifyPlayer);
        trackListView.setOnItemClickListener(onTrackClickListener);
    }

    @Override
    public void onDroidifyPlayerRetrieved(final IDroidifyPlayer droidifyPlayer) {
        this.droidifyPlayer = droidifyPlayer;

        retrieveTrackList();
    }
}
