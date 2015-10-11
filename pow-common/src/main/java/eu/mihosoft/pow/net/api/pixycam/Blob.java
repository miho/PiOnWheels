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
public interface Blob {

    /**
     * @return the id
     */
    public int getId();

    /**
     * @return the x
     */
    public int getX();

    /**
     * @return the y
     */
    public int getY();

    /**
     * @return the width
     */
    public int getWidth();

    /**
     * @return the height
     */
    public int getHeight();

    public static Blob newInstance(int id, int x, int y, int w, int h) {
        return new BlobImpl(id, x, y, w, h);
    }

    public static int[] toIntArray(Blob b) {
        return new int[]{
            b.getId(),
            b.getX(), b.getY(),
            b.getWidth(), b.getHeight()};
    }

    public static int[] toIntArray(Blob b, int[] data, int offset) {
        data[offset + 0] = b.getId();
        data[offset + 1] = b.getX();
        data[offset + 2] = b.getY();
        data[offset + 3] = b.getWidth();
        data[offset + 4] = b.getHeight();
        return data;
    }
    
    public static int getBlobSize() {
        return 5;
    }

    public static Blob fromIntArray(int[] data, int offset) {
        return new BlobImpl(
                data[offset + 0],
                data[offset + 1], data[offset + 2],
                data[offset + 3], data[offset + 4]);
    }

}
