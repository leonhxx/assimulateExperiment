package assimulation;
import org.jzy3d.plot3d.builder.Mapper;
/**
 * Created by root on 16-12-21.
 */
public class MyMapper extends Mapper{
    double [] rss;
    BaseStation[] baseStations;
    double aveRss;
    double squareRss;
    assimulateExperiment assimulateTool;

    public void setAssimulateTool(assimulateExperiment assimulateTool) {
        this.assimulateTool = assimulateTool;
    }

    public void setRss(double[] rss) {
        this.rss = rss;
    }

    public void setBaseStations(BaseStation[] baseStations) {
        this.baseStations = baseStations;
    }

    public void setAveRss(double aveRss) {
        this.aveRss = aveRss;
    }

    public void setSquareRss(double squareRss) {
        this.squareRss = squareRss;
    }

    @Override
    public double f(double x, double y) {
        return assimulateTool.correlationCoefficient.getCorrelationCoefficient(x,y,rss,baseStations,aveRss,squareRss).getCorrelation();
    }
}
