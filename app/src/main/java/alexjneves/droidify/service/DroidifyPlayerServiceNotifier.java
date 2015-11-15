package alexjneves.droidify.service;

import android.app.Notification;

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
