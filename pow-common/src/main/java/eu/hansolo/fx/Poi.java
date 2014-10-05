package eu.hansolo.fx;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Created by hansolo on 10.09.14.
 */
public class Poi {
    private StringProperty name;
    private DoubleProperty x;
    private DoubleProperty y;


    // ******************** Constructors **************************************
    public Poi(final String NAME) {
        this(NAME, 0, 0);
    }
    public Poi(final String NAME, final double X, final double Y) {
        name = new SimpleStringProperty(this, "name", NAME);

        x = new DoublePropertyBase(clamp(-1d, 1d, X)) {
            @Override public void set(final double X) { super.set(clamp(-1d, 1d, X)); }
            @Override public Object getBean() { return Poi.this; }
            @Override public String getName() { return "x"; }
        };

        y = new DoublePropertyBase(clamp(-1d, 1d, Y)) {
            @Override public void set(final double Y) { super.set(clamp(-1d, 1d, Y)); }
            @Override public Object getBean() { return Poi.this; }
            @Override public String getName() { return "y"; }
        };
    }


    // ******************** Methods *******************************************
    public final String getName() { return name.get(); }
    public final void setName(final String NAME) { name.set(NAME); }
    public final StringProperty nameProperty() { return name; }

    public final double getX() { return x.get(); }
    public final void setX(final double X) { x.set(X); }
    public final DoubleProperty xProperty() { return x;}

    public final double getY() { return y.get(); }
    public final void setY(final double Y) { y.set(Y); }
    public final DoubleProperty yProperty() { return y;}

    private static double clamp(final double MIN, final double MAX, final double VALUE) {
        if (VALUE < MIN) return MIN;
        if (VALUE > MAX) return MAX;
        return VALUE;
    }
}
