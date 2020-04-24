import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class CoronaWorld extends JPanel implements IRunner {


    private ArrayList<Individual> individuals = new ArrayList();

    public ArrayList<Distribution> getDistributions() {
        return distributions;
    }

    private ArrayList<Distribution> distributions = new ArrayList<>();

    private final CoronaPlayGround coronaPlayGround;
    private long running = 0;
    private Thread thread;
    private int generation = 0;
    private boolean active = false;
    private final double worldSize;
    private final double infectionPropability;
    private int maxInfected = 0;
    private Random generator;

    /// constructor
    public CoronaWorld(CoronaPlayGround cpg, double ws, double p) {

        generator = new Random(System.currentTimeMillis());
        worldSize = ws;
        infectionPropability = p;
        coronaPlayGround = cpg;
        setFocusable(true);
        setBackground(Color.BLACK);

        initIndividuals();

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    oneInfectionStep();
                } else if (e.getKeyCode() == KeyEvent.VK_A) {
                    coronaPlayGround.statistics("00:00:00");
                } else if (e.getKeyCode() == KeyEvent.VK_I) {
                    initIndividuals();
                } else if (e.getKeyCode() == KeyEvent.VK_S) {
                    startStop();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
                repaint();
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                requestFocus();
                coronaPlayGround.deactivateAll();
                active = true;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    coronaPlayGround.resetAll();
                } else {
                    coronaPlayGround.startStopAll();
                }
            }
        });
    }

    @Override
    public void run() {

        while (running-- > 0) {
//            MUtilityTools.pauseMillis(delay);
            oneInfectionStep();
            running = allImmune();
            if (running == 0) {
                coronaPlayGround.done();
            }
            repaint();
        }
    }

    @Override
    public void start(long l) {
//        MTools.println( "[" + getClass() + "]->start " + l);
        if (l <= 0) {
            return;
        }
        running = l;
        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
        repaint();
    }

    @Override
    public void stop() {
//        MTools.println( "[" + getClass() + "]->stop ");
        if (thread != null) {
            running = 0;
            // wait for completion
            while (thread.isAlive()) {
                // the show must go on ...
            }
        }
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Graphics2D g2d = (Graphics2D) g;

//        drawIndividuals(g2d);
        drawLegende(g2d);
//        drawCurves(g2d);
    }

    private void drawIndividuals(Graphics2D g2d) {

        for (Individual ind : individuals) {

            Rectangle2D.Double is;
            is = new Rectangle.Double(ind.box.x - 3, ind.box.y - 3, ind.box.width, ind.box.height);
            if (ind.isInfected()) {
                g2d.setColor(Color.RED);
            } else if (ind.isImmune()) {
                g2d.setColor(Color.GREEN);
            } else {
                g2d.setColor(Color.PINK);
            }
            g2d.fill(is);
        }
    }

    private void drawCurves(Graphics2D g2d) {

        g2d.setColor(Color.PINK);
        g2d.fill(new Rectangle2D.Double(worldSize, 0, worldSize, worldSize));

        int numSteps = distributions.size();
        double w = worldSize;
        double wBar = w / numSteps;

        for (int i = 0; i < distributions.size(); i++) {

            Distribution d = distributions.get(i);
            double x = i * wBar + w;

            g2d.setColor(Color.RED);
            double infected = worldSize * d.infected;
            double y = worldSize - infected;
            g2d.fill(new Rectangle2D.Double(x, y, wBar, infected));

            g2d.setColor(Color.PINK);
            double susceptible = worldSize * d.susceptible;
            y -= susceptible;
            g2d.fill(new Rectangle2D.Double(x, y, wBar, susceptible));

            g2d.setColor(Color.GREEN);
            double immune = worldSize * d.immune;
            y -= immune;
            g2d.fill(new Rectangle2D.Double(x, y, wBar, immune));


        }
    }

    private void drawLegende(Graphics2D g2d) {

        if (running > 0) {
            g2d.setColor(Color.BLACK);
        } else {
            g2d.setColor(Color.GRAY);
        }
        g2d.fill(new Rectangle2D.Double(0, 0, 120, 140));
        g2d.setColor(Color.GRAY);
        int yPos = 20;
        int yInc = 20;
        if (active) {
            g2d.setColor(Color.ORANGE);
        } else {
            g2d.setColor(Color.LIGHT_GRAY);
        }
        g2d.drawString("" + generation, 2, yPos);
//        yPos += yInc;
//        g2d.setColor(Color.LIGHT_GRAY);
//        g2d.drawString("delay " + delay, 10, yPos);
        g2d.setColor(Color.PINK);
        yPos += yInc;
        g2d.drawString("" + getNumberSusceptible(), 2, yPos);
        g2d.setColor(Color.RED);
//        yPos += yInc;
//        g2d.drawString("" + getNumberInfected(), 2, yPos);
        yPos += yInc;
        g2d.drawString("" + maxInfected, 2, yPos);
        g2d.setColor(Color.GREEN);
        yPos += yInc;
        g2d.drawString("" + getNumberImmune(), 2, yPos);
//        g2d.setColor(Color.ORANGE);
//        yPos += yInc;
//        int sum = getNumberSusceptible() + getNumberInfected() + getNumberImmune() - numIndividuals;
//        g2d.drawString("check " + sum, 10, yPos);
//        g2d.setColor(Color.CYAN);
//        yPos += yInc;
//        g2d.drawString("" + (int) (infectionPropability * 100) + " %", 2, yPos);

        /// percent
//        g2d.setColor(Color.LIGHT_GRAY);
//        yPos += yInc;
//        g2d.drawString("healthy [%] " + (int) (100 * getNumberSusceptiblePercent()), 10, yPos);
//        g2d.setColor(Color.RED);
//        yPos += yInc;
//        g2d.drawString("infected [%] " + (int) (100 * getNumberInfectedPercent()), 10, yPos);
//        g2d.setColor(Color.GREEN);
//        yPos += yInc;
//        g2d.drawString("immune [%] " + (int) (100 * getNumberImmunePercent()), 10, yPos);
    }

    private long allImmune() {
        if (getNumberImmune() + getNumberSusceptible() == CoronaPlayGround.numIndividuals) {
            return 0;
        }
        return Long.MAX_VALUE;
    }

    private void updateInfected() {
        for (Individual ind : individuals) {
            if (ind.isInfected()) {
                ind.incInfectedTime();
            }
        }
        if (getNumberInfected() > maxInfected) {
            maxInfected = getNumberInfected();
        }
    }

    public void startStop() {
        if (running > 0) {
            stop();
        } else {
            start(Long.MAX_VALUE);
        }
    }

    public void setActive(boolean b) {
        active = b;
        repaint();
    }

    private void oneInfectionStep() {
        move();
        infect();
        updateInfected();
    }

    private int getNumberSusceptible() {
        int count = 0;
        for (Individual ind : individuals)
            if (ind.isSusceptible()) {
                count++;
            }
        return count;
    }

    private double getNumberSusceptiblePercent() {
        return getNumberSusceptible() / (double) CoronaPlayGround.numIndividuals;
    }

    private int getNumberImmune() {
        int count = 0;
        for (Individual ind : individuals)
            if (ind.isImmune()) {
                count++;
            }
        return count;
    }

    private double getNumberImmunePercent() {
        return getNumberImmune() / (double) CoronaPlayGround.numIndividuals;
    }

    private int getNumberInfected() {
        int count = 0;
        for (Individual ind : individuals)
            if (ind.isInfected()) {
                count++;
            }
        return count;
    }

    private double getNumberInfectedPercent() {
        return getNumberInfected() / (double) CoronaPlayGround.numIndividuals;
    }

    void initIndividuals() {

        maxInfected = 0;
        generation = 0;
        distributions.clear();
        individuals.clear();
        Individual ind = new Individual();
        ind.box.x = worldSize / 2;
        ind.box.y = worldSize / 2;
        ind.incInfectedTime();
        individuals.add(ind);

        for (int i = 0; i < CoronaPlayGround.numIndividuals - 1; i++) {

            ind = new Individual();
            ind.box.x = Math.random() * worldSize;
            ind.box.y = Math.random() * worldSize;
            individuals.add(ind);
        }

        repaint();
    }

    private void move() {

        generation++;
        double f = CoronaPlayGround.move - CoronaPlayGround.moveHalf;
        for (Individual ind : individuals) {

//            ind.box.x += Math.random() * f;
//            ind.box.y += Math.random() * f;

            ind.box.x += generator.nextDouble() * f;
            ind.box.y += generator.nextDouble() * f;

            if (ind.box.x > worldSize) {
                ind.box.x = 0;
            }
            if (ind.box.y > worldSize) {
                ind.box.y = 0;
            }
            if (ind.box.x < 0) {
                ind.box.x = worldSize;
            }
            if (ind.box.y < 0) {
                ind.box.y = worldSize;
            }
        }
    }

    private void infect() {

        for (int i = 0; i < individuals.size(); i++) {
            Individual ind1 = individuals.get(i);
            for (int j = i + 1; j < individuals.size(); j++) {
                Individual ind2 = individuals.get(j);
                if (ind2.isImmune()) continue;
                if (!ind1.isInfected()) continue;
                infect(ind1, ind2);
            }
        }

        Distribution d = new Distribution();
        d.susceptible = getNumberSusceptiblePercent();
        d.infected = getNumberInfectedPercent();
        d.immune = getNumberImmunePercent();
        distributions.add(d);
    }

    private void infect(Individual ind1, Individual ind2) {

        if (!ind1.box.contains(ind2.box.x, ind2.box.y)) {
            return;
        }
        if (!(Math.random() < infectionPropability)) {
            return;
        }
        ind2.incInfectedTime();
    }

    public int getMaxInfected() {
        return maxInfected;
    }

    public int getMaxGeneration() {
        return generation;
    }
}
