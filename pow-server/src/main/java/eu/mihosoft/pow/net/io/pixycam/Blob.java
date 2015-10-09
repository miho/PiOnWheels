/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.io.pixycam;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class Blob {
    private int id;
    private int x;
    private int y;
    private int width;
    private int height;

    public Blob(int id, int x, int y, int width, int height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }
    
    
}
