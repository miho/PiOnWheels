/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.io;

/**
 * 
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Servo extends PWM{

    public Servo() {
        super();
        calibrateMinMax(0.1, 0.2);
    }

    public Servo(int gpio) {
        super(gpio);
        calibrateMinMax(0.1, 0.2);
    }

    public Servo turn(double value) {

        set(value);
        
        return this;
    }

    public Servo stop() {
        release();
        return this;
    }
}
