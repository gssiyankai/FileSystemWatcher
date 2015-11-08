package fs.watcher;

import fs.watcher.Event.EventType;

import java.nio.file.Path;

public interface ChangeListener {

    void onChange(EventType eventType, Path path) throws Exception;

}
