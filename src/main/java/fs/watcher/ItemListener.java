package fs.watcher;

import java.nio.file.Path;

interface ItemListener {

    void onInsertion(Path path) throws Exception;

}
