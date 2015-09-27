package fs.watcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class Helper {

    static Path createTempDir() {
        try {
            return Files.createTempDirectory("FsWatcher");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Path createTempFile() {
        return createTempFile(createTempDir());
    }

    static Path createTempFile(Path directory) {
        try {
            return Files.createTempFile(directory, "tmp", "");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
