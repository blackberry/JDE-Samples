/**
 * ConnectionDetailsScreen.java
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
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;

import net.rim.device.api.browser.field2.BrowserField;
import net.rim.device.api.browser.field2.BrowserFieldConfig;
import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A screen showing connection details: transport, URL and results of a
 * connection request. Clicking the "Render HTML" button shows the content of an
 * HTML response in a BrowserField. Clicking the "Render RAW" button shows the
 * content of a response as text.
 */
public final class ConnectionDetailsScreen extends MainScreen {
    // Displays text results from the server
    private final RichTextField _contentsField;

    // URL requested
    private final String _originalUrl;

    // Triggers rendering of results from the server in a BrowserField
    private final ButtonField _renderBtn;

    // Parses HTML results from the server and displays them like a browser
    private final BrowserField _browserField;

    private boolean _renderRaw;

    private final UiApplication _uiApp;

    /**
     * Creates a new ConnectionDetailsScreen object
     * 
     * @param connectionDescriptor
     *            The ConnectionDescriptor used to connect
     * @param originalUrl
     *            The original URL used to connect
     */
    public ConnectionDetailsScreen(
            final ConnectionDescriptor connectionDescriptor,
            final String originalUrl) {
        _originalUrl = originalUrl;

        setTitle("Connection Details");

        final BrowserFieldConfig browserFieldConfig = new BrowserFieldConfig();

        // Enable caret navigation mode
        browserFieldConfig.setProperty(BrowserFieldConfig.NAVIGATION_MODE,
                BrowserFieldConfig.NAVIGATION_MODE_NODE);

        // Disable JavaScript
        browserFieldConfig.setProperty(BrowserFieldConfig.JAVASCRIPT_ENABLED,
                Boolean.FALSE);

        _browserField = new BrowserField(browserFieldConfig);

        add(new SeparatorField());

        // Get the URL from the ConnectionDescriptor
        final String url = getUrl(connectionDescriptor);

        // Display transport and URL
        add(new LabelField("Transport: "
                + TransportInfo.getTransportTypeName(connectionDescriptor
                        .getTransportDescriptor().getTransportType())));
        add(new LabelField("Url: " + url));

        add(new SeparatorField());

        // Initialize button for displaying results in a browser field
        // or as raw HTML.
        _renderBtn =
                new ButtonField("Render HTML", ButtonField.NEVER_DIRTY
                        | Field.FIELD_HCENTER | ButtonField.CONSUME_CLICK);
        _renderBtn.setEnabled(false);
        _renderBtn.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (_renderRaw) {
                    renderRawContents();
                } else {
                    renderHtmlContents();
                }
            }
        });
        add(_renderBtn);

        add(new SeparatorField());

        // Display "Fetching content..." message below the buttons
        _contentsField =
                new RichTextField("Fetching content...", Field.NON_FOCUSABLE);
        add(_contentsField);

        add(new SeparatorField());

        // Start thread to read content
        final ContentReaderThread contentReader =
                new ContentReaderThread(url, connectionDescriptor);
        contentReader.start();

        _uiApp = UiApplication.getUiApplication();
    }

    /**
     * Displays a response received from a connection
     * 
     * @param content
     *            The String response of a connection
     */
    public void showContents(final String content) {
        _uiApp.invokeLater(new Runnable() {
            /**
             * @see Runnable#run()
             */
            public void run() {
                _contentsField.setText(content);
            }
        });
    }

    /**
     * Renders a response in a <code>BrowserField</code>
     */
    public void renderHtmlContents() {
        _uiApp.invokeLater(new Runnable() {
            /**
             * @see Runnable#run()
             */
            public void run() {
                // Remove text results from the screen
                delete(_contentsField);

                // Render HTML results
                _browserField.displayContent(_contentsField.getText(),
                        _originalUrl);
                add(_browserField);

                // Update button label from "Render HTML" to "Render RAW"
                _renderBtn.setLabel("Render RAW");
                _renderRaw = true;
            }
        });
    }

    /**
     * Renders a response in a RichTextField
     */
    public void renderRawContents() {
        _uiApp.invokeLater(new Runnable() {
            /**
             * @see Runnable#run()
             */
            public void run() {
                // Remove HTML results from the screen
                delete(_browserField);

                // Displays text results
                add(_contentsField);

                // Update button label from "Render RAW" to "Render HTML"
                _renderBtn.setLabel("Render HTML");
                _renderRaw = false;
            }
        });
    }

    /**
     * Retrieves URL from ConnectionDesciptor
     * 
     * @param connectionDescriptor
     *            The ConnectionDesciptor used to connect
     * @return URL String
     */
    private static String
            getUrl(final ConnectionDescriptor connectionDescriptor) {
        if (connectionDescriptor == null) {
            return null;
        }

        return connectionDescriptor.getUrl().toLowerCase();
    }

    /**
     * A Thread class which opens a connection to a given URL and reads the
     * server's results
     */
    private final class ContentReaderThread extends Thread {
        private final String _url;
        private final ConnectionDescriptor _connectionDescriptor;

        /**
         * Creates a new ContentReaderThread object
         * 
         * @param url
         *            The URL to connect to
         * @param connectionDescriptor
         *            Stores information about a <code>Connection</code>
         */
        ContentReaderThread(final String url,
                final ConnectionDescriptor connectionDescriptor) {
            _url = url;
            _connectionDescriptor = connectionDescriptor;
        }

        /**
         * @see Thread#run()
         */
        public void run() {
            String result = "";
            OutputStream os = null;
            InputStream is = null;
            final Connection connection = _connectionDescriptor.getConnection();

            try {
                // Check if URL starts with "socket", "tls", or "ssl" protocols.
                // In this case send a HTTP GET request before opening
                // InputStream
                if (_url.startsWith("socket://") || _url.startsWith("tls://")
                        || _url.startsWith("ssl://")) {
                    // Send HTTP GET to the server
                    final OutputConnection outputConn =
                            (OutputConnection) connection;
                    os = outputConn.openOutputStream();
                    final String getCommand =
                            "GET " + "/" + " HTTP/1.0\r\n\r\n";
                    os.write(getCommand.getBytes());
                    os.flush();
                }

                // Get InputConnection and read the server's response
                final InputConnection inputConn = (InputConnection) connection;
                is = inputConn.openInputStream();
                final byte[] data =
                        net.rim.device.api.io.IOUtilities.streamToBytes(is);
                result = new String(data);
                is.close();

                _renderBtn.setEnabled(true);
            } catch (final Exception e) {
                result = "ERROR fetching content: " + e.toString();
                _renderBtn.setEnabled(false);
            } finally {
                // Close OutputStream
                if (os != null) {
                    try {
                        os.close();
                    } catch (final IOException e) {
                    }
                }

                // Close InputStream
                if (is != null) {
                    try {
                        is.close();
                    } catch (final IOException e) {
                    }
                }

                // Close InputStream
                try {
                    connection.close();
                } catch (final IOException ioe) {
                }
            }

            // Show a response received from a connection or an error
            showContents(result);
        }
    }
}
