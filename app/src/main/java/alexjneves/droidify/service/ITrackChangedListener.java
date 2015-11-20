package alexjneves.droidify.service;

/**
 * An interface to listen for when the current track has changed within the droidify player
 */
interface ITrackChangedListener {
    /**
     * This callback will be triggered when the current track has changed.
     *
     * @param resourcePath The resource path of the newly set track
     */
    void onTrackChanged(final String resourcePath);
}
