package alexjneves.droidify;

public final class Track {
    private final AudioFile audioFile;
    private final AudioFileMetadata metadata;

    public Track(AudioFile audioFile, AudioFileMetadata metadata) {
        this.audioFile = audioFile;
        this.metadata = metadata;
    }

    public AudioFile getAudioFile() {
        return audioFile;
    }

    public AudioFileMetadata getMetadata() {
        return metadata;
    }
}