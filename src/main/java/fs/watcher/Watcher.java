package fs.watcher;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

import static fs.watcher.Event.EventType.*;
import static java.nio.file.StandardWatchEventKinds.*;

final class Watcher {

    static final int MAX_NB_WATCHERS = 32;

    private static Watcher INSTANCE;

    private final WatchService watchService;
    private final ExecutorService watchersPool;
    private final List<Path> items;
    private final List<ItemListener> itemListeners;
    private final List<ChangeListener> changeListeners;

    private Watcher() throws Exception {
        watchService = FileSystems.getDefault().newWatchService();
        watchersPool = Executors.newFixedThreadPool(MAX_NB_WATCHERS);
        items = new ArrayList<>();
        itemListeners = new ArrayList<>();
        changeListeners = new ArrayList<>();
    }

    static Watcher singleton() throws Exception {
        if(INSTANCE == null) {
            INSTANCE = new Watcher();
        }
        return INSTANCE;
    }

    static void reset() {
        INSTANCE = null;
    }

    public void registerItemListener(ItemListener listener) {
        itemListeners.add(listener);
    }

    public void registerChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public List<Path> items() {
        return items;
    }

    public void watch(Path path) throws Exception {
        if(items.size() >= MAX_NB_WATCHERS) {
            throw new RuntimeException("Number of watchers exceeded");
        }
        items.add(path);
        Path directory;
        Optional<Path> filter;
        if (path.toFile().isDirectory()) {
            directory = path;
            filter = Optional.empty();
        } else {
            directory = path.getParent();
            filter = Optional.of(path.getFileName());
        }
        watchersPool.submit(new DirectoryWatcher(directory, filter));
        for (ItemListener listener : itemListeners) {
            listener.onInsertion(path);
        }
    }

    private class DirectoryWatcher implements Callable<Void> {

        private final Path directory;
        private final Optional<Path> filter;

        public DirectoryWatcher(Path directory, Optional<Path> filter) {
            this.directory = directory;
            this.filter = filter;
        }

        @Override
        public Void call() throws Exception {
            directory.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
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
                    } else if (kind == ENTRY_MODIFY) {
                        eventType = MODIFY;
                    }

                    @SuppressWarnings("unchecked")
                    Path filename = ((WatchEvent<Path>) watchEvent).context();
                    Path child = directory.resolve(filename);

                    if (filter.isPresent()) {
                        if (!child.endsWith(filter.get())) {
                            continue;
                        }
                    }

                    Event event = new Event(eventType, child);
                    System.out.println("Pushing event " + event);
                    for (ChangeListener listener : changeListeners) {
                        listener.onChange(eventType, child);
                    }
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
