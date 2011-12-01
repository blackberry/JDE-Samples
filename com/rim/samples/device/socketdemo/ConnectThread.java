/*
 * ConnectThread.java
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

package com.rim.samples.device.socketdemo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import net.rim.device.api.ui.UiApplication;

/**
 * A thread class to handle communication with the server component.
 */
public class ConnectThread extends Thread {
    private InputStream _in;
    private OutputStreamWriter _out;
    private final SocketDemoScreen _screen;

    /**
     * Creates a new ConnectThread object
     */
    public ConnectThread() {
        _screen = ((SocketDemo) UiApplication.getUiApplication()).getScreen();
    }

    /**
     * Pass some data to the server and wait for a response.
     * 
     * @param data
     *            The data to send.
     */
    private void exchange(final String data) throws IOException {
        // Cache the length locally for better efficiency.
        final int length = data.length();

        // Create an input array just big enough to hold the data
        // (we're expecting the same string back that we send).
        final char[] input = new char[length];
        _out.write(data, 0, length);

        // Read character by character into the input array.
        for (int i = 0; i < length; ++i) {
            input[i] = (char) _in.read();
        }

        // Hand the data to the parent class for updating the GUI. By explicitly
        // creating the stringbuffer we can save a few object creations.
        final StringBuffer s = new StringBuffer();
        s.append("Received: ");
        s.append(input, 0, length);
        _screen.updateDisplay(s.toString());
    }

    /**
     * Implementation of Thread.
     */
    public void run() {
        StreamConnection connection = null;

        try {
            _screen.updateDisplay("Opening Connection...");
            final String url =
                    "socket://" + _screen.getHostFieldText() + ":44444"
                            + (_screen.isDirectTCP() ? ";deviceside=true" : "");
            connection = (StreamConnection) Connector.open(url);
            _screen.updateDisplay("Connection open");

            _in = connection.openInputStream();

            _out = new OutputStreamWriter(connection.openOutputStream());

            // Send the HELLO string.
            exchange("Hello");

            // Execute further data exchange here...

            // Send the GOODBYE string.
            exchange("Goodbye and farewell");

            _screen.updateDisplay("Done!");
        } catch (final IOException e) {
            System.err.println(e.toString());
        } finally {
            _screen.setThreadRunning(false);

            try {
                _in.close();
            } catch (final IOException ioe) {
            }
            try {
                _out.close();
            } catch (final IOException ioe) {
            }
            try {
                connection.close();
            } catch (final IOException ioe) {
            }
        }
    }
}
