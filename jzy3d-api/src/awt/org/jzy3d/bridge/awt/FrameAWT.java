package org.jzy3d.bridge.awt;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.jzy3d.bridge.IFrame;
import org.jzy3d.chart.Chart;
import org.jzy3d.maths.Rectangle;

public class FrameAWT extends java.awt.Frame implements IFrame {
	
	// public constructor for easier construction by reflexion
	public FrameAWT() {	
	}
	
	public FrameAWT(Chart chart, Rectangle bounds, String title) {
		initialize(chart, bounds, title);
	}

	public FrameAWT(Chart chart, Rectangle bounds, String title, String message) {
		initialize(chart, bounds, title, message);
	}
	
	@Override
	public void initialize(Chart chart, Rectangle bounds, String title) {
		initialize(chart, bounds, title, "[Awt]");
	}
	
	@Override
    public void initialize(Chart chart, Rectangle bounds, String title, String message) {
		this.chart = chart;
		if(message!=null){
	        this.setTitle(title + message);		    
		}
		else{
            this.setTitle(title);         
        }
		this.add((java.awt.Component) chart.getCanvas());
		this.pack();
		this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter() {
			@Override
            public void windowClosing(WindowEvent e) {
				FrameAWT.this.remove((java.awt.Component) FrameAWT.this.chart
						.getCanvas());
				FrameAWT.this.chart.dispose();
				FrameAWT.this.chart = null;
				FrameAWT.this.dispose();
			}
		});
	}

	private Chart chart;
	private static final long serialVersionUID = 1L;


}
