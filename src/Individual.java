import java.awt.geom.Rectangle2D;

public class Individual {

    public int getInfectedTime() {
        return infectedTime;
    }

    public void incInfectedTime() {

        infectedTime++;
        if (infectedTime > 600) {
            infectedTime = -1;
        }

        /// quarantine
        if (infectedTime > CoronaPlayGround.quarantineTime) {
            if (Math.random() < CoronaPlayGround.quarantineProbability) {
                infectedTime = -1;
            }
        }
    }

    private int infectedTime = 0;
    Rectangle2D.Double box = new Rectangle2D.Double();

    public boolean isInfected() {
        return infectedTime > 0;
    }

    public Individual() {
        box.width = CoronaPlayGround.mysize;
        box.height = CoronaPlayGround.mysize;
    }

    public boolean isImmune() {
        return infectedTime < 0;
    }

    public boolean isSusceptible() {
        return infectedTime == 0;
    }
}
