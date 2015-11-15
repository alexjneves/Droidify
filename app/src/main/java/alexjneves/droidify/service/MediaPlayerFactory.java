package alexjneves.droidify.service;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;

import java.io.IOException;

final class MediaPlayerFactory {
    private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private final Context applicationContext;
    private final String resourcePath;

    public MediaPlayerFactory(final Context applicationContext, final String resourcePath) {
        this.applicationContext = applicationContext;
        this.resourcePath = resourcePath;
    }

    public MediaPlayer createPreparedPlayer() {
        final MediaPlayer mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioStreamType(STREAM_TYPE);
        mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK);

        try {
            mediaPlayer.setDataSource(applicationContext, Uri.parse(resourcePath));
            mediaPlayer.prepare();
        } catch (final IOException ex) {
            throw new RuntimeException();
        }

        return mediaPlayer;
    }
}
