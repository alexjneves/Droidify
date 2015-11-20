package alexjneves.droidify.service;

/**
 * An interface to listen for when the current track completes playback.
 */
public interface ITrackCompleteListener {
    /**
     * This callback will be triggered when the current track completes playback.
     */
    void onTrackComplete();
}
