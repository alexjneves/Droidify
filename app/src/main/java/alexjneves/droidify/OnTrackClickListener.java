package alexjneves.droidify;

import android.view.View;
import android.widget.AdapterView;

import java.util.List;

public final class OnTrackClickListener implements AdapterView.OnItemClickListener {
    private final List<Track> tracks;

    public OnTrackClickListener(final List<Track> tracks) {
        this.tracks = tracks;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}
