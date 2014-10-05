package xbee;

import com.rapplogic.xbee.api.*;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import com.rapplogic.xbee.util.ByteUtils;
import eu.mihosoft.vrl.system.VSysUtil;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import org.apache.commons.io.FileUtils;

/**
 * User: hansolo Date: 16.10.13 Time: 16:16
 */
public enum XBeeReceiver {

    INSTANCE;

    private Map<String, String> xbees = new HashMap<>();

    public void addXBee(String ID, String name) {
        xbees.put(ID, name);
    }

    //private String         comPort = "/dev/ttyAMA0";
    private String comPort = "tty.usbserial-A9M15VFJ";
    private XBee xbee = new XBee();
    private PacketListener packetListener;

    // ******************** Constructors **************************************
    private XBeeReceiver() {
        initNativeLib();
        initListeners();
    }

    private void initNativeLib() {
        String libName = "librxtxSerial."
                + VSysUtil.getPlatformSpecificLibraryEnding();
        try {
            URL inputUrl = getClass().
                    getResource("/xbee/natives/"
                            + VSysUtil.getPlatformSpecificPath() + "/"
                            + libName);
            
            File tmp = new File(System.getProperty("java.io.tmpdir"));
            File dest = new File(tmp,"xbee/" + libName);
            FileUtils.copyURLToFile(inputUrl, dest);
            VSysUtil.addNativeLibraryPath(
                    dest.getParentFile().getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(XBeeReceiver.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    // ******************** Initialization ************************************
    private void initListeners() {
        packetListener = response -> {
            if (ApiId.ZNET_IO_SAMPLE_RESPONSE == response.getApiId()) {
                ZNetRxIoSampleResponse rx = (ZNetRxIoSampleResponse) response;

                fireXBeeEvent(new XBeeEvent(ByteUtils.toBase16(rx.getRemoteAddress64().getAddress()),
                        rx.getAnalog3(),
                        rx.isD4On(),
                        rx.getSupplyVoltage() * 3.6 / 1024.0 * 1.38856, // connected to 3.3 V powersource
                        XBeeEvent.XBEE_EVENT_RECEIVED));
                
                XBeeAddress64 remoteAddress = new XBeeAddress64(0x00,0x13,0xa2,0x00,0x40,0x64,0x52,0xf2);
                RemoteAtRequest D4_HIGH = new RemoteAtRequest(remoteAddress, "D4", new int[]{5});
                RemoteAtRequest D4_LOW = new RemoteAtRequest(remoteAddress, "D4", new int[]{4});

                try {
                    xbee.sendAsynchronous(rx.isD4On() ? D4_LOW : D4_HIGH);
                } catch (XBeeException exception) {

                }
            }
        };
    }

    // ******************** Methods *******************************************
    public void setComPort(final String COM_PORT) {
        comPort = COM_PORT;
        System.setProperty("gnu.io.rxtx.SerialPorts", comPort);
    }

    public void start() throws XBeeException {
        if (null == comPort || comPort.isEmpty()) {
            return;
        }
        try {
            xbee.open(comPort, 9600);
            xbee.addPacketListener(packetListener);

            System.out.println(xbee.isConnected());

            XBeeAddress64 remoteAddress = new XBeeAddress64(0x00,0x13,0xa2,0x00,0x40,0x64,0x52,0xf2);
            RemoteAtRequest D4_HIGH = new RemoteAtRequest(remoteAddress, "D4", new int[]{5});
            RemoteAtRequest D4_LOW = new RemoteAtRequest(remoteAddress, "D4", new int[]{4});

            RemoteAtRequest D3_ANALOG_IN = new RemoteAtRequest(remoteAddress, "D3", new int[]{2});

            AtCommandResponse response = (AtCommandResponse) xbee.sendSynchronous(D4_LOW, 5000);

            if (response.isOk()) {
                System.out.println(response);
            }

            while (true) {

            }
        } finally {
            if (xbee.isConnected()) {
                xbee.removePacketListener(packetListener);
                xbee.close();
            }
        }
    }

    public void stop() {
        if (xbee.isConnected()) {
            xbee.removePacketListener(packetListener);
            xbee.close();
        }
    }

    // ******************** Event Handling ************************************
    public final ObjectProperty<EventHandler<XBeeEvent>> onXBeeEventReceivedProperty() {
        return onXBeeEventReceived;
    }

    public final void setOnXBeeEventReceived(EventHandler<XBeeEvent> handler) {
        onXBeeEventReceivedProperty().set(handler);
    }

    public final EventHandler<XBeeEvent> getOnXBeeEventReceived() {
        return onXBeeEventReceivedProperty().get();
    }
    private final ObjectProperty<EventHandler<XBeeEvent>> onXBeeEventReceived
            = new ObjectPropertyBase<EventHandler<XBeeEvent>>() {
                @Override
                public Object getBean() {
                    return this;
                }

                @Override
                public String getName() {
                    return "onXBeeEventReceived";
                }
            };

    public void fireXBeeEvent(final XBeeEvent EVENT) {
        final EventHandler<XBeeEvent> HANDLER = getOnXBeeEventReceived();
        if (null == HANDLER) {
            return;
        }

        HANDLER.handle(EVENT);
    }

    // ******************** Inner Classes *************************************
    public static class XBeeEvent extends Event {

        public static final EventType<XBeeEvent> XBEE_EVENT_RECEIVED = new EventType(ANY, "XBEE_EVENT_RECEIVED");
        public String xbeeId;
        public String xbeeName;
        public double valueAD3;
        public boolean isAD4On;
        public double supplyVoltage;

        // ******************* Constructors ***************************************        
        public XBeeEvent(final String ID, final double VALUE_AD3, final boolean IS_AD4_ON, final double SUPPLY_VOLTAGE, final Object SOURCE) {
            super(SOURCE, null, XBEE_EVENT_RECEIVED);
            xbeeId = ID;
            xbeeName = "unknown";

            if (INSTANCE.xbees.containsKey(ID)) {
                xbeeName = INSTANCE.xbees.get(ID);
            }

            valueAD3 = VALUE_AD3;
            isAD4On = IS_AD4_ON;
            supplyVoltage = SUPPLY_VOLTAGE;
        }
    }
}
