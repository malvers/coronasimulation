import java.awt.*;

class ColorSeperator {

    private Color leftColor = Color.WHITE;
    private Color rightColor = Color.WHITE;
    private double value = 0.5;

    public ColorSeperator(double v) {
        value = v;
    }

    public ColorSeperator(double v, Color cl, Color cr) {
        value = v;
        leftColor = cl;
        rightColor = cr;
    }

    public Color getLeftColor() {
        return leftColor;
    }

    public void setLeftColor(Color leftColor) {
        this.leftColor = leftColor;
    }

    public Color getRightColor() {
        return rightColor;
    }

    public void setRightColor(Color rightColor) {
        this.rightColor = rightColor;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
