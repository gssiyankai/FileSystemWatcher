package fs.watcher;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static fs.watcher.Helper.createTempFile;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;
import static org.fest.assertions.Assertions.assertThat;

public class WatcherServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(WatcherService.class);
    }

    /**
     * Given a watcher service
     * When a request to watch an existing file is sent
     * Then the status of the response is 200
     */
    @Test
    public void test1() throws Exception {
        Response response = watch(createTempFile().toFile().getAbsolutePath());
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
    }

    /**
     * Given a watcher service
     * When a request to watch a non-existing file is sent
     * Then the status of the response is 400
     */
    @Test
    public void test2() throws Exception {
        Response response = watch("dummy");
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
        assertThat(response.readEntity(String.class)).isEqualTo("The path dummy does not exist");
    }

    private Response watch(String path) {
        Entity<String> entity = Entity.entity(path, MediaType.TEXT_PLAIN);
        return target("/watch").request().post(entity);
    }

}
