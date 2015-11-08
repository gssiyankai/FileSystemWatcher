package fs.watcher;

interface ChangeListener {

    void onChange(Long time, Event event) throws Exception;

}
