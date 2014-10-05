package eu.mihosoft.pow.client;

import eu.mihosoft.pow.net.api.IPScanner;
import eu.mihosoft.pow.net.api.POWRemoteAPI;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
int port = 8089;

        try {
            IPScanner.setOnDeviceDetectedEventHandler((String ip) -> {

                Logger.getLogger(Main.class.getName()).log(Level.INFO, "testing " + ip);

                XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
                try {
                    config.setServerURL(new URL("http://" + ip + ":" + port + "/xmlrpc"));
                } catch (MalformedURLException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

                XmlRpcClient client = new XmlRpcClient();
                client.setConfig(config);

                ClientFactory factory = new ClientFactory(client);
                POWRemoteAPI api = (POWRemoteAPI) factory.newInstance(POWRemoteAPI.class);

                if (Objects.equals(api.identify(), POWRemoteAPI.IDENTITY)) {
                    System.out.println(" --> POW Device detected: " + ip);
                    api.setStatusLED(1.0);
                }


                api.setFullTurnLeftDuration(2900);
                api.setFullTurnRightDuration(2800);

                 api.moveForward(2000);
                 api.turnLeft(50);
                 api.moveForward(1000);
                 api.turnRight(90);
                 api.moveBackward(2000);
                 

            });

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        IPScanner.scanAllNetworkDevices();
    }
}
