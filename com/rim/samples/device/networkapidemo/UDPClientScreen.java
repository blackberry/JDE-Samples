/**
 * UDPClientScreen.java
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

package com.rim.samples.device.networkapidemo;

import java.io.IOException;

import javax.microedition.io.Datagram;
import javax.microedition.io.UDPDatagramConnection;

import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This screen acts as a UDP client which can get messages from a UDP server.
 * The server component is started by executing run.bat in the
 * com.rim.samples.server.udpdemo directory.
 */
public final class UDPClientScreen extends MainScreen implements
        FieldChangeListener {
    private static final int SERVER_PORT = 2010;

    // The first part of the URL
    private final String _domain = "datagram://localhost:";

    // Field for URLs
    private final BasicEditField _urlField;

    // Button that sends a request using wifi
    private final ButtonField _wifiButton;

    // Button that sends a request using cellullar
    private final ButtonField _cellButton;

    // Field for displaying data
    private final RichTextField _rtfDisplay;

    /**
     * Creates a new UDPClientScreen object
     */
    public UDPClientScreen() {
        // Initialize UI components --------------------------------------------
        setTitle("UDP Client");

        _urlField =
                new BasicEditField("URL:  ", _domain + SERVER_PORT, 128,
                        BasicEditField.FILTER_URL);

        final HorizontalFieldManager hfmButtons = new HorizontalFieldManager();
        _wifiButton =
                new ButtonField("TCP WiFi", Field.FIELD_HCENTER
                        | ButtonField.CONSUME_CLICK);
        _wifiButton.setChangeListener(this);
        _cellButton =
                new ButtonField("TCP Cellular", Field.FIELD_HCENTER
                        | ButtonField.CONSUME_CLICK);
        _cellButton.setChangeListener(this);
        hfmButtons.add(_wifiButton);
        hfmButtons.add(_cellButton);

        _rtfDisplay = new RichTextField("Click a button");

        // Add components to screen --------------------------------------------
        add(_urlField);
        add(new SeparatorField());

        add(hfmButtons);
        add(_rtfDisplay);
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        _rtfDisplay.setText("");

        int transportType = 0;

        if (field == _wifiButton) {
            transportType = TransportInfo.TRANSPORT_TCP_WIFI;

        } else if (field == _cellButton) {
            transportType = TransportInfo.TRANSPORT_TCP_CELLULAR;
        }

        // Check to see if transport is available
        if (!TransportInfo.isTransportTypeAvailable(transportType)) {
            final String connectionName =
                    TransportInfo.getTransportTypeName(transportType);
            Dialog.inform(connectionName + " not available.");
            _rtfDisplay.setText(connectionName + " Connection Failed!");
        } else {
            final String url = _urlField.getText().trim();

            final ConnectThread connectThread =
                    new ConnectThread(transportType, url, null);
            connectThread.start();
        }
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Supress the "Save" dialog
        return true;
    }

    /**
     * Thread to handle connections
     */
    private final class ConnectThread extends Thread {
        private final int _connectionType;
        private final String _connectionName;
        private final String _url;
        private final String _connectionUID;

        /**
         * Creates a new ConnectThread object
         * 
         * @param connectionType
         *            Transport type, WIFI or cellular
         * @param url
         *            The URL to connect to
         * @param connectionUID
         *            The UID of the <code>ServiceRecord</code> for a specific
         *            transport instance
         */
        public ConnectThread(final int connectionType, final String url,
                final String connectionUID) {
            _connectionType = connectionType;
            _url = url;
            _connectionUID = connectionUID;
            _connectionName =
                    TransportInfo.getTransportTypeName(_connectionType);
        }

        /**
         * @see Thread#run()
         */
        public void run() {
            // Create a ConnectionFactory
            final ConnectionFactory factory = new ConnectionFactory();

            // Use the factory to get a connection
            final ConnectionDescriptor connectionDescriptor =
                    factory.getConnection(_url, _connectionType, _connectionUID);

            if (connectionDescriptor != null) {
                // Connection succeeded
                final int transportUsed =
                        connectionDescriptor.getTransportDescriptor()
                                .getTransportType();
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    /**
                     * @see Runnable#run()
                     */
                    public void run() {
                        Status.show("Connection succeeded using transport ID: "
                                + transportUsed, 2000);
                    }
                });

                final UDPDatagramConnection udpCon =
                        (UDPDatagramConnection) connectionDescriptor
                                .getConnection();
                try {
                    final byte[] buf = new byte[256];

                    // Send request
                    Datagram packet = udpCon.newDatagram(buf, buf.length);
                    udpCon.send(packet);

                    // Get response
                    packet = udpCon.newDatagram(buf, buf.length);
                    udpCon.receive(packet);

                    // Display response
                    final String message =
                            new String(packet.getData(), 0, packet.getLength());
                    displayMessage(_connectionName + " " + message);
                } catch (final IOException ioe) {
                    displayMessage(ioe.toString());
                } finally {
                    if (udpCon != null) {
                        try {
                            udpCon.close();
                        } catch (final IOException e) {
                        }
                    }
                }
            } else {
                displayMessage(_connectionName + " Connection Failed!");
            }
        }
    }

    /**
     * Displays a message on the screen
     * 
     * @param msg
     *            The message to display
     */
    private void displayMessage(final String msg) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            /**
             * @see Runnable#run()
             */
            public void run() {
                _rtfDisplay.setText(msg);
            }
        });
    }
}
