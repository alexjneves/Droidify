package alexjneves.droidify;

import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

import alexjneves.droidify.service.IDroidifyPlayer;

public final class TrackListView {
    private final ListView trackListView;
    private final List<Track> tracks;
    private final ListAdapter listAdapter;
    private final OnTrackClickListener onTrackClickListener;
    private final IRunOnUiThread runOnUiThread;
    private View currentSelectedItem;

    public TrackListView(final ListView trackListView, final List<Track> tracks, final ListAdapter listAdapter, final OnTrackClickListener onTrackClickListener, final IRunOnUiThread runOnUiThread) {
        this.trackListView = trackListView;
        this.tracks = tracks;
        this.listAdapter = listAdapter;
        this.onTrackClickListener = onTrackClickListener;
        this.runOnUiThread = runOnUiThread;
        this.currentSelectedItem = null;

        trackListView.setAdapter(listAdapter);
        trackListView.setOnItemClickListener(onTrackClickListener);
    }

    public void changeSelection(final String resourcePath) {
        final int trackPosition = lookupTrackPosition(resourcePath);
        final Runnable changeSelectionRunnable = changeSelectionRunnable(trackPosition);


        runOnUiThread.executeOnUiThread(changeSelectionRunnable);
    }

    private int lookupTrackPosition(final String resourcePath) {
        for (int i = 0; i < tracks.size(); ++i) {
            if (tracks.get(i).getResourcePath().equals(resourcePath)) {
                return i;
            }
        }

        return -1;
    }

    private Runnable changeSelectionRunnable(final int trackPosition) {
        return new Runnable() {
            @Override
            public void run() {
                if (currentSelectedItem != null) {
                    currentSelectedItem.setBackgroundResource(0);
                }

                currentSelectedItem = getChildView(trackPosition, trackListView);
                currentSelectedItem.setBackgroundResource(R.drawable.list_view_item_border_selected);
            }
        };
    }

    // http://stackoverflow.com/questions/24811536/android-listview-get-item-view-by-position
    public View getChildView(final int position, final ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (position < firstListItemPosition || position > lastListItemPosition ) {
            return listView.getAdapter().getView(position, null, listView);
        } else {
            final int childIndex = position - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
