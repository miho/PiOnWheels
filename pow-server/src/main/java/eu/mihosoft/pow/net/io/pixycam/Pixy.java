/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.io.pixycam;

import eu.mihosoft.pow.net.api.pixycam.Blob;
import eu.mihosoft.pow.net.api.pixycam.Frame;
import eu.mihosoft.pow.net.api.pixycam.FrameImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Pixy {

    private Frame frame;

    public Pixy() {
        init();
    }

    private void init() {

        if (isPixyRunning()) {
            return;
        }

        prepareExecutables();

        startPixy();

    }

    public static void stopPixy() {
        if (!isPixyRunning()) {

            java.util.logging.Logger.getLogger(Pixy.class.getName()).
                    log(java.util.logging.Level.INFO, "pow-pixy not running");

        } else {

            Runtime rt = Runtime.getRuntime();
            try {

                java.util.logging.Logger.getLogger(Pixy.class.getName()).
                        log(java.util.logging.Level.INFO,
                                "shutting down previous pow-pixy");

                String msg = "";

                Process pr = rt.exec("sudo killall pow-pixy");

                pr.waitFor();

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(pr.getInputStream()));

                String line = null;

                while ((line = input.readLine()) != null) {
                    msg += line + "\n";
                }

            } catch (InterruptedException | IOException ex) {
                java.util.logging.Logger.getLogger(Pixy.class.getName()).
                        log(java.util.logging.Level.SEVERE, null, ex);

            }
        }
    }

    public void startPixy() {

        if (!isCamConnected()) {
            System.err.println("ERROR: pixycam is not connected!");
            return;
        }

        execute("sudo chmod +x /tmp/pow-pixy", true);
        executeThread("sudo /tmp/pow-pixy");

        System.out.println("PixyCam: isRunning: " + isPixyRunning());
    }

    private void executeThread(String cmd) {

        stopPixy();

        Runnable r = () -> {

            try {
                String msg = "";

                Runtime rt = Runtime.getRuntime();

                Process pr = rt.exec(cmd);

                BufferedReader sout = new BufferedReader(
                        new InputStreamReader(pr.getInputStream()));
                BufferedReader err = new BufferedReader(
                        new InputStreamReader(pr.getInputStream()));

                String line = null;

                while ((line = sout.readLine()) != null) {
                    msg += line + "\n";

                    String trimmedLine = line.trim();

                    try {

                        if (trimmedLine.startsWith("frame")) {
                            String numberString = trimmedLine.split(":")[1].trim();
                            synchronized (this) {
                                frame = Frame.newInstance(
                                        Integer.parseInt(numberString));
                            }
                        } else if (trimmedLine.startsWith("sig")) {
                            String[] entries = trimmedLine.split(";");
                            int id = Integer.parseInt(entries[0].split(":")[1].trim());
                            int x = Integer.parseInt(entries[1].split(":")[1].trim());
                            int y = Integer.parseInt(entries[2].split(":")[1].trim());
                            int w = Integer.parseInt(entries[3].split(":")[1].trim());
                            int h = Integer.parseInt(entries[4].split(":")[1].trim());

                            synchronized (this) {
                                ((FrameImpl) frame).
                                        addBlob(Blob.newInstance(id, x, y, w, h));
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println(
                                ">> ERROR while parsing line: " + line);
                        ex.printStackTrace(System.err);
                    }
                } // end while readLine()

                while ((line = err.readLine()) != null) {
                    msg += line + "\n";

                    System.err.println(">> " + line);
                }
            } catch (IOException ex) {
                java.util.logging.Logger.
                        getLogger(Pixy.class.getName()).
                        log(java.util.logging.Level.SEVERE, null, ex);
            }
        };

        Thread t = new Thread(r);
        t.start();
    }

    private int executeWithResult(String cmd) {

        try {

            Runtime rt = Runtime.getRuntime();

            Process pr = rt.exec(cmd);

            pr.waitFor();

            BufferedReader sout = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));
            BufferedReader err = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));

            String line = null;

            while ((line = sout.readLine()) != null) {

                System.out.println(">> " + line);
            }

            while ((line = err.readLine()) != null) {

                System.out.println(">> " + line);
            }

            return pr.exitValue();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Pixy.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Pixy.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        }
        return 1;
    }

    private void execute(String cmd, boolean waitFor) {

        try {

            Runtime rt = Runtime.getRuntime();

            Process pr = rt.exec(cmd);

            if (waitFor) {
                pr.waitFor();
            }

            BufferedReader sout = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));
            BufferedReader err = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));

            String line = null;

            while ((line = sout.readLine()) != null) {

                System.out.println(">> " + line);
            }

            while ((line = err.readLine()) != null) {

                System.out.println(">> " + line);
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Pixy.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Pixy.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private static boolean isPixyRunning() {

        Runtime rt = Runtime.getRuntime();

        try {

            String msg = "";

            Process pr = rt.exec("sudo sh -c ps aux");

            pr.waitFor();

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
//                System.out.println("line: " + line);
                msg += line + "\n";
            }

            return msg.contains("pow-pixy");

        } catch (InterruptedException | IOException ex) {
            java.util.logging.Logger.getLogger(Pixy.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);

            return false;
        }
    }

    /**
     * @return the frame
     */
    public Frame getFrame() {
        Frame result;

        synchronized (this) {
            result = Frame.copy(frame);
        }

        return result;
    }

    private void prepareExecutables() {
        try {
            URL inputUrl = getClass().
                    getResource("/eu/mihosoft/pow/net/io/pixycam/pow-pixy-connected");
            File dest = new File("/tmp/pow-pixy-connected");
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (Exception ex) {
            // we don't care if file already exists
        }
        execute("sudo chmod +x /tmp/pow-pixy-connected", true);

        try {
            URL inputUrl = getClass().
                    getResource("/eu/mihosoft/pow/net/io/pixycam/pow-pixy");
            File dest = new File("/tmp/pow-pixy");
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (Exception ex) {
            // we don't care if file already exists
        }
        execute("sudo chmod +x /tmp/pow-pixy", true);

    }

    public boolean isCamConnected() {

        if (isPixyRunning()) {
            return true;
        }

        int camResult = executeWithResult("sudo /tmp/pow-pixy-connected");

        return camResult == 0;
    }
}
