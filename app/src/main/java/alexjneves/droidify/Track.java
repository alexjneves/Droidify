package alexjneves.droidify;

import android.media.MediaMetadataRetriever;

import java.io.File;

public final class Track {
    private final File file;
    private final String name;
    private final String artist;

    public Track(File file) {
        // TODO: Replace with assertion
        if (!SupportedAudioFileFilter.isSupportedFile(file)) {
            throw new RuntimeException();
        }

        this.file = file;

        name = formatFileName(this.file);
        artist = retrieveArtist(this.file);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        String output = name;

        if (!artist.isEmpty()) {
            output += " by " + artist;
        }

        return output;
    }

    private static String formatFileName(File track) {
        final String name = track.getName();

        final String[] withoutSuffix = name.split("\\.");

        return withoutSuffix[0];
    }

    private static String retrieveArtist(File track) {
        final String trackPath = track.getAbsolutePath();

        final MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
        metadataRetriever.setDataSource(trackPath);

        final String artistMetaData = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

        if (artistMetaData.equals(null)) {
            return "";
        }

        return artistMetaData;
    }
}
