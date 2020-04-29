import mratools.MTools;

import java.io.FileNotFoundException;

public class SimulatorCore implements IRunner {

    private static Grapher grapher = null;
    static int numThreads = 10;
    private static long globalStartTime;
    private final String name;
    private long duration;
    private Thread thread;
    private static int readyCount = 0;

    public SimulatorCore(String nameIn) throws FileNotFoundException {

        name = nameIn;
        grapher = new Grapher();

        /// times numThreads !!!
        PlayGround.numSimulations = 1000;

        start(Long.MAX_VALUE);
    }

    @Override
    public void start(long l) {
        thread = new Thread(this);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    @Override
    public void stop() {
    }

    @Override
    public void run() {

        for (int run = 0; run < PlayGround.numSimulations; run++) {

            long startTime = System.currentTimeMillis();
            CoronaWorld cw = new CoronaWorld();

            int count = 0;
            while (cw.allImmune() > 0) {
                count++;
                cw.oneInfectionStep();
            }
            grapher.add(cw.getDistributions());

            duration += System.currentTimeMillis() - startTime;
            System.out.println(name + " run: " + run + " time: " + PlayGround.getTimeString(duration) + " max: " + count);
        }
        readyCount++;

        if (readyCount >= numThreads) {

            long duration = System.currentTimeMillis() - globalStartTime;
            System.out.println();
            int simus = PlayGround.numSimulations * numThreads;
            System.out.println(simus + " total time needed: " + PlayGround.getTimeString(duration));
            grapher.calcAverageInfectionCurve();
            readyCount = 0;
            try {
                grapher.writeData();
                PlayGround.infectionProbability += 0.01;
                if( PlayGround.infectionProbability >= 1.0 ) return;
                startSimulations();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            
            MTools.println( "[" + getClass() + "]->: ");
        }
    }

    private static void startSimulations() throws FileNotFoundException {
        for (int i = 0; i < numThreads; i++) {
            new SimulatorCore("thread: " + i);
        }
    }

    public static void main(String[] args) throws FileNotFoundException {

        globalStartTime = System.currentTimeMillis();
        startSimulations();
    }
}



