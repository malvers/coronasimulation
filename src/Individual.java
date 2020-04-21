import java.awt.geom.Rectangle2D;

public class Individual {

    private final double range = 5;

    public int getInfectedTime() {
        return infectedTime;
    }

    public void incInfectedTime() {
        infectedTime++;
        if (infectedTime > 600) {
            infectedTime = -1;
        }
    }

    private int infectedTime = 0;
    Rectangle2D.Double box = new Rectangle2D.Double();

    public boolean isInfected() {
        return infectedTime > 0;
    }

    public Individual() {
        box.width = range;
        box.height = range;
    }

    public boolean isImmune() {
        return infectedTime < 0;
    }

    public boolean isSusceptible() {
        return infectedTime == 0;
    }
}
