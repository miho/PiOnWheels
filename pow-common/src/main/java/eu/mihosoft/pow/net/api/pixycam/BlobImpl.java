/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.api.pixycam;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
final class BlobImpl implements Blob {

    private final int id;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public BlobImpl(int id, int x, int y, int width, int height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * @return the id
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @return the x
     */
    @Override
    public int getX() {
        return x;
    }

    /**
     * @return the y
     */
    @Override
    public int getY() {
        return y;
    }

    /**
     * @return the width
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * @return the height
     */
    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "[id: " + id
                + ", x: " + x + ", y: " + y
                + ", w: " + width + ", h: " + height + "]";
    }

}
