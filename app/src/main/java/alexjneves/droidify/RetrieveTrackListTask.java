package alexjneves.droidify;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public final class RetrieveTrackListTask extends AsyncTask<String, Void, List<Track>> {
    private final ITrackListRetrievedListener trackListRetrievedListener;

    public RetrieveTrackListTask(final ITrackListRetrievedListener trackListRetrievedListener) {
        this.trackListRetrievedListener = trackListRetrievedListener;
    }

    @Override
    protected List<Track> doInBackground(final String... params) {
        if (params.length <= 0 || params[0] == null) {
            throw new RuntimeException("Root File not provided");
        }

        final String rootDirectoryPath = params[0];

        final recursiveFilePathReader recursiveFilePathReader = new recursiveFilePathReader(rootDirectoryPath);
        final List<String> allFilePaths = recursiveFilePathReader.getFilePaths();

        final SupportedAudioFileFilter supportedAudioFileFilter = new SupportedAudioFileFilter(allFilePaths);
        final List<String> supportedFiles = supportedAudioFileFilter.getSupportedAudioFiles();

        final List<Track> tracks = new ArrayList<>();
        for (final String supportedFile : supportedFiles) {
            final AudioFile audioFile = new AudioFile(supportedFile);
            final AudioFileMetadata metadata = new AudioFileMetadata(audioFile);

            tracks.add(new Track(audioFile, metadata));
        }

        return tracks;
    }

    @Override
    protected void onPostExecute(final List<Track> tracks) {
        super.onPostExecute(tracks);

        trackListRetrievedListener.onTrackListRetrieved(tracks);
    }
}
