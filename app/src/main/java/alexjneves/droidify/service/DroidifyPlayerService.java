package alexjneves.droidify.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alexjneves.droidify.DroidifyConstants;

public final class DroidifyPlayerService extends Service implements IDroidifyPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private final DroidifyPlayerServiceBinder droidifyPlayerServiceBinder;
    private final List<IDroidifyPlayerStateChangeListener> stateChangeListeners;
    private final MediaPlayer mediaPlayer;

    private DroidifyPlayerState droidifyPlayerState;
    private Uri currentTrack;
    private boolean awaitingPlayback;

    public DroidifyPlayerService() {
        droidifyPlayerServiceBinder = new DroidifyPlayerServiceBinder();
        stateChangeListeners = new ArrayList<>();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        droidifyPlayerState = DroidifyPlayerState.STOPPED;
        currentTrack = null;
        awaitingPlayback = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        resetMediaPlayer();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return droidifyPlayerServiceBinder;
    }

    @Override
    public void changeTrack(final String resourcePath) {
        Log.d(DroidifyConstants.LogCategory, "Change Track: " + resourcePath);

        changeState(DroidifyPlayerState.PREPARING);
        currentTrack = Uri.parse(resourcePath);

        resetMediaPlayer();

        try {
            mediaPlayer.setDataSource(getApplicationContext(), currentTrack);
        } catch (final IOException ex) {
            // TODO: Handle
        }

        mediaPlayer.prepareAsync();
    }

    @Override
    public void playCurrentTrack() {
        Log.d(DroidifyConstants.LogCategory, "Player Play");

        if (droidifyPlayerState == DroidifyPlayerState.PAUSED) {
            mediaPlayer.start();
            changeState(DroidifyPlayerState.PLAYING);
        } else if (droidifyPlayerState == DroidifyPlayerState.PREPARING) {
            awaitingPlayback = true;
        }
    }

    @Override
    public void pauseCurrentTrack() {
        Log.d(DroidifyConstants.LogCategory, "Player Pause");

        if (droidifyPlayerState == DroidifyPlayerState.PLAYING) {
            mediaPlayer.pause();
            changeState(DroidifyPlayerState.PAUSED);
        }
    }

    @Override
    public void registerStateChangeListener(final IDroidifyPlayerStateChangeListener droidifyPlayerStateChangeListener) {
        stateChangeListeners.add(droidifyPlayerStateChangeListener);
    }

    @Override
    public void onPrepared(final MediaPlayer mediaPlayer) {
        changeState(DroidifyPlayerState.PAUSED);

        if (awaitingPlayback) {
            awaitingPlayback = false;
            playCurrentTrack();
        }
    }

    private void changeState(final DroidifyPlayerState newState) {
        droidifyPlayerState = newState;

        for (final IDroidifyPlayerStateChangeListener stateChangeListener : stateChangeListeners) {
            stateChangeListener.onDroidifyPlayerStateChange(droidifyPlayerState);
        }
    }

    private void resetMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    @Override
    public void onCompletion(final MediaPlayer mp) {
        changeState(DroidifyPlayerState.STOPPED);
        resetMediaPlayer();
    }

    public final class DroidifyPlayerServiceBinder extends Binder {
        public IDroidifyPlayer getDroidifyPlayer() {
            return DroidifyPlayerService.this;
        }
    }
}
