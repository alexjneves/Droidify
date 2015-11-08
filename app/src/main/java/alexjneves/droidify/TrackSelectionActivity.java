package alexjneves.droidify;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TrackSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_selection);

        try {
            readTrackNames();
        } catch (Exception e) {
            e.printStackTrace();

            // Popup Window?
        }
    }

    private void readTrackNames() throws Exception {
        EditText numberOfTracks = (EditText) findViewById(R.id.text_numberOfTracks);

        numberOfTracks.setText("Hello");

        if (!isExternalStorageReadable()) {
            throw new Exception("Unable to read external storage");
        }

        File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        ReadTracksTask readTracksTask = new ReadTracksTask();
        readTracksTask.execute(musicDirectory);
    }

    private boolean isExternalStorageReadable() {
        String currentState = Environment.getExternalStorageState();

        return currentState.equals(Environment.MEDIA_MOUNTED) ||
                currentState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }

}
