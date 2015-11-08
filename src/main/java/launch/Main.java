package launch;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Main {

    private static final int PORT = 8443;
    private static String WEBAPP_PATH;

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setConnector(httpsConnector());
        tomcat.getService().addConnector(tomcat.getConnector());

        tomcat.addWebapp("", webappPath());

        tomcat.start();
        tomcat.getServer().await();
    }

    private static Connector httpsConnector() throws Exception {
        Connector httpsConnector = new Connector("HTTP/1.1");
        httpsConnector.setPort(PORT);
        httpsConnector.setSecure(true);
        httpsConnector.setScheme("https");
        httpsConnector.setAttribute("keyAlias", "axa");
        httpsConnector.setAttribute("keystorePass", "axaaxa01");
        httpsConnector.setAttribute("keystoreFile", keystoreFile());
        httpsConnector.setAttribute("clientAuth", "false");
        httpsConnector.setAttribute("sslProtocol", "TLS");
        httpsConnector.setAttribute("SSLEnabled", true);
        return httpsConnector;
    }

    private static String keystoreFile() throws Exception {
        return Paths.get(webappPath(), "ssl", "keystore").toFile().getAbsolutePath();
    }

    private static String webappPath() throws IOException {
        if(WEBAPP_PATH == null) {
            WEBAPP_PATH = Files.createTempDirectory("FileSystemWatcher").toFile().getAbsolutePath();
            copyWebappResource(WEBAPP_PATH, "index.jsp");
            copyWebappResource(WEBAPP_PATH, "css", "styles.css");
            copyWebappResource(WEBAPP_PATH, "ssl", "keystore");
            copyWebappResource(WEBAPP_PATH, "WEB-INF", "web.xml");
        }
        return WEBAPP_PATH;
    }

    private static void copyWebappResource(String webapp, String folder, String resource) throws IOException {
        Files.createDirectories(Paths.get(webapp, folder));
        String resourcePath = folder + '/' + resource;
        copyWebappResource(webapp, resourcePath);
    }

    private static void copyWebappResource(String webapp, String resource) throws IOException {
        IOUtils.copy(
                resourceStream("/" + resource),
                new FileOutputStream(Paths.get(webapp, resource).toFile()));
    }

    private static InputStream resourceStream(String path) {
        return Main.class.getResourceAsStream(path);
    }

}
