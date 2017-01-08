package assimulation;

/**
 * Created by hongxiaoxiao on 17/1/6.
 */
public class CorrelationCoefficientResult {
    private boolean isTooClose;

    private double correlation;

    public boolean isTooClose() {
        return isTooClose;
    }

    public void setTooClose(boolean tooClose) {
        isTooClose = tooClose;
    }

    public double getCorrelation() {
        return correlation;
    }

    public void setCorrelation(double correlation) {
        this.correlation = correlation;
    }
}
