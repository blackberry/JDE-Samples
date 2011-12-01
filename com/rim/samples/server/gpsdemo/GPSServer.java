/**
 * GPSServer.java
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

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * <p>The main server class
 * <p>To run this simple program, invoke the following from the command line:
 *  java  com.rim.samples.server.gspdemo.GPSServer
 */
public class GPSServer 
{
    private static final int PORT = 5555;    
    private static Store _store;

    public static void main(String[] args)
    {
    	_store = new Store();
    	GPSServer app = new GPSServer();
    }

    public GPSServer() 
    {    
    	ServerSocket serverSocket = null;
    	try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("GPSServer.started");
    	} catch (IOException e) {
            System.err.println(e + "- port:" + PORT);
            System.exit(-1);
    	}
    
    	for(;;)
    	{
            Socket clientSocket = null;
            try {
            	System.out.println("GPSServer.WaitingForClient");
            	clientSocket = serverSocket.accept(); //blocking call - waits for a connection
            	System.out.println("GPSServer.ClientConnecting" + ":" + clientSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
            	System.err.println(e);
            	System.exit(-1);
            }
            //spin off a new thread to handle this socket - this way new socket connections can be served immediately
            (new Worker(clientSocket, _store)).start();
    	}
    }

    /**
     * <p>Worker is just a simple thread that handles each inbound socket connection
     * so that further socket connections can be accepted while another socket is handled!
     */
    /*package*/ class Worker extends Thread
    {
        private Socket _clientSocket;
        private static final String RECEIVED = "Received";
        
        private double longitude;
        private double latitude;
        private double altitude;
        private double distance;
        private long time;
        private double speed;
        private String returnString;
        private StringBuffer receiveBuffer;
        private Store store;
    
        public Worker(Socket c,Store s)
        {
            _clientSocket = c;
            receiveBuffer = new StringBuffer();
            store = s;       
        }
            
        public void run()
        {
            PrintWriter printWriter;
            InputStream inputStream;
            Vector v = null;		
            try {
                try {
                    _clientSocket.setSoTimeout(0); //wait forever
                	
                    inputStream = _clientSocket.getInputStream();
                    printWriter = new PrintWriter(_clientSocket.getOutputStream(), true);
                	
                    int i = -1;
                    while (( i = inputStream.read()) != 'z') //'z' is the terminator
                    {
                        receiveBuffer.append((char)i);
                    }
                } catch (SocketTimeoutException ste){
                    System.out.println(_clientSocket.getPort() + " Timeout waiting for Hello");
                    return;            
                }
                
                returnString = RECEIVED;
                String data = receiveBuffer.toString();
                try {
                    StringTokenizer st = new StringTokenizer(data, ":");
                    v = new Vector();
                    while(st.hasMoreTokens())
                    {
                        String point = st.nextToken();
                        StringTokenizer st1 = new StringTokenizer(point, ";");
                        longitude = Float.parseFloat(st1.nextToken());
                        latitude = Float.parseFloat(st1.nextToken());
                        altitude = Float.parseFloat(st1.nextToken());
                        distance = Float.parseFloat(st1.nextToken());
                        speed = Float.parseFloat(st1.nextToken());
                        time = Long.parseLong(st1.nextToken());
                        Point p = new Point(time,latitude, longitude, altitude, distance, speed);
                        store._map.put(new Long(time), p);
                        v.addElement(point);
                    }
                } catch(NoSuchElementException e) {
                	returnString = "Error";
                } catch(NumberFormatException e) {
                	returnString = "Error";
                }
                         
                printWriter.print(returnString);
                printWriter.flush();
                inputStream.close();
                printWriter.close();
                
                _clientSocket.close();
                GPSServer._store.save(v);  
                SpeedAltitudePlot.createCombinedChart(store._map.values());
                System.out.println(_clientSocket.getPort() + " Done");
            } catch(IOException ie) {
                System.out.println("Exception:"+ie);
            }
        }
    }
}


