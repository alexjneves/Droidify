package alexjneves.droidify;

import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import alexjneves.droidify.service.IDroidifyPlayer;

public final class OnTrackClickListener implements AdapterView.OnItemClickListener {
    private final List<Track> tracks;
    private final IDroidifyPlayer droidifyPlayer;

    public OnTrackClickListener(final List<Track> tracks, final IDroidifyPlayer droidifyPlayer) {
        this.tracks = tracks;
        this.droidifyPlayer = droidifyPlayer;
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        final Track selectedTrack = tracks.get(position);

        droidifyPlayer.changeTrack(selectedTrack.getResourcePath());
        droidifyPlayer.playCurrentTrack();
    }
}
