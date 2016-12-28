package assimulation;

/**
 * Created by hongxiaoxiao on 16/12/22.
 */
public interface ComputeLoc {
    public Point getLoc(TargetLoc targetLoc,double [] rss,
                        BaseStation[] originBaseStations, BaseStation[] baseStations,
                        ComputeLocOptions options,CorrelationCoefficient correlationCoefficient);
}
