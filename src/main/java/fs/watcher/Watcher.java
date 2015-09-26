package fs.watcher;

import java.nio.file.*;
import java.util.concurrent.*;

import static fs.watcher.Event.EventType.CREATE;
import static fs.watcher.Event.EventType.DELETE;
import static fs.watcher.Event.EventType.MODIFY;
import static java.nio.file.StandardWatchEventKinds.*;

public final class Watcher {

    public static final int MAX_DIRECTORIES = 32;

    private final WatchService watchService;
    private final ExecutorService directoryWatchers;
    private final BlockingDeque<Event> eventQueue;

    public Watcher() throws Exception {
        watchService = FileSystems.getDefault().newWatchService();
        directoryWatchers = Executors.newFixedThreadPool(MAX_DIRECTORIES);
        eventQueue = new LinkedBlockingDeque<>();
    }

    public void watch(Path directory) throws Exception {
        directoryWatchers.submit(new DirectoryWatcher(directory));
    }

    public Event nextEvent() throws Exception {
        return eventQueue.take();
    }

    private class DirectoryWatcher implements Callable<Void> {

        private final Path directory;

        public DirectoryWatcher(Path directory) {
            this.directory = directory;
        }

        @Override
        public Void call() throws Exception {
            directory.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException x) {
                    System.err.println("Watch interrupted");
                    break;
                }

                for (WatchEvent<?> watchEvent : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = watchEvent.kind();

                    Event.EventType eventType = null;
                    if (kind == OVERFLOW) {
                        continue;
                    } else if (kind == ENTRY_CREATE) {
                        eventType = CREATE;
                    } else if (kind == ENTRY_DELETE) {
                        eventType = DELETE;
                    } else if(kind == ENTRY_MODIFY) {
                        eventType = MODIFY;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>)watchEvent;
                    Path filename = ev.context();
                    Path child = directory.resolve(filename);

                    Event event = new Event(eventType, child);
                    System.out.println("Pushing event " + event);
                    eventQueue.push(event);
                }

                boolean valid = key.reset();
                if (!valid) {
                    System.err.println("Watch interrupted after watch key reset");
                    break;
                }
            }
            return null;
        }

    }

}
