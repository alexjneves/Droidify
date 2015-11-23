package alexjneves.droidify;

import java.util.List;

/**
 * A custom callback to know when the asynchronous retrieval of device audio media has completed.
 */
public interface ITrackListRetrievedListener {
    /**
     * Callback to be executed when track information has been retrieved.
     *
     * @param tracks The collection of tracks that exist on the device.
     */
    void onTrackListRetrieved(final List<Track> tracks);
}
