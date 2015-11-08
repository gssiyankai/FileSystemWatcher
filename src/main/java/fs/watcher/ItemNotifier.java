package fs.watcher;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.nio.file.Path;

import static java.util.stream.Collectors.toList;

@ServerEndpoint(value = "/websocket/items/notify")
public class ItemNotifier implements ItemListener {

    private final Watcher watcher;
    private Session session;

    public ItemNotifier() throws Exception {
        watcher = Watcher.singleton();
        watcher.registerItemListener(this);
    }

    @OnOpen
    public void start(Session session) throws Exception {
        this.session = session;
        synchronized (watcher) {
            this.session
                    .getBasicRemote()
                    .sendText(
                            String.join(";",
                                    watcher.items().
                                            stream().
                                            map(p -> p.toFile().getAbsolutePath())
                                            .collect(toList())));
        }
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
    public void onInsertion(Path path) throws Exception {
        session.getBasicRemote().sendText(path.toFile().getAbsolutePath());
    }

}
