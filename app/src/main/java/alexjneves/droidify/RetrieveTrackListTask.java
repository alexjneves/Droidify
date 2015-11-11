package alexjneves.droidify;

import android.content.ContentResolver;
import android.os.AsyncTask;

import java.util.List;

public final class RetrieveTrackListTask extends AsyncTask<String, Void, List<Track>> {
    private final DeviceAudioMediaRetriever deviceAudioMediaRetriever;
    private final ITrackListRetrievedListener trackListRetrievedListener;

    public RetrieveTrackListTask(final ContentResolver contentResolver, final ITrackListRetrievedListener trackListRetrievedListener) {
        this.deviceAudioMediaRetriever = new DeviceAudioMediaRetriever(contentResolver);
        this.trackListRetrievedListener = trackListRetrievedListener;
    }

    @Override
    protected List<Track> doInBackground(final String... params) {
        return deviceAudioMediaRetriever.retrieveTracks();
    }

    @Override
    protected void onPostExecute(final List<Track> tracks) {
        super.onPostExecute(tracks);

        trackListRetrievedListener.onTrackListRetrieved(tracks);
    }
}
