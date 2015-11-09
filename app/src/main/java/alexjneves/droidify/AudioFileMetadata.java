package alexjneves.droidify;

import android.media.MediaMetadataRetriever;

import java.io.File;

public final class AudioFileMetadata {
    private final AudioFile audioFile;
    private final String title;
    private final String artist;

    public AudioFileMetadata(final AudioFile audioFile) {
        this.audioFile = audioFile;

        title = determineTitle();
        artist = determineArtist();
    }

    private String determineTitle() {
        final String name = new File(audioFile.getPath()).getName();

        final String[] withoutExtension = name.split("\\.");

        return withoutExtension[0];
    }

    private String determineArtist() {
        final String trackPath = audioFile.getPath();

        final MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(trackPath);

        final String artistMetaData = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        if (artistMetaData.equals(null)) {
            return "";
        }

        return artistMetaData;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}
