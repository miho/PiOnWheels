package eu.hansolo.fx;

import com.sun.javafx.css.converters.PaintConverter;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Control;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * User: hansolo
 * Date: 09.09.14
 * Time: 17:29
 */
public class Radar extends Region {
    private static final double        PREFERRED_WIDTH     = 500;
    private static final double        PREFERRED_HEIGHT    = 500;
    private static final double        MINIMUM_WIDTH       = 50;
    private static final double        MINIMUM_HEIGHT      = 50;
    private static final double        MAXIMUM_WIDTH       = 1024;
    private static final double        MAXIMUM_HEIGHT      = 1024;
    private static final double        ROTATION_TIME_IN_MS = 4000;
    public static final  Color         DEFAULT_RADAR_COLOR = Color.web("#00f300");
    private Stop[]                     stops;
    private Stop[]                     poiStops;
    private double                     size;
    private double                     width;
    private double                     height;
    private double                     centerX;
    private double                     centerY;
    private double                     lineWidth;
    private Pane                       pane;
    private Canvas                     background;
    private GraphicsContext            backgroundCtx;
    private Circle                     ring1;
    private Circle                     ring2;
    private Circle                     ring3;
    private Circle                     ring4;
    private Circle                     ring5;
    private Line                       verLine;
    private Line                       horLine;
    private Canvas                     poiCanvas;
    private GraphicsContext            poiCtx;
    private ConicalGradient            beamGradient;
    private ImageView                  beam;
    private Timeline                   timeline;
    private BooleanProperty            beamVisible;
    private ObjectProperty<Paint>      radarColor;
    private ObservableMap<String, Poi> pois;
    private InvalidationListener       poiListener;


    // ******************** Constructors **************************************
    public Radar() {
        this(true);
    }
    public Radar(final Poi... POIS) {
        this(true, POIS);
    }
    public Radar(final boolean BEAM_VISIBLE, final Poi... POIS) {
        getStylesheets().add(Radar.class.getResource("radar.css").toExternalForm());
        getStyleClass().add("radar");

        beamVisible = new SimpleBooleanProperty(this, "beamVisible", BEAM_VISIBLE);
        radarColor  = new StyleableObjectProperty<Paint>(DEFAULT_RADAR_COLOR) {
            @Override public void set(final Paint RADAR_COLOR) {
                super.set(RADAR_COLOR);
                setStyle("-radar-color:" + RADAR_COLOR.toString().replace("0x", "#") + ";");
            }

            @Override public CssMetaData getCssMetaData() { return StyleableProperties.RADAR_COLOR; }

            @Override public Object getBean() { return Radar.this; }

            @Override public String getName() { return "radarColor"; }
        };
        poiListener = observable -> drawPois();
        pois        = FXCollections.observableHashMap();
        if (null != POIS) { Arrays.asList(POIS).forEach(poi -> {
            pois.put(poi.getName(), poi);
            poi.xProperty().addListener(poiListener);
            poi.yProperty().addListener(poiListener);
        }); }
        init();
        initGraphics();
        registerListeners();

        if (BEAM_VISIBLE) timeline.play();
    }


    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getPrefWidth(), 0.0) <= 0 || Double.compare(getPrefHeight(), 0.0) <= 0 ||
                Double.compare(getWidth(), 0.0) <= 0 || Double.compare(getHeight(), 0.0) <= 0) {
            if (getPrefWidth() > 0 && getPrefHeight() > 0) {
                setPrefSize(getPrefWidth(), getPrefHeight());
            } else {
                setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getMinWidth(), 0.0) <= 0 || Double.compare(getMinHeight(), 0.0) <= 0) {
            setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getMaxWidth(), 0.0) <= 0 || Double.compare(getMaxHeight(), 0.0) <= 0) {
            setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        stops = new Stop[] {
            new Stop(0.0, Color.rgb(0, 243, 0, 0.0)),
            new Stop(0.8, Color.rgb(0, 243, 0, 0.0)),
            new Stop(0.85, Color.rgb(0, 243, 0, 0.2)),
            new Stop(1.0, Color.rgb(0, 243, 0, 0.75))
        };

        poiStops = new Stop[] {
            new Stop(0.0, Color.WHITE),
            new Stop(0.4, Color.WHITE),
            new Stop(0.4, getRadarColorAsColor()),
            new Stop(1.0, Color.TRANSPARENT)
        };

        background    = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        backgroundCtx = background.getGraphicsContext2D();

        ring1 = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.04);
        ring1.getStyleClass().add("line");

        ring2 = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.12);
        ring2.getStyleClass().add("line");

        ring3 = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.23);
        ring3.getStyleClass().add("line");

        ring4 = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.34);
        ring4.getStyleClass().add("line");

        ring5 = new Circle(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.45);
        ring5.getStyleClass().add("line");

        verLine = new Line(PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.05, PREFERRED_WIDTH * 0.5, PREFERRED_HEIGHT * 0.95);
        verLine.getStyleClass().add("line");

        horLine = new Line(PREFERRED_WIDTH * 0.05, PREFERRED_HEIGHT * 0.5, PREFERRED_WIDTH * 0.95, PREFERRED_HEIGHT * 0.5);
        horLine.getStyleClass().add("line");

        poiCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        poiCtx    = poiCanvas.getGraphicsContext2D();

        beamGradient = new ConicalGradient(new Point2D(PREFERRED_WIDTH * 0.9 * 0.5, PREFERRED_HEIGHT * 0.9 * 0.5), true, 0, stops);

        beam = new ImageView(beamGradient.getImage(PREFERRED_WIDTH * 0.9, PREFERRED_HEIGHT * 0.9));
        beam.relocate(PREFERRED_WIDTH * 0.05, PREFERRED_HEIGHT * 0.05);
        beam.setMouseTransparent(true);
        beam.setVisible(isBeamVisible());

        KeyValue kvStart = new KeyValue(beam.rotateProperty(), 0, Interpolator.LINEAR);
        KeyValue kvStop  = new KeyValue(beam.rotateProperty(), 360, Interpolator.LINEAR);
        KeyFrame kfStart = new KeyFrame(Duration.ZERO, kvStart);
        KeyFrame kfStop  = new KeyFrame(Duration.millis(ROTATION_TIME_IN_MS), kvStop);
        timeline         = new Timeline();
        timeline.getKeyFrames().addAll(kfStart, kfStop);
        timeline.setCycleCount(-1);

        pane = new Pane();
        pane.getStyleClass().add("background");
        pane.getChildren().setAll(background, ring1, ring2, ring3, ring4, ring5, verLine, horLine, poiCanvas, beam);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        widthProperty().addListener(observable -> resize());
        heightProperty().addListener(observable -> resize());
        beamVisible.addListener(observable -> handleControlPropertyChanged("BEAM_VISIBLE"));
        radarColor.addListener(observable -> handleControlPropertyChanged("RADAR_COLOR"));
        pois.addListener((MapChangeListener<String, Poi>) change -> handleControlPropertyChanged("POIS"));
    }


    // ******************** Methods *******************************************
    private void handleControlPropertyChanged(final String PROPERTY) {
        if ("RADAR_COLOR".equals(PROPERTY)) {
            stops[0]    = new Stop(0.0, Color.color(getRadarColorAsColor().getRed(), getRadarColorAsColor().getGreen(), getRadarColorAsColor().getBlue(), 0.0));
            stops[1]    = new Stop(0.8, Color.color(getRadarColorAsColor().getRed(), getRadarColorAsColor().getGreen(), getRadarColorAsColor().getBlue(), 0.0));
            stops[2]    = new Stop(0.85, Color.color(getRadarColorAsColor().getRed(), getRadarColorAsColor().getGreen(), getRadarColorAsColor().getBlue(), 0.2));
            stops[3]    = new Stop(1.0, Color.color(getRadarColorAsColor().getRed(), getRadarColorAsColor().getGreen(), getRadarColorAsColor().getBlue(), 0.75));

            poiStops[2] = new Stop(0.4, getRadarColorAsColor());
            resize();
        } else if ("BEAM_VISIBLE".equals(PROPERTY)) {
            if (isBeamVisible()) {
                beam.setVisible(true);
                timeline.play();
            } else {
                beam.setVisible(false);
                timeline.stop();
            }
            resize();
        } else if ("POIS".equals(PROPERTY)) {
            pois.values().forEach(poi -> {
                poi.xProperty().removeListener(poiListener);
                poi.yProperty().removeListener(poiListener);
                poi.xProperty().addListener(poiListener);
                poi.yProperty().addListener(poiListener);
            });
        }
    }

    public final boolean isBeamVisible() { return beamVisible.get(); }
    public final void setBeamVisible(final boolean BEAM_VISIBLE) { beamVisible.set(BEAM_VISIBLE); }
    public final BooleanProperty beamVisibleProperty() { return beamVisible; }

    public final Paint getRadarColor() { return radarColor.get(); }
    public final Color getRadarColorAsColor() { return (Color) radarColor.get(); }
    public final void setRadarColor(final Paint RADAR_COLOR) { radarColor.set(RADAR_COLOR); }
    public final ObjectProperty<Paint> radarColorProperty() { return radarColor; }

    private void drawBackground(final GraphicsContext CTX) {
        CTX.clearRect(0, 0, size, size);
        Color rasterColor = Color.color(getRadarColorAsColor().getRed(), getRadarColorAsColor().getGreen(), getRadarColorAsColor().getBlue(), 0.4);

        CTX.setStroke(rasterColor);
        CTX.setLineWidth(lineWidth);
        double stepSize = size / 10.0;
        for (int i = 0 ; i < size ; i += stepSize) {
            CTX.strokeLine(i, 0, i, size);
            CTX.strokeLine(0, i, size, i);
        }

        // Draw the direction bearingCanvas
        double  sinValue;
        double  cosValue;
        double  offset = 180;
        Point2D center = new Point2D(size * 0.5, size * 0.5);

        Color scaleColor = Color.color(getRadarColorAsColor().getRed(), getRadarColorAsColor().getGreen(), getRadarColorAsColor().getBlue(), 0.7);
        CTX.setFill(scaleColor);
        CTX.setStroke(scaleColor);
        CTX.setLineCap(StrokeLineCap.ROUND);
        CTX.setTextAlign(TextAlignment.CENTER);
        CTX.setTextBaseline(VPos.CENTER);
        for (double angle = 0, counter = 0 ; Double.compare(counter, 360) < 0 ; angle -= 1, counter++) {
            sinValue = Math.sin(Math.toRadians(angle + offset));
            cosValue = Math.cos(Math.toRadians(angle + offset));

            Point2D innerMainPoint   = new Point2D(center.getX() + size * 0.425 * sinValue, center.getY() + size * 0.425 * cosValue);
            Point2D innerMinorPoint  = new Point2D(center.getX() + size * 0.44 * sinValue, center.getY() + size * 0.44 * cosValue);
            Point2D outerPoint       = new Point2D(center.getX() + size * 0.45 * sinValue, center.getY() + size * 0.45 * cosValue);
            Point2D textPoint        = new Point2D(center.getX() + size * 0.47 * sinValue, center.getY() + size * 0.47 * cosValue);

            if (counter % 10 == 0) {
                // Draw major tick mark
                CTX.setLineWidth(size * 0.003);
                CTX.strokeLine(innerMainPoint.getX(), innerMainPoint.getY(), outerPoint.getX(), outerPoint.getY());

                // Draw scale text
                CTX.save();
                    CTX.setFont(Font.font(0.025 * size));
                        CTX.save();
                            CTX.translate(textPoint.getX(), textPoint.getY());
                            CTX.rotate(-angle + offset + 180);
                            CTX.fillText(Integer.toString((int) counter), 0, 0);
                            CTX.translate(-textPoint.getX(), -textPoint.getY());
                        CTX.restore();
                CTX.restore();
            } else if (counter % 1 == 0) {
                // Draw minor tick mark
                CTX.setLineWidth(size * 0.002);
                CTX.strokeLine(innerMinorPoint.getX(), innerMinorPoint.getY(), outerPoint.getX(), outerPoint.getY());
            }
        }

    }

    private void drawPois() {
        double poiRadius = size * 0.02;
        Color poiColor = Color.color(getRadarColorAsColor().getRed(), getRadarColorAsColor().getGreen(), getRadarColorAsColor().getBlue(), 0.4);
        poiCtx.clearRect(0, 0, size, size);
        poiCtx.save();
        poiCtx.setEffect(new DropShadow(BlurType.TWO_PASS_BOX, poiColor, 3 * poiRadius, 0.5, 0, 0));
        for (Poi poi : pois.values()) {
            RadialGradient gradient = new RadialGradient(0, 0, centerX + poi.getX() * size * 0.5, centerY + poi.getY() * size * 0.5, poiRadius, false, CycleMethod.NO_CYCLE, poiStops);
            poiCtx.setFill(gradient);
            poiCtx.fillOval(centerX + poi.getX() * size * 0.5 - poiRadius, centerY + poi.getY() * size * 0.5 - poiRadius, 2 * poiRadius, 2 * poiRadius);
        }
        poiCtx.restore();
    }


    // ******************** CSS Meta Data *************************************
    private static class StyleableProperties {
        private static final CssMetaData<Radar, Paint> RADAR_COLOR =
                new CssMetaData<Radar, Paint>("-radar-color", PaintConverter.getInstance(), DEFAULT_RADAR_COLOR) {

                    @Override public boolean isSettable(Radar node) {
                        return null == node.radarColor || !node.radarColor.isBound();
                    }
                    @Override public StyleableProperty<Paint> getStyleableProperty(Radar node) {
                        return (StyleableProperty) node.radarColorProperty();
                    }
                    @Override public Paint getInitialValue(Radar node) {
                        return node.getRadarColor();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Control.getClassCssMetaData());
            Collections.addAll(styleables,
                               RADAR_COLOR);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }


    // ******************** Resizing ******************************************
    private void resize() {
        width = getWidth();
        height = getHeight();
        size = width < height ? width : height;

        if (width > 0 && height > 0) {
            pane.setPrefSize(width, height);
            pane.setMaxSize(width, height);
            lineWidth = size * 0.003;
            centerX = width * 0.5;
            centerY = height * 0.5;

            background.setWidth(size);
            background.setHeight(size);
            background.relocate((width - size) * 0.5, (height - size) * 0.5);

            drawBackground(backgroundCtx);

            ring1.setCenterX(centerX);
            ring1.setCenterY(centerY);
            ring1.setRadius(size * 0.04);
            ring1.setStrokeWidth(lineWidth);

            ring2.setCenterX(centerX);
            ring2.setCenterY(centerY);
            ring2.setRadius(size * 0.12);
            ring2.setStrokeWidth(lineWidth);

            ring3.setCenterX(centerX);
            ring3.setCenterY(centerY);
            ring3.setRadius(size * 0.23);
            ring3.setStrokeWidth(lineWidth);

            ring4.setCenterX(centerX);
            ring4.setCenterY(centerY);
            ring4.setRadius(size * 0.34);
            ring4.setStrokeWidth(lineWidth);

            ring5.setCenterX(centerX);
            ring5.setCenterY(centerY);
            ring5.setRadius(size * 0.45);
            ring5.setStrokeWidth(lineWidth);

            verLine.setStartX(centerX);
            verLine.setStartY(centerY - size * 0.5 + size * 0.05);
            verLine.setEndX(centerX);
            verLine.setEndY(centerY - size * 0.5 + size * 0.95);
            verLine.setStrokeWidth(lineWidth);

            horLine.setStartX(centerX - size * 0.5 + size * 0.05);
            horLine.setStartY(centerY);
            horLine.setEndX(centerX - size * 0.5 + size * 0.95);
            horLine.setEndY(centerY);
            horLine.setStrokeWidth(lineWidth);

            poiCanvas.setWidth(size);
            poiCanvas.setHeight(size);
            poiCanvas.relocate((width - size) * 0.5, (height - size) * 0.5);

            drawPois();

            if (isBeamVisible()) {
                beamGradient = new ConicalGradient(new Point2D(size * 0.9 * 0.5, size * 0.9 * 0.5), true, 0, stops);
                beam.setImage(beamGradient.getImage(size * 0.9, size * 0.9));
                beam.relocate(centerX - size * 0.5 + size * 0.05, centerY - size * 0.5 + size * 0.05);
            }
        }
    }
}
