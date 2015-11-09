package alexjneves.droidify;

import java.io.File;

public final class AudioFile {
    private final File file;

    public AudioFile(File file) {
        if (!SupportedAudioFileFilter.isSupportedFile(file)) {
            throw new RuntimeException("Unsupported file type - Must be an Android support audio file");
        }

        this.file = file;
    }

    public File getFile() {
        return file;
    }
}
