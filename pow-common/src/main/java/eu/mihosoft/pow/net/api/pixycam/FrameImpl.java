/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.api.pixycam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
@Deprecated
public class FrameImpl implements Frame {

    private final int number;
    private final List<Blob> blobs = new ArrayList<>();

    public FrameImpl(int number) {
        this.number = number;
    }

    public FrameImpl(int number, Collection<Blob> blobs) {
        this.number = number;
        this.blobs.addAll(blobs);
    }

    FrameImpl(FrameImpl other) {
        this.number = other.number;
        this.blobs.addAll(other.blobs);
    }

    /**
     * @return the number
     */
    @Override
    public int getNumber() {
        return number;
    }

    /**
     * @return the blobs
     */
    @Override
    public List<Blob> getBlobs() {
        return Collections.unmodifiableList(blobs);
    }

    public void addBlob(Blob blob) {
        blobs.add(blob);
    }

    @Override
    public boolean hasDetected(int id) {
        return getBlobs().stream().filter((b -> b.getId() == id)).count() > 0;
    }

    @Override
    public String toString() {
        String result = "[ number: " + number
                + "\n> #blobs: " + blobs.size() + "\n";
        for (Blob b : getBlobs()) {
            result += "  -> " + b.toString() + "\n";
        }
        result += "\n]";
        return result;
    }

}
