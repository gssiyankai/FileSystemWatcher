package fs.watcher;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.text.DateFormat;

@ServerEndpoint(value = "/websocket/changes/notify")
public final class ChangeNotifier implements ChangeListener {

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
    public void onChange(Long time, Event event) throws Exception {
        session.getBasicRemote().sendText(
                String.join(";",
                        DateFormat.getInstance().format(time),
                        event.type().name(),
                        event.path().toFile().getAbsolutePath(),
                        "pending"));
        session.getBasicRemote().sendText(
                String.join(";",
                        DateFormat.getInstance().format(time),
                        event.type().name(),
                        event.path().toFile().getAbsolutePath(),
                        "done"));
    }

}
