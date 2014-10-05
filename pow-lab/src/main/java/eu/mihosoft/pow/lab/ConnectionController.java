/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.lab;

import javafx.beans.property.BooleanProperty;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public interface ConnectionController {

    public BooleanProperty onlineProperty();

    public void setOnline(boolean online);

    public boolean isOnline();
}
