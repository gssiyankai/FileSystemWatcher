package fs.watcher;

import java.nio.file.Path;

public class Event {

    public enum EventType {
        CREATE, DELETE, MODIFY
    }

    private final EventType type;
    private final Path path;

    public Event(EventType type, Path path) {
        this.type = type;
        this.path = path;
    }

    public EventType type() {
        return type;
    }

    public Path path() {
        return path;
    }

    @Override
    public String toString() {
        return "Event{" +
                "type=" + type +
                ", path=" + path +
                '}';
    }

}
