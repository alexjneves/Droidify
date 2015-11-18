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
    void registerStateChangeListener(final IDroidifyPlayerStateChangeListener droidifyPlayerStateChangeListener);
    String getCurrentTrack();
    boolean isShuffleOn();
}
