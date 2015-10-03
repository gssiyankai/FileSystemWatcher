package launch;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public final class Main {

    private static final int PORT = 8443;

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setConnector(httpsConnector());
        tomcat.getService().addConnector(tomcat.getConnector());

        String webappDirLocation = "src/main/webapp/";
        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        System.out.println("Configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

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
        File keystore = Files.createTempFile("keystore", "").toFile();
        IOUtils.copy(Main.class.getResourceAsStream("/ssl/keystore"), new FileOutputStream(keystore));
        return keystore.getAbsolutePath();
    }

}
