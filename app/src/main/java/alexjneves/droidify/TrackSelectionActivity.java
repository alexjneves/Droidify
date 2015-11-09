package alexjneves.droidify;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TrackSelectionActivity extends AppCompatActivity implements ITrackListRetrievedListener {
    private static final String TRACK_LIST_VIEW_MAIN_TEXT = "title";
    private static final String TRACK_LIST_VIEW_SUB_TEXT = "artist";

    private File musicDirectory;
    private ListView trackListView;

    public TrackSelectionActivity() {
        musicDirectory = null;
        trackListView = null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_selection);

        trackListView = (ListView) this.findViewById(R.id.trackList);

        setMusicDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));

        try {
            retrieveTrackList();
        } catch (Exception e) {
            e.printStackTrace();

            // Popup Window?
        }
    }

    private void setMusicDirectory(final File directory) {
        musicDirectory = directory;

        final TextView musicDirectoryTextView = (TextView) findViewById(R.id.musicDirectory);
        musicDirectoryTextView.setText(musicDirectory.getPath());
    }

    private void retrieveTrackList() throws Exception {
        if (!isExternalStorageReadable()) {
            throw new Exception("Unable to read external storage");
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
        final OnTrackClickListener onTrackClickListener = new OnTrackClickListener(tracks);
        trackListView.setOnItemClickListener(onTrackClickListener);

        updateTrackListUi(tracks);
    }

    private void updateTrackListUi(final List<Track> tracks) {
        final List<Map<String, String>> trackListViewData = new ArrayList<>();

        for (Track track : tracks) {
            final Map<String, String> trackData = new HashMap<>(2);

            final AudioFileMetadata metadata = track.getMetadata();

            trackData.put(TRACK_LIST_VIEW_MAIN_TEXT, metadata.getTitle());
            trackData.put(TRACK_LIST_VIEW_SUB_TEXT, metadata.getArtist());

            trackListViewData.add(trackData);
        }

        SimpleAdapter trackListViewAdapter = new SimpleAdapter(this, trackListViewData,
                android.R.layout.simple_list_item_2,
                new String[] { TRACK_LIST_VIEW_MAIN_TEXT, TRACK_LIST_VIEW_SUB_TEXT },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );

        trackListView.setAdapter(trackListViewAdapter);
    }
}
