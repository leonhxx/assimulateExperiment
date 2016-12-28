package assimulation;

/**
 * Created by hongxiaoxiao on 16/12/22.
 */
public class ComputeLocOptions {
    private int maxCoolingTime;
    private int maxIterationTime;
    private double temperature;
    private double coolingRate;
    private double length;

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }


    public int getMaxCoolingTime() {
        return maxCoolingTime;
    }

    public void setMaxCoolingTime(int maxCoolingTime) {
        this.maxCoolingTime = maxCoolingTime;
    }

    public int getMaxIterationTime() {
        return maxIterationTime;
    }

    public void setMaxIterationTime(int maxIterationTime) {
        this.maxIterationTime = maxIterationTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getCoolingRate() {
        return coolingRate;
    }

    public void setCoolingRate(double coolingRate) {
        this.coolingRate = coolingRate;
    }


}
