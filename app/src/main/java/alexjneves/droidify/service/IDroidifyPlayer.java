package alexjneves.droidify.service;

public interface IDroidifyPlayer {
    void changeTrack(final String resourcePath);
    void playCurrentTrack();
    void pauseCurrentTrack();
    void registerStateChangeListener(final IDroidifyPlayerStateChangeListener droidifyPlayerStateChangeListener);
}
