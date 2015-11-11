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

public final class DroidifyPlayerService extends Service implements IDroidifyPlayer, MediaPlayer.OnPreparedListener {
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
    public int onStartCommand(Intent intent, int flags, int startId) {
        mediaPlayer.setOnPreparedListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        mediaPlayer.setOnPreparedListener(this);

        return droidifyPlayerServiceBinder;
    }

    @Override
    public void changeTrack(final String filePath) {
        Log.d(DroidifyConstants.LogCategory, "Change Track: " + filePath);

        changeState(DroidifyPlayerState.PREPARING);

        currentTrack = Uri.parse(filePath);

        mediaPlayer.stop();
        mediaPlayer.reset();

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

    private void changeState(final DroidifyPlayerState newState) {
        droidifyPlayerState = newState;

        for (final IDroidifyPlayerStateChangeListener stateChangeListener : stateChangeListeners) {
            stateChangeListener.onDroidifyPlayerStateChange(droidifyPlayerState);
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

    public final class DroidifyPlayerServiceBinder extends Binder {
        public IDroidifyPlayer getDroidifyPlayer() {
            return DroidifyPlayerService.this;
        }
    }
}
