/**
 * A simple socket server application to demonstrate the socket capabilities of the system
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
package com.rim.samples.server.socketdemo;

import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

/**
 * <p>Worker is just a simple thread that handles each inbound socket connection.
 * so that further socket connections can be accepted while another socket is handled!
 */
class Worker extends Thread
{
    private Socket _clientSocket;

    private static final String HELLO = "Hello";
    private static final String GOODBYE = "Goodbye and farewell";
    private static final String RESOURCE_PATH = "com/rim/samples/server/socketdemo/resources";
    private static ResourceBundle _resources = java.util.ResourceBundle.getBundle(RESOURCE_PATH);


    public Worker(Socket c)
    {
        _clientSocket = c;
    }

    public void run()
    {
        byte[] b = new byte[80];

        try {
            // 20 second timeout
            _clientSocket.setSoTimeout(20000);
            BufferedInputStream _in = new BufferedInputStream(_clientSocket.getInputStream());
            OutputStreamWriter _out = new OutputStreamWriter(_clientSocket.getOutputStream());

            StringBuffer receiveBuffer = new StringBuffer();
            int totalBytesReadSoFar = 0;
            int bytesReadThisIteration = 0;
            int numBytesToRead = HELLO.length();
            
            //Wait for the HELLO string.  Loops until it receives the necessary amount of bytes, and stores the accumulated bytes
            // in receiveBuffer  
            while (totalBytesReadSoFar < numBytesToRead)
            {
	            try
	            {
	            	
	                bytesReadThisIteration = _in.read(b, 0, 80);
	                receiveBuffer.append(new String(b, 0, bytesReadThisIteration));
	                totalBytesReadSoFar += bytesReadThisIteration;
	            }
	            catch (SocketTimeoutException ste)
	            {
	                System.out.println(_clientSocket.getPort() + " Timeout waiting for Hello");
	                return;
	            }
            }

	    // check to see if we've read the proper amount of data, and that the data matches the HELLO string
            if ((totalBytesReadSoFar > 0 && totalBytesReadSoFar <= numBytesToRead) && receiveBuffer.toString().equals(HELLO))
            {
                System.out.println(_clientSocket.getPort() +" Received: " + receiveBuffer);
            }
            else
            {
            	System.out.println(_clientSocket.getPort() +" Received an invalid response of: " + receiveBuffer);	
            	return;
            }

            System.out.println(_clientSocket.getPort() + " Send Hello");
            _out.write(HELLO);
            _out.flush();

            System.out.println(_clientSocket.getPort() + " Now, wait for the Goodbye");
            
            receiveBuffer.setLength(0);
            totalBytesReadSoFar = 0;
            numBytesToRead = GOODBYE.length();
            
            // wait for the GOODBYE string.  Loops until it receives the necessary amount of bytes, and stores the accumulated bytes
            // in receiveBuffer  
            while (totalBytesReadSoFar < numBytesToRead)
            {
	            try
	            {
	                bytesReadThisIteration = _in.read(b, 0, 80);
	                receiveBuffer.append(new String(b, 0, bytesReadThisIteration));
	                totalBytesReadSoFar += bytesReadThisIteration;
	            }
	            catch (SocketTimeoutException ste)
	            {
	                System.out.println(_clientSocket.getPort() + " Timeout waiting for Goodbye");
	                return;
	            }
	     }
	     
	     // check to see if we've read the proper amount of data, and that the data matches the GOODBYE string
	     if ((totalBytesReadSoFar > 0 && totalBytesReadSoFar <= numBytesToRead) && receiveBuffer.toString().equals(GOODBYE))
	     {
	            System.out.println(_clientSocket.getPort() + " Received: " + receiveBuffer);
	     } 	
	     else
	     {
	            System.out.println(_clientSocket.getPort() +" Received an invalid response of: " + receiveBuffer);
	            return;
	     }
        

            _out.write(GOODBYE);
            _out.flush();

            _out.close();
            _in.close();
            _out = null;
            _in = null;
            _clientSocket.close();
            System.out.println(_clientSocket.getPort() + " Done");
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}

/**
 * <p>The main server class
 * <p>To run this simple program, invoke the following from the command line:
 * <pre>
 *    javac SimpleSocketServer.java
 *    pushd ..\..\..\..\..
 *     java com.rim.samples.server.socketdemo.SimpleSocketServer
 *    popd
 * </pre>
 */
public class SimpleSocketServer implements Runnable {

    //constants ---------------------------------------------------------------
    private static final int PORT = 4444;
    //statics -----------------------------------------------------------------
    private static final String RESOURCE_PATH = "com/rim/samples/server/socketdemo/resources";
    private static ResourceBundle _resources = java.util.ResourceBundle.getBundle(RESOURCE_PATH);

    public static void main(String[] args)
    {
        new SimpleSocketServer().run();
    }

    public void run() {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(PORT);
	    System.out.println(_resources.getString("SimpleSocketServer.Started"));
        } catch (IOException e) {
            System.err.println(e + "- port:" + PORT);
            System.exit(-1);
        }

        for(;;)
        {
            Socket clientSocket = null;
            try {
                System.out.println(_resources.getString("SimpleSocketServer.WaitingForClient"));
                clientSocket = serverSocket.accept(); //blocking call - waits for a connection
                System.out.println(_resources.getString("SimpleSocketServer.ClientConnecting") + ":" + clientSocket.getInetAddress().getHostAddress());
            } catch (IOException e) {
                System.err.println(e);
                System.exit(-1);
            }
            //spin off a new thread to handle this socket - this way new socket connections can be served immediately
            (new Worker(clientSocket)).start();
        }
    }

}

