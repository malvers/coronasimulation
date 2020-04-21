import mratools.MFrame.MFrame;
import mratools.MTabbedPane.MTabbedPane;
import mratools.MTools;
import mratools.MUtilityTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Calendar;

public class CoronaPlayGround extends JPanel {

    private static int numberCoronaWorlds = 1;
    private final Grapher grapher;
    private static double infectionProbability = 0.1;
    private ArrayList<CoronaWorld> coronaWorlds = new ArrayList<>();
    private int doneCounter = 0;
    private long startTime;

    public CoronaPlayGround() {

        MTools.println("[" + getClass() + "]->ip: " + infectionProbability);
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
        setLayout(new GridLayout(1, 2));
        JPanel simu = new JPanel();
        simu.setLayout(new GridLayout(20, 1, 0, 0));
        for (CoronaWorld cw : coronaWorlds) {
            simu.add(cw);
        }
        grapher = new Grapher();
        grapher.setGlobalStatistics(0, 0, 0, 0, infectionProbability, numberCoronaWorlds, "00:00:00");

        MTabbedPane tabbedPane = new MTabbedPane(simu, grapher);
        add(tabbedPane);
//        removeAll();
//        JSplitPane split = new JSplitPane();
//        split.setDividerLocation(500);
//        split.setLeftComponent(simu);
//        split.setRightComponent(grapher);
//        add(split);
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
        if( mHour < 10 ) sh = "0" + sh;
        String sm= "" + mMin;
        if( mMin < 10 ) sm = "0" + sm;
        String ss = "" + mSec;
        if( mSec < 10 ) ss = "0" + ss;

        return "duration [hour:min:sec] - " + sh + ":" + sm + ":" + ss;
    }

    public void statistics(String timeString) {

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
                numberCoronaWorlds,
                timeString);
        grapher.saveImage(numberCoronaWorlds + "_" + infectionProbability + "_corona_simu.png");
        grapher.createHistogramImage(infectionProbability + "_corona_simu_histogram.png");
        repaint();
    }

    /// main for testing
    public static void main(String[] args) {

        numberCoronaWorlds = 2000;
        infectionProbability = 0.07;
        CoronaPlayGround cpg = new CoronaPlayGround();

        MFrame f = new MFrame();
        f.add(cpg);
        f.setVisible(true);
        Dimension sz = Toolkit.getDefaultToolkit().getScreenSize();
        f.setBounds(0, 0, sz.width, sz.height);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /// wait 10 seconds to start
        MUtilityTools.pauseMillis(10000);
        cpg.startStopAll();
    }
}
