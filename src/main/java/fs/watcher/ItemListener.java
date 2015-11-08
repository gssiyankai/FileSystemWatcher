package fs.watcher;

import java.nio.file.Path;

public interface ItemListener {

    void onInsertion(Path path) throws Exception;

}
