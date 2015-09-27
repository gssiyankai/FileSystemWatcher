package fs.watcher;

import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.Paths;

@Singleton
@Path("/")
public class WatcherService {

    private final Watcher watcher;

    public WatcherService() throws Exception {
        watcher = new Watcher();
    }

    @POST
    @Path("/watch")
    public Response watch(String path) throws Exception {
        java.nio.file.Path p = Paths.get(path);
        if (p.toFile().exists()) {
            watcher.watch(p);
            return Response.ok().build();
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
