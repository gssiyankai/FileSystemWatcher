package fs.watcher;

public interface ChangeListener {

    void onChange(Long time, Event event) throws Exception;

}
