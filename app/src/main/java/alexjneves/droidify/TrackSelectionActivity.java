package alexjneves.droidify;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.File;

public class TrackSelectionActivity extends AppCompatActivity {
    private File musicDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_selection);

        setMusicDirectory(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));

        try {
            readTracks();
        } catch (Exception e) {
            e.printStackTrace();

            // Popup Window?
        }
    }

    private void setMusicDirectory(File directory) {
        musicDirectory = directory;

        TextView musicDirectoryTextView = (TextView) findViewById(R.id.musicDirectory);
        musicDirectoryTextView.setText(musicDirectory.getPath());
    }

    private void readTracks() throws Exception {
        if (!isExternalStorageReadable()) {
            throw new Exception("Unable to read external storage");
        }

        ReadTracksTask readTracksTask = new ReadTracksTask(this);
        readTracksTask.execute(musicDirectory);
    }

    private boolean isExternalStorageReadable() {
        String currentState = Environment.getExternalStorageState();

        return currentState.equals(Environment.MEDIA_MOUNTED) ||
                currentState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

}
