/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.api;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IPScanner {

    private static DeviceDetectedEventHandler deviceDetectedEventHandler;
    private static NetworkErrorEventHandler errorEventHandler;

    public static void setOnDeviceDetectedEventHandler(DeviceDetectedEventHandler ddeh) {
        IPScanner.deviceDetectedEventHandler = ddeh;
    }

    public static void setOnNetworkErrorEventHandler(NetworkErrorEventHandler neeh) {
        IPScanner.errorEventHandler = neeh;
    }

    public static Collection<String> getHostAddresses() {

        // sorry, i'm too dumb for ipv6
        System.setProperty("java.net.preferIPv4Stack", "true");

        List result = new ArrayList();

        Enumeration e;
        try {
            e = NetworkInterface.getNetworkInterfaces();

            while (e.hasMoreElements()) {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = (InetAddress) ee.nextElement();
                    try {
                        if (!i.getHostAddress().equals("127.0.0.1")) {
                            result.add(i.getHostAddress());
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(IPScanner.class.getName()).
                                log(Level.SEVERE, null, ex);
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(IPScanner.class.getName()).
                    log(Level.SEVERE, null, ex);
            if (errorEventHandler != null) {
                errorEventHandler.onError(ex);
            }
        }

        return result;
    }

    public static void scanAllNetworkDevices() {

        // sorry, i'm too dumb for ipv6
        getHostAddresses().stream().
                filter(ip -> ip.split("\\.").length == 4).
                forEach(address -> scan(address));
    }

    private static void scan(String addressInput) {

        String[] addressParts = addressInput.split("\\.");

        if (addressParts.length < 4) {
            throw new IllegalArgumentException("Illegal input address: " + addressInput);
        }

        String addressBegin = addressParts[0] + "." + addressParts[1] + "." + addressParts[2];

        List<Thread> threads = new ArrayList<>();
        
        for (int i = 0; i <= 255; i++) {
            final int finalI = i;
            Thread t = new Thread(() -> {
                String address = addressBegin + "." + finalI;
                try {
                    InetAddress ip = InetAddress.getByName(address);
                    if (ip.isReachable(1000)) {
                        if (deviceDetectedEventHandler != null) {
                            deviceDetectedEventHandler.onDeviceDetected(address);
                        }
                    }
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            });
            
            threads.add(t);
            t.start();
        } // end for i
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(IPScanner.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }
    }
}
