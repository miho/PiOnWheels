/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.lab;

import eu.mihosoft.vrl.fxscad.JFXScad;
import eu.mihosoft.vrl.fxscad.OutputFilter;
import eu.mihosoft.vrl.fxscad.RedirectableStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class LabScreenController implements Initializable {

    private RadarMonitor radarMonitor;
    @FXML
    private StackPane connectionContainer;
    @FXML
    private StackPane interactionContainer;
    @FXML
    private StackPane designContainer;
    @FXML
    private Parent rootView;
    
    private ConnectionScreenController connectionController;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        Parent connectionView = ConnectionScreenController.loadFromFXML();
        connectionController = ConnectionScreenController.getController();

        connectionContainer.getChildren().add(connectionView);
        
        
        Parent interactionUI = 
                InteractionScreenController.loadFromFXML();
 
        
        interactionContainer.getChildren().add(interactionUI);
        
        radarMonitor = 
                InteractionScreenController.getController().getRadarMonitor();
        
        addDesignContent(JFXScad.loadFromFXML());
        
        // redirect sout
        RedirectableStream sout = new RedirectableStream(
                RedirectableStream.ORIGINAL_SOUT,
                connectionController.getLogView(),JFXScad.getLogView());
        sout.setRedirectToUi(true);
        System.setOut(sout);
        
        sout.setFilter(JFXScad.getLogView(), (String s) -> {
            return !s.startsWith("Searching devices")
                    && !s.startsWith(" -> scanning");
        });

        // redirect err
        RedirectableStream serr = new RedirectableStream(
                RedirectableStream.ORIGINAL_SERR,
                connectionController.getLogView(),JFXScad.getLogView());
        serr.setRedirectToUi(true);
        System.setErr(serr);
        
        serr.setFilter(JFXScad.getLogView(), (String s) -> {
            return !s.startsWith("Searching devices")
                    && !s.startsWith(" -> scanning")
                     && !s.contains("#api");
        });
    }

    public Parent getRootView() {
        return rootView;
    }

    public ConnectionController getConnectionController() {
        return connectionController.getConnectionView();
    }

    public void addInteractionContent(Node interactionContent) {
        interactionContainer.getChildren().add(interactionContent);
    }

    public void addDesignContent(Node designContent) {
        designContainer.getChildren().add(designContent);
    }

}
