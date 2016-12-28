package assimulation;

import java.util.List;

/**
 * Created by hongxiaoxiao on 16/12/22.
 */
public class PearsonCoefficient {

    /**
     * add operate method
     */
    public static double calcuteNumerator(List<Double> xList ,List<Double> yList){
        double result =0.0;
        double xAverage = 0.0;
        double temp = 0.0;

        int xSize = xList.size();
        for(int x=0;x<xSize;x++){
            temp += xList.get(x);
        }
        xAverage = temp/xSize;

        double yAverage = 0.0;
        temp = 0.0;
        int ySize = yList.size();
        for(int x=0;x<ySize;x++){
            temp +=yList.get(x);
        }
        yAverage = temp/ySize;

        //double sum = 0.0;
        for(int x=0;x<xSize;x++){
            result+=(xList.get(x)-xAverage)*(yList.get(x)-yAverage);
        }
        return result;
    }
    public static double calculateDenominator(List<Double> xList ,List<Double> yList){
        double standardDifference = 0.0;
        int size = xList.size();
        double xAverage = 0.0;
        double yAverage = 0.0;
        double xException = 0.0;
        double yException = 0.0;
        double temp = 0.0;
        for(int i=0;i<size;i++){
            temp += xList.get(i);
        }
        xAverage = temp/size;

        for(int i=0;i<size;i++){
            temp += yList.get(i);
        }
        yAverage = temp/size;

        for(int i=0;i<size;i++){
            xException += Math.pow(xList.get(i)-xAverage,2);
            yException += Math.pow(yList.get(i)-yAverage, 2);
        }
        //calculate denominator of
        return standardDifference = Math.sqrt(xException*yException);
    }
    public static double coefficient(List<Double> xList ,List<Double> yList){
        return calcuteNumerator(xList,yList)/calculateDenominator(xList,yList);
    }
}
