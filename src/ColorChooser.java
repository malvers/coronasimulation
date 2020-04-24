import mratools.MTools;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class ColorChooser extends JPanel {

    ArrayList<ColorSeperator> seperators = new ArrayList<>();

    public ColorChooser() {

        seperators.add(new ColorSeperator(0.0));
        Color cl = Color.RED;
        Color cr = Color.BLUE;
        ColorSeperator cs = new ColorSeperator(0.2, cl, cr);
        seperators.add(cs);
        cs = new ColorSeperator(0.7, Color.YELLOW, Color.ORANGE);
        seperators.add(cs);
        seperators.add(new ColorSeperator(1.0));
    }

    @Override
    public void paint(Graphics gr) {

        super.paint(gr);
        Graphics2D g2d = (Graphics2D) gr;

        int range = 1000;
        double h = getHeight();
        double x = 0.0;

        double paintWidth = getWidth() / (double) range;

        double to = 0;
        for (int i = 0; i < seperators.size() - 1; i++) {

            ColorSeperator lsFrom = seperators.get(i);
            ColorSeperator lsTo = seperators.get(i + 1);

            to = (range * (lsTo.getValue() - lsFrom.getValue()));
            MTools.println("\n" + " vf: " + lsFrom.getValue() + " vt: " + lsTo.getValue() + " to: " + to + "\n");

            for (int j = 0; j < to; j++) {
                float percent = j * (1.0f / (float)to);
                MTools.println(x + " percent: " + percent);
                g2d.setColor(blendColors(lsFrom.getLeftColor(), lsTo.getRightColor(), 1 - percent));
                g2d.fill(new Rectangle2D.Double(x, 0, paintWidth, h));
                x += paintWidth;
            }
        }
    }

    public Color blendColors(Color color1, Color color2, double percent) {

        double inverse_percent = 1.0 - percent;
        int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    static Color myGetColor(float value, float range) {

        return new Color(Color.HSBtoRGB(value * (1.0f / range), 1.0f, 1.0f));
    }

    public static void main(String[] args) {

        JFrame f = new JFrame();
        f.add(new ColorChooser());
        f.setBounds(0, 10, 1440, 200);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
