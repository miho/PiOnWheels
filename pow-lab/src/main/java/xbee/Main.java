package xbee;

import com.rapplogic.xbee.api.XBeeException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * User: hansolo
 * Date: 16.10.13
 * Time: 16:15
 */
public class Main {
    private static DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss");
    private SensorListener listener;

//    public Main() throws XBeeException {
////        this("/dev/tty.usbserial-A9M15VFJ");
//    }
    
    public static void runXbee(SensorListener listener) {
        try {
            Main main = new Main("/dev/tty.usbserial-A9M15VFJ", listener);
        } catch (XBeeException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Main(final String SERIAL_PORT, SensorListener l) throws XBeeException {
        
        XBeeReceiver.INSTANCE.addXBee("0x00,0x13,0xa2,0x00,0x40,0x64,0x53,0x3c", "sensor0");
        XBeeReceiver.INSTANCE.addXBee("0x00,0x13,0xa2,0x00,0x40,0x64,0x52,0xf2", "sensor1");
        XBeeReceiver.INSTANCE.addXBee("0x00,0x13,0xa2,0x00,0x40,0x64,0x52,0xab", "sensor2");
        
        XBeeReceiver.INSTANCE.setOnXBeeEventReceived(xBeeEvent -> {
            System.out.println("XBee ID            : " + xBeeEvent.xbeeName);    
            System.out.println("Time               : " + TF.format(LocalDateTime.now()));            
            System.out.println("XBee AD3           : " + String.format(Locale.US, "%.5f", xBeeEvent.valueAD3));
            System.out.println("XBee AD4 on        : " + xBeeEvent.isAD4On);
            System.out.println("XBee supply voltage: " + xBeeEvent.supplyVoltage);
            System.out.println();        
            
            System.out.println("lll: " + l);
            
            if(l!=null) {
                
                int sensorId = -1;
                try{
                    String nrString = xBeeEvent.xbeeName.replace("sensor", "");
                    sensorId = Integer.parseInt(nrString);
                } catch(Exception ex) {
                    //
                }
                l.onEvent(sensorId,xBeeEvent.valueAD3);
            }
        });
        XBeeReceiver.INSTANCE.setComPort(SERIAL_PORT);
        XBeeReceiver.INSTANCE.start();        
    }

//    public static void main(String[] args) throws XBeeException {
//        new Main();
//    }
}
