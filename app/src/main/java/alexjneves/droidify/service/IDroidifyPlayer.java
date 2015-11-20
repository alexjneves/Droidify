package alexjneves.droidify.service;

import java.util.List;

public interface IDroidifyPlayer {
    /**
     * Change the current track. This will prepare the resource but not automatically
     * begin playback.
     *
     * @param resourcePath The path to the audio resource to be played
     */
    void changeTrack(final String resourcePath);

    /**
     * Change the current playlist. This will not set the current track, requiring
     * {@link #changeTrack(String)} )} to be called afterwards.
     *
     * @param resourcePaths A collection of paths to the audio resources to be played
     */
    void changePlaylist(final List<String> resourcePaths);

    /**
     * Play the currently set track.
     */
    void playCurrentTrack();

    /**
     * Pause the currently set track.
     */
    void pauseCurrentTrack();

    /**
     * Change the currently set track to be the next track in the playlist. Note that the next
     * track is affected by shuffle (see {@link #toggleShuffle(boolean)}).
     */
    void skipForward();

    /**
     * Change the currently set track to be the previous track in the playlist. Note that the next
     * track is affected by shuffle (see {@link #toggleShuffle(boolean)}).
     */
    void skipBackward();

    /**
     * Toggles shuffle. When set, the currently set playlist will be randomised, thus affecting
     * the previous/next track to be played.
     *
     * @param on Shuffle on or off
     */
    void toggleShuffle(final boolean on);

    /**
     * Toggles auto play. When on, the next track in the playlist will begin playback when the
     * current track has completed without explicit calls to {@link #changeTrack(String)} and
     * {@link #playCurrentTrack()}.
     *
     * @param on Auto play on or off
     */
    void toggleAutoPlay(final boolean on);

    /**
     * Changes the volume of the droidify player. This is on a scale of 0.0f - 1.0f.
     *
     * @param volume The new volume
     */
    void setVolume(final float volume);

    /**
     * Registers a state change listener to be notified of any internal state change within
     * the droidify player.
     *
     * @param droidifyPlayerStateChangeListener The listener to be notified of any state change
     */
    void registerStateChangeListener(final IDroidifyPlayerStateChangeListener droidifyPlayerStateChangeListener);

    /**
     * Query the player for the currently set track.
     *
     * @return The resource path to the currently set audio track.
     */
    String getCurrentTrack();

    /**
     * Query the player for the current shuffle state.
     *
     * @return A boolean representing the shuffle state, where true == on.
     */
    boolean isShuffleOn();
}
