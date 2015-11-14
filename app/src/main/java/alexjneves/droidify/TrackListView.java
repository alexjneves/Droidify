package alexjneves.droidify;

import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public final class TrackListView {
    private final ListView trackListView;
    private final List<Track> tracks;
    private final ListAdapter listAdapter;
    private final OnTrackClickListener onTrackClickListener;
    private final IRunOnUiThread runOnUiThread;

    public TrackListView(final ListView trackListView, final List<Track> tracks, final ListAdapter listAdapter, final OnTrackClickListener onTrackClickListener, final IRunOnUiThread runOnUiThread) {
        this.trackListView = trackListView;
        this.tracks = tracks;
        this.listAdapter = listAdapter;
        this.onTrackClickListener = onTrackClickListener;
        this.runOnUiThread = runOnUiThread;

        trackListView.setAdapter(listAdapter);
        trackListView.setOnItemClickListener(onTrackClickListener);
    }

    public void changeSelection(final int trackPosition) {
        runOnUiThread.executeOnUiThread(changeSelectionRunnable(trackPosition));
    }

    private Runnable changeSelectionRunnable(final int trackPosition) {
        return new Runnable() {
            @Override
            public void run() {
                // TODO: Implement
            }
        };
    }
}
