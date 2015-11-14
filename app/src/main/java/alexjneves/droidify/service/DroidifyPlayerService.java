package alexjneves.droidify.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alexjneves.droidify.DroidifyConstants;

public final class DroidifyPlayerService extends Service implements IDroidifyPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private final int DROIDIFY_PLAYER_SERVICE_NOTIFICATION_ID = 1;

    private final DroidifyPlayerServiceBinder droidifyPlayerServiceBinder;
    private final List<IDroidifyPlayerStateChangeListener> stateChangeListeners;
    private final MediaPlayer mediaPlayer;

    private Uri currentTrack;
    private DroidifyPlayerServiceNotificationFactory droidifyPlayerServiceNotificationFactory;
    private DroidifyPlayerState droidifyPlayerState;
    private boolean awaitingPlayback;

    public DroidifyPlayerService() {
        droidifyPlayerServiceBinder = new DroidifyPlayerServiceBinder();
        stateChangeListeners = new ArrayList<>();
        mediaPlayer = new MediaPlayer();

        currentTrack = null;
        droidifyPlayerServiceNotificationFactory = null;
        droidifyPlayerState = DroidifyPlayerState.STOPPED;
        awaitingPlayback = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
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

        droidifyPlayerServiceNotificationFactory = new DroidifyPlayerServiceNotificationFactory(resourcePath, getApplicationContext());
        mediaPlayer.prepareAsync();
    }

    @Override
    public void playCurrentTrack() {
        Log.d(DroidifyConstants.LogCategory, "Player Play");

        if (droidifyPlayerState == DroidifyPlayerState.PAUSED) {
            mediaPlayer.start();
            pushNotification(droidifyPlayerServiceNotificationFactory.playingNotification());
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
            pushNotification(droidifyPlayerServiceNotificationFactory.pausedNotification());
            changeState(DroidifyPlayerState.PAUSED);
        }
    }

    private void pushNotification(final Notification notification) {
        this.startForeground(DROIDIFY_PLAYER_SERVICE_NOTIFICATION_ID, notification);
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
    public void onCompletion(final MediaPlayer mediaPlayer) {
        changeState(DroidifyPlayerState.STOPPED);
        this.stopForeground(true);
        resetMediaPlayer();
    }

    final class DroidifyPlayerServiceBinder extends Binder implements IDroidifyPlayerServiceBinder {
        public IDroidifyPlayer getDroidifyPlayer() {
            return DroidifyPlayerService.this;
        }
    }
}
