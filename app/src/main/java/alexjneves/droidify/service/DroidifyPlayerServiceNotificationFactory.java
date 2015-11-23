package alexjneves.droidify.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import alexjneves.droidify.TrackSelectionActivity;

final class DroidifyPlayerServiceNotificationFactory {
    private static final int REQUEST_CODE = 0;

    private final Context applicationContext;
    private final String notificationContent;

    public DroidifyPlayerServiceNotificationFactory(final String resourcePath, final Context applicationContext) {
        this.applicationContext = applicationContext;

        final TrackMetadataRetriever trackMetadataRetriever = new TrackMetadataRetriever(resourcePath);
        this.notificationContent = trackMetadataRetriever.getTrackName() + " - " + trackMetadataRetriever.getTrackArtist();
    }

    public Notification playingNotification() {
        final String notificationTitle = "Now Playing";
        final int playingIcon = android.R.drawable.ic_media_play;

        return createNotification(notificationTitle, notificationContent, playingIcon);
    }

    public Notification pausedNotification() {
        final String notificationTitle = "Paused";
        final int pausedIcon = android.R.drawable.ic_media_pause;

        return createNotification(notificationTitle, notificationContent, pausedIcon);
    }

    private Notification createNotification(final String title, final String content, final int icon) {
        return new Notification.Builder(applicationContext)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(icon)
                .setContentIntent(createPendingIntent())
                .build();
    }

    private PendingIntent createPendingIntent() {
        final Intent trackSelectionActivityIntent = new Intent(applicationContext, TrackSelectionActivity.class);
        return PendingIntent.getActivity(applicationContext, REQUEST_CODE, trackSelectionActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
