package fs.watcher;

import fs.watcher.Event.EventType;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static fs.watcher.Event.EventType.*;
import static fs.watcher.Helper.createTempDir;
import static fs.watcher.Helper.createTempFile;
import static org.fest.assertions.Assertions.assertThat;

public class WatcherTest {

    private Watcher watcher;

    @Before
    public void setup() throws Exception {
        watcher = new Watcher();
    }

    /**
     * Given a watcher monitoring a directory
     * When a new file is created in the directory
     * Then it notifies a file creation event
     */
    @Test(timeout = 1000)
    public void test1() throws Exception {
        Path directory = createTempDir();
        watch(directory);
        Path newFile = createTempFile(directory);
        assertNextEvent(CREATE, newFile.toFile());
    }

    /**
     * Given a watcher monitoring a directory
     * When an existing file is deleted in the directory
     * Then it notifies a file deletion event
     */
    @Test(timeout = 1000)
    public void test2() throws Exception {
        Path directory = createTempDir();
        Path newFile = createTempFile(directory);
        watch(directory);
        Files.delete(newFile);
        assertNextEvent(DELETE, newFile.toFile());
    }

    /**
     * Given a watcher monitoring a directory
     * When an existing file is modified in the directory
     * Then it notifies a file modification event
     */
    @Test(timeout = 1000)
    public void test3() throws Exception {
        Path directory = createTempDir();
        Path newFile = createTempFile(directory);
        watch(directory);
        FileUtils.touch(newFile.toFile());
        assertNextEvent(MODIFY, newFile.toFile());
    }

    /**
     * Given a watcher monitoring a file
     * When the file is modified
     * Then it notifies a file modification event
     */
    @Test(timeout = 1000)
    public void test4() throws Exception {
        Path newFile = createTempFile();
        watch(newFile);
        FileUtils.touch(newFile.toFile());
        assertNextEvent(MODIFY, newFile.toFile());
    }

    private void watch(Path path) throws Exception {
        watcher.watch(path);
        Thread.sleep(50);
    }

    private void assertNextEvent(EventType create, File expected) throws Exception {
        Event event = watcher.nextEvent();
        assertThat(event).isNotNull();
        assertThat(event.type()).isEqualTo(create);
        assertThat(event.path().toFile()).isEqualTo(expected);
    }

}
