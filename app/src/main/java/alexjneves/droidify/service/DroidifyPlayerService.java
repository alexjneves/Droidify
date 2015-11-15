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
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class DroidifyPlayerService extends Service implements IDroidifyPlayer, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {
    private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final int DROIDIFY_PLAYER_SERVICE_NOTIFICATION_ID = 1;
    private static final float MAX_VOLUME = 1.0f;
    private static final float MIN_VOLUME = 1.0f;

    private final DroidifyPlayerServiceBinder droidifyPlayerServiceBinder;
    private final List<IDroidifyPlayerStateChangeListener> stateChangeListeners;

    private DroidifyPlayerServiceNotificationFactory droidifyPlayerServiceNotificationFactory;
    private AudioManager audioManager;
    private PlaybackTrackQueue playbackTrackQueue;
    private DroidifyPlayerState droidifyPlayerState;
    private int previousAudioFocusState;

    public DroidifyPlayerService() {
        droidifyPlayerServiceBinder = new DroidifyPlayerServiceBinder();
        stateChangeListeners = new ArrayList<>();

        droidifyPlayerServiceNotificationFactory = null;
        audioManager = null;
        playbackTrackQueue = null;
        droidifyPlayerState = DroidifyPlayerState.STOPPED;
        previousAudioFocusState = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playbackTrackQueue.cleanUp();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return droidifyPlayerServiceBinder;
    }

    @Override
    public void changeTrack(final String resourcePath) {
        if (playbackTrackQueue == null) {
            return;
        }

        droidifyPlayerServiceNotificationFactory = new DroidifyPlayerServiceNotificationFactory(resourcePath, getApplicationContext());
        playbackTrackQueue.changeTrack(resourcePath);
        changeState(DroidifyPlayerState.PAUSED);
    }

    @Override
    public void changePlaylist(final List<String> resourcePaths) {
        playbackTrackQueue = new PlaybackTrackQueue(resourcePaths, getApplicationContext());
        playbackTrackQueue.registerOnCompletionListener(this);
    }

    @Override
    public void playCurrentTrack() {
        if (droidifyPlayerState == DroidifyPlayerState.PAUSED) {
            if (!requestAudioFocus()) {
                changeState(DroidifyPlayerState.ERROR);
                return;
            }

            playbackTrackQueue.playCurrentTrack();
            pushNotification(droidifyPlayerServiceNotificationFactory.playingNotification());
            changeState(DroidifyPlayerState.PLAYING);
        }
    }

    @Override
    public void pauseCurrentTrack() {
        if (droidifyPlayerState == DroidifyPlayerState.PLAYING) {
            playbackTrackQueue.pauseCurrentTrack();
            pushNotification(droidifyPlayerServiceNotificationFactory.pausedNotification());
            changeState(DroidifyPlayerState.PAUSED);
        }
    }

    @Override
    public void registerStateChangeListener(final IDroidifyPlayerStateChangeListener droidifyPlayerStateChangeListener) {
        stateChangeListeners.add(droidifyPlayerStateChangeListener);
    }

    @Override
    public void onCompletion(final MediaPlayer mediaPlayer) {
        changeState(DroidifyPlayerState.STOPPED);
        this.stopForeground(true);
        audioManager.abandonAudioFocus(this);
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                playbackTrackQueue.setVolume(MAX_VOLUME);

                if (previousAudioFocusState != AudioManager.AUDIOFOCUS_LOSS) {
                    playCurrentTrack();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pauseCurrentTrack();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                playbackTrackQueue.setVolume(MIN_VOLUME);
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

    final class DroidifyPlayerServiceBinder extends Binder implements IDroidifyPlayerServiceBinder {
        public IDroidifyPlayer getDroidifyPlayer() {
            return DroidifyPlayerService.this;
        }
    }
}
