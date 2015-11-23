package alexjneves.droidify;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public final class TrackChangedBroadcastReceiver {
    private static final String TRACK_CHANGED_BROADCAST_KEY = "TrackChangedBroadcast";
    private static final String TRACK_RESOURCE_PATH_KEY = "TrackResourcePath";

    private final Context context;
    private final Activity activity;
    private final TrackListView trackListView;

    public TrackChangedBroadcastReceiver(final Context context, final Activity activity, final TrackListView trackListView) {
        this.context = context;
        this.activity = activity;
        this.trackListView = trackListView;

        final IntentFilter onTrackChangedIntentFilter = new IntentFilter(TRACK_CHANGED_BROADCAST_KEY);
        LocalBroadcastManager.getInstance(context).registerReceiver(trackChangedBroadcastHandler, onTrackChangedIntentFilter);
    }

    public void unregister() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(trackChangedBroadcastHandler);
    }

    public static void sendBroadcast(final Context context, final String trackResourcePath) {
        final Intent trackChangedBroadcastIntent = new Intent(TRACK_CHANGED_BROADCAST_KEY);
        trackChangedBroadcastIntent.putExtra(TRACK_RESOURCE_PATH_KEY, trackResourcePath);

        LocalBroadcastManager.getInstance(context).sendBroadcast(trackChangedBroadcastIntent);
    }

    private final BroadcastReceiver trackChangedBroadcastHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String currentTrack = intent.getStringExtra(TRACK_RESOURCE_PATH_KEY);
            trackListView.changeSelection(currentTrack);

            final TrackMetadataRetriever trackMetadataRetriever = new TrackMetadataRetriever(currentTrack);
            activity.setTitle(trackMetadataRetriever.getTrackName() + " - " + trackMetadataRetriever.getTrackArtist());
        }
    };
}
