/*
 * Copyright (C) 2014 Artitelly Inc,
 *
 * Licensed under the Common Public Attribution License Version 1.0 (CPAL-1.0) (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/CPAL-1.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.testmax.framework;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class CreateGraph {
    
   protected TimeSeries ts = new TimeSeries("data", Millisecond.class); 
   
    private String graphName=null;
    
    private String xName=null;
    
    private String yName=null;
    
    private JFreeChart chart=null;
    
    public CreateGraph(String Gname, String Xname, String Yname){
        this.graphName=Gname;
        this.xName=Xname;
        this.yName=Yname;
        
    }
    
    /*
     *  Create a Chart
     *  @Gname = Name of the Graph
     *  @Xname= X axis caption
     *  @Yname= Y axis caption
     */
    protected void createChart(double timeout){
    	
        TimeSeriesCollection dataset = new TimeSeriesCollection(ts); 
        this.chart = ChartFactory.createTimeSeriesChart( 
        this.graphName, 
        this.xName, 
        this.yName, 
        dataset, 
        true, 
        true, 
        false 
        ); 
        final XYPlot plot = chart.getXYPlot(); 
        ValueAxis axis = plot.getDomainAxis(); 
        axis.setAutoRange(true); 
        axis.setFixedAutoRange(timeout * 1000.0); 

        JFrame frame = new JFrame(this.graphName); 
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        ChartPanel label = new ChartPanel(chart); 
        frame.getContentPane().add(label); 
        //Suppose I add combo boxes and buttons here later 

        frame.pack(); 
        if(ConfigLoader.getConfig("SHOW_GRAPH_RUNTIME").equalsIgnoreCase("yes")){
        	frame.setVisible(true); 
        }else{
        	frame.setVisible(false); 
        }
    }
    
    public JFreeChart getChart(){
    	return(this.chart);
    }

}
