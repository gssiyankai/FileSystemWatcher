package fs.watcher;

import fs.watcher.Event.EventType;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static fs.watcher.Event.EventType.CREATE;
import static fs.watcher.Event.EventType.DELETE;
import static fs.watcher.Event.EventType.MODIFY;
import static org.fest.assertions.Assertions.assertThat;

public class WatcherTest {

    private Watcher watcher;

    @Before
    public void setup() throws Exception {
        watcher = new Watcher();
    }

    @Test(timeout = 1000)
    public void it_sould_notify_a_file_creation() throws Exception {
        Path directory = Files.createTempDirectory(getClass().getSimpleName());
        watch(directory);
        Path newFile = Files.createTempFile(directory, "new", "");
        assertNextEvent(CREATE, newFile.toFile());
    }

    @Test(timeout = 1000)
    public void it_sould_notify_a_file_deletion() throws Exception {
        Path directory = Files.createTempDirectory(getClass().getSimpleName());
        Path newFile = Files.createTempFile(directory, "new", "");
        watch(directory);
        Files.delete(newFile);
        assertNextEvent(DELETE, newFile.toFile());
    }

    @Test(timeout = 1000)
    public void it_sould_notify_a_file_modification() throws Exception {
        Path directory = Files.createTempDirectory(getClass().getSimpleName());
        Path newFile = Files.createTempFile(directory, "new", "");
        watch(directory);
        FileUtils.touch(newFile.toFile());
        assertNextEvent(MODIFY, newFile.toFile());
    }

    private void watch(Path directory) throws Exception {
        watcher.watch(directory);
        Thread.sleep(50);
    }

    private void assertNextEvent(EventType create, File expected) throws Exception {
        Event event = watcher.nextEvent();
        assertThat(event).isNotNull();
        assertThat(event.type()).isEqualTo(create);
        assertThat(event.path().toFile()).isEqualTo(expected);
    }

}
