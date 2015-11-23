package alexjneves.droidify;

import android.media.MediaMetadataRetriever;

public final class TrackMetadataRetriever {
    private final String trackName;
    private final String trackArtist;

    public TrackMetadataRetriever(final String resourcePath) {
        final MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(resourcePath);

        this.trackName = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        this.trackArtist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackArtist() {
        return trackArtist;
    }
}
