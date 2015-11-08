package alexjneves.droidify;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ReadTracksTask extends AsyncTask<File, Void, List<Track>> {
    private final Activity activityContext;

    public ReadTracksTask(Activity activityContext) {
        this.activityContext = activityContext;
    }

    @Override
    protected List<Track> doInBackground(File... params) {
        if (params.length <= 0) {
            throw new RuntimeException();
        }

        final File rootDirectory = params[0];

        final RecursiveFileReader recursiveFileReader = new RecursiveFileReader(rootDirectory);

        final List<File> allFiles = recursiveFileReader.getFiles();
        final SupportedAudioFileFilter supportedAudioFileFilter = new SupportedAudioFileFilter(allFiles);

        final List<File> supportedFiles = supportedAudioFileFilter.getSupportedAudioFiles();

        final List<Track> tracks = new ArrayList<>();

        for (File supportedFile : supportedFiles) {
            tracks.add(new Track(supportedFile));
        }

        return tracks;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {
        super.onPostExecute(tracks);

        // TODO: Remove debug logging
        for (Track track : tracks) {
            final String fileName = track.getName();
            Log.d(DroidifyConstants.LogCategory, fileName);
        }

        final ListView trackList = (ListView) activityContext.findViewById(R.id.trackList);

        // TODO: Remove warning
        ArrayAdapter trackAdapter = new ArrayAdapter(activityContext, android.R.layout.simple_list_item_1, tracks);
        trackList.setAdapter(trackAdapter);
    }
}
