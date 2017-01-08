package assimulation;

import weka.clusterers.forOPTICSAndDBScan.DataObjects.DataObject;
import weka.clusterers.forOPTICSAndDBScan.Databases.Database;
import weka.core.Instance;

/**
 * Created by root on 16-4-16.
 */
public class MyDataObject extends weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject {
    public MyDataObject(Instance originalInstance, String key, Database database) {
        super(originalInstance,key,database);
    }
    public double distance(DataObject dataObject) {
        double dist = 0.0D;
        Instance firstInstance = this.getInstance();
        Instance secondInstance = dataObject.getInstance();
        Point firstPoint=new Point(firstInstance.value(assimulateExperiment.getxAttribute()),firstInstance.value(assimulateExperiment.getyAttribute()));
        Point secondPoint=new Point(secondInstance.value(assimulateExperiment.getxAttribute()),secondInstance.value(assimulateExperiment.getyAttribute()));
        return assimulateExperiment.distance(firstPoint,secondPoint);
    }
}
