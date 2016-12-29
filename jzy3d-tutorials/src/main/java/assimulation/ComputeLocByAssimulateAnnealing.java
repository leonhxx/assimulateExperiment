package assimulation;

import java.util.Random;

/**
 * Created by hongxiaoxiao on 16/12/22.
 */
public class ComputeLocByAssimulateAnnealing implements ComputeLoc{
    public Point neighborhood(double startX,double startY,Random random,double com){
        double rand=random.nextDouble();
        double len=Math.sqrt(com);
        double addX=rand*len;
        if(random.nextBoolean()){
            addX*=(-1);
        }
        double addY=Math.sqrt(com-addX*addX);
        if(random.nextBoolean()){
            addY*=(-1);
        }
        return new Point(startX+addX,startY+addY);
    }
//    public Point getLoc(TargetLoc targetLoc,double [] rss, BaseStation[] baseStations,ComputeLocOptions options,CorrelationCoefficient correlationCoefficient){
public Point getLoc(TargetLoc targetLoc,double [] rss,
                    BaseStation[] originBaseStations, BaseStation[] baseStations,
                    ComputeLocOptions options,CorrelationCoefficient correlationCoefficient){
        double minX=Integer.MAX_VALUE+0.0;
        double maxX=Integer.MIN_VALUE+0.0;
        double minY=Integer.MAX_VALUE+0.0;
        double maxY=Integer.MIN_VALUE+0.0;
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
        for(int i=0;i<rss.length;i++){
            squareRss+=Math.pow((rss[i]-aveRss),2);
        }

        double startX=(minX+maxX)/2;
        double startY=(minY+maxY)/2;

//        double bestCorrelation=correlationByOthers(startX,startY,rss,baseStations);
        double bestCorrelation=correlationCoefficient.getCorrelationCoefficient(startX,startY,rss,baseStations,aveRss,squareRss);
        double bestX=startX;
        double bestY=startY;
        double preCorrelation=bestCorrelation;

        int maxCoolingTime=options.getMaxCoolingTime();
        int maxIterationTime=options.getMaxIterationTime();
        double temperature=options.getTemperature();
        double coolingRate=options.getCoolingRate();

        double com=Math.pow(options.getLength(),2);
        Random random=new Random(System.currentTimeMillis());

        double x=startX;
        double y=startY;

        double aveCorrelationDiff=0.0;
        int gaussTime=0;

        for(int coolingTime=0;coolingTime<maxCoolingTime||Math.abs(temperature)>1e-6;coolingTime++){
            for(int iterationTime=0;iterationTime<maxIterationTime;iterationTime++){
                Point next=null;
//                int findTime=0;
                do{
                    next=neighborhood(x,y,random,com);
//                    findTime++;
//                    if(findTime>1000){
//                        System.out.println();
//                    }
                }while(!(next.getX()>=minX&&next.getX()<=maxX&&next.getY()>=minY&&next.getY()<=maxY));
                gaussTime+=1;
//                    double correlation=correlationByOthers(next.getX(),next.getY(),rss,baseStations);
                double correlation=correlationCoefficient.getCorrelationCoefficient(next.getX(),next.getY(),rss,baseStations,aveRss,squareRss);
                if(correlation<bestCorrelation){
                    bestX=next.getX();
                    bestY=next.getY();
                    bestCorrelation=correlation;
                }
                double rand=random.nextDouble();
                aveCorrelationDiff+=(correlation-preCorrelation);
                if(correlation<preCorrelation||Math.exp((preCorrelation-correlation)/temperature)>rand){
                    x=next.getX();
                    y=next.getY();
                    preCorrelation=correlation;
                }

            }
            temperature*=coolingRate;
        }
        System.out.println("aveCorrelationDiff="+(aveCorrelationDiff/gaussTime));
//        System.out.println("rss="+rss+";aveRss="+aveRss+";squareRss="+squareRss);
        System.out.println("targetLoc correlation="+correlationCoefficient.getCorrelationCoefficient(targetLoc.getX(),targetLoc.getY(),rss,baseStations,aveRss,squareRss));
        System.out.println("result correlation="+correlationCoefficient.getCorrelationCoefficient(bestX,bestY,rss,baseStations,aveRss,squareRss));
        System.out.println("start correlation="+correlationCoefficient.getCorrelationCoefficient(startX,startY,rss,baseStations,aveRss,squareRss));
        return new Point(bestX,bestY);
    }
}
