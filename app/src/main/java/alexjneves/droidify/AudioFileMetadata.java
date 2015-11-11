package alexjneves.droidify;

import android.media.MediaMetadataRetriever;

public final class AudioFileMetadata {
    private final AudioFile audioFile;
    private final String title;
    private final String artist;

    public AudioFileMetadata(final AudioFile audioFile) {
        this.audioFile = audioFile;

        title = extractMetadataOrDefault(MediaMetadataRetriever.METADATA_KEY_TITLE, audioFile.getPath());
        artist = extractMetadataOrDefault(MediaMetadataRetriever.METADATA_KEY_ARTIST, "");
    }

    private String extractMetadataOrDefault(final int dataToRetrieve, final String defaultValue) {
        final String trackPath = audioFile.getPath();

        final MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(trackPath);

        final String metadata = metadataRetriever.extractMetadata(dataToRetrieve);

        if (metadata == null) {
            return defaultValue;
        }

        return metadata;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}
