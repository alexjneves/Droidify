package alexjneves.droidify;

public final class AudioFile {
    private final String filePath;

    public AudioFile(final String filePath) {
        if (!SupportedAudioFileFilter.isSupportedFile(filePath)) {
            throw new RuntimeException("Unsupported file type - Must be an Android support audio file");
        }

        this.filePath = filePath;
    }

    public String getPath() {
        return filePath;
    }
}
