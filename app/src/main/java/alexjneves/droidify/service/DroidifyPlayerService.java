package alexjneves.droidify.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class DroidifyPlayerService extends Service implements IDroidifyPlayer, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private final int DROIDIFY_PLAYER_SERVICE_NOTIFICATION_ID = 1;
    public static final float MAX_VOLUME = 1.0f;
    private final float MIN_VOLUME = 1.0f;

    private final DroidifyPlayerServiceBinder droidifyPlayerServiceBinder;
    private final List<IDroidifyPlayerStateChangeListener> stateChangeListeners;
    private final MediaPlayer mediaPlayer;

    private Uri currentTrack;
    private DroidifyPlayerServiceNotificationFactory droidifyPlayerServiceNotificationFactory;
    private AudioManager audioManager;
    private DroidifyPlayerState droidifyPlayerState;
    private int previousAudioFocusState;
    private boolean awaitingPlayback;

    public DroidifyPlayerService() {
        droidifyPlayerServiceBinder = new DroidifyPlayerServiceBinder();
        stateChangeListeners = new ArrayList<>();
        mediaPlayer = new MediaPlayer();

        currentTrack = null;
        droidifyPlayerServiceNotificationFactory = null;
        audioManager = null;
        droidifyPlayerState = DroidifyPlayerState.STOPPED;
        previousAudioFocusState = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        awaitingPlayback = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

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
        if (droidifyPlayerState == DroidifyPlayerState.PAUSED) {
            if (!requestAudioFocus()) {
                changeState(DroidifyPlayerState.ERROR);
                return;
            }

            mediaPlayer.start();
            pushNotification(droidifyPlayerServiceNotificationFactory.playingNotification());
            changeState(DroidifyPlayerState.PLAYING);
        } else if (droidifyPlayerState == DroidifyPlayerState.PREPARING) {
            awaitingPlayback = true;
        }
    }

    @Override
    public void pauseCurrentTrack() {
        if (droidifyPlayerState == DroidifyPlayerState.PLAYING) {
            mediaPlayer.pause();
            pushNotification(droidifyPlayerServiceNotificationFactory.pausedNotification());
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

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        changeState(DroidifyPlayerState.STOPPED);
        this.stopForeground(true);
        resetMediaPlayer();
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                mediaPlayer.setVolume(MAX_VOLUME, MAX_VOLUME);

                if (previousAudioFocusState != AudioManager.AUDIOFOCUS_LOSS) {
                    playCurrentTrack();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pauseCurrentTrack();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mediaPlayer.setVolume(MIN_VOLUME, MIN_VOLUME);
                break;
            default:
                break;
        }

        previousAudioFocusState = focusChange;
    }

    private void changeState(final DroidifyPlayerState newState) {
        droidifyPlayerState = newState;

        for (final IDroidifyPlayerStateChangeListener stateChangeListener : stateChangeListeners) {
            stateChangeListener.onDroidifyPlayerStateChange(droidifyPlayerState);
        }
    }

    private void pushNotification(final Notification notification) {
        this.startForeground(DROIDIFY_PLAYER_SERVICE_NOTIFICATION_ID, notification);
    }

    private boolean requestAudioFocus() {
        final int audioFocusRequestResult = audioManager.requestAudioFocus(this, STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN);

        return audioFocusRequestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    private void resetMediaPlayer() {
        mediaPlayer.stop();
        mediaPlayer.reset();
    }

    final class DroidifyPlayerServiceBinder extends Binder implements IDroidifyPlayerServiceBinder {
        public IDroidifyPlayer getDroidifyPlayer() {
            return DroidifyPlayerService.this;
        }
    }
}
