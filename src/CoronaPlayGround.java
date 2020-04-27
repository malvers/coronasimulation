import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

public class CoronaPlayGround extends JPanel {

    public static double quarantineProbability = 0.01;
    public static int quarantineTime = 300;
    private static int numberCoronaWorlds = 1;
    static double scale;
    private final Grapher grapher;
    private static double infectionProbability = 0.1;
    private ArrayList<CoronaWorld> coronaWorlds = new ArrayList<>();
    private int doneCounter = 0;
    private long startTime;

    static double worldSize = 160;
    static double move = worldSize * 0.3;
    final static double moveHalf = move / 2.0;

    static double mysize = 4;
    static int numIndividuals = 500;
    private final static int randomLimit = 100000;
    private final static double[] randomList = new double[randomLimit];
    private static int randomCounter = 0;

    private void initRandom() {
        for (int i = 0; i < randomList.length; i++) {
            randomList[i] = Math.random() * move - moveHalf;
        }
    }

    static double getNextRandom() {
        randomCounter++;
        if (randomCounter >= randomLimit) {
            randomCounter = 0;
        }
        if (randomCounter >= randomLimit) {
            System.out.println("randomCounter: " + randomCounter + " rl: " + randomLimit);
        }
        return randomList[randomCounter];
    }

    public CoronaPlayGround() {

        initRandom();

        System.out.println("[" + getClass() + "]->ip: " + infectionProbability);

        for (int i = 0; i < numberCoronaWorlds; i++) {
            coronaWorlds.add(new CoronaWorld(this, worldSize, infectionProbability));
        }
        setLayout(new GridLayout(1, 2));
        JPanel simu = new JPanel();
        simu.setLayout(new GridLayout(10, 2, 0, 0));
        for (CoronaWorld cw : coronaWorlds) {
            simu.add(cw);
        }
        grapher = new Grapher();
        grapher.setGlobalStatistics(0, 0, 0, 0, infectionProbability, numberCoronaWorlds, "00:00:00");

        removeAll();
        JSplitPane split = new JSplitPane();
        split.setDividerLocation(1000);
        split.setLeftComponent(simu);
        split.setRightComponent(grapher);
        add(split);
    }

    void deactivateAll() {
        for (CoronaWorld cw : coronaWorlds) {
            cw.setActive(false);
        }
    }

    public void startStopAll() {
        startTime = System.currentTimeMillis();
        for (CoronaWorld cw : coronaWorlds) {
            cw.startStop();
        }
    }

    public void resetAll() {
        for (CoronaWorld cw : coronaWorlds) {
            cw.initIndividuals();
        }
    }

    public void done() {

        doneCounter++;
        if (doneCounter >= numberCoronaWorlds) {
            statistics(getTimeString());
        }
    }

    private String getTimeString() {

        long duration = System.currentTimeMillis() - startTime;
//            long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(duration);
        int mYear = calendar.get(Calendar.YEAR);
        // https://stackoverflow.com/questions/1755199/calendar-returns-wrong-month
        int mMonth = calendar.get(Calendar.MONTH) + 1; // ONLY month count from 0

        int mHour = calendar.get(Calendar.HOUR) - 1;
        int mMin = calendar.get(Calendar.MINUTE);
        int mSec = calendar.get(Calendar.SECOND);

        String sh = "" + mHour;
        if (mHour < 10) {
            sh = "0" + sh;
        }
        String sm = "" + mMin;
        if (mMin < 10) {
            sm = "0" + sm;
        }
        String ss = "" + mSec;
        if (mSec < 10) {
            ss = "0" + ss;
        }

        return "duration [hour:min:sec] - " + sh + ":" + sm + ":" + ss;
    }

    public void statistics(String timeString) {

        System.out.println("[" + getClass() + "]->statistics: ");
        int maxGeneration = 0;
        int minGeneration = Integer.MAX_VALUE;
        int maxInfected = 0;
        int minInfected = Integer.MAX_VALUE;

        for (CoronaWorld cw : coronaWorlds) {

            grapher.add(cw.getDistributions());

            if (cw.getMaxGeneration() > maxGeneration) {
                maxGeneration = cw.getMaxGeneration();
            }
            if (cw.getMaxGeneration() < minGeneration) {
                minGeneration = cw.getMaxGeneration();
            }
            if (cw.getMaxInfected() > maxInfected) {
                maxInfected = cw.getMaxInfected();
            }
            if (cw.getMaxInfected() < minInfected) {
                minInfected = cw.getMaxInfected();
            }
        }
        System.out.println("[" + getClass() + "]->maxGeneration: " + maxGeneration);
        System.out.println("[" + getClass() + "]->minGeneration: " + minGeneration);
        System.out.println("[" + getClass() + "]->maxInfected:   " + maxInfected);
        System.out.println("[" + getClass() + "]->minInfected:   " + minInfected);
        grapher.calcAverageInfectionCurve();
        grapher.setGlobalStatistics(
                maxGeneration,
                minGeneration,
                maxInfected,
                minInfected,
                infectionProbability,
                numberCoronaWorlds,
                timeString);
        grapher.savePaneImage(quarantineProbability*100 + "% "
                + " qt " + quarantineTime
                + " ms " + mysize
                + " cw " + numberCoronaWorlds
                + " ip " + infectionProbability + "_corona_simu.png");
//        grapher.saveHistogramImage(CoronaPlayGround.quarantineProbability*100 + "% " + mysize + " " + numberCoronaWorlds + "_" + infectionProbability + "_corona_simu_histogram.png");
        repaint();
    }

    /// main for testing
    public static void main(String[] args) {

        scale = 1.0;

        worldSize = 160 * scale;
        numIndividuals = 500;
        numIndividuals = (int) (numIndividuals * scale * scale);
        mysize = 5 * scale;
        quarantineProbability = 0.01; /// 1%
        quarantineTime = 100;
        infectionProbability = 0.03;
        numberCoronaWorlds = 4000;

        CoronaPlayGround cpg = new CoronaPlayGround();

        double density = (worldSize*worldSize) / numIndividuals;
        System.out.println( "density: " + density );

        JFrame f = new JFrame();
        f.add(cpg);
        f.setVisible(true);
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        f.setBounds(0, 0, sz.width, sz.height);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /// wait x seconds to start
        f.setTitle("still waiting ... ");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        f.setTitle("now running ... ");
        cpg.startStopAll();
    }
}
