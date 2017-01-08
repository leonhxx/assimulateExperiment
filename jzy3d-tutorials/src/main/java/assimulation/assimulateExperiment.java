package assimulation;

import org.jzy3d.analysis.AnalysisLauncher;

import java.awt.geom.GeneralPath;
import java.util.*;
import java.util.List;

import weka.classifiers.*;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.DBSCAN;
import weka.core.*;

/**
 * Created by root on 16-12-19.
 */
class BaseStation extends Point{
    double u;
    double rss;
    int group;
    public void setRss(double rss){
        this.rss=rss;
    }
    public BaseStation(double x,double y){
        super(x,y);
    }
    public BaseStation(double x,double y,double u){
        super(x,y);
        this.u=u;
    }
}
class TargetLoc extends Point{
    public TargetLoc(double x,double y){
        super(x,y);
    }
}
class CorrelationPoint extends Point implements Comparable<CorrelationPoint>{
    double correlation;
    public CorrelationPoint(double x,double y,double correlation){
        super(x,y);
        this.correlation=correlation;
    }
    public int compareTo(CorrelationPoint o){
        if(this.correlation<o.correlation){
            return -1;
        }else if(this.correlation>o.correlation){
            return 1;
        }else{
            return 0;
        }
    }
}
public class assimulateExperiment {
    public void setWidth(int width) {
        this.width = width;
    }


    public void setBaseStationNum(int baseStationNum) {
        this.baseStationNum = baseStationNum;
    }

    public void setTargetNum(int targetNum) {
        this.targetNum = targetNum;
    }

    public void setNlosMaxNum(int nlosMaxNum) {
        this.nlosMaxNum = nlosMaxNum;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public void setuMax(double uMax) {
        this.uMax = uMax;
    }

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public void setP0(double p0) {
        this.p0 = p0;
    }

    int width;
    int baseStationNum;
    int targetNum;
    int nlosMaxNum;
    double beta,uMax,sigma,p0;
    int armseK;

    public double getDiscreteRatio() {
        return discreteRatio;
    }

    public void setDiscreteRatio(double discreteRatio) {
        this.discreteRatio = discreteRatio;
    }

    double discreteRatio;
    public int getArmseK() {
        return armseK;
    }

    public void setArmseK(int armseK) {
        this.armseK = armseK;
    }



    public void setBlockSize(double blockSize) {
        this.blockSize = blockSize;
    }

    double blockSize;
    CorrelationCoefficient correlationCoefficient;
    public BaseStation[] createBaseStationUniformly(){
        Random random=new Random();
        BaseStation[] baseStations=new BaseStation[baseStationNum];
        int widthNum=(int)Math.floor((width+blockSize)/blockSize);
        int totalNum=widthNum*widthNum;
        boolean [] mark=new boolean[totalNum];
//        int nlosNum=0;
        for(int i=0;i<baseStationNum;i++){
            int next=random.nextInt(totalNum);
            if(mark[next]){
                i--;
                continue;
            }
            mark[next]=true;
            int x=next%widthNum;
            int y=next/widthNum;
            baseStations[i]=new BaseStation(x+blockSize/2,y+blockSize/2,random.nextDouble()*uMax);
        }
        return baseStations;
    }
    public BaseStation getNeighborhood(Random random,BaseStation center){
        double rand;
        double len;
        double com;
        BaseStation neighborhood=null;
        do{
            rand=random.nextDouble();
            len=random.nextDouble()*10;
            com=len*len;
            double addX=rand*len;
            if(random.nextBoolean()){
                addX*=(-1);
            }
            double addY=Math.sqrt(com-addX*addX);
            if(random.nextBoolean()){
                addX*=(-1);
            }
            neighborhood=new BaseStation(center.getX()+addX,center.getY()+addY,random.nextDouble()*uMax);
        }while(!(neighborhood.getX()>=0&&neighborhood.getX()<width&&neighborhood.getY()>=0&&neighborhood.getY()<width));
        return neighborhood;
    }
    public BaseStation[] createBaseStationCluster(){
        Random random=new Random();
        BaseStation[] baseStations=new BaseStation[baseStationNum];
        int widthNum=(int)Math.floor((width+blockSize)/blockSize);
        int totalNum=widthNum*widthNum;
        Map<BaseStation,Boolean> mark=new HashMap<>();
        int num=0;
        int cluserNum=(int)(baseStationNum*(1-discreteRatio));
        while(num<cluserNum){
            int next=random.nextInt(totalNum);
            int x=next%widthNum;
            int y=next/widthNum;
            BaseStation center=new BaseStation((x+1/2)*blockSize,(y+1/2)*blockSize,random.nextDouble()*uMax);
            if(!mark.containsKey(center)){
                baseStations[num++]=center;
                mark.put(center,true);
                if((cluserNum-num)>0){
                    int neighborhoodNum=random.nextInt(cluserNum-num);
                    neighborhoodNum=neighborhoodNum<30?neighborhoodNum:30;
                    int hasgenerate=0;
                    while(hasgenerate<neighborhoodNum){
                        BaseStation neighborhood=getNeighborhood(random,center);
                        if(!mark.containsKey(neighborhood)){
                            baseStations[num++]=neighborhood;
                            mark.put(neighborhood,true);
                            hasgenerate++;
                        }
                    }
                }
            }
        }
        while(num<baseStationNum){
            int next=random.nextInt(totalNum);
            int x=next%widthNum;
            int y=next/widthNum;
            BaseStation center=new BaseStation((x+1/2)*blockSize,(y+1/2)*blockSize,random.nextDouble()*uMax);
            if(!mark.containsKey(center)){
                baseStations[num++]=center;
                mark.put(center,true);
            }
        }
        return baseStations;
    }
    public boolean checkInHull(TargetLoc targetLoc,GeneralPath generalPath){
        return generalPath.contains(targetLoc.getX(),targetLoc.getY());
    }
    public GeneralPath getGeneralPath(BaseStation[] baseStations){
        List<Point> points=new LinkedList<Point>();
        for(int i=0;i<baseStations.length;i++){
            points.add(new Point(baseStations[i].getX(),baseStations[i].getY()));
        }
        MinimumBoundingPolygon minimumBoundingPolygon=new MinimumBoundingPolygon();
        LinkedList<Point> res=minimumBoundingPolygon.findSmallestPolygon(points);
        GeneralPath generalPath=new GeneralPath();
        Point first=res.getFirst();
        generalPath.moveTo(first.getX(), first.getY());
        res.remove(0);
        for(int i=0;i<res.size();i++){
            Point point=res.get(i);
            generalPath.lineTo(point.getX(),point.getY());
        }
        generalPath.lineTo(first.getX(),first.getY());
        generalPath.closePath();
        return generalPath;
    }
    public TargetLoc[] createTargetLoc(BaseStation[] baseStations){
        Random random=new Random();
        TargetLoc[] targetLocs=new TargetLoc[targetNum];
        int totalNum=width*width;
        boolean [] mark=new boolean[totalNum];

        GeneralPath generalPath=getGeneralPath(baseStations);

        for(int i=0;i<targetNum;i++){
            int next=random.nextInt(totalNum);
            if(mark[next]){
                i--;
                continue;
            }
            mark[next]=true;
            int x=next%width;
            int y=next/width;
            targetLocs[i]=new TargetLoc(x+0.5,y+0.5);
            if(!checkInHull(targetLocs[i],generalPath)){
                i--;
                continue;
            }else{
                for(int j=0;j<baseStations.length;j++){ //当距离为0时信号强度为无穷小infinity，因此要避免该情况
                    if(Math.abs(assimulateExperiment.distance(targetLocs[i],baseStations[j]))<1e-6){
                        i--;
                        break;
                    }
                }
            }
        }
        return targetLocs;
    }
    public double gaussNlos(Random random,double u){
//        return 0;
        return sigma*random.nextGaussian()+u;
    }
    public double [] getRss(TargetLoc targetLoc,BaseStation[] baseStations){
        Random random=new Random();
        boolean[] baseStationNlos=new boolean[baseStations.length];
        for(int i=0;i<nlosMaxNum&&i<baseStations.length;i++){
            if(!baseStationNlos[random.nextInt(baseStations.length)]){
                baseStationNlos[random.nextInt(baseStations.length)]=true;
            }else{
                i--;
            }
        }

        double [] rss=new double[baseStations.length];
        for(int i=0;i<baseStations.length;i++){
            rss[i]=p0-10*beta*Math.log10(distance(targetLoc,baseStations[i]));
            if(baseStationNlos[i]){
                rss[i]+=gaussNlos(random,baseStations[i].u);
            }
        }
        return rss;
    }
    public static double distance(Point targetLoc,Point baseStation){
        return Math.sqrt(Math.pow(targetLoc.getX()-baseStation.getX(),2)+Math.pow(targetLoc.getY()-baseStation.getY(),2));
    }
    public void draw(double aveRss,double squareRss,BaseStation[] baseStations,double [] rss){
        MyMapper mapper=new MyMapper();
        mapper.setAssimulateTool(this);
        mapper.setAveRss(aveRss);
        mapper.setSquareRss(squareRss);
        mapper.setBaseStations(baseStations);
        mapper.setRss(rss);

        MySurface surface=new MySurface();
        surface.setMapper(mapper);
        surface.setMax((float) this.width);
        surface.setMin(0.0f);
        surface.setSteps(100);
        try{
            AnalysisLauncher.open(surface);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public Point getLocByOrigin(int divNum, double [] rss, BaseStation[] baseStations){
        double minX=Integer.MAX_VALUE+0.0;
        double maxX=Integer.MIN_VALUE+0.0;
        double minY=Integer.MAX_VALUE+0.0;
        double maxY=Integer.MIN_VALUE+0.0;
        for(int i=0;i<baseStations.length;i++){
            if(minX>baseStations[i].getX()){
                minX=baseStations[i].getX();
            }
            if(maxX<baseStations[i].getX()){
                maxX=baseStations[i].getX();
            }
            if(minY>baseStations[i].getY()){
                minY=baseStations[i].getY();
            }
            if(maxY<baseStations[i].getY()){
                maxY=baseStations[i].getY();
            }
        }
        double aveRss=0.0;
        double squareRss=0.0;
        for(int i=0;i<rss.length;i++){
            aveRss+=rss[i];
        }
        aveRss/=rss.length;
        for(int i=0;i<rss.length;i++){
            squareRss+=Math.pow((rss[i]-aveRss),2);
        }
        double width=(maxX-minX)/divNum;
        double length=(maxY-minY)/divNum;
        double startX=minX+width/2;
        double startY=minY+length/2;
        double maxCorrelation=Integer.MIN_VALUE+0.0;
        double preWidth=width;
        double preLength=length;
        int totalNum=divNum*divNum;

        draw(aveRss,squareRss,baseStations,rss);

        while(!(width<=0.5&&length<=0.5)){
            CorrelationPoint[] correlationPoints=new CorrelationPoint[totalNum];
            for(int i=0;i<divNum;i++){
                for(int j=0;j<divNum;j++){
                    double centerX=startX+j*width;
                    double centerY=startY+i*length;
//                    double correlation=correlationByOthers(centerX,centerY,rss,baseStations);
                    double correlation=correlationCoefficient.getCorrelationCoefficient(centerX,centerY,rss,baseStations,aveRss,squareRss).getCorrelation();
                    correlationPoints[i*divNum+j]=new CorrelationPoint(centerX,centerY,correlation);
                }
            }
            Arrays.sort(correlationPoints);
            minX=Integer.MAX_VALUE+0.0;
            maxX=Integer.MIN_VALUE+0.0;
            minY=Integer.MAX_VALUE+0.0;
            maxY=Integer.MIN_VALUE+0.0;
            for(int i=0;i<(totalNum/2);i++){
                if(minX>correlationPoints[i].getX()){
                    minX=correlationPoints[i].getX();
                }
                if(maxX<correlationPoints[i].getX()){
                    maxX=correlationPoints[i].getX();
                }

                if(minY>correlationPoints[i].getY()){
                    minY=correlationPoints[i].getY();
                }
                if(maxY<correlationPoints[i].getY()){
                    maxY=correlationPoints[i].getY();
                }
            }

            preWidth=width;
            preLength=length;

            startX=minX-width/2;
            startY=minY-length/2;
            width=(maxX-minX+width)/divNum;
            length=(maxY-minY+length)/divNum;
            startX+=(width/2);
            startY+=(length/2);

            if(preWidth==width && preLength==length){
                System.out.println("stay the same");
            }
//            else if(preWidth!=width && preLength!=length){
//                System.out.println("not the same");
//            }
        }

        CorrelationPoint[] correlationPoints=new CorrelationPoint[totalNum];
        for(int i=0;i<divNum;i++){
            for(int j=0;j<divNum;j++){
                double centerX=startX+j*width;
                double centerY=startY+i*length;
//                double correlation=correlationByOthers(centerX,centerY,rss,baseStations);
                double correlation=correlationCoefficient.getCorrelationCoefficient(centerX,centerY,rss,baseStations,aveRss,squareRss).getCorrelation();
                correlationPoints[i*divNum+j]=new CorrelationPoint(centerX,centerY,correlation);
            }
        }

        double rssSum=0.0;
        double resultX=0.0;
        double resultY=0.0;
        for(int i=0;i<totalNum;i++){
            resultX+=(correlationPoints[i].getX()*correlationPoints[i].correlation);
            resultY+=(correlationPoints[i].getY()*correlationPoints[i].correlation);
            rssSum=rssSum+correlationPoints[i].correlation;
        }
        resultX=resultX/rssSum;
        resultY=resultY/rssSum;

        return new Point(resultX,resultY);
    }
    public BaseStation[] deRepetition(BaseStation[] baseStations){
        List<BaseStation> parsedList=new LinkedList<>();
        Arrays.sort(baseStations, new Comparator<BaseStation>() {
            @Override
            public int compare(BaseStation o1, BaseStation o2) {
                if(o1.getX()<o2.getX()){
                    return -1;
                }else if(o1.getX()>o2.getX()){
                    return 1;
                }else{
                    if(o1.getY()<o2.getY()){
                        return -1;
                    }else if(o1.getY()>o2.getY()){
                        return 1;
                    }else{
                        return 0;
                    }
                }
            }
        });

        int  begin=0;
        int end=0;
        double sum=baseStations[0].rss;
        for(int i=1;i<baseStations.length;i++){
            if((baseStations[i].getX()==baseStations[i-1].getX())&&(baseStations[i].getY()==baseStations[i-1].getY())){
                sum+=baseStations[i].rss;
                end=i;
            }else{
                BaseStation ave=new BaseStation(baseStations[i-1].getX(),baseStations[i-1].getY());
                ave.rss=sum/(end-begin+1);
                parsedList.add(ave);
                begin=i;
                end=i;
                sum=baseStations[i].rss;
            }
        }
        BaseStation ave=new BaseStation(baseStations[baseStations.length-1].getX(),baseStations[baseStations.length-1].getY());
        ave.rss=sum/(end-begin+1);
        parsedList.add(ave);
        BaseStation[] res=new BaseStation[parsedList.size()];
        return parsedList.toArray(res);
    }
    private static final Attribute xAttribute=new Attribute("x");
    private static final Attribute yAttribute=new Attribute("y");
    public static Attribute getxAttribute() {
        return xAttribute;
    }

    public static Attribute getyAttribute() {
        return yAttribute;
    }
    public Instances getInstances(BaseStation[] baseStations){
        FastVector fastVector=new FastVector(2);
        fastVector.addElement(xAttribute);
        fastVector.addElement(yAttribute);
        Instances ins=new Instances("loc",fastVector,0);
//        Instances ins=new Instances("loc",fastVector,1);
        for(BaseStation loc:baseStations){
            Instance tmp=new Instance(2);
            tmp.setValue(xAttribute,loc.getX());
//            tmp.setValue(0,loc.getX());
            tmp.setValue(yAttribute,loc.getY());
//            tmp.setValue(1,loc.getY());
            tmp.setDataset(ins);
            ins.add(tmp);
        }
        return ins;
    }
    public List<BaseStation> getValidInstances(Instances ins,DBSCAN dbscan,BaseStation[] baseStations){
        List<BaseStation> points=new LinkedList<>();
        Map<Point,Double> rssMap=new HashMap<>();
        for(BaseStation x:baseStations){
            rssMap.put(new Point(x.getX(),x.getY()),x.rss);
        }
//        try{
//            ClusterEvaluation eval = new ClusterEvaluation();
//            eval.setClusterer(dbscan);
//            eval.evaluateClusterer(ins);
//            System.out.println(ins.toString());
//            double[] num = eval.getClusterAssignments();
//            for (int i = 0; i < num.length; i++)
//            {
//                System.out.println(String.valueOf( num[i]));
//            }
//            System.out.println(eval.clusterResultsToString());
//            System.out.println(eval.getNumClusters());
//        }catch (Exception e){
//        }
        Enumeration<Instance> enumeration=ins.enumerateInstances();
        while(enumeration.hasMoreElements()){
            Instance in=enumeration.nextElement();
            int j=0;
            BaseStation point=new BaseStation(in.value(xAttribute),in.value(yAttribute));
//            BaseStation point=new BaseStation(in.valueSparse(0),in.valueSparse(1));
            int group=-1;
            try{
                group=dbscan.clusterInstance(in);
                point.group=group;
                point.rss=rssMap.get(new Point(in.value(xAttribute),in.value(yAttribute)));
                points.add(point);
            }catch (Exception e){
//                e.printStackTrace();
            }
        }
        Collections.sort(points, new Comparator<BaseStation>() {
            @Override
            public int compare(BaseStation o1, BaseStation o2) {
                if(o1.group<o2.group){
                    return -1;
                }else if(o1.group>o2.group){
                    return 1;
                }else{
                    return 0;
                }
            }
        });
        return points;
    }
    public List<BaseStation> dbscanParse(BaseStation[] baseStations,double block){
        Instances ins=getInstances(baseStations);
        DBSCAN dbscan=new DBSCAN();
//        String [] options={"-D","weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject"};
//        String [] options={"-D","assimulation.MyDataObject"};
        try{
//            dbscan.setOptions(options);
//            dbscan.setDatabase_Type("weka.clusterers.forOPTICSAndDBScan.DataObjects.DataObject");
//            dbscan.setDatabase_Type("weka.clusterers.forOPTICSAndDBScan.Databases.SequentialDatabase");
            dbscan.setDatabase_distanceType("assimulation.MyDataObject");
//            dbscan.setDatabase_distanceType("weka.clusterers.forOPTICSAndDBScan.DataObjects.EuclideanDataObject");
            dbscan.setEpsilon(block);
            dbscan.setMinPoints(3);
            dbscan.buildClusterer(ins);
        }catch (Exception e){
            e.printStackTrace();
        }
        return getValidInstances(ins,dbscan,baseStations);
    }
    public List<BaseStation> parseDomain(List<BaseStation> points,int begin,int end,
                                         double maxX,double minX,double maxY,double minY,
                                         int initSize,int maxSize,double blockSize){
        int x=(int)Math.floor((assimulateExperiment.distance(new Point(maxX,maxY),new Point(minX,maxY))+blockSize)/blockSize);
        int y=(int)Math.floor((assimulateExperiment.distance(new Point(maxX,maxY),new Point(maxX,minY))+blockSize)/blockSize);
        List<BaseStation> [][] blocks=new LinkedList[y+1][x+1];
        for(int i=begin;i<=end;i++){
            BaseStation point=points.get(i);
            x=(int)Math.floor(assimulateExperiment.distance(point,new Point(minX,point.getY()))/blockSize);
            y=(int)Math.floor(assimulateExperiment.distance(point,new Point(point.getX(),minY))/blockSize);
            if(blocks[y][x]==null){
                blocks[y][x]=new LinkedList<>();
            }
            blocks[y][x].add(point);
        }
        int width=blocks[0].length;
        int length=blocks.length;
        List<BaseStation> result=new LinkedList<>();
        for(int ii=0;ii<length;ii++){
            for(int jj=0;jj<width;jj++){
                if(blocks[ii][jj]==null){
                    continue;
                }
                for(int kk=0;kk<blocks[ii][jj].size();kk++){
                    BaseStation now=blocks[ii][jj].get(kk);
                    double maxRss=0.0;
                    double minRss=0.0;
                    double medRss=0.0;
                    int size=initSize;
                    while (!(minRss<medRss&&medRss<maxRss)&&size<=maxSize){
                        List<Double> around=new LinkedList<>();
                        double len=size*blockSize;
                        for(int ll=ii-size;ll<=(ii+size);ll++){
                            for(int mm=jj-size;mm<=(jj+size);mm++){
                                if(ll>=0&&ll<length&&mm>=0&&mm<width&&blocks[ll][mm]!=null){
                                    for(int nn=0;nn<blocks[ll][mm].size();nn++){
                                        BaseStation p=blocks[ll][mm].get(nn);
                                        if(p.getX()==now.getX()&&p.getY()==now.getY()){
                                            around.add(p.rss);
                                        }else{
                                            if(assimulateExperiment.distance(p,now)<=len){
                                                around.add(p.rss);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //need to test

                        Collections.sort(around);
                        if(around.size()>0){
                            minRss=around.get(0);
                            maxRss=around.get(around.size()-1);
                            medRss=around.get(around.size()/2);
                        }
                        size+=1;
                    }
                    if(minRss<medRss&&medRss<maxRss){
                        if(minRss<now.rss&&now.rss<maxRss){
                            result.add(now);
                        }else{
                            BaseStation tem=new BaseStation(now.getX(),now.getY());
                            tem.rss=medRss;
                            result.add(tem);
                        }
                    }else{
                        result.add(now);
                    }

                }
            }
        }
        return result;
    }
    class ParsedData{
        public ParsedData(){
            dbscanData=new LinkedList<>();
            discreteData=new LinkedList<>();
        }
        List<BaseStation> dbscanData;
        List<BaseStation> discreteData;
    }
    public ParsedData getParsedData(BaseStation[] originBaseStations,List<BaseStation> dbscanPoints){
//        if(dbscanPoints==null||dbscanPoints.size()==0){
//            return null;
//        }
        BaseStation[] baseStations=new BaseStation[originBaseStations.length];
        for(int i=0;i<originBaseStations.length;i++){
            baseStations[i]=new BaseStation(originBaseStations[i].getX(),originBaseStations[i].getY(),originBaseStations[i].u);
            baseStations[i].rss=originBaseStations[i].rss;
            baseStations[i].group=Integer.MIN_VALUE;
        }
        BaseStation[] locList=deRepetition(baseStations);
//        List<BaseStation> points=dbscanParse(baseStations,blockSize);
        Map<BaseStation,BaseStation> rssMap=new HashMap<>();
        for(BaseStation data:locList){
            rssMap.put(data,data);
        }
        ParsedData parsedData=new ParsedData();
        for(BaseStation data:dbscanPoints){
            BaseStation origData=rssMap.get(data);
            if(origData!=null){
                origData.group=data.group;
                rssMap.remove(data);
                parsedData.dbscanData.add(origData);
            }
        }
        parsedData.discreteData.addAll(rssMap.values());
        return parsedData;
    }
    public BaseStation[] midValueDenoising(BaseStation[] originBaseStations,List<BaseStation> dbscanPoints,double blockSize){
//        double blockSize=0.8;
        ParsedData parsedData=getParsedData(originBaseStations,dbscanPoints);
        List<BaseStation> points=parsedData.dbscanData;
        int initSize=1;
        int maxSize=2;
        int begin=0;
        int end=-1;
        double maxX=Integer.MIN_VALUE+0.0;
        double minX=Integer.MAX_VALUE+0.0;
        double maxY=Integer.MIN_VALUE+0.0;
        double minY=Integer.MAX_VALUE+0.0;
        List<BaseStation> result=new LinkedList<>();
        for(int i=0;i<points.size();i++){
            BaseStation point=points.get(i);
            if(point.group==points.get(begin).group){
                if(maxX<point.getX()){
                    maxX=point.getX();
                }
                if(minX>point.getX()){
                    minX=point.getX();
                }
                if(maxY<point.getY()){
                    maxY=point.getY();
                }
                if(minY>point.getY()){
                    minY=point.getY();
                }
                end=i;
            }else{
                result.addAll(parseDomain(points,begin,end,maxX,minX,maxY,minY,initSize,maxSize,blockSize));
                maxX=point.getX();
                minX=point.getX();
                maxY=point.getY();
                minY=point.getY();
                begin=i;
                end=i;
            }
        }
        if(end>=begin){
            result.addAll(parseDomain(points,begin,end,maxX,minX,maxY,minY,initSize,maxSize,blockSize));
        }
        result.addAll(parsedData.discreteData);
        BaseStation[] res=new BaseStation[result.size()];
        return result.toArray(res);
    }
//    public List<BaseStation> addOriginalBaseStation(BaseStation [] baseStations,List<BaseStation> afterDenoising){
//
//    }

    public void computeResult(ComputeLocOptions options, BaseStation[] baseStations,
                              TargetLoc[] targetLocs,ComputeLoc computeLoc,double [][] allRss){
//        List<Double> locationResDifDenoising=new LinkedList<Double>();
        double [] locationResDifDenoising=new double[targetLocs.length];
        double [] locationResDif=new double[targetLocs.length];
        double armse=0.0,armseDenoising=0.0;
        System.out.println("computing");
        double dbscanBlockSize=5;
        List<BaseStation> dbscanPoints=dbscanParse(baseStations,dbscanBlockSize);

        for(int i=0;i<locationResDifDenoising.length;i++){
            locationResDifDenoising[i]=-1;
        }
        for(int i=0;i<targetLocs.length;i++){
            for(int j=0;j<baseStations.length;j++){
                baseStations[j].setRss(allRss[i][j]);
            }
            BaseStation[] afterDenoising=midValueDenoising(baseStations,dbscanPoints,dbscanBlockSize);
            double [] rss=null;
            if(afterDenoising!=null&&afterDenoising.length>0){
                locationResDifDenoising[i]=0;
                rss=new double[afterDenoising.length];
                for(int j=0;j<afterDenoising.length;j++){
                    rss[j]=afterDenoising[j].rss;
                }
            }
            for(int k=0;k<armseK;k++){
                Point res= computeLoc.getLoc(targetLocs[i],allRss[i],baseStations,baseStations,options,correlationCoefficient);
                locationResDif[i]=locationResDif[i]*k/(k+1.0)+Math.pow(distance(res,targetLocs[i]),2)/(k+1.0);

                if(rss!=null){
                    Point resDenoising= computeLoc.getLoc(targetLocs[i],rss,baseStations,afterDenoising,options,correlationCoefficient);
                    locationResDifDenoising[i]=locationResDifDenoising[i]*k/(k+1.0)+Math.pow(distance(resDenoising,targetLocs[i]),2)/(k+1.0);
//                    System.out.println("dist="+dist+"; resDenoising:x="+resDenoising.getX()+",y="+resDenoising.getY()+";targetLoc:x="+targetLocs[i].getX()+",y="+targetLocs[i].getY());
//                    System.out.println(".");
                }
            }
//            locationResDif[i]/=armseK;
            locationResDif[i]=Math.sqrt(locationResDif[i]);
//            locationResDifDenoising[i]/=armseK;
            locationResDifDenoising[i]=Math.sqrt(locationResDifDenoising[i]);
            System.out.println("locationResDif="+locationResDif[i]+" ; locationResDifDenoising[i]="+locationResDifDenoising[i]);
            System.out.println(".");
        }
        int denoisnum=0;
        for(int i=0;i<targetLocs.length;i++){
            armse=armse*i/(i+1.0)+locationResDif[i]/(i+1.0);
            if(locationResDifDenoising[i]!=-1){
                armseDenoising=armseDenoising*denoisnum/(denoisnum+1.0)+locationResDifDenoising[i]/(denoisnum+1.0);
                denoisnum++;
            }
        }
        System.out.println("armse="+armse+" ; armseDenoising="+armseDenoising);
    }
    public void assimulate(){
        System.out.println("begining base stations ...!");
        BaseStation[] baseStations= createBaseStationCluster();
        System.out.println("end base stations\nbegining TargetLoc ...!");
        TargetLoc[] targetLocs = createTargetLoc(baseStations);
        System.out.println("end TargetLoc!");


        Draw draw=new Draw("baseStation");
        draw.drawDistribution(baseStations);

        ComputeLocOptions options=new ComputeLocOptions();
        options.setMaxCoolingTime(1200);
        options.setMaxIterationTime(1000);
        options.setCoolingRate(0.99);
        options.setTemperature(100);
        options.setLength(0.25);

//        correlationCoefficient=new CorrelationCoefficientByOthers();
        correlationCoefficient=new CorrelationCoefficientByMyself();

        double [][] allRss=new double[this.targetNum][this.baseStationNum];
        for(int i=0;i<this.targetNum;i++){
            allRss[i]=getRss(targetLocs[i],baseStations);
        }
        long startTime = System.currentTimeMillis();
//        System.out.println("\n=========ByDivideToSmall============\n");
//        computeResult(options,baseStations,targetLocs,new ComputeLocByDivideToSmall(),allRss);
//        System.out.println((System.currentTimeMillis()-startTime)/1000);
        System.out.println("\n=========ByAssimulateAnnealing============\n");
        options.setLength(0.35);
        startTime = System.currentTimeMillis();
        computeResult(options,baseStations,targetLocs,new ComputeLocByAssimulateAnnealing(),allRss);
        System.out.println((System.currentTimeMillis()-startTime)/1000);
//        drawConvex(baseStations,targetLocs,allRss);

    }
    public void drawConvex(BaseStation[] baseStations,TargetLoc[] targetLocs,double [][] allRss){
        for(int j=0;j<targetLocs.length;j++){
            double [] rss=allRss[j];
            double minX=Integer.MAX_VALUE+0.0;
            double maxX=Integer.MIN_VALUE+0.0;
            double minY=Integer.MAX_VALUE+0.0;
            double maxY=Integer.MIN_VALUE+0.0;
            int divNum=4;
            for(int i=0;i<baseStations.length;i++){
                if(minX>baseStations[i].getX()){
                    minX=baseStations[i].getX();
                }else if(maxX<baseStations[i].getX()){
                    maxX=baseStations[i].getX();
                }

                if(minY>baseStations[i].getY()){
                    minY=baseStations[i].getY();
                }else if(maxY<baseStations[i].getY()){
                    maxY=baseStations[i].getY();
                }
            }
            double aveRss=0.0;
            double squareRss=0.0;
            for(int i=0;i<rss.length;i++){
                aveRss+=rss[i];
            }
            aveRss/=rss.length;
            for(int i=0;i<rss.length;i++){
                squareRss+=Math.pow((rss[i]-aveRss),2);
            }

            draw(aveRss,squareRss,baseStations,rss);

        }
    }
    public static void main(String[] args){
        assimulateExperiment experiment=new assimulateExperiment();
        experiment.setBlockSize(1);
        experiment.setWidth(400);
        experiment.setBaseStationNum(100);
        experiment.setTargetNum(5);
        experiment.setNlosMaxNum(80);
        experiment.setBeta(3.5);
        experiment.setuMax(10);
        experiment.setSigma(10);
        experiment.setP0(30);
        experiment.setArmseK(1);
        experiment.setDiscreteRatio(0.2);
        experiment.assimulate();
//        experiment.testConvex();
    }
}
