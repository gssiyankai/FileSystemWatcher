package fs.watcher;

import fs.watcher.Event.EventType;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import static fs.watcher.Event.EventType.*;
import static fs.watcher.Helper.createTempDir;
import static fs.watcher.Helper.createTempFile;
import static fs.watcher.Watcher.MAX_NB_WATCHERS;
import static java.util.stream.Collectors.toList;
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

    /**
     * Given a watcher
     * When items are registered
     * Then the watcher keeps track of them
     */
    @Test(timeout = 1000)
    public void test5() throws Exception {
        List<Path> newFiles = IntStream.range(0, 3).mapToObj(i -> createTempFile()).collect(toList());
        newFiles.forEach(this::watch);
        assertThat(watcher.items()).isEqualTo(newFiles);
    }

    /**
     * Given a watcher
     * When more items than MAX_NB_WATCHERS are registered
     * Then there is an explicit exception thrown
     */
    @Test(expected = RuntimeException.class)
    public void test6() throws Exception {
        List<Path> newFiles = IntStream.range(0, MAX_NB_WATCHERS+1).mapToObj(i -> createTempFile()).collect(toList());
        newFiles.forEach(this::watch);
    }

    private void watch(Path path) {
        try {
            watcher.watch(path);
            Thread.sleep(50);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void assertNextEvent(EventType create, File expected) throws Exception {
        Event event = watcher.nextEvent();
        assertThat(event).isNotNull();
        assertThat(event.type()).isEqualTo(create);
        assertThat(event.path().toFile()).isEqualTo(expected);
    }

}
