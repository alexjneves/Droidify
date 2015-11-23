package alexjneves.droidify;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

final class DeviceAudioMediaRetriever {
    private final Uri contentUri;
    private final String[] columnsToRetrieve;
    private final String musicOnlyFilter;
    private final String[] musicOnlyFilterArgs;
    private final String sortOrder;

    private final ContentResolver contentResolver;

    public DeviceAudioMediaRetriever(final ContentResolver contentResolver) {
        this.contentResolver = contentResolver;

        columnsToRetrieve = new String[] {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DATA
        };

        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        musicOnlyFilter = MediaStore.Audio.Media.IS_MUSIC + " = ?";
        musicOnlyFilterArgs = new String[] { "1" };
        sortOrder = null;
    }

    public List<Track> retrieveTracks() {
        final Cursor cursor = contentResolver.query(
                contentUri,
                columnsToRetrieve,
                musicOnlyFilter,
                musicOnlyFilterArgs,
                sortOrder
        );

        final List<Track> tracks = new ArrayList<>();

        if (cursor == null) {
            return tracks;
        }

        while (cursor.moveToNext()) {
            final String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            final String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            final String resourcePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));

            tracks.add(new Track(title, artist, resourcePath));
        }

        cursor.close();

        return tracks;
    }
}
