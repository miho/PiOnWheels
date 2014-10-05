/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.lab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class ConnectionView extends Region implements ConnectionController {

    private BooleanProperty onlineProperty = new SimpleBooleanProperty();

    private final static String OFF_IMG = "/eu/mihosoft/pow/lab/bot-offline.png";
    private final static String ON_IMG = "/eu/mihosoft/pow/lab/bot-online.png";

    final ImageView stateView = new ImageView(OFF_IMG);

    public ConnectionView() {
        stateView.setPreserveRatio(true);
        getChildren().add(stateView);

        onlineProperty().addListener((ov,oldV,newV) -> {
            if (newV) {
                stateView.setImage(new Image(ON_IMG));
            } else {
                stateView.setImage(new Image(OFF_IMG));
            }
        });
        
        setStyle("-fx-background-color:"
                + "linear-gradient(to top, rgb(50,50,50), rgb(110,110,120));");
    }

    @Override
    protected void layoutChildren() {

        double imgWidth = getWidth() * 0.5;
        double imgHeight = getHeight() * 0.5;

        stateView.setFitWidth(imgWidth);
        stateView.setFitHeight(imgHeight);

        imgWidth = stateView.getBoundsInLocal().getWidth();
        imgHeight = stateView.getBoundsInLocal().getHeight();

        stateView.relocate(getWidth() * 0.5 - imgWidth * 0.5,
                getHeight() * 0.5 - imgHeight * 0.5);

    }

    @Override
    public final BooleanProperty onlineProperty() {
        return onlineProperty;
    }

    @Override
    public final void setOnline(boolean online) {
        onlineProperty().set(online);
    }

    @Override
    public final boolean isOnline() {
        return onlineProperty().get();
    }
}
