/*
 * UdpServer.java
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

package com.rim.samples.server.udpdemo;

import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * This class represents the server in a client/server configuration
 */
public class UdpServer implements Runnable
{
    final static int BROADCAST_PORT = 2010;

    /**
     * Entry point for application.
     */
    public static void main(String args[])
    {
        new UdpServer().run();
    }


    public void run()
    {

        System.out.println("               -----------------UDP Demo Server-----------------" + "\n\n");

        while(true)
        {
            try
            {
                // Create a new socket connection bound to the defined port
                DatagramSocket sock = new DatagramSocket(BROADCAST_PORT);

                System.out.println("Waiting for data on local port: " + sock.getLocalPort());

                // Create a packet to contain incoming data
                byte[] buf = new byte[256];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);

                // Wait for incoming data (receive() is a blocking method)     
                sock.receive(packet);

                // Retrieve data from packet and display
                String data = new String(packet.getData());
                int endIndex = data.indexOf(0);
                data = data.substring(0, endIndex);
                int remotePort = packet.getPort();
                System.out.println("Received data from remote port " + remotePort + ":\n" + data);

                // Determine origin of packet and display information
                InetAddress remoteAddress = packet.getAddress();
                System.out.println("Sent from address: " + remoteAddress.getHostAddress());

                // Send back an acknowledgment
                String ack = "RECEIVED " + new Date().toString();
                sock.send(new DatagramPacket(ack.getBytes(), ack.length(), remoteAddress, remotePort));

                sock.close();
            }
            catch(IOException ioe)
            {
                System.out.println("Error: IOException - " + ioe.toString());
            }
        }
    }
}
