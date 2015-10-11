/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.api.pixycam;

import eu.mihosoft.pow.io.BinaryInputStream;
import eu.mihosoft.pow.io.BinaryOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface Frame {

    public static Frame newInstance(int parseInt) {
        return new FrameImpl(parseInt);
    }

    public static Frame copy(Frame frame) {
        return new FrameImpl((FrameImpl) frame);
    }

    public static Frame fromIntArray(int[] data) {
        int id = data[0];
        int numBlobs = data[1];

        List<Blob> blobs = new ArrayList<>(numBlobs);

        int blobsOffset = 2;

        for (int i = 0; i < numBlobs; i++) {
            blobs.add(Blob.fromIntArray(data,
                    blobsOffset + i * Blob.getBlobSize()));
        }

        return new FrameImpl(id, blobs);
    }

    public static int getFrameSize() {
        return 2;
    }

    public static int[] toIntArray(Frame f) {
        int numBlobs = f.getBlobs().size();
        int[] data = new int[getFrameSize() + numBlobs * Blob.getBlobSize()];
        data[0] = f.getNumber();
        data[1] = numBlobs;

        int blobsOffset = 2;

        for (int i = 0; i < numBlobs; i++) {
            Blob.toIntArray(f.getBlobs().get(i), data, blobsOffset + i * Blob.getBlobSize());
        }

        return data;
    }

    public static byte[] toByteArray(Frame f) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BinaryOutputStream bos
                = new BinaryOutputStream(baos);
        try {
            bos.writeIntArray(toIntArray(f));
            bos.flush();
            return baos.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static Frame fromByteArray(byte[] data) {
        BinaryInputStream bis = new BinaryInputStream(data);
        int numEntries = data.length / Integer.BYTES;
        try {
            int[] intData = bis.readIntArray(numEntries);
            return Frame.fromIntArray(intData);
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * @return the number
     */
    public int getNumber();

    /**
     * @return the blobs
     */
    public List<Blob> getBlobs();

    public boolean hasDetected(int id);

}
