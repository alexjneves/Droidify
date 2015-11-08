package alexjneves.droidify;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.util.List;

public final class ReadTracksTask extends AsyncTask<File, Void, List<File>> {
    @Override
    protected List<File> doInBackground(File... params) {
        final File rootDirectory = params[0];
        final RecursiveFileReader recursiveFileReader = new RecursiveFileReader(rootDirectory);

        return recursiveFileReader.getFiles();
    }

    @Override
    protected void onPostExecute(List<File> tracks) {
        super.onPostExecute(tracks);

        for (File track : tracks) {
            final String fileName = track.getName();
            Log.d(DroidifyConstants.LogCategory, fileName);
        }
    }
}
