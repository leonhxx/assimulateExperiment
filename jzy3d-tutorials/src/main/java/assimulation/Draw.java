package assimulation;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.FastScatterPlot;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;

/**
 * Created by hongxiaoxiao on 17/1/3.
 */
public class Draw extends ApplicationFrame {
    public Draw(String title) {
        super(title);
    }

    public void drawDistribution(BaseStation[] baseStations){
        double maxX=Integer.MIN_VALUE+0.0;
        double minX=Integer.MAX_VALUE+0.0;
        double maxY=Integer.MIN_VALUE+0.0;
        double minY=Integer.MAX_VALUE+0.0;

        float [][] data=new float[2][baseStations.length];
        for(int i=0;i<baseStations.length;i++){
            data[0][i]=(float)baseStations[i].getX();
            data[1][i]=(float)baseStations[i].getY();
            if(maxX<baseStations[i].getX()){
                maxX=baseStations[i].getX();
            }
            if(minX>baseStations[i].getX()){
                minX=baseStations[i].getX();
            }
            if(maxY<baseStations[i].getY()){
                maxY=baseStations[i].getY();
            }
            if(minY>baseStations[i].getY()){
                minY=baseStations[i].getY();
            }
        }

        final NumberAxis domainAxis = new NumberAxis("X");
        domainAxis.setAutoRangeIncludesZero(false);
        final NumberAxis rangeAxis = new NumberAxis("Y");
        rangeAxis.setAutoRangeIncludesZero(false);
        final FastScatterPlot plot = new FastScatterPlot(data, domainAxis, rangeAxis);
        final JFreeChart chart = new JFreeChart("Fast Scatter Plot", plot);

        chart.getRenderingHints().put
                (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        final ChartPanel panel = new ChartPanel(chart, true);
        panel.setPreferredSize(new java.awt.Dimension(500, 500));
        //      panel.setHorizontalZoom(true);
        //    panel.setVerticalZoom(true);
        panel.setMinimumDrawHeight(10);
        panel.setMaximumDrawHeight(2000);
        panel.setMinimumDrawWidth(20);
        panel.setMaximumDrawWidth(2000);

        setContentPane(panel);

        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }
}
