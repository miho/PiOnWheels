/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.server.api;

import eu.mihosoft.pow.net.api.POWRemoteAPI;
import eu.mihosoft.pow.net.io.LED;
import eu.mihosoft.pow.net.io.Servo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class POWServerAPI implements POWRemoteAPI {

    private static final Servo leftServo = new Servo(4);
    private static final Servo rightServo = new Servo(17);
    private static final LED statusLED = new LED(25);

    private int fullTurnRight = 2730;
    private int fullTurnLeft = 2500;

    {
        leftServo.calibrateCenter(0.525);
        rightServo.calibrateCenter(0.47);
    }

    public POWServerAPI() {
        System.out.println("init");

    }

    @Override
    public String identify() {
        return IDENTITY;
    }

    private void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            Logger.getLogger(POWServerAPI.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean turnLeft(int angle) {

        leftServo.turn(1.0);
        rightServo.turn(1.0);
        sleep((long) (fullTurnLeft * angle / 360.0));

        stopServos();

        return true;
    }

    @Override
    public boolean turnRight(int angle) {

        leftServo.turn(0.0);
        rightServo.turn(0.0);

        sleep((long) (fullTurnRight * angle / 360.0));

        stopServos();

        return true;
    }

    @Override
    public boolean moveForward(int duration) {

        leftServo.turn(0.0);
        rightServo.turn(1.0);
        sleep(duration);

        stopServos();

        return true;
    }

    @Override
    public boolean moveBackward(int duration) {

        leftServo.turn(1.0);
        rightServo.turn(0.0);
        sleep(duration);

        stopServos();

        return true;
    }

    @Override
    public int readSensor() {
        return 0;
    }

    @Override
    public boolean stopServos() {

        leftServo.stop();
        rightServo.stop();

        return true;
    }

    @Override
    public boolean turnOnIR() {

        return true;
    }

    @Override
    public boolean turnOffIR() {

        return true;
    }

    @Override
    public boolean turnOnIR(int duration) {
        turnOnIR();
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            Logger.getLogger(POWServerAPI.class.getName()).log(Level.SEVERE, null, ex);
        }
        turnOffIR();
        return true;
    }

    @Override
    public boolean reboot() {
        Runtime rt = Runtime.getRuntime();
        try {

            java.util.logging.Logger.getLogger(POWServerAPI.class.getName()).
                    log(java.util.logging.Level.SEVERE, "reboting the pi");

            String msg = "";

            Process pr = rt.exec("sudo reboot");

            pr.waitFor();

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(pr.getInputStream()));

            String line = null;

            while ((line = input.readLine()) != null) {
                msg += line + "\n";
            }

        } catch (InterruptedException | IOException ex) {
            java.util.logging.Logger.getLogger(POWServerAPI.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    @Override
    public boolean turnLeftServo(double value) {
        leftServo.turn(value);
        return true;
    }

    @Override
    public boolean turnRightServo(double value) {
        rightServo.turn(value);
        return true;
    }

    @Override
    public boolean setStatusLED(int duration, double value) {
        statusLED.setBrightness(duration, value);

        return true;
    }

    @Override
    public boolean releaseStatusLED() {
        statusLED.release();

        return true;
    }

    @Override
    public boolean blinkStatusLED(int onFreq, int offFreq) {

        statusLED.blink(onFreq, offFreq);

        return true;
    }

    @Override
    public boolean setStatusLED(double value) {
        statusLED.setBrightness(value);

        return true;
    }

    @Override
    public boolean setFullTurnLeftDuration(int duration) {
        this.fullTurnLeft = duration;

        return true;
    }

    @Override
    public boolean setFullTurnRightDuration(int duration) {
        this.fullTurnRight = duration;

        return true;
    }

}
