import mratools.MTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

interface ColorGradientListener {

    void mouseEvent(MouseEvent e);
}

public class ColorGradient extends JPanel {

    ArrayList<ColorSeperator> seperators = new ArrayList<>();
    private BufferedImage image;
    private boolean drawSeperators = false;

    /// constructor
    public ColorGradient() {

        setFocusable(true);
        seperators.add(new ColorSeperator(0.0));

        ColorSeperator cs = new ColorSeperator(0.25, Color.RED, Color.BLUE);
        seperators.add(cs);

        cs = new ColorSeperator(0.4, Color.BLACK, Color.WHITE);
        seperators.add(cs);

        cs = new ColorSeperator(0.8, Color.YELLOW, Color.ORANGE);
        seperators.add(cs);

        seperators.add(new ColorSeperator(1.0));

        try {
            readGradient();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    drawSeperators = !drawSeperators;
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    try {
                        writeGradient();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                myMousePressed(e);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                drawSeperators = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                drawSeperators = false;
                repaint();
            }
        });

        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                for (int i = 0; i < seperators.size(); i++) {
                    ColorSeperator sep = seperators.get(i);
                    sep.setHilite(false);
                    double val = sep.getValue() * getWidth() - e.getX();
                    if (i == 0 && val > 8) {
                        sep.setHilite(true);
                    } else if (Math.abs(val) < 8) {
                        sep.setHilite(true);
                    } else if (i == seperators.size() - 1 && val < 4) {
                        sep.setHilite(true);
                    }
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                informInterested(e);
                for (int i = 1; i < seperators.size() - 1; i++) {
                    ColorSeperator sep = seperators.get(i);
                    if (sep.isHilite()) {
                        sep.setValue((double) e.getX() / (double) getWidth());
                    }
                }
                repaint();
            }
        });
    }

    private void myMousePressed(MouseEvent e) {

        if (e.getClickCount() == 2 && !isSeperatorHighlited()) {

            double val = (double) e.getX() / (double) getWidth();

            for (int i = 0; i < seperators.size() - 1; i++) {
                ColorSeperator sep1 = seperators.get(i);
                ColorSeperator sep2 = seperators.get(i + 1);
                if (val >= sep1.getValue() && val <= sep2.getValue()) {
                    ColorSeperator cs = new ColorSeperator(val);
                    cs.setHilite(true);
                    seperators.add(i + 1, cs);
                    repaint();
                    break;
                }
            }

        }

        if (e.getClickCount() == 2 && isSeperatorHighlited()) {

            Color c = JColorChooser.showDialog(null, "Choose", Color.WHITE);

            for (ColorSeperator sep : seperators) {

                if (sep.isHilite()) {
                    sep.setLeftColor(c);
                    sep.setRightColor(c);
                }
            }
            informInterested(e);
            repaint();
            return;
        }
    }

    private boolean isSeperatorHighlited() {
        for (ColorSeperator sep : seperators) {
            if (sep.isHilite()) {
                return true;
            }
        }
        return false;
    }

    private List<ColorGradientListener> listeners = new ArrayList<>();

    public void addListener(ColorGradientListener toAdd) {
        listeners.add(toAdd);
    }

    public void informInterested(MouseEvent e) {
        for (ColorGradientListener hl : listeners)
            hl.mouseEvent(e);
    }

    void readGradient() throws IOException, ClassNotFoundException {

        FileInputStream f = new FileInputStream("colorGradient.bin");
        ObjectInputStream in = new ObjectInputStream(f);
        int ns = in.readInt();

        MTools.println("[" + getClass() + "]->ns: " + ns);
        seperators.clear();
        for (int i = 0; i < ns; i++) {
            seperators.add((ColorSeperator) in.readObject());
        }

        in.close();
        f.close();
    }

    void writeGradient() throws IOException {

        FileOutputStream f = new FileOutputStream("colorGradient.bin");
        ObjectOutputStream out = new ObjectOutputStream(f);
        out.writeInt(seperators.size());
        for (int i = 0; i < seperators.size(); i++) {
            out.writeObject(seperators.get(i));
        }
        out.close();
        f.close();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(-1, 50);
    }

    @Override
    public void paint(Graphics gr) {

        super.paint(gr);
        Graphics2D g2d = (Graphics2D) gr;

        int range = 1000;

        double x = 0.0;
        double inc = 1.0 / (double) range;
        double paintWidth = getWidth() / (double) range;

        for (double value = 0; value < 1.0; value += inc) {
            g2d.setColor(getNiceColor(value));
            g2d.fill(new Rectangle2D.Double(x, 0, paintWidth, getHeight()));
            x += paintWidth;
        }

        if (drawSeperators) {
            for (int i = 0; i < seperators.size(); i++) {
                ColorSeperator sep = seperators.get(i);
                double xpos = sep.getValue() * getWidth();
                if (i == seperators.size() - 1) {
                    xpos -= 2;
                }
                g2d.setColor(Color.WHITE);
                g2d.fill(new Rectangle2D.Double(xpos, 0, 1, getHeight()));
                g2d.setColor(Color.BLACK);
                g2d.fill(new Rectangle2D.Double(xpos + 1, 0, 1, getHeight()));

                if (sep.isHilite()) {
//                    g2d.setColor(Color.GRAY);
//                    g2d.fill(new Rectangle2D.Double(xpos - 2, 0, 5, getHeight()));
                    g2d.setColor(Color.RED);
                    g2d.fill(new Rectangle2D.Double(xpos, 0, 1, getHeight()));
                }
            }
        }
    }

    private void getNiceColor(Graphics2D g2d, int range) {

        double h = getHeight();
        double x = 0.0;

        double paintWidth = getWidth() / (double) range;

        for (int i = 0; i < seperators.size() - 1; i++) {

            ColorSeperator lsFrom = seperators.get(i);
            ColorSeperator lsTo = seperators.get(i + 1);

            double to = (float) (range * (lsTo.getValue() - lsFrom.getValue()));
            MTools.println("\n" + " vf: " + lsFrom.getValue() + " vt: " + lsTo.getValue() + " to: " + to + "\n");

            for (int j = 0; j < to; j++) {
                float percent = (float) (j * (1.0f / to));
                MTools.println(x + " percent: " + percent);
                g2d.setColor(blendColors(lsFrom.getLeftColor(), lsTo.getRightColor(), 1 - percent));
                g2d.fill(new Rectangle2D.Double(x, 0, paintWidth, h));
                x += paintWidth;
            }
        }
    }

    Color getNiceColor(double value) {

        for (int i = 0; i < seperators.size() - 1; i++) {

            ColorSeperator lsFrom = seperators.get(i);

            ColorSeperator lsTo = seperators.get(i + 1);
            double dx = value - lsFrom.getValue();
            if (dx >= 0.0 && value <= lsTo.getValue()) {
                double xrange = lsTo.getValue() - lsFrom.getValue();
                double percent = dx / xrange;
                return blendColors(lsFrom.getLeftColor(), lsTo.getRightColor(), 1 - percent);
            }
        }
        return Color.WHITE;
    }

    private Color blendColors(Color color1, Color color2, double percent) {

        double inverse_percent = 1.0 - percent;
        int redPart = (int) (color1.getRed() * percent + color2.getRed() * inverse_percent);
        int greenPart = (int) (color1.getGreen() * percent + color2.getGreen() * inverse_percent);
        int bluePart = (int) (color1.getBlue() * percent + color2.getBlue() * inverse_percent);
        return new Color(redPart, greenPart, bluePart);
    }

    static Color myGetColor(float value, float range) {

        return new Color(Color.HSBtoRGB(value * (1.0f / range), 1.0f, 1.0f));
    }

    public void setImage(BufferedImage img) {
        image = img;
    }

    /// main for testing
    public static void main(String[] args) {

        ColorGradient cc = new ColorGradient();
        JFrame f = new JFrame();
        f.add(cc);
        f.setBounds(0, 10, 1440, 200);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MTools.println("left test:  " + cc.getNiceColor(0.0));
        MTools.println("left test:  " + cc.getNiceColor(0.4));
        MTools.println("left test:  " + cc.getNiceColor(0.8));
        MTools.println("right test: " + cc.getNiceColor(1.0));
    }

    public void printSeperators() {

        MTools.println("printSeperators ...");
        for (ColorSeperator sep : seperators) {
            MTools.println("val: " + sep.getValue());
        }
    }
}
