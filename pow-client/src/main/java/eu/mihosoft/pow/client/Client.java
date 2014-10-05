/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.client;

import eu.mihosoft.pow.net.api.POWRemoteAPI;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.client.util.ClientFactory;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Client {

    private int port = 8089;
    
    public POWRemoteAPI connect(String ip) {
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
            
            return api;
        }
        
        throw new RuntimeException("Cannot connect to " + ip + ":" + port);
    }
}
