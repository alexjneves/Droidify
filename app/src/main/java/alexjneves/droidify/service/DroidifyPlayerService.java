package alexjneves.droidify.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import alexjneves.droidify.DroidifyConstants;

public final class DroidifyPlayerService extends Service implements IDroidifyPlayer {
    private final DroidifyPlayerServiceBinder droidifyPlayerServiceBinder;

    public DroidifyPlayerService() {
        droidifyPlayerServiceBinder = new DroidifyPlayerServiceBinder(this);
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

    }

    @Override
    public void pauseCurrentTrack() {

    }
}
