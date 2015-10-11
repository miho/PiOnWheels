/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.pow.net.api;

import eu.hansolo.fx.Poi;
import eu.mihosoft.pow.net.api.pixycam.Frame;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface POWRemoteAPI {
    
    static final String IDENTITY = "I'm a PoW device.";
    String identify();
    boolean turnLeft(int duration);
    boolean turnRight(int duration);
    boolean moveForward(int duration);
    boolean moveBackward(int duration);
    boolean stopServos();
    boolean turnOnIR();
    boolean turnOffIR();
    boolean turnOnIR(int duration);
    boolean setStatusLED(int duration, double value);
    boolean setStatusLED(double value);
    boolean blinkStatusLED(int onFreq, int offFreq);
    boolean releaseStatusLED();
    boolean reboot();
    int readSensor();
    boolean turnLeftServo(double value);
    boolean turnRightServo(double value);
    
    boolean setFullTurnLeftDuration(int duration);
    boolean setFullTurnRightDuration(int duration);
    
    boolean hasPixyCam();
    byte[] getPixyFrameInfo();
    boolean startPixyCam();
    boolean stopPixyCam();
}
