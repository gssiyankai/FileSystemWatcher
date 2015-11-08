package fs.watcher;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Paths;

import static java.util.stream.Collectors.toList;

@Singleton
@Path("/")
public class WatcherService {

    private final Watcher watcher;
    private String username;
    private String password;

    public WatcherService() throws Exception {
        watcher = Watcher.singleton();
    }

    @POST
    @Path("/login")
    public boolean login(String credentials) {
        int separatorIndex = credentials.indexOf(":");
        this.username = credentials.substring(0, separatorIndex);
        this.password = credentials.substring(separatorIndex+1);
        return true;
    }

    @GET
    @Path("/list")
    public String list() {
        synchronized (watcher) {
            return String.join(";", watcher.items().stream().map(p -> p.toFile().getAbsolutePath()).collect(toList()));
        }
    }

    @POST
    @Path("/watch")
    public boolean watch(String path) throws Exception {
        java.nio.file.Path p = Paths.get(path);
        if (p.toFile().exists()) {
            synchronized (watcher) {
                watcher.watch(p);
            }
            return true;
        } else {
            throw new FileNotFoundException(path);
        }
    }

    public class FileNotFoundException extends WebApplicationException {
        public FileNotFoundException(String path) {
            super(Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(String.format("The path %s does not exist", path))
                    .type(MediaType.TEXT_PLAIN)
                    .build());
        }
    }

}
