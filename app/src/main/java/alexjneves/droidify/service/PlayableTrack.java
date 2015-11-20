package alexjneves.droidify.service;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstraction of a playable audio file. Wraps the media player to reduce the dependency chain.
 * Exposes only the media player functionality that the droidify player service is interested in.
 */
final class PlayableTrack implements MediaPlayer.OnCompletionListener {
    private final String resourcePath;
    private final List<ITrackCompleteListener> trackCompleteListeners;
    private MediaPlayerFactory mediaPlayerFactory;
    private MediaPlayer mediaPlayer;

    public PlayableTrack(final String resourcePath, final Context applicationContext) {
        this.resourcePath = resourcePath;
        this.trackCompleteListeners = new ArrayList<>();
        this.mediaPlayerFactory = new MediaPlayerFactory(applicationContext, resourcePath);
        this.mediaPlayer = null;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void play() {
        if (mediaPlayer == null) {
            mediaPlayer = mediaPlayerFactory.createPreparedPlayer(this);
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
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer.reset();
        mediaPlayer = null;
    }

    public void setVolume(final float volume) {
        if (mediaPlayer == null) {
            return;
        }

        mediaPlayer.setVolume(volume, volume);
    }

    public void registerTrackCompleteListener(final ITrackCompleteListener trackCompleteListener) {
        trackCompleteListeners.add(trackCompleteListener);
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        stop();

        for (final ITrackCompleteListener trackCompleteListener : trackCompleteListeners) {
            trackCompleteListener.onTrackComplete();
        }
    }
}
