/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.io.pixycam;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author miho
 */
public class Pixy {

    public Pixy() {
        init();
    }

    private void init() {

        if (isPixyRunning()) {
            return;
        }

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
                        log(java.util.logging.Level.INFO, "shutting down previous pow-pixy");

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

    private void startPixy() {

        String msg = "";

        try {
            URL inputUrl = getClass().
                    getResource("/eu/mihosoft/pow/net/io/pixycam/pow-pixy");
            File dest = new File("/tmp/pow-pixy");
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (Exception ex) {
            // we don't care if file already exists
        }

        execute("sudo chmod +x /tmp/pow-pixy", true);
        execute("sudo /tmp/pow-pixy", false);

        System.out.println("isRunning: " + isPixyRunning());
    }

    private void execute(String cmd, boolean waitFor) {

        try {
            String msg = "";

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
                msg += line + "\n";

                System.out.println(">> " + line);
            }

            while ((line = err.readLine()) != null) {
                msg += line + "\n";

                System.out.println(">> " + line);
            }
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Pixy.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Pixy.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private static boolean isPixyRunning() {

        Runtime rt = Runtime.getRuntime();

        try {

            String msg = "";

            Process pr = rt.exec("sh -c ps aux");

            pr.waitFor();

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
                msg += line + "\n";
            }

            return msg.contains("pow-pixy");

        } catch (InterruptedException | IOException ex) {
            java.util.logging.Logger.getLogger(Pixy.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);

            return false;
        }
    }
}
