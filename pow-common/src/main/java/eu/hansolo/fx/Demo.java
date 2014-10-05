package eu.hansolo.fx;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.Scene;


/**
 * User: hansolo
 * Date: 09.09.14
 * Time: 17:37
 */
public class Demo extends Application {
    private Poi            poi;
    private Radar          radar;
    private long           lastTimerCall;
    private AnimationTimer timer;


    @Override public void init() {
        poi   = new Poi("Robot", -1, -1);
        radar = new Radar(poi);

        lastTimerCall = System.nanoTime();
        timer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (now > lastTimerCall + 50_000_000l) {
                    poi.setX(poi.getX() + 0.005);
                    poi.setY(poi.getY() + 0.005);
                    if (Double.compare(poi.getX(), 1d) == 0) {
                        poi.setX(-1);
                    }
                    if (Double.compare(poi.getY(), 1d) == 0) {
                        poi.setY(-1);
                    }
                    lastTimerCall = now;
                }
            }
        };
    }

    @Override public void start(Stage stage) {
        StackPane pane = new StackPane();
        pane.getChildren().addAll(radar);

        Scene scene = new Scene(pane);

        stage.setScene(scene);
        stage.show();

        // Set the radar color to another value
        radar.setRadarColor(Color.LIGHTGREEN);

        timer.start();
    }

    @Override public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
