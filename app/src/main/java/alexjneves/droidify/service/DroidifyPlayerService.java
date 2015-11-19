package alexjneves.droidify.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import alexjneves.droidify.TrackChangedBroadcastReceiver;

public final class DroidifyPlayerService extends IntentService implements IDroidifyPlayer, ITrackCompleteListener, ITrackChangedListener {
    public static final String PAUSE_PLAYBACK_INTENT_ACTION = "PausePlayback";

    private static final String SERVICE_NAME = "DroidifyPlayerService";

    private final DroidifyPlayerServiceBinder droidifyPlayerServiceBinder;
    private final List<IDroidifyPlayerStateChangeListener> stateChangeListeners;

    private AudioFocusHandler audioFocusHandler;
    private PlaylistController playlistController;
    private DroidifyPlayerServiceNotifier droidifyPlayerServiceNotifier;
    private DroidifyPlayerState droidifyPlayerState;
    private boolean shuffle;
    private boolean autoPlay;

    public DroidifyPlayerService() {
        super(SERVICE_NAME);

        droidifyPlayerServiceBinder = new DroidifyPlayerServiceBinder();
        stateChangeListeners = new ArrayList<>();

        audioFocusHandler = null;
        playlistController = null;
        droidifyPlayerServiceNotifier = null;
        droidifyPlayerState = DroidifyPlayerState.STOPPED;
        shuffle = false;
        autoPlay = true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        audioFocusHandler = new AudioFocusHandler(this, this);
        playlistController = new PlaylistController(new ArrayList<String>(), getApplicationContext(), this);
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
    protected void onHandleIntent(final Intent intent) {
        final String intentAction = intent.getAction();

        if (intentAction.equals(PAUSE_PLAYBACK_INTENT_ACTION)) {
            pauseCurrentTrack();
        }
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
        playlistController.registerTrackCompleteListener(this);

        if (shuffle) {
            playlistController.shufflePlaylist();
        }
    }

    @Override
    public void playCurrentTrack() {
        if (droidifyPlayerState == DroidifyPlayerState.PAUSED) {
            if (!audioFocusHandler.requestAudioFocus()) {
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
    public void toggleAutoPlay(final boolean on) {
        autoPlay = on;
    }

    @Override
    public void setVolume(final float volume) {
        playlistController.setVolume(volume);
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
    public void onTrackComplete() {
        changeState(DroidifyPlayerState.STOPPED);
        this.stopForeground(true);
        audioFocusHandler.abandonAudioFocus();

        if (autoPlay) {
            final PlayableTrack nextTrack = playlistController.getNextTrack();
            changeTrack(nextTrack.getResourcePath());
            playCurrentTrack();
        }
    }

    private void changeState(final DroidifyPlayerState newState) {
        droidifyPlayerState = newState;

        for (final IDroidifyPlayerStateChangeListener stateChangeListener : stateChangeListeners) {
            stateChangeListener.onDroidifyPlayerStateChange(droidifyPlayerState);
        }
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
