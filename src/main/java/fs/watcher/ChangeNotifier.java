package fs.watcher;

import fs.watcher.Event.EventType;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.file.Path;

@ServerEndpoint(value = "/websocket/changes/notify")
public class ChangeNotifier implements ChangeListener {

    private final Watcher watcher;
    private Session session;

    public ChangeNotifier() throws Exception {
        watcher = Watcher.singleton();
        watcher.registerChangeListener(this);
    }

    @OnOpen
    public void start(Session session) throws Exception {
        this.session = session;
    }

    @OnClose
    public void end() {
    }

    @OnMessage
    public void incoming(String message) throws Exception {
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
    }

    @Override
    public void onChange(EventType eventType, Path path) throws Exception {

    }

}
