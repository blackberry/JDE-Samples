/**
 * HTTPDemo.java
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

package com.rim.samples.device.httpdemo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A simple example using the HTTP connection.
 */
public class HTTPDemo extends UiApplication {
    // Constants
    // ----------------------------------------------------------------
    private static String SAMPLE_HTTPS_PAGE =
            "https://www.blackberry.com/go/mobile/samplehttps.shtml";
    private static final String[] HTTP_PROTOCOL = { "http://", "http:\\",
            "https://", "https:\\" };
    private static final char HTML_TAG_OPEN = '<';
    private static final char HTML_TAG_CLOSE = '>';
    private static String HEADER_CONTENTTYPE = "content-type";
    private static String CONTENTTYPE_TEXTHTML = "text/html";

    private static final int STATE_0 = 0;
    private static final int STATE_1 = 1;
    private static final int STATE_2 = 2;
    private static final int STATE_3 = 3;
    private static final int STATE_4 = 4;
    private static final int STATE_5 = 5;

    private static final char CR = 0x000D;
    private static final char LF = 0x000A;

    // Members
    // ------------------------------------------------------------------
    private final HTTPDemoScreen _mainScreen;
    private final EditField _url;
    private final RichTextField _content;
    private boolean _useWapStack;
    private final WapOptionsScreen _wapOptionsScreen; // cache for reuse

    // Cache the fetch menu item for reuse.
    private final MenuItem _fetchMenuItem = new MenuItem("Fetch", 100, 10) {
        public void run() {
            // Don't execute on a blank url.
            if (_url.getText().length() > 0) {
                if (!_connectionThread.isStarted()) {
                    fetchPage(_url.getText());
                } else {
                    Dialog.alert("An outstanding fetch request hasn't yet completed!");
                }
            }
        }
    };

    // Cache the clear content menu item for reuse.
    private final MenuItem _clearContent = new MenuItem("Clear Content", 105,
            10) {
        public void run() {
            _content.setText("<content>");
        }
    };

    // Cache the https menu item for reuse.
    private final MenuItem _fetchTTPSPage = new MenuItem(
            "Fetch sample HTTPS Page", 110, 10) {
        public void run() {
            if (!_connectionThread.isStarted()) {
                // Menu items are executed on the event thread, therefore we can
                // edit the
                // URL field in place.
                _url.setText(SAMPLE_HTTPS_PAGE);
                fetchPage(SAMPLE_HTTPS_PAGE);
            } else {
                Dialog.alert("An outstanding fetch request hasn't yet completed!");
            }
        }
    };

    // Cache the wap stack toggle for reuse.
    private final MenuItem _wapStackOption = new MenuItem("Use Wap Stack", 115,
            10) {
        public void run() {
            _useWapStack = !_useWapStack; // Toggle the wap stack option.
        }
    };

    // Cache the wap stack options screen invocation item for reuse.
    private final MenuItem _wapStackOptionScreen = new MenuItem("Wap Options",
            120, 10) {
        public void run() {
            _wapOptionsScreen.display();
        }
    };

    StatusThread _statusThread = new StatusThread();
    ConnectionThread _connectionThread = new ConnectionThread();

    public static void main(final String[] args) {
        final HTTPDemo theApp = new HTTPDemo();
        theApp.enterEventDispatcher();
    }

    // Inner Classes
    // -------------------------------------------------------------

    /**
     * The ConnectionThread class manages the HTTP connection. Fetch operations
     * are not queued, however, if a fetch call is made and, while active,
     * another request made, the 2nd request will stall until the previous
     * request completes.
     */
    private class ConnectionThread extends Thread {
        private static final int TIMEOUT = 500; // ms

        private String _theUrl;

        private volatile boolean _start = false;
        private volatile boolean _stop = false;

        // Retrieve the URL.
        public synchronized String getUrl() {
            return _theUrl;
        }

        public boolean isStarted() {
            return _start;
        }

        // Fetch a page.
        // Synchronized so that I don't miss requests.
        public void fetch(final String url) {
            synchronized (this) {
                _start = true;
                _theUrl = url;
            }
        }

        // Shutdown the thread.
        public void stop() {
            _stop = true;
        }

        public void run() {
            for (;;) {
                // Thread control.
                while (!_start && !_stop) {
                    // Sleep for a bit so we don't spin.
                    try {
                        sleep(TIMEOUT);
                    } catch (final InterruptedException e) {
                        System.err.println(e.toString());
                    }
                }

                // Exit condition.
                if (_stop) {
                    return;
                }

                // This entire block is synchronized, this ensures I won't miss
                // fetch requests
                // made while I process a page.
                synchronized (this) {
                    // Open the connection and extract the data.
                    StreamConnection s = null;

                    try {
                        s = (StreamConnection) Connector.open(getUrl());
                        final HttpConnection httpConn = (HttpConnection) s;

                        final int status = httpConn.getResponseCode();

                        if (status == HttpConnection.HTTP_OK) {
                            // Is this html?
                            final String ctype =
                                    httpConn.getHeaderField(HEADER_CONTENTTYPE);
                            final boolean htmlContent =
                                    ctype != null
                                            && ctype.equals(CONTENTTYPE_TEXTHTML);

                            final InputStream input = s.openInputStream();

                            final byte[] data = new byte[256];
                            int len = 0;
                            int size = 0;
                            final StringBuffer raw = new StringBuffer();

                            while (-1 != (len = input.read(data))) {
                                raw.append(new String(data, 0, len));
                                size += len;
                            }

                            raw.insert(0, "bytes received]\n");
                            raw.insert(0, size);
                            raw.insert(0, '[');
                            String content = raw.toString();

                            if (htmlContent) {
                                content = prepareData(raw.toString());
                            }

                            // The long operation is the parsing above, after
                            // the parsing is complete, shutdown
                            // the status thread before setting the text (since
                            // both threads modify the content
                            // pane, we want to make sure we don't have the
                            // status thread overwriting our data).
                            stopStatusThread();
                            updateContent(content);
                            input.close();
                        } else {
                            stopStatusThread();
                            updateContent("response code = " + status);
                        }

                        s.close();

                    } catch (final IOException e) {
                        System.err.println(e.toString());
                        stopStatusThread();
                        updateContent(e.toString());
                    }

                    // We're done one connection so reset the start state.
                    _start = false;

                }
            }
        }

        private void stopStatusThread() {
            _statusThread.pause();
            try {
                synchronized (_statusThread) {
                    // Check the paused condition, incase the notify fires prior
                    // to our wait, in which
                    // case we may never see that nofity.
                    while (!_statusThread.isPaused()) {
                        ;
                    }
                    {
                        _statusThread.wait();
                    }
                }
            } catch (final InterruptedException e) {
                System.err.println(e.toString());
            }
        }
    }

    /**
     * The StatusThread class manages display of the status message while
     * lengthy HTTP/HTML operations are taking place.
     */
    private class StatusThread extends Thread {
        private static final int INTERVAL = 6;
        private static final int TIMEOUT = 500; // ms
        private static final int THREAD_TIMEOUT = 500;

        private volatile boolean _stop = false;
        private volatile boolean _running = false;
        private volatile boolean _isPaused = false;

        // Resume the thread.
        public void go() {
            _running = true;
            _isPaused = false;
        }

        // Pause the thread.
        public void pause() {
            _running = false;
        }

        // Query the paused status.
        public boolean isPaused() {
            return _isPaused;
        }

        // Shutdown the thread.
        public void stop() {
            _stop = true;
        }

        public void run() {
            int i = 0;

            // Setup the status messages.
            final String[] statusMsg = new String[6];
            final StringBuffer status = new StringBuffer("Working");
            statusMsg[0] = status.toString();

            // Preincrement improves efficiency.
            for (int j = 1; j < 6; ++j) {
                statusMsg[j] = status.append(" .").toString();
            }

            for (;;) {
                while (!_stop && !_running) {
                    // Sleep a bit so we don't spin.
                    try {
                        sleep(THREAD_TIMEOUT);
                    } catch (final InterruptedException e) {
                        System.err.println(e.toString());
                    }

                }

                if (_stop) {
                    return;
                }

                i = 0;

                // Clear the status buffer.
                status.delete(0, status.length());

                for (;;) {
                    // We're not synchronizing on the boolean flag! value is
                    // declared volatile therefore.
                    if (_stop) {
                        return;
                    }

                    if (!_running) {
                        _isPaused = true;

                        synchronized (this) {
                            this.notify();
                        }

                        break;
                    }

                    updateContent(statusMsg[++i % 6]);

                    try {
                        Thread.sleep(TIMEOUT); // Wait for a bit.
                    } catch (final InterruptedException e) {
                        System.err.println(e.toString());
                    }
                }
            }
        }
    }

    private class HTTPDemoScreen extends MainScreen {

        /**
         * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            menu.add(_fetchMenuItem);
            menu.add(_clearContent);
            menu.add(_fetchTTPSPage);
            menu.add(_wapStackOptionScreen);

            final StringBuffer sb = new StringBuffer();

            if (_useWapStack) {
                sb.append(Characters.CHECK_MARK);
            }

            sb.append("Use Wap Stack");
            _wapStackOption.setText(sb.toString());
            menu.add(_wapStackOption);

            menu.addSeparator();

            super.makeMenu(menu, instance);
        }

        /**
         * Prevent the save dialog from being displayed.
         * 
         * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            return true;
        }

        /**
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            _statusThread.stop();
            _connectionThread.stop();

            super.close();
        }

        /**
         * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
         */
        protected boolean keyChar(final char key, final int status,
                final int time) {
            // UiApplication.getUiApplication().getActiveScreen().
            if (getLeafFieldWithFocus() == _url && key == Characters.ENTER) {
                _fetchMenuItem.run();
                return true; // I've absorbed this event, so return true.
            } else {
                return super.keyChar(key, status, time);
            }
        }
    }

    // Constructors
    // -------------------------------------------------------------
    public HTTPDemo() {
        _wapOptionsScreen = new WapOptionsScreen(this);
        _mainScreen = new HTTPDemoScreen();
        _mainScreen.setTitle(new LabelField("HTTP Demo", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));

        _url =
                new EditField("URL: ", null, Integer.MAX_VALUE,
                        BasicEditField.FILTER_URL);
        _mainScreen.add(_url);

        _mainScreen.add(new SeparatorField());

        _content = new RichTextField("<content>");
        _mainScreen.add(_content);

        // Start the helper threads.
        _statusThread.start();
        _connectionThread.start();

        pushScreen(_mainScreen); // Push the main screen - a method on the
                                 // UiApplication class.
    }

    // Methods
    // ------------------------------------------------------------------
    private void fetchPage(String url) {
        // First, normalize the url.
        final String lcase = url.toLowerCase();
        boolean validHeader = false;
        int i = 0;

        // Winding down and comparing to 0 saves instructions.
        for (i = HTTP_PROTOCOL.length - 1; i >= 0; --i) {
            if (-1 != lcase.indexOf(HTTP_PROTOCOL[i])) {
                validHeader = true;
                break;
            }
        }

        if (_useWapStack) {
            url = url + _wapOptionsScreen.getWapParameters();
        }

        if (!validHeader) {
            url = HTTP_PROTOCOL[0] + url; // Prepend the protocol specifier.
        }

        /*
         * It is illegal to open a connection on the event thread, due to the
         * system architecture, therefore, spawn a new thread for connection
         * operations.
         */
        _connectionThread.fetch(url);

        // Create a thread for showing status of the operation.
        _statusThread.go();

    }

    private void updateContent(final String text) {
        // This will create significant garbage, but avoids threading issues
        // (compared with creating a static Runnable and setting the text).
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                _content.setText(text);
            }
        });
    }

    private String prepareData(final String text) {

        // Do some simple operations on the html data. A simple state machine
        // for removing
        // tags, comments, whitespace and inserting newlines for the <p> tag.

        final int text_length = text.length();
        final StringBuffer data = new StringBuffer(text_length);
        int state = STATE_0;
        int count = 0;
        int writeIndex = -1;
        char c = (char) 0;

        // Cache the text length and preincrement the counter for.
        for (int i = 0; i < text_length; ++i) {
            c = text.charAt(i);
            switch (state) {
            case STATE_0:
                if (c == HTML_TAG_OPEN) {
                    ++count;
                    state = STATE_1;
                } else if (c == ' ') {
                    data.insert(++writeIndex, c);
                    state = STATE_5;
                } else if (!specialChar(c)) {
                    data.insert(++writeIndex, c);
                }
                break;

            case STATE_1:
                if (c == '!' && text.charAt(i + 1) == '-'
                        && text.charAt(i + 2) == '-') {
                    System.out.println("Entering Comment state");
                    i += 2;
                    state = STATE_3;
                } else if (Character.toLowerCase(c) == 'p') {
                    state = STATE_4;
                } else if (c == HTML_TAG_CLOSE) {
                    --count;
                    state = STATE_0;
                } else {
                    state = STATE_2;
                }
                break;

            case STATE_2:
                if (c == HTML_TAG_OPEN) {
                    ++count;
                } else if (c == HTML_TAG_CLOSE) {
                    if (--count == 0) {
                        state = STATE_0;
                    }
                }
                break;

            case STATE_3:
                if (c == '-' && text.charAt(i + 1) == '-'
                        && text.charAt(i + 2) == HTML_TAG_CLOSE) {
                    --count;
                    i += 2;
                    state = STATE_0;
                    System.out.println("Exiting comment state");
                }
                break;

            case STATE_4:
                if (c == HTML_TAG_CLOSE) {
                    --count;
                    data.insert(++writeIndex, '\n');
                    state = STATE_0;
                } else {
                    state = STATE_1;
                }
                break;

            case STATE_5:
                if (c == HTML_TAG_OPEN) {
                    ++count;
                    state = STATE_1;
                } else if (c != ' ') {
                    state = STATE_0;
                    if (!specialChar(c)) {
                        data.insert(++writeIndex, c);
                    }
                }
                break;
            }
        }

        return data.toString().substring(0, writeIndex + 1);
    }

    private boolean specialChar(final char c) {
        return c == LF || c == CR;
    }

}
