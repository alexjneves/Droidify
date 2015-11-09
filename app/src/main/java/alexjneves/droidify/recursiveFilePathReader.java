package alexjneves.droidify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class recursiveFilePathReader {
    private final String rootPath;
    private final List<String> filePaths;

    public recursiveFilePathReader(final String rootPath) {
        this.rootPath = rootPath;
        filePaths = new ArrayList<>();
    }

    public List<String> getFilePaths() {
        final File root = new File(rootPath);
        return getFilesPaths(root);
    }

    private List<String> getFilesPaths(final File root) {
        for (final File file : root.listFiles()) {
            if (file.isFile()) {
                filePaths.add(file.getAbsolutePath());
            } else {
                getFilesPaths(file);
            }
        }

        return filePaths;
    }
}
