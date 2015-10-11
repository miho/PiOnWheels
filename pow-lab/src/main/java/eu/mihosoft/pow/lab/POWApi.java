/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.lab;

import eu.mihosoft.pow.net.api.POWRemoteAPI;
import eu.mihosoft.pow.net.api.pixycam.Frame;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface POWApi {

    public static POWApi newApi(POWRemoteAPI api) {
        return new POWApiImpl(api);
    }

    public static POWApi newApi() {
        if (Main.getPOWRemoteAPI().getDevices().isEmpty()) {
            return null;
        }
        return new POWApiImpl(Main.getPOWRemoteAPI().
                getDevices().iterator().next());
    }

    public void move(Direction d);

    public void move(Direction d, int duration);

    public void moveForward();

    public void moveForward(int duration);

    public void moveBackward();

    public void moveBackward(int duration);

    public void stopServos();

    public void turnLeft();

    public void turnRight();

    public void turnLeft(int duration);

    public void turnRight(int duration);

    public void turnOnLED();

    public void turnOffLED();

    public void blinkLEDFast();

    public void blinkLEDSlow();

    public boolean hasPixyCam();

    Frame getPixyFrame();

    void startPixyCam();

    void stopPixyCam();

    boolean hasPixyCamDetected(int id);
}

class POWApiImpl implements POWApi {

    private final POWRemoteAPI remoteApi;
    private final int defaultMoveDuration = 1000;
    private final int defaultTurnDuration = 1000;

    POWApiImpl(POWRemoteAPI remoteAPI) {
        this.remoteApi = remoteAPI;
    }

    @Override
    public void moveForward() {
        remoteApi.moveForward(defaultMoveDuration);
    }

    @Override
    public void moveForward(int duration) {
        remoteApi.moveForward(duration);
    }

    @Override
    public void moveBackward() {
        remoteApi.moveBackward(defaultMoveDuration);
    }

    @Override
    public void moveBackward(int duration) {
        remoteApi.moveBackward(duration);
    }

    @Override
    public void stopServos() {
        remoteApi.stopServos();
    }

    @Override
    public void turnLeft() {
        remoteApi.turnLeft(defaultTurnDuration);
    }

    @Override
    public void turnRight() {
        remoteApi.turnRight(defaultTurnDuration);
    }

    @Override
    public void turnLeft(int duration) {
        remoteApi.turnLeft(duration);
    }

    @Override
    public void turnRight(int duration) {
        remoteApi.turnRight(duration);
    }

    @Override
    public void turnOnLED() {
        remoteApi.setStatusLED(1.0);
    }

    @Override
    public void turnOffLED() {
        remoteApi.setStatusLED(0);
        remoteApi.releaseStatusLED();
    }

    @Override
    public void blinkLEDFast() {
        remoteApi.blinkStatusLED(250, 250);
    }

    @Override
    public void blinkLEDSlow() {
        remoteApi.blinkStatusLED(500, 500);
    }

    @Override
    public boolean hasPixyCam() {
        return remoteApi.hasPixyCam();
    }

    @Override
    public Frame getPixyFrame() {

        return Frame.fromByteArray(remoteApi.getPixyFrameInfo());
    }

    @Override
    public void startPixyCam() {
        remoteApi.startPixyCam();
    }

    @Override
    public void stopPixyCam() {
        remoteApi.stopPixyCam();
    }

    @Override
    public boolean hasPixyCamDetected(int id) {
        return getPixyFrame().hasDetected(id);
    }

    @Override
    public void move(Direction d) {
        switch (d) {
            case FORWARD:
                moveForward(defaultMoveDuration);
                break;
            case BACKWARD:
                moveBackward(defaultMoveDuration);
                break;
            case LEFT:
                turnLeft(defaultTurnDuration);
                break;
            case RIGHT:
                turnRight(defaultTurnDuration);
                break;
            default:
            //
        }
    }

    @Override
    public void move(Direction d, int duration) {
        switch (d) {
            case FORWARD:
                moveForward(duration);
                break;
            case BACKWARD:
                moveBackward(duration);
                break;
            case LEFT:
                turnLeft(duration);
                break;
            case RIGHT:
                turnRight(duration);
                break;
            default:
            //
        }
    }

}
