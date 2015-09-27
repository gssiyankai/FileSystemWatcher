package launch;

import org.apache.catalina.startup.Tomcat;

import java.io.File;

public final class Main {

    private static final int PORT = 8080;

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp/";
        Tomcat tomcat = new Tomcat();

        tomcat.setPort(PORT);

        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());
        System.out.println("Configuring app with basedir: " + new File("./" + webappDirLocation).getAbsolutePath());

        tomcat.start();
        tomcat.getServer().await();
    }

}
