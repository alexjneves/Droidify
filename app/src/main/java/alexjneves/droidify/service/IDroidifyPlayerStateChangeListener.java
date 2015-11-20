package alexjneves.droidify.service;

/**
 * An interface to listen to any state changes within the droidify player.
 */
public interface IDroidifyPlayerStateChangeListener {
    /**
     * This callback will be triggered when the droidify player changes state.
     *
     * @param newState The new state of the droidify player
     */
    void onDroidifyPlayerStateChange(final DroidifyPlayerState newState);
}
