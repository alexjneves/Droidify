package alexjneves.droidify;

import java.util.ArrayList;
import java.util.List;

public final class SupportedAudioFileFilter {
    private final List<String> filePaths;

    public SupportedAudioFileFilter(final List<String> filePaths) {
        this.filePaths = filePaths;
    }

    public List<String> getSupportedAudioFiles() {
        List<String> supportedFiles = new ArrayList<>();

        for (final String filePath : filePaths) {
            if (isSupportedFile(filePath)) {
                supportedFiles.add(filePath);
            }
        }

        return supportedFiles;
    }

    public static boolean isSupportedFile(final String filePath) {
        final String[] split = filePath.split("\\.");
        final String fileExtension = split[split.length - 1];

        return fileExtension.equals(DroidifyConstants.SupportedAudioType);
    }
}
