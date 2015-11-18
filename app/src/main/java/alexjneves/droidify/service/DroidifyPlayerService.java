package alexjneves.droidify.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import alexjneves.droidify.DroidifyConstants;
import alexjneves.droidify.TrackChangedBroadcastReceiver;

public final class DroidifyPlayerService extends Service implements IDroidifyPlayer, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener, ITrackChangedListener {
    private static final int STREAM_TYPE = AudioManager.STREAM_MUSIC;
    private static final float MAX_VOLUME = 1.0f;
    private static final float MIN_VOLUME = 1.0f;

    private final DroidifyPlayerServiceBinder droidifyPlayerServiceBinder;
    private final List<IDroidifyPlayerStateChangeListener> stateChangeListeners;

    private AudioManager audioManager;
    private PlaylistController playlistController;
    private DroidifyPlayerServiceNotifier droidifyPlayerServiceNotifier;
    private DroidifyPlayerState droidifyPlayerState;
    private int previousAudioFocusState;
    private boolean shuffle;

    public DroidifyPlayerService() {
        droidifyPlayerServiceBinder = new DroidifyPlayerServiceBinder();
        stateChangeListeners = new ArrayList<>();

        audioManager = null;
        playlistController = null;
        droidifyPlayerServiceNotifier = null;
        droidifyPlayerState = DroidifyPlayerState.STOPPED;
        previousAudioFocusState = AudioManager.AUDIOFOCUS_REQUEST_FAILED;
        shuffle = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playlistController.cleanUp();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return droidifyPlayerServiceBinder;
    }

    @Override
    public void changeTrack(final String resourcePath) {
        final DroidifyPlayerServiceNotificationFactory droidifyPlayerServiceNotificationFactory = new DroidifyPlayerServiceNotificationFactory(resourcePath, getApplicationContext());

        droidifyPlayerServiceNotifier = new DroidifyPlayerServiceNotifier(droidifyPlayerServiceNotificationFactory, this);

        changeState(DroidifyPlayerState.PAUSED);
        playlistController.changeTrack(resourcePath);
    }

    @Override
    public void changePlaylist(final List<String> resourcePaths) {
        playlistController = new PlaylistController(resourcePaths, getApplicationContext(), this);
        playlistController.registerOnCompletionListener(this);

        if (shuffle) {
            playlistController.shufflePlaylist();
        }
    }

    @Override
    public void playCurrentTrack() {
        if (droidifyPlayerState == DroidifyPlayerState.PAUSED) {
            if (!requestAudioFocus()) {
                changeState(DroidifyPlayerState.ERROR);
                return;
            }

            playlistController.playCurrentTrack();
            changeState(DroidifyPlayerState.PLAYING);
        }
    }

    @Override
    public void pauseCurrentTrack() {
        if (droidifyPlayerState == DroidifyPlayerState.PLAYING) {
            playlistController.pauseCurrentTrack();
            changeState(DroidifyPlayerState.PAUSED);
        }
    }

    @Override
    public void skipForward() {
        final PlayableTrack nextTrack = playlistController.getNextTrack();
        changeTrack(nextTrack.getResourcePath());
        playCurrentTrack();
    }

    @Override
    public void skipBackward() {
        final PlayableTrack previousTrack = playlistController.getPreviousTrack();
        changeTrack(previousTrack.getResourcePath());
        playCurrentTrack();
    }

    @Override
    public void toggleShuffle(final boolean on) {
        shuffle = on;

        if (shuffle) {
            playlistController.shufflePlaylist();
        } else {
            playlistController.resetShuffle();
        }
    }

    @Override
    public void registerStateChangeListener(final IDroidifyPlayerStateChangeListener droidifyPlayerStateChangeListener) {
        stateChangeListeners.add(droidifyPlayerStateChangeListener);
    }

    @Override
    public String getCurrentTrack() {
        return playlistController.getCurrentTrack().getResourcePath();
    }

    @Override
    public boolean isShuffleOn() {
        return shuffle;
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
                playlistController.setVolume(MAX_VOLUME);

                if (previousAudioFocusState != AudioManager.AUDIOFOCUS_LOSS) {
                    playCurrentTrack();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                pauseCurrentTrack();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                playlistController.setVolume(MIN_VOLUME);
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

    private boolean requestAudioFocus() {
        final int audioFocusRequestResult = audioManager.requestAudioFocus(this, STREAM_TYPE, AudioManager.AUDIOFOCUS_GAIN);

        return audioFocusRequestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    @Override
    public void onTrackChanged(final String resourcePath) {
        TrackChangedBroadcastReceiver.sendBroadcast(this, resourcePath);
    }

    final class DroidifyPlayerServiceBinder extends Binder implements IDroidifyPlayerServiceBinder {
        public IDroidifyPlayer getDroidifyPlayer() {
            return DroidifyPlayerService.this;
        }
    }
}
