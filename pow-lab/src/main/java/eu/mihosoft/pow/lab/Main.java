package eu.mihosoft.pow.lab;

import eu.hansolo.fx.Poi;
import eu.mihosoft.pow.client.Client;
import eu.mihosoft.pow.net.api.IPScanner;
import eu.mihosoft.pow.net.api.POWRemoteAPI;
import eu.mihosoft.vrl.fxscad.JFXScad;
import eu.mihosoft.vrl.fxscad.RedirectableStream;
import eu.mihosoft.vrl.v3d.Transform;
import eu.mihosoft.vrl.v3d.Vector3d;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

    private static final Map<String, POWRemoteAPI> devices
            = Collections.synchronizedMap(new HashMap<>());
    private static Thread iPScannerThread;

    private boolean stoppedScanning;

    private static final POWRemoteGroupAPI groupAPI = new POWRemoteGroupAPI();

    // TODO remove code repetition
    static class POWRemoteGroupAPI implements POWRemoteAPI {

        private Poi poi = new Poi("Pow-Bot");
        private Vector3d pos = new Vector3d(0, 0);
        private Vector3d direction = Vector3d.y(-1);

        public void setPoi(Poi poi) {
            this.poi = poi;
        }

        @Override
        public String identify() {
            throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
        }

        @Override
        public boolean turnLeft(int angle) {

            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.turnLeft(angle);
                });

                t.start();
                threads.add(t);
            }

            direction = direction.transformed(Transform.unity().rotZ(angle));

//            System.out.println("-> bot-direction: " + direction);
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean turnRight(int angle) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.turnRight(angle);
                });

                t.start();
                threads.add(t);
            }

            direction = direction.transformed(Transform.unity().rotZ(-angle));

//            System.out.println("dir: " + direction);
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean moveForward(int duration) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.moveForward(duration);
                });

                t.start();
                threads.add(t);
            }

            double dt = 0.00015 * duration;
            Vector3d oldPos = pos;
            pos = pos.plus(direction.times(dt));

            Timeline tl = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(poi.xProperty(), oldPos.x),
                            new KeyValue(poi.yProperty(), oldPos.y)),
                    new KeyFrame(Duration.millis(duration),
                            new KeyValue(poi.xProperty(), pos.x),
                            new KeyValue(poi.yProperty(), pos.y)));

            tl.play();

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
            
            if (threads.isEmpty()) {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            tl.stop();

            Platform.runLater(() -> {
                poi.setX(pos.x);
                poi.setY(pos.y);
            });

            return true;
        }

        @Override
        public boolean moveBackward(int duration) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.moveBackward(duration);
                });

                t.start();
                threads.add(t);
            }

            double dt = 0.00015 * duration;
            Vector3d oldPos = pos;
            pos = pos.plus(direction.negated().times(dt));

            Timeline tl = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(poi.xProperty(), oldPos.x),
                            new KeyValue(poi.yProperty(), oldPos.y)),
                    new KeyFrame(Duration.millis(duration),
                            new KeyValue(poi.xProperty(), pos.x),
                            new KeyValue(poi.yProperty(), pos.y)));

            tl.play();

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }
            
            if (threads.isEmpty()) {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            tl.stop();

            Platform.runLater(() -> {
                poi.setX(pos.x);
                poi.setY(pos.y);
            });

            return true;
        }

        @Override
        public boolean stopServos() {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.stopServos();
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean turnOnIR() {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.turnOnIR();
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean turnOffIR() {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.turnOffIR();
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean turnOnIR(int duration) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.turnOnIR(duration);
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean setStatusLED(int duration, double value) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.setStatusLED(duration, value);
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean setStatusLED(double value) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.setStatusLED(value);
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean blinkStatusLED(int onFreq, int offFreq) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.blinkStatusLED(onFreq, offFreq);
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean releaseStatusLED() {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.releaseStatusLED();
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean reboot() {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.reboot();
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public int readSensor() {
            throw new UnsupportedOperationException("Not supported yet."); // TODO NB-AUTOGEN
        }

        @Override
        public boolean turnLeftServo(double value) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.turnLeftServo(value);
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean turnRightServo(double value) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.turnRightServo(value);
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean setFullTurnLeftDuration(int duration) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.setFullTurnLeftDuration(duration);
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

        @Override
        public boolean setFullTurnRightDuration(int duration) {
            List<Thread> threads = new ArrayList<>();
            for (POWRemoteAPI api : devices.values()) {
                Thread t = new Thread(() -> {
                    api.setFullTurnRightDuration(duration);
                });

                t.start();
                threads.add(t);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            return true;
        }

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("LabScreen.fxml"));

        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        LabScreenController controller = loader.getController();

        Scene scene = new Scene(controller.getRootView());

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.show();

        tryConnect(controller.getConnectionController());
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    private void tryConnect(ConnectionController controller) {
        
//        if (true)return;

        Client client = new Client();

        if (iPScannerThread != null) {
            stoppedScanning = true;
            try {
                iPScannerThread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).
                        log(Level.SEVERE, null, ex);
            }
        }

        IPScanner.setOnDeviceDetectedEventHandler((String ip) -> {

            System.out.println(" -> scanning " + ip);

            if (!devices.keySet().contains(ip)) {
                POWRemoteAPI api = client.connect(ip);
                devices.put(ip, api);
            }

            System.out.println("#api: " + devices.keySet().size());

            Platform.runLater(() -> controller.setOnline(!devices.isEmpty()));
        });

        iPScannerThread = new Thread(() -> {
            stoppedScanning = false;
            while (!stoppedScanning) {
                System.out.println("Searching devices [" + new Date() + "]...");
                IPScanner.scanAllNetworkDevices();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

        });

        iPScannerThread.start();

    }

    public static POWRemoteAPI getPOWRemoteAPI() {
        return groupAPI;
    }

    public Poi getPoi() {
        return groupAPI.poi;
    }

    static void setPoi(Poi poi) {
        groupAPI.poi = poi;
    }

    static void resetBotPosition() {
        groupAPI.pos = new Vector3d(0, 0);
        groupAPI.direction = Vector3d.y(-1);

        Timeline tl = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(groupAPI.poi.xProperty(), groupAPI.poi.getX()),
                        new KeyValue(groupAPI.poi.yProperty(), groupAPI.poi.getY())),
                new KeyFrame(Duration.millis(500),
                        new KeyValue(groupAPI.poi.xProperty(), 0),
                        new KeyValue(groupAPI.poi.yProperty(), 0)));

        tl.play();
    }

    private static void invokeAndWait(Runnable r) {
        if (Platform.isFxApplicationThread()) {
            r.run();
        } else {
            FutureTask<Boolean> task = new FutureTask<>(r, true);

            Platform.runLater(task);

            try {
                task.get(); // like join()
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(RedirectableStream.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
