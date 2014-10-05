/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.pow.net.api;

import javafx.geometry.Point2D;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface POWLocationAPI {
    public Point2D getLocation(POWRemoteAPI remoteApi);
}
