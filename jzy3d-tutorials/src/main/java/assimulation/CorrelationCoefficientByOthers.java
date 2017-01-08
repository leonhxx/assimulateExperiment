package assimulation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hongxiaoxiao on 16/12/22.
 */
public class CorrelationCoefficientByOthers implements CorrelationCoefficient {
    @Override
    public CorrelationCoefficientResult getCorrelationCoefficient(double startX, double startY, double[] rss, BaseStation[] baseStations, double aveRss, double squareRss) {
        List<Double> rssList=new ArrayList();
        List<Double> dist=new ArrayList<Double>();
        for(int i=0;i<rss.length;i++){
            rssList.add(i,rss[i]);
        }
        Point startPoint=new Point(startX,startY);
        for(int i=0;i<baseStations.length;i++){
            dist.add(i,Math.log(assimulateExperiment.distance(startPoint,baseStations[i])));
        }
        CorrelationCoefficientResult result=new CorrelationCoefficientResult();
        result.setTooClose(false);
        result.setCorrelation(PearsonCoefficient.coefficient(rssList,dist));
        return result;
    }
}
