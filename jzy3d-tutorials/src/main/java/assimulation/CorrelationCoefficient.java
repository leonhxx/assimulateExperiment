package assimulation;

/**
 * Created by hongxiaoxiao on 16/12/22.
 */
public interface CorrelationCoefficient {
    public  CorrelationCoefficientResult getCorrelationCoefficient(double startX, double startY, double [] rss, BaseStation[] baseStations, double aveRss, double squareRss);
}
