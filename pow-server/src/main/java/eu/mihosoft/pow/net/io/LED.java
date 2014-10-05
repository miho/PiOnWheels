/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.io;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class LED extends PWM {

    private BlinkThread blinkThread;

    public LED() {
        super();
        calibrateMinMax(0.0, 1.0);
    }

    public LED(int gpio) {
        super(gpio);
        calibrateMinMax(0.0, 1.0);
    }

    public LED setBrightness(double value) {
        terminateBlinkThread();
        set(value);
        return this;
    }

    public LED setBrightness(int duration, double value) {
        terminateBlinkThread();
        set(value);
        sleep(duration);
        return this;
    }

    public LED _setBrightness(int duration, double value) {
        set(value);
        sleep(duration);
        return this;
    }

    @Override
    public void release() {
        terminateBlinkThread();
        super.release();
    }

    public LED blink(int onFreq, int offFreq) {

        terminateBlinkThread();

        blinkThread = new BlinkThread(onFreq, offFreq);

        blinkThread.start();

        return this;
    }

    private void sleep(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            Logger.getLogger(PWM.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    private void terminateBlinkThread() {
        if (blinkThread != null) {
            blinkThread.stopBlinking();

            try {
                blinkThread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(LED.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    class BlinkThread extends Thread {

        private boolean stopBlinking;
        private int onFreq;
        private int offFreq;

        public BlinkThread(int onFreq, int offFreq) {
            this.onFreq = onFreq;
            this.offFreq = offFreq;
        }

        @Override
        public void run() {
            while (!stopBlinking) {
                _setBrightness(onFreq, 1.0);
                _setBrightness(offFreq, 0.0);
            }
        }

        /**
         * @param stopBlinking the stopBlinking to set
         */
        public void stopBlinking() {
            this.stopBlinking = true;
        }
    }

}
