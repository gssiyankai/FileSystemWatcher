package fs.watcher;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.fest.assertions.Assertions.assertThat;

public class WatcherServiceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(WatcherService.class);
    }

    /**
     * Given a watcher service
     * When a request to watch an existing file is sent
     * Then the response status is 200
     */
    @Test
    public void test1() throws Exception {
        Response response = watch(createTempFile());
        assertThat(response.getStatus()).isEqualTo(200);
    }

    /**
     * Given a watcher service
     * When a request to watch a non-existing file is sent
     * Then the response status is 400 and the entity contains a clear error message
     */
    @Test
    public void test2() throws Exception {
        Response response = watch("dummy");
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.readEntity(String.class)).isEqualTo("The path dummy does not exist");
    }

    /**
     * Given a watcher service
     * and 3 registered items
     * When a request to list the registered items is sent
     * Then the response status is 200 and the entity contains the 3 items
     */
    @Test
    public void test3() throws Exception {
        List<String> tempFiles = IntStream.range(0, 3).mapToObj(i -> createTempFile()).collect(toList());
        tempFiles.forEach(this::watch);
        Response response = list();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.readEntity(String.class)).isEqualTo(String.join(";", tempFiles));
    }

    private String createTempFile() {
        return Helper.createTempFile().toFile().getAbsolutePath();
    }

    private Response list() {
        return target("/list").request().get();
    }

    private Response watch(String path) {
        Entity<String> entity = Entity.entity(path, MediaType.TEXT_PLAIN);
        return target("/watch").request().post(entity);
    }

}
