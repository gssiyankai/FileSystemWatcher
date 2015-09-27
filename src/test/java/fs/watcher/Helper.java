package fs.watcher;

import java.nio.file.Files;
import java.nio.file.Path;

public class Helper {

    static Path createTempDir() throws Exception {
        return Files.createTempDirectory("FsWatcher");
    }

    static Path createTempFile() throws Exception {
        return createTempFile(createTempDir());
    }

    static Path createTempFile(Path directory) throws Exception {
        return Files.createTempFile(directory, "tmp", "");
    }

}
