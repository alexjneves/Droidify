package alexjneves.droidify.service;

import java.util.List;

public interface IDroidifyPlayer {
    void changeTrack(final String resourcePath);
    void changePlaylist(final List<String> resourcePaths);
    void playCurrentTrack();
    void pauseCurrentTrack();
    void skipForward();
    void skipBackward();
    void toggleShuffle(final boolean on);
    void toggleAutoPlay(final boolean on);
    void setVolume(final float volume);
    void registerStateChangeListener(final IDroidifyPlayerStateChangeListener droidifyPlayerStateChangeListener);
    void registerOnErrorListener(final IDroidifyPlayerOnErrorListener droidifyPlayerOnErrorListener);

    String getCurrentTrack();
    boolean isShuffleOn();
}
