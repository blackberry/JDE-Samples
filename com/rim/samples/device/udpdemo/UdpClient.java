/*
 * UDPClient.java
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

package com.rim.samples.device.udpdemo;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.UDPDatagramConnection;

import net.rim.device.api.ui.UiApplication;

/**
 * This class represents the client in a client/server configuration
 */
public final class UDPClient extends Thread {
    private final String _msg;
    private final UdpDemoScreen _screen;
    private final UDPDemo _app;
    private UDPDatagramConnection _conn;

    /**
     * Creates a new UDPClient object
     * 
     * @param msg
     *            The message sent to the server
     */
    public UDPClient(final String msg) {
        _msg = msg;
        _app = (UDPDemo) UiApplication.getUiApplication();
        _screen = _app.getScreen();
    }

    /**
     * @see Thread#run()
     */
    public void run() {
        try {
            // Make a UDP(datagram) connection to the local loopback address.
            // Specify 2010 (the port the server listens on) as the destination
            // port and specify 3000 as the source port.
            _conn =
                    (UDPDatagramConnection) Connector
                            .open("datagram://127.0.0.1:2010;3000");

            // Convert the message to a byte array for sending.
            final byte[] bufOut = _msg.getBytes();

            // Create a datagram and send it across the connection.
            final Datagram outDatagram =
                    _conn.newDatagram(bufOut, bufOut.length);
            _conn.send(outDatagram);

            // Expect a response
            final byte[] bufIn = new byte[8];
            final Datagram inDatagram = _conn.newDatagram(bufIn, bufIn.length);
            _conn.receive(inDatagram);
            final String response = new String(inDatagram.getData());

            if (response.startsWith("RECEIVED")) {
                // Display the status message on the event thread.
                _app.invokeLater(new Runnable() {
                    public void run() {
                        _screen.updateStatus("Message received by server");
                    }
                });
            }
        } catch (final IOException ioe) {
            final String error = ioe.toString();

            // Display the error message on the event thread.
            _app.invokeLater(new Runnable() {
                public void run() {
                    _screen.updateStatus(error);
                }
            });
        } finally {
            try {
                if (_conn != null) {
                    // Close the connection
                    _conn.close();
                }
            } catch (final IOException ioe) {
            }
        }
    }
}
