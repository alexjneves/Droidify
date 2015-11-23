package alexjneves.droidify;

/**
 * A DTO to represent a Track on the device, containing only the information needed for updating the
 * UI and triggering playback.
 */
public final class Track {
    private final String title;
    private final String artist;
    private final String resourcePath;

    public Track(final String title, final String artist, final String resourcePath) {
        this.title = title;
        this.artist = artist;
        this.resourcePath = resourcePath;
    }

    public String getTitle() {

        return title;
    }

    public String getArtist() {

        return artist;
    }

    public String getResourcePath() {
        return resourcePath;
    }
}