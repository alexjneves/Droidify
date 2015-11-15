package alexjneves.droidify.service;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

final class PlayableTrack implements MediaPlayer.OnCompletionListener {
    private final List<MediaPlayer.OnCompletionListener> onCompletionListeners;
    private MediaPlayerFactory mediaPlayerFactory;
    private MediaPlayer mediaPlayer;
    private boolean initialized;

    public PlayableTrack(final String resourcePath, final Context applicationContext) {
        this.onCompletionListeners = new ArrayList<>();
        this.mediaPlayerFactory = new MediaPlayerFactory(applicationContext, resourcePath);
        this.mediaPlayer = new MediaPlayer();
        this.initialized = false;
    }

    public void registerOnCompletionListener(final MediaPlayer.OnCompletionListener onCompletionListener) {
        this.onCompletionListeners.add(onCompletionListener);
    }

    public void play() {
        if (!initialized) {
            mediaPlayer = mediaPlayerFactory.createPreparedPlayer();
            initialized = true;
        }

        mediaPlayer.start();
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        initialized = false;
    }

    public void setVolume(final float volume) {
        mediaPlayer.setVolume(volume, volume);
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        stop();
        initialized = false;

        for (final MediaPlayer.OnCompletionListener onCompletionListener : onCompletionListeners) {
            onCompletionListener.onCompletion(mediaPlayer);
        }
    }
}
