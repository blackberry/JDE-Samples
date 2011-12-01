/**
 * SpeedAltitudePlot.java
 *
 * Copyright © 1998-2011 Research In Motion Limited
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Note: For the sake of simplicity, this sample application may not leverage
 * resource bundles and resource strings.  However, it is STRONGLY recommended
 * that application developers make use of the localization features available
 * within the BlackBerry development platform to ensure a seamless application
 * experience across a variety of languages and geographies.  For more information
 * on localizing your application, please refer to the BlackBerry Java Development
 * Environment Development Guide associated with this release.
 */

package com.rim.samples.server.gpsdemo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.imageio.ImageIO;


/**
 * <p>This class draws the graphs and plot on the server
 * It will write out three image files containing data compiled from the
 * client device. The Files are:
 * <ol>
 *   <li>Plot.jpg - contains an overhead plot of the route
 *   <li>Speed.jpg - contains the speed plots
 *   <li>Altitude.jpg - contains the altitude plots
 * </ol>
 * <p>The files are written to the current running directory (if invoked
 * from the default install, the directory will be the Samples
 * directory of the RIM JDE installation)
 */
public class SpeedAltitudePlot
{	
    static double highLatitude = -90.0;
    static double lowLatitude = 90.0;
    static double highLongitude = -180.0;
    static double lowLongitude = 180.0;
    static double totalDistance;
    
    private static final int PLOT_WIDTH = 1200;
    private static final int PLOT_HEIGHT = 1200;        

    public static void createCombinedChart(Collection c)
    {
        RenderedImage rendImage = drawPlot(c);
        File file = new File("Plot.jpg");
        try {
            ImageIO.write(rendImage, "jpg", file);
        } catch (IOException e) {
        e.printStackTrace();
        }  
    	
	rendImage = drawAltitudeGraph(c);
    	file = new File("Altitude.jpg");
    	try {
    	   ImageIO.write(rendImage, "jpg", file);
    	} catch (IOException e) {
    	   e.printStackTrace();
    	}  
        rendImage = drawSpeedGraph(c);
    	file = new File("Speed.jpg");
    	try {
    	   ImageIO.write(rendImage, "jpg", file);
    	} catch (IOException e) {
    	   e.printStackTrace();
    	} 	
    } 

    private static RenderedImage drawSpeedGraph(Collection c){
	Iterator it=c.iterator();
	double highSpeed=0;
	double lowSpeed=Double.MAX_VALUE;
	double totalDistance=0;
	
	// calculate the total distance covered
	while(it.hasNext()){
	   Point p = (Point)it.next();
	   if (p.speed > highSpeed) highSpeed = p.speed;
	   if (p.speed < lowSpeed) lowSpeed = p.speed;
	   totalDistance += p.distance;
	}	
	BufferedImage bufferedImage = new BufferedImage(PLOT_WIDTH, PLOT_HEIGHT, BufferedImage.TYPE_INT_RGB);
    
	Graphics2D g2d = bufferedImage.createGraphics();
	g2d.setColor(Color.white);
	g2d.fillRect(0, 0, PLOT_WIDTH, PLOT_HEIGHT);
	g2d.setColor(Color.black);
	g2d.drawLine(150,150,150,PLOT_HEIGHT-150);
	g2d.drawLine(150,PLOT_HEIGHT-150,PLOT_WIDTH-100,PLOT_HEIGHT-150);
	double speedDiff=highSpeed-lowSpeed;
	
	// draw the labels on the y axis
	for(int i=0;i<15;i++ ){
	   double yLabelValue=lowSpeed+i*speedDiff/14;
	   double y=(150+ (highSpeed-yLabelValue)*(PLOT_HEIGHT-400)/(speedDiff));
	   yLabelValue=round(yLabelValue,1);
	   g2d.drawString(String.valueOf(yLabelValue),50f,(float)y);	
	}
	
// draw the labels on the x axis
	for(int i=0;i<15;i++ ){
	   double XLabelValue=i*totalDistance/14;
	   int x=(int)(150+ XLabelValue*(PLOT_WIDTH-250)/totalDistance);
	   XLabelValue= round(XLabelValue,1);
	   g2d.drawString(String.valueOf(XLabelValue),(float)x,PLOT_HEIGHT-100);	
	}
	
	it=c.iterator();
	int previousX=0;
	int previousY=0;
	float distance=0;
	while(it.hasNext()){
	   Point p=(Point)it.next();
	   distance+=p.distance;
	   int newY=(int)(150+ (highSpeed-p.speed)*(PLOT_HEIGHT-400)/(speedDiff));
	   int newX=(int)(150+ distance*(PLOT_WIDTH-250)/totalDistance);
	   if(previousX!=0)g2d.drawLine(previousX,previousY,newX,newY);
	   previousX=newX;
	   previousY=newY;
	}
	g2d.setFont(new Font(null,Font.BOLD ,30));
	g2d.drawString("Distance",300,PLOT_HEIGHT-50);
	g2d.drawString("Speed",20,100);
	g2d.dispose();
    
	return bufferedImage; 	
	}
	
	
    private static RenderedImage drawAltitudeGraph(Collection c){
	Iterator it=c.iterator();
	double highAltitude=0;
	double lowAltitude=Double.MAX_VALUE;
	double totalDistance=0;
	// calculate the total distance
	while(it.hasNext()){
	   Point p = (Point)it.next();
	   if (p.altitude > highAltitude) highAltitude = p.altitude;
	   if (p.altitude < lowAltitude) lowAltitude = p.altitude;
	   totalDistance += p.distance;
	}	
	BufferedImage bufferedImage = new BufferedImage(PLOT_WIDTH, PLOT_HEIGHT, BufferedImage.TYPE_INT_RGB);
    
	Graphics2D g2d = bufferedImage.createGraphics();
	g2d.setColor(Color.white);
	g2d.fillRect(0, 0, PLOT_WIDTH, PLOT_HEIGHT);
	g2d.setColor(Color.black);
	g2d.drawLine(150,150,150,PLOT_HEIGHT-150);
	g2d.drawLine(150,PLOT_HEIGHT-150,PLOT_WIDTH-100,PLOT_HEIGHT-150);

	double altitudeDiff=highAltitude-lowAltitude;
	
	// draw the labels on the y axis
	for(int i=0;i<15;i++ ){
	   double yLabelValue=lowAltitude+i*altitudeDiff/14;
	   double y=(150+ (highAltitude-yLabelValue)*(PLOT_HEIGHT-400)/(altitudeDiff));
	   yLabelValue=round(yLabelValue,1);
	   g2d.drawString(String.valueOf(yLabelValue),50f,(float)y);	
	}
	
	// draw the labels on the x axis
	for(int i=0;i<15;i++ ){
	   double XLabelValue=i*totalDistance/14;
	   int x=(int)(150+ XLabelValue*(PLOT_WIDTH-250)/totalDistance);
	   XLabelValue= round(XLabelValue,1);
	   g2d.drawString(String.valueOf(XLabelValue),(float)x,PLOT_HEIGHT-100);	
	}
	
	it=c.iterator();

	int previousX=0;
	int previousY=0;
	float distance=0;
	while(it.hasNext()){
	   Point p=(Point)it.next();
	   distance+=p.distance;
	   int newY=(int)(150+ (highAltitude-p.altitude)*(PLOT_HEIGHT-400)/(altitudeDiff));
	   int newX=(int)(150+ distance*(PLOT_WIDTH-250)/totalDistance);
	   if(previousX!=0)g2d.drawLine(previousX,previousY,newX,newY);
	   previousX=newX;
	   previousY=newY;
	}
	g2d.setFont(new Font(null,Font.BOLD ,30));
	g2d.drawString("Distance",300,PLOT_HEIGHT-50);
	g2d.drawString("Altitude",20,100);
	
	g2d.dispose();
    
	return bufferedImage;	
    }    
    
    private static double round(double d, int decimal) 
	{
	   double powerOfTen = 1;
	   while (decimal-- > 0)
	   {
		powerOfTen *= 10.0;
	   }
	   double d1 = d * powerOfTen;
	   int d1asint = (int)d1; //clip the decimal portion away and cache the cast, this is a costly transformation
	   double d2 = d1 - d1asint; //get the remainder of the double
		//is the remainder > 0.5? if so, round up, otherwise round down (lump in .5 with > case for simplicity)
	   return ( d2 >= 0.5 ? (d1asint + 1)/powerOfTen : (d1asint)/powerOfTen);
	}
	
    private static RenderedImage drawPlot(Collection c)
    {
	Iterator it=c.iterator();
	totalDistance=0;
	while(it.hasNext()){
	   Point p = (Point)it.next();
	   totalDistance += p.distance;
	   if (p.latitude > highLatitude) highLatitude = p.latitude;
	   if (p.latitude < lowLatitude) lowLatitude = p.latitude;
	   if (p.longitude > highLongitude) highLongitude = p.longitude;
	   if (p.longitude < lowLongitude) lowLongitude = p.longitude;
	}    	
    	
        double lonDiff = highLongitude - lowLongitude;
        double latDiff = highLatitude - lowLatitude;
        if (lonDiff > latDiff) latDiff = lonDiff;
        else lonDiff = latDiff;
    	
        double x;
        double y;
        double oldX = -200;
        double oldY = -200;
    	
        int x1;
        int y1;
        int oldX1;
        int oldY1;    	
    
        BufferedImage bufferedImage = new BufferedImage(PLOT_WIDTH, PLOT_HEIGHT, BufferedImage.TYPE_INT_RGB);
    
        Graphics2D g2d = bufferedImage.createGraphics();
    
    	// Draw graphics
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, PLOT_WIDTH, PLOT_HEIGHT);
        g2d.setColor(Color.black);
        g2d.setFont(new Font(null,Font.BOLD ,40));
        g2d.drawString("Path", PLOT_WIDTH/2-50, PLOT_HEIGHT-50);
        g2d.drawString("N", 75, PLOT_HEIGHT-125);
        g2d.drawString("S", 75, PLOT_HEIGHT-20);
        g2d.drawString("E", 135, PLOT_HEIGHT-75);
        g2d.drawString("W", 5, PLOT_HEIGHT-75);
        
        it = c.iterator();
        while (it.hasNext()) 
        {
            Point p = (Point)it.next();
            x = p.longitude;
            y = p.latitude;
            if(oldX > -200 && oldY > -200){
            	x1 = (int)((x - lowLongitude) * PLOT_WIDTH/lonDiff);
            	y1 = PLOT_HEIGHT - (int)((y - lowLatitude) * PLOT_HEIGHT/latDiff);
            	oldX1 = (int)((oldX - lowLongitude) * PLOT_WIDTH/lonDiff);
            	oldY1 = PLOT_HEIGHT - (int)((oldY - lowLatitude) * PLOT_HEIGHT/latDiff);
            	g2d.drawLine(x1, y1, oldX1, oldY1);
            }
            oldX = x;
            oldY = y;        
        }
        
        // Graphics context no longer needed so dispose it
    	g2d.dispose();
    
    	return bufferedImage;    	
    }    
}
