import mratools.MTools;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class MImage extends BufferedImage {

    /// constructor
    public MImage(int width, int height) {

        super(width, height, BufferedImage.TYPE_INT_ARGB);

        init(128, 128, 128);

        addNoise(40); /// :-)

//        testInc();
//        scaleToFullRange();
    }

    private void addNoise(int v) {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                addToPixel(x, y, (int)(Math.random() * v - (v/2.0)));
            }
        }
    }

    private void testInc() {
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                addToPixel(x, y, (int) (Math.random() * 10));
            }
        }
    }

    public boolean write(String name) {

        File file = new File(name);
        boolean write = false;
        try {
            write = ImageIO.write(this, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
            return write;
        }
        return write;
    }

    void initGray(int val) {
        init(val, val, val);
    }

    void init(int r, int g, int b) {

        ColorModel model = getColorModel();
        WritableRaster raster = getRaster();

        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {

                Color cNeu = new Color(r, g, b);

                int argbNeu = cNeu.getRGB();

                Object dataNeu = model.getDataElements(argbNeu, null);

                raster.setDataElements(x, y, dataNeu);
            }
        }
    }

    void scaleToFullRange() {

        ColorModel model = getColorModel();
        WritableRaster raster = getRaster();

        double average = 0.0;
        int count = 0;
        int maxGray = 0;
        int minGray = Integer.MAX_VALUE;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                Object dataAlt = raster.getDataElements(x, y, null);

                /// o.B.d.A. "g"
                int g = model.getGreen(dataAlt);
                average += g;
                count++;
                if (g >= maxGray) {
                    maxGray = g;
                }
                if (g <= minGray) {
                    minGray = g;
                }
            }
        }
        average /= count;
        double m = 255.0 / (maxGray - minGray);
        double n1 = 255.0 - m * maxGray;
        double n2 = 0.0 - m * minGray;

        MTools.println("[" + getClass() + "]->min b: " + minGray);
        MTools.println("[" + getClass() + "]->av  b: " + average);
        MTools.println("[" + getClass() + "]->max b: " + maxGray);
//        MTools.println("[" + getClass() + "]->m:     " + m);
//        MTools.println("[" + getClass() + "]->n1:    " + n1);
//        MTools.println("[" + getClass() + "]->n2:    " + n2);

        average = 0.0;
        count = 0;
        maxGray = 0;
        minGray = Integer.MAX_VALUE;
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {

                Object dataAlt = raster.getDataElements(x, y, null);
                int r = (int) (m * model.getRed(dataAlt) + n1);
                int g = (int) (m * model.getGreen(dataAlt) + n1);
                int b = (int) (m * model.getBlue(dataAlt) + n1);

                average += g;
                count++;

                if (g > maxGray) {
                    maxGray = g;
                }
                if (g < minGray) {
                    minGray = g;
                }
                if (r > 255) {
                    r = 255;
                }
                if (r < 0) {
                    r = 0;
                }
                if (g > 255) {
                    g = 255;
                }
                if (g < 0) {
                    g = 0;
                }
                if (b > 255) {
                    b = 255;
                }
                if (b < 0) {
                    b = 0;
                }
                Color cNeu = new Color(r, g, b);

                int argbNeu = cNeu.getRGB();

                Object dataNeu = model.getDataElements(argbNeu, null);

                raster.setDataElements(x, y, dataNeu);
            }
        }
        average /= count;
        MTools.println("[" + getClass() + "]->min a: " + minGray);
        MTools.println("[" + getClass() + "]->av  a: " + average);
        MTools.println("[" + getClass() + "]->max a: " + maxGray);
    }

    void addToPixel(int x, int y, int inc) {

        ColorModel model = getColorModel();
        WritableRaster raster = getRaster();
        Object dataAlt = raster.getDataElements(x, y, null);
        int argbAlt = model.getRGB(dataAlt);

        Color cAlt = new Color(argbAlt, true);
        int r = cAlt.getRed() + inc;
        if (r > 255) {
            r = 255;
        }
        if (r < 0) {
            r = 0;
        }
        int g = cAlt.getGreen() + inc;
        if (g > 255) {
            g = 255;
        }
        if (g < 0) {
            g = 0;
        }
        int b = cAlt.getBlue() + inc;
        if (b > 255) {
            b = 255;
        }
        if (b < 0) {
            b = 0;
        }
        Color cNeu = new Color(r, g, b);
        int argbNeu = cNeu.getRGB();

        Object dataNeu = model.getDataElements(argbNeu, null);

        raster.setDataElements(x, y, dataNeu);
    }

    void setPixel(int x, int y, int val) {

        ColorModel model = getColorModel();
        WritableRaster raster = getRaster();
        Color cNeu = new Color(val, val, val);
        int argbNeu = cNeu.getRGB();
        Object dataNeu = model.getDataElements(argbNeu, null);
        raster.setDataElements(x, y, dataNeu);
    }

    void setPixel(int x, int y, Color c) {

        ColorModel model = getColorModel();
        WritableRaster raster = getRaster();
        int argbNeu = c.getRGB();
        Object dataNeu = model.getDataElements(argbNeu, null);
        raster.setDataElements(x, y, dataNeu);
    }

    /// main for testing
    public static void main(String[] args) {

        MImage img = new MImage(200, 200);

        JFrame f = new JFrame();
        f.setBounds(0, 0, 200, 200);

        JPanel pane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(img, 0, 0, null);
            }
        };

        img.write("mytest.png");

        f.setContentPane(pane);
        f.setVisible(true);
    }

}
