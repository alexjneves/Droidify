package alexjneves.droidify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class SupportedAudioFileFilter {
    private final List<File> files;

    public SupportedAudioFileFilter(List<File> files) {
        this.files = files;
    }

    public List<File> getSupportedAudioFiles() {
        List<File> supportedFiles = new ArrayList<>();

        for (File file : files) {
            if (isSupportedFile(file)) {
                supportedFiles.add(file);
            }
        }

        return supportedFiles;
    }

    public static boolean isSupportedFile(File file) {
        final String fileName = file.getName();

        final String[] split = fileName.split("\\.");
        final String fileExtension = split[split.length - 1];

        return fileExtension.equals(DroidifyConstants.SupportedAudioType);
    }
}
