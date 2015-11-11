package alexjneves.droidify;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import alexjneves.droidify.service.DroidifyPlayerService;
import alexjneves.droidify.service.IDroidifyPlayer;

public final class DroidifyPlayerServiceConnection implements ServiceConnection {
    private final IDroidifyPlayerRetrievedListener droidifyPlayerRetrievedListener;

    public DroidifyPlayerServiceConnection(final IDroidifyPlayerRetrievedListener droidifyPlayerRetrievedListener) {
        this.droidifyPlayerRetrievedListener = droidifyPlayerRetrievedListener;
    }

    @Override
    public void onServiceConnected(final ComponentName name, final IBinder service) {
        final DroidifyPlayerService.DroidifyPlayerServiceBinder droidifyPlayerServiceBinder = (DroidifyPlayerService.DroidifyPlayerServiceBinder) service;
        final IDroidifyPlayer droidifyPlayer = droidifyPlayerServiceBinder.getDroidifyPlayer();

        droidifyPlayerRetrievedListener.onDroidifyPlayerRetrieved(droidifyPlayer);
    }

    @Override
    public void onServiceDisconnected(final ComponentName name) {
        // This does not need to be handled as the service is running the same process:
        // This callback is triggered when the host process crashes, and since that is our app this should
        // never get called.
    }
}