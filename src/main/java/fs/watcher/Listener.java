package fs.watcher;

import fs.watcher.Event.EventType;

import java.nio.file.Path;

public interface Listener {

    void onChange(EventType eventType, Path path);

}
