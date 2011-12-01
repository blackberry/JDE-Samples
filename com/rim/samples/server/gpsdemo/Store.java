/**
 * Store.java
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

import java.io.*;
import java.util.*;

/**
 * Store save the gps information sent by the device to a file for drawing the graphs and plots
 * The file is called data.txt and lives in the working directory, typically SDK/samples.
 */
public class Store {
    
    private final String _filename = "data.txt";
    /*package*/ TreeMap _map; //referenced by GPSServer
    
    public Store()
    {
    	_map = new TreeMap();
    	loadData();
    }
	
    private void loadData()
    {
    	long time = 0;
    	double latitude = 0.0;
    	double longitude = 0.0;
    	double altitude = 0.0;
    	double distance = 0.0;
    	double speed = 0.0;
    	    	
    	try {
            BufferedReader in = new BufferedReader(new FileReader(_filename));
            String str;
            while ((str = in.readLine()) != null) 
            {
            	StringTokenizer tokenizer = new StringTokenizer(str,";");
            	longitude = Double.parseDouble(tokenizer.nextToken());
            	latitude = Double.parseDouble(tokenizer.nextToken());
            	altitude = Double.parseDouble(tokenizer.nextToken());
            	distance = Double.parseDouble(tokenizer.nextToken());
            	speed = Double.parseDouble(tokenizer.nextToken());
            	time = Long.parseLong(tokenizer.nextToken());
            	_map.put(new Long(time), new Point(time, latitude, longitude, altitude, distance, speed));
            }            
            in.close();
    	} catch (IOException e) {
    		System.err.println(e);
    	}
    }
	
    public void save(Vector points){

    	try {
            PrintWriter out = new PrintWriter(new FileOutputStream(_filename, true));
            Iterator it = points.iterator();
            while(it.hasNext())
            {
            	out.println((String)it.next()); 
            }            
            out.close();
    	} catch (IOException e) {
            System.err.println(e);
    	}
    }
}
	
/**
 * A simple class for dealing with a single location
 */    
/*package*/ class Point{
    long time;
    double latitude;
    double longitude;
    double altitude;
    double distance;
    double speed;
    
    public Point(long time, double latitude, double longitude, double altitude, double distance, double speed)
    {
    	this.time = time;
    	this.latitude = latitude;
    	this.longitude = longitude;
    	this.altitude = altitude;
    	this.distance = distance;
    	this.speed = speed;    				
    }
}
