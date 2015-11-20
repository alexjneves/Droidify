package alexjneves.droidify.service;

/**
 * The binder interface which will be returned to the client when binding to the droidify player
 * service.
 */
public interface IDroidifyPlayerServiceBinder {
    /**
     * Get the interface to the droidify player service.
     *
     * @return The droidify player instance
     */
    IDroidifyPlayer getDroidifyPlayer();
}
