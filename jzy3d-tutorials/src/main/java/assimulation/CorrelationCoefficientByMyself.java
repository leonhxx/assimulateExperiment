package assimulation;

/**
 * Created by hongxiaoxiao on 16/12/22.
 */
public class CorrelationCoefficientByMyself implements CorrelationCoefficient{
    @Override
    public  double getCorrelationCoefficient(double startX, double startY, double[] rss, BaseStation[] baseStations, double aveRss, double squareRss) {
        double top=0.0;
        double squareDis=0.0;
        double[] dis=new double[baseStations.length];
        int k=0;
        double totalDis=0.0;
        Point startPoint=new Point(startX,startY);
        while(k<baseStations.length){
//            dis[k]=assimulateExperiment.distance(startPoint,baseStations[k]);
            dis[k]=Math.log(assimulateExperiment.distance(startPoint,baseStations[k]));
            totalDis+=dis[k];
            k+=1;
        }
        double aveDis=totalDis/k;
        top=0.0;
        squareDis=0.0;
        for(int l=0 ;l< k;l++){
            top+=(rss[l]-aveRss)*(dis[l]-aveDis);
            squareDis+=(dis[l]-aveDis)*(dis[l]-aveDis);
        }
        //        correlation(i)(j)=top/Math.sqrt(squareDis*squareRss)
        double corre=0.0;
        if(squareDis!=0&&squareRss!=0){
            corre= (top/Math.sqrt(squareDis*squareRss));
        }
        return corre;
    }
}
