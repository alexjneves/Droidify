package alexjneves.droidify;

import alexjneves.droidify.service.IDroidifyPlayer;

/**
 * A custom callback used to know when our ServiceConnection implementation has retrieved the
 * instance of the Droidify Service.
 */
public interface IDroidifyPlayerRetrievedListener {
    /**
     * Callback to be executed when the service instance has been retrieved.
     *
     * @param droidifyPlayer The IDroidifyPlayer instance
     */
    void onDroidifyPlayerRetrieved(final IDroidifyPlayer droidifyPlayer);
}
