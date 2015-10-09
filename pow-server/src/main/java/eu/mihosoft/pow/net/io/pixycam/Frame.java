/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.net.io.pixycam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public final class Frame {
    private long number;
    private final List<Blob> blobs = new ArrayList<>();

    public Frame(long number) {
        this.number = number;
    }

    /**
     * @return the number
     */
    public long getNumber() {
        return number;
    }

    /**
     * @return the blobs
     */
    public List<Blob> getBlobs() {
        return Collections.unmodifiableList(blobs);
    }
    
    List<Blob> getBlobsModifiable() {
        return blobs;
    }
    
    
}
