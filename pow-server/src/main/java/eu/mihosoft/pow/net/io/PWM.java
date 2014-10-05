/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.io;

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
public class PWM {

    private double minValue = 0.0;
    private double maxValue = 1.0;

    private double center = 0.5; // interpolated, center of [0.0,1.0]

    private int gpioNumber = 4;

    private static FileWriter writer;

    public void calibrateMinMax(double minValue, double maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public void calibrateCenter(double center) {
        this.center = center;
    }

    public PWM() {
        init();
    }

    public PWM(int gpio) {
        this.gpioNumber = gpio;
        init();
    }

    public PWM set(double speed) {

        if (speed > 1.0 || speed < 0) {
            throw new IllegalArgumentException(
                    "Illegal speed specified: "
                    + speed
                    + ". Only values between 0.0 and 1.0 are valid.");
        }

//        double realCenter = minValue + (maxValue - minValue) * 0.5;
//        
//        double b = realCenter/(1.0-(0.5-center));
//        if(speed <= center) {
//            b = realCenter/(0.5-(0.5-center));
//        } else {
//            b = realCenter/(0.5+(0.5-center));
//        }
        double val = minValue + speed * (maxValue - minValue);

        executePWMCmd(gpioNumber + "=" + val);
        return this;
    }

    public void release() {
        executePWMCmd("release " + gpioNumber);
    }

    private void init() {

        if (isPWMRunning()) {
            return;
        }

        startPWM();

        try {
            writer = new FileWriter("/dev/pi-blaster");
        } catch (IOException ex) {
            ex.printStackTrace();
            java.util.logging.Logger.getLogger(PWM.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private synchronized static void executePWMCmd(String cmd) {
        try {
            writer.append(cmd + "\n");
            writer.flush();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(PWM.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    public static void stopPWM() {
        if (!isPWMRunning()) {

            java.util.logging.Logger.getLogger(PWM.class.getName()).
                    log(java.util.logging.Level.INFO, "pi-blaser not running");

        } else {

            Runtime rt = Runtime.getRuntime();
            try {

                java.util.logging.Logger.getLogger(PWM.class.getName()).
                        log(java.util.logging.Level.INFO, "shutting down previous pi-blaser");

                String msg = "";

                Process pr = rt.exec("sudo killall pi-blaster");

                pr.waitFor();

                BufferedReader input = new BufferedReader(
                        new InputStreamReader(pr.getInputStream()));

                String line = null;

                while ((line = input.readLine()) != null) {
                    msg += line + "\n";
                }

            } catch (InterruptedException | IOException ex) {
                java.util.logging.Logger.getLogger(PWM.class.getName()).
                        log(java.util.logging.Level.SEVERE, null, ex);

            }
        }
    }

    private void startPWM() {

        String msg = "";

        try {
            URL inputUrl = getClass().
                    getResource("/eu/mihosoft/pow/net/io/pi-blaster");
            File dest = new File("/tmp/pi-blaster");
            FileUtils.copyURLToFile(inputUrl, dest);
        } catch (Exception ex) {
            // we don't care if file already exists
        }

        execute("sudo chmod +x /tmp/pi-blaster");
        execute("sudo /tmp/pi-blaster");

        System.out.println("isRunning: " + isPWMRunning());

    }

    private void execute(String cmd) {

        try {
            String msg = "";

            Runtime rt = Runtime.getRuntime();

            Process pr = rt.exec(cmd);

            pr.waitFor();

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
            java.util.logging.Logger.getLogger(PWM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(PWM.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private static boolean isPWMRunning() {

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

            return msg.contains("pi-blaster");

        } catch (InterruptedException | IOException ex) {
            java.util.logging.Logger.getLogger(PWM.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);

            return false;
        }
    }
}
