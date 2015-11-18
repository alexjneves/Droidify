package alexjneves.droidify.service;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

final class PlayableTrack implements MediaPlayer.OnCompletionListener {
    private final String resourcePath;
    private final List<MediaPlayer.OnCompletionListener> onCompletionListeners;
    private MediaPlayerFactory mediaPlayerFactory;
    private MediaPlayer mediaPlayer;

    public PlayableTrack(final String resourcePath, final Context applicationContext) {
        this.resourcePath = resourcePath;
        this.onCompletionListeners = new ArrayList<>();
        this.mediaPlayerFactory = new MediaPlayerFactory(applicationContext, resourcePath);
        this.mediaPlayer = new MediaPlayer();
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void play() {
        if (mediaPlayer == null) {
            mediaPlayer = mediaPlayerFactory.createPreparedPlayer();
        }

        mediaPlayer.start();
    }

    public void pause() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer = null;
    }

    public void setVolume(final float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    public void registerOnCompletionListener(final MediaPlayer.OnCompletionListener onCompletionListener) {
        this.onCompletionListeners.add(onCompletionListener);
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        stop();

        for (final MediaPlayer.OnCompletionListener onCompletionListener : onCompletionListeners) {
            onCompletionListener.onCompletion(mediaPlayer);
        }
    }
}
