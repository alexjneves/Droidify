package alexjneves.droidify.service;

import android.app.Notification;

/**
 * A notifier that will respond to droidify player state changes, using the injected notification
 * factory to create appropriate notifications and then display them on the device.
 */
final class DroidifyPlayerServiceNotifier implements IDroidifyPlayerStateChangeListener{
    private static final int DROIDIFY_PLAYER_SERVICE_NOTIFICATION_ID = 1;

    private final DroidifyPlayerServiceNotificationFactory droidifyPlayerServiceNotificationFactory;
    private final DroidifyPlayerService droidifyPlayerService;

    public DroidifyPlayerServiceNotifier(final DroidifyPlayerServiceNotificationFactory droidifyPlayerServiceNotificationFactory, final DroidifyPlayerService droidifyPlayerService) {
        this.droidifyPlayerServiceNotificationFactory = droidifyPlayerServiceNotificationFactory;
        this.droidifyPlayerService = droidifyPlayerService;
        this.droidifyPlayerService.registerStateChangeListener(this);
    }

    @Override
    public void onDroidifyPlayerStateChange(final DroidifyPlayerState newState) {
        switch (newState) {
            case PLAYING:
                pushNotification(droidifyPlayerServiceNotificationFactory.playingNotification());
                break;
            case PAUSED:
                pushNotification(droidifyPlayerServiceNotificationFactory.pausedNotification());
                break;
            default:
                break;
        }
    }

    private void pushNotification(final Notification notification) {
        droidifyPlayerService.startForeground(DROIDIFY_PLAYER_SERVICE_NOTIFICATION_ID, notification);
    }
}
