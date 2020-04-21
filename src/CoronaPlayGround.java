import mratools.MFrame.MFrame;
import mratools.MTools;
import mratools.MUtilityTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class CoronaPlayGround extends JPanel {

    private static int numberCoronaWorlds = 1;
    private final Grapher grapher;
    private static double infectionProbability = 0.1;
    private ArrayList<CoronaWorld> coronaWorlds = new ArrayList<>();
    private int doneCounter = 0;
    private long startTime = 0;

    public CoronaPlayGround() {

//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                super.componentResized(e);
//                double worldSize = getHeight() / 5;
//
//                for (CoronaWorld cw : coronaWorlds) {
//                    cw.setWorldSize(worldSize);
//                }
//                repaint();
//            }
//        });

        for (int i = 0; i < numberCoronaWorlds; i++) {
            coronaWorlds.add(new CoronaWorld(this, infectionProbability));
        }
        setLayout(new GridLayout());
        JPanel coronaWordlsPanel = new JPanel();
        coronaWordlsPanel.setLayout(new GridLayout(5, 1, 1, 1));
        for (CoronaWorld cw : coronaWorlds) {
            coronaWordlsPanel.add(cw);
        }
        grapher = new Grapher();
        grapher.setGlobalStatistics(0, 0, 0, 0, infectionProbability, numberCoronaWorlds);

//        MTabbedPane tabbedPane = new MTabbedPane(simu, grapher);
//        add(tabbedPane);
        removeAll();
        JSplitPane split = new JSplitPane();
        split.setDividerLocation(600);
        split.setLeftComponent(coronaWordlsPanel);
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
            statistics();
            String str = getTimeString();
            MTools.println( str);
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

        int mHour = calendar.get(Calendar.HOUR);
        int mMin = calendar.get(Calendar.MINUTE);
        int mSec = calendar.get(Calendar.SECOND);

        return "duration [hour:min:sec] - "  + mHour + ":" + mMin + ":" + mSec;
    }

    public void statistics() {

        MTools.println("[" + getClass() + "]->statistics: ");
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
        MTools.println("[" + getClass() + "]->maxGeneration: " + maxGeneration);
        MTools.println("[" + getClass() + "]->minGeneration: " + minGeneration);
        MTools.println("[" + getClass() + "]->maxInfected:   " + maxInfected);
        MTools.println("[" + getClass() + "]->minInfected:   " + minInfected);
        grapher.calcAverageInfectionCurve();
        grapher.setGlobalStatistics(
                maxGeneration,
                minGeneration,
                maxInfected,
                minInfected,
                infectionProbability,
                numberCoronaWorlds);
        grapher.saveImage(numberCoronaWorlds + "_" + infectionProbability + "_corona_simu.png");
//        grapher.createHistogramImage(infectionProbability + "_corona_simu_histogram.png");
        repaint();
    }

    /// main for testing
    public static void main(String[] args) {

        numberCoronaWorlds = 10;
        infectionProbability = 0.1;
        CoronaPlayGround cpg = new CoronaPlayGround();

        MFrame f = new MFrame();
        f.add(cpg);
        f.setVisible(true);
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        f.setBounds(0, 0, 1200, sz.height);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MUtilityTools.pauseMillis(2000);
        cpg.startStopAll();
    }
}
