package alexjneves.droidify.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import alexjneves.droidify.DroidifyConstants;

public final class DroidifyPlayerService extends Service implements IDroidifyPlayer {
    private final DroidifyPlayerServiceBinder droidifyPlayerServiceBinder;
    private final List<IDroidifyPlayerStateChangeListener> stateChangeListeners;

    private DroidifyPlayerState droidifyPlayerState;

    public DroidifyPlayerService() {
        droidifyPlayerServiceBinder = new DroidifyPlayerServiceBinder();
        stateChangeListeners = new ArrayList<>();

        droidifyPlayerState = DroidifyPlayerState.PAUSED;
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return droidifyPlayerServiceBinder;
    }

    @Override
    public void changeTrack(final String filePath) {
        Log.d(DroidifyConstants.LogCategory, "Change Track: " + filePath);
    }

    @Override
    public void playCurrentTrack() {
        Log.d(DroidifyConstants.LogCategory, "Player Play");
        changeState(DroidifyPlayerState.PLAYING);
    }

    @Override
    public void pauseCurrentTrack() {
        Log.d(DroidifyConstants.LogCategory, "Player Pause");
        changeState(DroidifyPlayerState.PAUSED);
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

    public final class DroidifyPlayerServiceBinder extends Binder {
        public IDroidifyPlayer getDroidifyPlayer() {
            return DroidifyPlayerService.this;
        }
    }
}
