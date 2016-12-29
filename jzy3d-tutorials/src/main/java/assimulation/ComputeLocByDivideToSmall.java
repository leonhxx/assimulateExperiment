package assimulation;

/**
 * Created by hongxiaoxiao on 16/12/22.
 */
public class ComputeLocByDivideToSmall implements ComputeLoc{
//    public Point getLoc(TargetLoc targetLoc,double [] rss, BaseStation[] baseStations,ComputeLocOptions options,CorrelationCoefficient correlationCoefficient){
    public Point getLoc(TargetLoc targetLoc,double [] rss,
            BaseStation[] originBaseStations, BaseStation[] baseStations,
            ComputeLocOptions options,CorrelationCoefficient correlationCoefficient){
        double minX=Integer.MAX_VALUE+0.0;
        double maxX=Integer.MIN_VALUE+0.0;
        double minY=Integer.MAX_VALUE+0.0;
        double maxY=Integer.MIN_VALUE+0.0;
        double width=options.getLength();
        for(int i=0;i<originBaseStations.length;i++){
            if(minX>originBaseStations[i].getX()){
                minX=originBaseStations[i].getX();
            }
            if(maxX<originBaseStations[i].getX()){
                maxX=originBaseStations[i].getX();
            }
            if(minY>originBaseStations[i].getY()){
                minY=originBaseStations[i].getY();
            }
            if(maxY<originBaseStations[i].getY()){
                maxY=originBaseStations[i].getY();
            }
        }
        double aveRss=0.0;
        double squareRss=0.0;
        for(int i=0;i<rss.length;i++){
            aveRss=aveRss*(i/(i+1.0))+rss[i]/(i+1);
        }
//        for(int i=0;i<rss.length;i++){
//            aveRss+=rss[i];
//        }
//        aveRss/=rss.length;
        for(int i=0;i<rss.length;i++){
            squareRss+=Math.pow((rss[i]-aveRss),2);
        }


        int xTime=(int)Math.floor((maxX-minX+width)/width);
        int yTime=(int)Math.floor((maxY-minY+width)/width);
        double startX=minX+width/2;
        double startY=minY+width/2;
        double minCorrelation=Integer.MAX_VALUE+0.0;
        double resultX=startX;
        double resultY=startY;
        for(int xi=0;xi<xTime;xi++){
            for(int yi=0;yi<yTime;yi++){
                double centerX=startX+xi*width;
                double centerY=startY+yi*width;
//                double correlation=correlationByOthers(centerX,centerY,rss,baseStations);
                double correlation=correlationCoefficient.getCorrelationCoefficient(centerX,centerY,rss,baseStations,aveRss,squareRss);
                if(Double.isNaN(correlation)){
                    System.out.println("wrong");
                    aveRss=0.0;
                    squareRss=0.0;
                    double preAveRss=aveRss;
                    for(int i=0;i<rss.length;i++){
                        preAveRss=aveRss;
                        aveRss=aveRss*(i/(i+1))+rss[i]/(i+1);
                        if(Double.isNaN(aveRss)){
                            System.out.println("wrong");
                        }
                    }
                    for(int i=0;i<rss.length;i++){
                        squareRss+=Math.pow((rss[i]-aveRss),2);
                    }
                }
                if(correlation<minCorrelation){
                    minCorrelation=correlation;
                    resultX=centerX;
                    resultY=centerY;
                }
            }
        }
//        System.out.println("rss="+rss+";aveRss="+aveRss+";squareRss="+squareRss);
        System.out.println("targetLoc correlation="+correlationCoefficient.getCorrelationCoefficient(targetLoc.getX(),targetLoc.getY(),rss,baseStations,aveRss,squareRss));
//        System.out.println("targetLoc correlation="+correlationByOthers(targetLoc.getX(),targetLoc.getY(),rss,baseStations));
        System.out.println("result correlation="+correlationCoefficient.getCorrelationCoefficient(resultX,resultY,rss,baseStations,aveRss,squareRss));
//        System.out.println("result correlation="+correlationByOthers(resultX,resultY,rss,baseStations));
        return new Point(resultX,resultY);
    }
}
