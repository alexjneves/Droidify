package alexjneves.droidify;

import android.content.Context;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A factory for constructing the adapter to be attached to our TrackListView. The adapter is the
 * component which populates the ListView UI element with child elements, which in our case display
 * the tracks name and artist name.
 */
public final class TrackListViewAdapterFactory {
    private static final String TRACK_LIST_VIEW_MAIN_TEXT = "title";
    private static final String TRACK_LIST_VIEW_SUB_TEXT = "artist";

    public ListAdapter createAdapter(final Context context, final List<Track> tracks) {
        final List<Map<String, String>> trackListViewData = new ArrayList<>();

        for (final Track track : tracks) {
            final Map<String, String> trackData = new HashMap<>(2);

            trackData.put(TRACK_LIST_VIEW_MAIN_TEXT, track.getTitle());
            trackData.put(TRACK_LIST_VIEW_SUB_TEXT, track.getArtist());

            trackListViewData.add(trackData);
        }

        return new SimpleAdapter(context, trackListViewData,
                android.R.layout.simple_list_item_2,
                new String[] { TRACK_LIST_VIEW_MAIN_TEXT, TRACK_LIST_VIEW_SUB_TEXT },
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
    }
}
