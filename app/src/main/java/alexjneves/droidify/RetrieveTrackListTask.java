package alexjneves.droidify;

import android.os.AsyncTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class RetrieveTrackListTask extends AsyncTask<File, Void, List<Track>> {
    private final ITrackListRetrievedListener trackListRetrievedListener;

    public RetrieveTrackListTask(final ITrackListRetrievedListener trackListRetrievedListener) {
        this.trackListRetrievedListener = trackListRetrievedListener;
    }

    @Override
    protected List<Track> doInBackground(File... params) {
        if (params.length <= 0 || params[0].equals(null)) {
            throw new RuntimeException("Root File not provided");
        }

        final File rootDirectory = params[0];

        final RecursiveFileReader recursiveFileReader = new RecursiveFileReader(rootDirectory);
        final List<File> allFiles = recursiveFileReader.getFiles();

        final SupportedAudioFileFilter supportedAudioFileFilter = new SupportedAudioFileFilter(allFiles);
        final List<File> supportedFiles = supportedAudioFileFilter.getSupportedAudioFiles();

        final List<Track> tracks = new ArrayList<>();
        for (File supportedFile : supportedFiles) {
            final AudioFile audioFile = new AudioFile(supportedFile);
            final AudioFileMetadata metadata = new AudioFileMetadata(audioFile);

            tracks.add(new Track(audioFile, metadata));
        }

        return tracks;
    }

    @Override
    protected void onPostExecute(List<Track> tracks) {
        super.onPostExecute(tracks);

        trackListRetrievedListener.onTrackListRetrieved(tracks);
    }
}
