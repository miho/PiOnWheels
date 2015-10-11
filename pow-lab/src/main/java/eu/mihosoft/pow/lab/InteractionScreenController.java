/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.pow.lab;

import eu.hansolo.enzo.led.Led;
import eu.mihosoft.vrl.fxscad.JFXScad;
import eu.mihosoft.vrl.fxscad.MainController;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import org.apache.commons.io.IOUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleSpansBuilder;
import xbee.SensorListener;

/**
 * FXML Controller class
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class InteractionScreenController implements Initializable {

    @FXML
    private StackPane statusContainer;
    @FXML
    private StackPane interactionContainer;

    private final RadarMonitor monitor = new RadarMonitor();

    private static InteractionScreenController controller;

    private static final String[] KEYWORDS = new String[]{
        "def", "in", "as", "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    };

    private static final Pattern KEYWORD_PATTERN
            = Pattern.compile("\\b(" + String.join("|", KEYWORDS) + ")\\b");

    private final CodeArea codeArea = new CodeArea();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Led led1 = new Led();
        led1.setLedColor(Color.RED);
        led1.setLedType(Led.LedType.ROUND);
        led1.setPrefSize(100, 100);
        Led led2 = new Led();
        led2.setLedColor(Color.YELLOW);
        led2.setLedType(Led.LedType.ROUND);
        led2.setPrefSize(100, 100);
        Led led3 = new Led();
        led3.setLedColor(Color.GREEN);
        led3.setPrefSize(100, 100);
        
        VBox rightBox = new VBox(led1,led2,led3);

        BorderPane monitorPane = new BorderPane(monitor);
//        monitorPane.setLeft(new StackPane(led1));
//        monitorPane.setTop(new StackPane(led2));
        monitorPane.setRight(new StackPane(rightBox));
        Pane p = new Pane();
        p.setMinWidth(100);
        monitorPane.setLeft(p);
        
        new Thread(()->{
        xbee.Main.runXbee((int sensorId, double value) -> {
            System.out.println("id: " + sensorId + ", " + value);
            Platform.runLater(()->{
                
                led1.setOn(sensorId == 0 && value > 280);
                led2.setOn(sensorId == 1 && value < 280);
                led3.setOn(sensorId == 2 && value < 280);});
        });
        }).start();

        statusContainer.getChildren().add(monitorPane);

        initEditor();

        Main.setPoi(monitor.getBotPos());
    }

    public static InteractionScreenController getController() {

        if (controller == null) {
            throw new IllegalStateException("call loadFromFXML first");
        }

        return controller;
    }

    public static Parent loadFromFXML() {

        if (controller != null) {
            throw new IllegalStateException("FXML already loaded");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(
                InteractionScreenController.class.getResource("InteractionScreen.fxml"));
        try {
            fxmlLoader.load();
        } catch (IOException ex) {
            Logger.getLogger(JFXScad.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

        Parent root = fxmlLoader.getRoot();

        controller = fxmlLoader.getController();

        return root;
    }

    public RadarMonitor getRadarMonitor() {
        return monitor;
    }

    private void initEditor() {
        codeArea.textProperty().addListener(
                (ov, oldText, newText) -> {
                    Matcher matcher = KEYWORD_PATTERN.matcher(newText);
                    int lastKwEnd = 0;
                    StyleSpansBuilder<Collection<String>> spansBuilder
                    = new StyleSpansBuilder<>();
                    while (matcher.find()) {
                        spansBuilder.add(Collections.emptyList(),
                                matcher.start() - lastKwEnd);
                        spansBuilder.add(Collections.singleton("keyword"),
                                matcher.end() - matcher.start());
                        lastKwEnd = matcher.end();
                    }
                    spansBuilder.add(Collections.emptyList(),
                            newText.length() - lastKwEnd);
                    codeArea.setStyleSpans(0, spansBuilder.create());
                });

//        EventStream<Change<String>> textEvents
//                = EventStreams.changesOf(codeArea.textProperty());
//        textEvents.reduceSuccessions((a, b) -> b, Duration.ofMillis(500)).
//                subscribe(code -> compile(code.getNewValue()));
        codeArea.replaceText(
                "// use api object\n");

        interactionContainer.getChildren().add(codeArea);

//        logContainer.setContent(logView);
//        // redirect sout
//        RedirectableStream sout = new RedirectableStream(
//                RedirectableStream.ORIGINAL_SOUT, logView);
//        sout.setRedirectToUi(true);
//        System.setOut(sout);
//
//        // redirect err
//        RedirectableStream serr = new RedirectableStream(
//                RedirectableStream.ORIGINAL_SERR, logView);
//        serr.setRedirectToUi(true);
//        System.setErr(serr);
    }

    private void setCode(String code) {
        codeArea.replaceText(code);
    }

    private String getCode() {
        return codeArea.getText();
    }

    private void clearLog() {
//        logView.setText("");
    }

    private void compile(String code) {

        clearLog();

        try {

            CompilerConfiguration cc = new CompilerConfiguration();

            cc.addCompilationCustomizers(
                    new ImportCustomizer().
                    addStarImports("eu.mihosoft.pow.client",
                            "eu.mihosoft.pow.net.api",
                            "eu.mihosoft.pow.lab"));

            GroovyShell shell = new GroovyShell(getClass().getClassLoader(),
                    new Binding(), cc);

            shell.setProperty("advancedApi", Main.getPOWRemoteAPI());
            shell.setProperty("api", POWApi.newApi());

            Script script = shell.parse(code);

            Thread t = new Thread(() -> {
                Object obj = script.run();
            });
            t.start();

        } catch (Throwable ex) {
            ex.printStackTrace(System.err);
        }
    }

    @FXML
    private void onResetBotPosition(ActionEvent e) {
        Main.resetBotPosition();
    }

    @FXML
    private void onRun(ActionEvent e) {
        compile(getCode());
    }

    @FXML
    private void onLoadFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open PoWScript File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "PoWScript files (*.powscript, *.groovy)",
                        "*.powscript", "*.groovy"));

        File f = fileChooser.showOpenDialog(null);

        if (f == null) {
            return;
        }

        String fName = f.getAbsolutePath();

        if (!fName.toLowerCase().endsWith(".groovy")
                && !fName.toLowerCase().endsWith(".powscript")) {
            fName += ".powscript";
        }

        try {
            setCode(new String(Files.readAllBytes(Paths.get(fName)), "UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onSaveFile(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PoWScript File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "PoWScript files (*.powscript, *.groovy)",
                        "*.powscript", "*.groovy"));

        File f = fileChooser.showSaveDialog(null);

        if (f == null) {
            return;
        }

        String fName = f.getAbsolutePath();

        if (!fName.toLowerCase().endsWith(".groovy")
                && !fName.toLowerCase().endsWith(".powscript")) {
            fName += ".powscript";
        }

        try {
            Files.write(Paths.get(fName), getCode().getBytes("UTF-8"));
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onSample1(ActionEvent e) {

        try {
            String code = IOUtils.toString(this.getClass().
                    getResourceAsStream("Sample1.powscript"),
                    "UTF-8");
            setCode(code);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onSample2(ActionEvent e) {

        try {
            String code = IOUtils.toString(this.getClass().
                    getResourceAsStream("Sample2.powscript"),
                    "UTF-8");
            setCode(code);
        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).
                    log(Level.SEVERE, null, ex);
        }

    }

}
