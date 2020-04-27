import mratools.MTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Colorizer extends JPanel implements ColorGradientListener {

    private final ImagePanel imagePanel;
    private final BufferedImage image;
    private final ColorGradient colorGradient;

    public Colorizer() {

        setLayout(new BorderLayout());
        colorGradient = new ColorGradient();
        colorGradient.addListener(this);
        add(BorderLayout.NORTH, colorGradient);

//        imagePanel = new ImagePanel("gauss_histo_4000_0.03_corona_simu_histogram.png");
        imagePanel = new ImagePanel("ma.jpg");

        add(BorderLayout.CENTER, imagePanel);

        image = imagePanel.getImage();

        colorGradient.setImage(image);

        imagePanel.setImage(niceColorImage(image));
    }

    private MImage niceColorImage(BufferedImage img) {

        int max = 0;
        int min = Integer.MAX_VALUE;
        for (int xHisto = 0; xHisto < img.getWidth(); xHisto++) {
            for (int yHisto = 0; yHisto < img.getHeight(); yHisto++) {
                int colorInt = img.getRGB(xHisto, yHisto);
                Color original = new Color(colorInt);
                int r = original.getRed();
                if (r > max) {
                    max = r;
                }
                if (r < min) {
                    min = r;
                }
            }
        }
        double m = 1.0 / (max - min);
        double n1 = 0.0 - m * min;
        double n2 = 1.0 - m * max;
//        System.out.println("min: " + min + " max: " + max);
//        System.out.println("m: " + m + " n1: " + n1 + " n2: " + n2);

//        colorGradient.printSeperators();
        MImage coloredImage = new MImage(img.getWidth(), img.getHeight());
        for (int xHisto = 0; xHisto < img.getWidth(); xHisto++) {
            for (int yHisto = 0; yHisto < img.getHeight(); yHisto++) {
                int colorInt = img.getRGB(xHisto, yHisto);
                Color original = new Color(colorInt);
                int r = original.getRed();
                double value = m * r + n1;
                if (value > 1.0) {
                    value = 1.0;
                }
                if (value < 0.0) {
                    value = 0.0;
                }
                Color newColor = colorGradient.getNiceColor(value);
                coloredImage.setPixel(xHisto, yHisto, newColor);
            }
        }
        return coloredImage;
    }

    @Override
    public void mouseEvent(MouseEvent e) {

//        if( e.isAltDown() ) {
//            System.out.println( "mouseEvent ..." );
//            MImage img = niceColorImage(image);
        imagePanel.setImage(niceColorImage(image));
        repaint();
//        }
    }

    /// main for testing
    public static void main(String[] args) {

        Colorizer cr = new Colorizer();
        JFrame f = new JFrame();
        f.add(cr);
        f.setBounds(100, 20, 1240, 460);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

}
