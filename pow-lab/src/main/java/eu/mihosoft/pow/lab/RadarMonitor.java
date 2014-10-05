/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.pow.lab;

import eu.hansolo.fx.Poi;
import eu.hansolo.fx.Radar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class RadarMonitor extends Region {

    private final static String IMG = "/eu/mihosoft/pow/lab/monitor-square.png";

    private final ImageView monitorView = new ImageView(IMG);
    private final Poi botPos = new Poi("PoW #1");
    private final Radar radarControl = new Radar(botPos);

    public RadarMonitor() {
        monitorView.setPreserveRatio(true);
        getChildren().add(radarControl);
        getChildren().add(monitorView);
                                                                                                                                                                                                                                                                                                                                                                                                                                                     
    }

    @Override
    protected void layoutChildren() {

        double imgWidth = getWidth();
        double imgHeight = getHeight();

        monitorView.setFitWidth(imgWidth);
        monitorView.setFitHeight(imgHeight);

        imgWidth = monitorView.getBoundsInLocal().getWidth();
        imgHeight = monitorView.getBoundsInLocal().getHeight();

        monitorView.relocate(getWidth() * 0.5 - imgWidth * 0.5,
                getHeight() * 0.5 - imgHeight * 0.5);
        
        radarControl.relocate(getWidth() * 0.5 - imgWidth * 0.5
                +imgWidth*0.1,
                getHeight() * 0.5 - imgHeight * 0.5
                +imgHeight*0.1
        );
        
        radarControl.resize(imgWidth-imgWidth*0.2, imgHeight-imgHeight*0.2);

    }
    
    public Poi getBotPos() {
        return botPos;
    }

}
