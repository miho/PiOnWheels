package eu.mihosoft.pow.net.server;

import eu.mihosoft.pow.net.server.api.POWServerAPI;
import eu.mihosoft.pow.net.api.POWRemoteAPI;
import eu.mihosoft.pow.net.io.pixycam.Pixy;
import java.io.IOException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.metadata.Util;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        Pixy pixy = new Pixy();
        
        System.out.println("---");
        
        init();
        
        try {
            server();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (XmlRpcException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }
    
      private static void server() throws IOException, XmlRpcException {
        WebServer webServer = new WebServer(8089);

        XmlRpcServer xmlRpcServer = webServer.getXmlRpcServer();

        PropertyHandlerMapping mapping = new PropertyHandlerMapping();

        mapping.addHandler(POWRemoteAPI.class.getName(), POWServerAPI.class);
        
        POWRemoteAPI api = (POWRemoteAPI) Util.newInstance(POWServerAPI.class);

        mapping.setRequestProcessorFactoryFactory(
                new RequestProcessorFactoryFactory.RequestSpecificProcessorFactoryFactory() {
            @Override
            protected Object getRequestProcessor(Class pClass, XmlRpcRequest pRequest) throws XmlRpcException {
                return api;
            }
        });

        xmlRpcServer.setHandlerMapping(mapping);

        XmlRpcServerConfigImpl serverConfig
                = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
        serverConfig.setEnabledForExtensions(true);
        serverConfig.setContentLengthOptional(false);

        webServer.start();
        
        api.blinkStatusLED(500, 500);
    }

    private static void init() {
        ConsoleAppender console = new ConsoleAppender(); //create appender
        //configure the appender
        String PATTERN = "%d [%p|%c|%C{1}] %m%n";
        console.setLayout(new PatternLayout(PATTERN));
        console.setThreshold(Level.TRACE);
        console.activateOptions();
        //add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(console);

        FileAppender fa = new FileAppender();
        fa.setName("FileLogger");
        fa.setFile("log.log");
        fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
        fa.setThreshold(Level.TRACE);
        fa.setAppend(true);
        fa.activateOptions();

        //add appender to any Logger (here is root)
        Logger.getRootLogger().addAppender(fa);

    }
}
