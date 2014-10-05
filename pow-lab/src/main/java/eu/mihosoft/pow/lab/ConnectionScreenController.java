/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.mihosoft.pow.lab;


import eu.mihosoft.vrl.fxscad.RedirectableStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class ConnectionScreenController implements Initializable {
    @FXML
    private StackPane connectionViewContainer;
    
     @FXML
    private TextArea log;
    
    private final ConnectionView connectionView = new ConnectionView();
    private static ConnectionScreenController controller;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        connectionViewContainer.getChildren().add(connectionView);
    } 
    
    ConnectionController getConnectionView() {
        return connectionView;
    }
    
    public static Parent loadFromFXML() {
        
        if (controller!=null) {
            throw new IllegalStateException("UI already loaded!");
        }
        
        FXMLLoader fxmlLoader = new FXMLLoader(
                ConnectionScreenController.class.getResource("ConnectionScreen.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        Parent root = fxmlLoader.getRoot();
        
        controller = fxmlLoader.getController();

        return root;
    }
    
    public static ConnectionScreenController getController() {
        if (controller == null) {
            throw new IllegalStateException("Load the UI first!");
        }
        
        return controller;
    }
    
    public TextArea getLogView() {
        return log;
    }
    
}
