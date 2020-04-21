import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Grapher extends JPanel {

    private double maxGeneration = 0;
    private ArrayList<ArrayList> allDistributions = new ArrayList<>();
    private ArrayList<Distribution> average = null;
    private int minGeneration = 0;
    private int minInfected = 0;
    private int maxInfected;
    private double infectionProbability;
    private double numberCoronaWorlds;
    private String timeString
            ;

    public void add(ArrayList<Distribution> d) {

        allDistributions.add(d);
        if (d.size() > maxGeneration) {
            maxGeneration = d.size();
        }
    }

    void calcAverageInfectionCurve() {

        if (average == null) {
            average = new ArrayList<>();
        }

        for (int i = 0; i < maxGeneration; i++) {
            average.add(new Distribution());
        }

        for (ArrayList curve : allDistributions) {

            for (int i = 0; i < curve.size(); i++) {

                Distribution actDis = (Distribution) curve.get(i);
                average.get(i).infected += actDis.infected;
            }
        }
        for (int i = 0; i < average.size(); i++) {
            average.get(i).infected /= (double) allDistributions.size();
        }
        allDistributions.add(average);
    }

    void createHistogramImage(String name) {

        MImage niceImage = new MImage((int) maxGeneration, (int) maxGeneration);
        niceImage.initGray(255);

        int width = niceImage.getWidth();
        int height = niceImage.getHeight();

        int count = 0;

        for (int j = 0; j < allDistributions.size() - 1; j++) {

            ArrayList curve = allDistributions.get(j);
            count++;

            for (int i = 0; i < curve.size(); i++) {

                Distribution e = (Distribution) curve.get(i);

                int x = (int) ((width * i) / maxGeneration);
                int y = (int) (e.infected * (double) height);

//                niceImage.set((int) x, (int) height - y, 255);
                niceImage.inc((int) x, (int) height - y, -1);
            }
        }
        niceImage.scaleToFullRange();
        niceImage.write(name);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        int width = getWidth();
        int height = getHeight();

        myPaint((Graphics2D) g, width, height);
    }

    private void myPaint(Graphics2D g2d, int width, int height) {

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.WHITE);
        g2d.fill(new Rectangle2D.Double(0, 0, width, height));

        g2d.setColor(Color.GRAY);

        String text;
        int ypos = 20;
        int xpos = width - 400;
        int inc = 20;

        ypos += inc;
        text = "parallel coronaverses: ";
        g2d.drawString(text + (int) numberCoronaWorlds, xpos, ypos);

        ypos += 2 * inc;
        text = "infection propability: ";
        g2d.drawString(text + (infectionProbability * 100) + " %", xpos, ypos);

        ypos += 2 * inc;
        text = "infected individuals - max: " + maxInfected + " min: " + minInfected + " [of 500]";
        g2d.drawString(text, xpos, ypos);

        ypos += 2 * inc;
        text = "generations until through - max: " + (int) maxGeneration + " min: " + minGeneration;
        g2d.drawString(text, xpos, ypos);

        ypos += 2 * inc;
        text = "runtime: " + timeString;
        g2d.drawString(text, xpos, ypos);

        for (int j = 0; j < allDistributions.size(); j++) {

            ArrayList curve = allDistributions.get(j);

            if (j == allDistributions.size() - 1) {
                g2d.setColor(Color.RED);
            }

            for (int i = 0; i < curve.size(); i++) {

                Distribution e = (Distribution) curve.get(i);

//                double x = (width * i) / maxGeneration;
                double x = (width * i) / 3500;
                double y = (e.infected * (double) height);

                g2d.fill(new Rectangle2D.Double(x, height - y, 1, 1));
            }
        }
    }

    public void setGlobalStatistics(int maxg, int ming, int maxi, int mini, double ip, double num, String ts) {

        numberCoronaWorlds = num;
        infectionProbability = ip;
        maxGeneration = maxg;
        minGeneration = ming;
        minInfected = mini;
        maxInfected = maxi;
        timeString = ts;
    }

    public void saveImage(String name) {

        int h = 1000;
        int w = (int) (h * Math.sqrt(2.0));
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        myPaint((Graphics2D) g, w, h);
        try {
            ImageIO.write(image, "png", new File(name));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
