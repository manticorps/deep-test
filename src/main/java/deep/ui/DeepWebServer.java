package deep.ui;//package ch.swissdotnet.osp.deep.ui;
//
//import ch.swissdotnet.osp.deep.Constants;
//import ch.swissdotnet.osp.webserver.AssetServlet;
//import ch.swissdotnet.osp.webserver.WebServer;
//import com.google.common.base.Charsets;
//import org.eclipse.jetty.server.ServerConnector;
//import org.eclipse.jetty.servlet.ServletContextHandler;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.servlet.Servlet;
//import javax.swing.*;
//import java.io.File;
//
//public class DeepWebServer extends JFrame {
//
//    // SLF4J Logger
//    private static final Logger LOG = LoggerFactory.getLogger(DeepWebServer.class);
//
//    private WebServer webServer;
//
//    public DeepWebServer() throws Exception {
//
//        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
//        System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
//
//        File workspace = new File(Constants.WORKSPACE);
//
//        WebServer server = WebServer.Builder
//            .newBuilder()
//            .withMaxThreads(100)
//            .build();
//
//        ServerConnector httpConnector = WebServer.HttpServerConnectorBuilder
//            .newServerConnectorBuilder(server, 10000)
//            .build();
//
//
//        Servlet staticContentServlet = new AssetServlet(workspace.toURL(), "index.html", Charsets.UTF_8);
//
//        WebServer.ServletContextHandlerWrapper contextHandler = WebServer.ServletContextHandlerBuilder
//            .newServletContextHandlerBuilder(WebServer.ServletContextHandlerWrapper.Type.ROOT, "/", ServletContextHandler.SESSIONS)
//            .addServlet("/*", staticContentServlet)
//            .build();
//
//        server.addHandlerAndStart(httpConnector, contextHandler);
//        server.start();
//
//        httpConnector.start();
//    }
//}