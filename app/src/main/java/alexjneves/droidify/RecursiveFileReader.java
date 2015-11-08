package alexjneves.droidify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class RecursiveFileReader {
    private final File root;
    private final List<File> files;

    public RecursiveFileReader(File root) {
        this.root = root;
        files = new ArrayList<>();
    }

    public List<File> getFiles() {
        return getFiles(root);
    }

    private List<File> getFiles(File root) {
        for (File file : root.listFiles()) {
            if (file.isFile()) {
                files.add(file);
            } else {
                getFiles(file);
            }
        }

        return files;
    }
}
