/**
 * A simple SMS send and receive demo.
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

package com.rim.samples.device.smsdemo;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A simple demo of SMS send and receive. This program requires the associated
 * server component found in the com.rim.samples.server.smsdemo package under
 * the samples directory in your JDE installation.
 */
class SmsDemo extends UiApplication {

    // Constants
    // ----------------------------------------------------------------
    private static final int MAX_PHONE_NUMBER_LENGTH = 32;

    // Members
    // ------------------------------------------------------------------
    private EditField _sendText;
    private EditField _address; // A phone number for outbound SMS messages.
    private EditField _status;
    private final ListeningThread _listener;
    private final SendThread _sender;
    private final StringBuffer _statusMsgs = new StringBuffer(); // Cached for
                                                                 // improved
                                                                 // performance.
    private MessageConnection _mc;
    private boolean _stop = false;

    private final MenuItem _sendMenuItem = new MenuItem("Send", 100, 10) {
        public void run() {
            final String text = _sendText.getText();
            final String addr = _address.getText();

            if (text.length() > 0 && addr.length() > 0) {
                send(addr, text);
            }
        }
    };

    // Statics
    // ------------------------------------------------------------------
    private static String _openString = "sms://:3590"; // See Connector
                                                       // implementation notes.

    public static void main(final String[] args) {

        // Create a new instance of the application and start
        // the application on the event thread.
        final SmsDemo sms = new SmsDemo();
        sms.enterEventDispatcher();
    }

    // Inner Classes
    // ------------------------------------------------------------
    private class ListeningThread extends Thread {
        private synchronized void stop() {
            _stop = true;

            try {
                if (_mc != null) {
                    // Close the connection so the thread will return.
                    _mc.close();
                }
            } catch (final IOException e) {
                System.err.println(e.toString());
            }
        }

        public void run() {
            try {
                _mc = (MessageConnection) Connector.open(_openString); // Closed
                                                                       // by the
                                                                       // stop()
                                                                       // method.

                for (;;) {
                    if (_stop) {
                        return;
                    }

                    final Message m = _mc.receive();

                    receivedSmsMessage(m);
                }
            } catch (final IOException e) {
                // Likely the stream was closed.
                System.err.println(e.toString());
            }
        }
    }

    /**
     * A simple abstraction of an sms message, used by the SendThread class.
     */
    private static final class SmsMessage {
        private final String _address;
        private final String _msg;

        private SmsMessage(final String address, final String msg) {
            _address = address;
            _msg = msg;
        }

        private Message toMessage(final MessageConnection mc) {
            final TextMessage m =
                    (TextMessage) mc.newMessage(MessageConnection.TEXT_MESSAGE,
                            "//" + _address + ":3590");
            m.setPayloadText(_msg);

            return m;
        }
    }

    /**
     * A thread to manage outbound transactions.
     */
    private class SendThread extends Thread {
        private static final int TIMEOUT = 500; // ms

        // Create a vector of SmsMessage objects with an initial capacity of 5.
        // For this implementation it is unlikely that more than 5 msgs will be
        // queued at any one time.
        private final Vector _msgs = new Vector(5);

        private volatile boolean _start = false;

        // Requests are queued.
        private synchronized void send(final String address, final String msg) {
            _start = true;
            _msgs.addElement(new SmsMessage(address, msg));
        }

        // Shutdown the thread.
        private synchronized void stop() {
            _stop = true;

            try {
                if (_mc != null) {
                    _mc.close();
                }
            } catch (final IOException e) {
                System.err.println(e);
                updateStatus(e.toString());
            }
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

                while (true) {
                    try {
                        SmsMessage sms = null;

                        synchronized (this) {
                            if (!_msgs.isEmpty()) {
                                sms = (SmsMessage) _msgs.firstElement();

                                // Remove the element so we don't send it again.
                                _msgs.removeElement(sms);
                            } else {
                                _start = false;
                                break;
                            }
                        }

                        _mc.send(sms.toMessage(_mc));

                    } catch (final IOException e) {
                        System.err.println(e);
                        updateStatus(e.toString());
                    }
                }
            }
        }
    }

    private class SmsDemoScreen extends MainScreen {

        // Constructor
        private SmsDemoScreen() {
            setTitle(new LabelField("SMS Demo", Field.USE_ALL_WIDTH));

            _address =
                    new EditField("Destination:", "", MAX_PHONE_NUMBER_LENGTH,
                            BasicEditField.FILTER_PHONE);
            add(_address);
            _sendText = new EditField("Message:", "");
            add(_sendText);
            add(new SeparatorField());

            _status = new EditField();
            add(_status);

            addMenuItem(_sendMenuItem);
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
         * Close application
         * 
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            _listener.stop();
            _sender.stop();

            super.close();
        }
    }

    // Constructor
    private SmsDemo() {
        _listener = new ListeningThread();
        _listener.start();

        _sender = new SendThread();
        _sender.start();

        final SmsDemoScreen screen = new SmsDemoScreen();
        pushScreen(screen);
    }

    /**
     * Update the GUI with the data just received.
     */
    private void updateStatus(final String msg) {
        invokeLater(new Runnable() {
            public void run() {

                // Clear the string buffer.
                _statusMsgs.delete(0, _statusMsgs.length());

                _statusMsgs.append(_status.getText());
                _statusMsgs.append('\n');
                _statusMsgs.append(msg);
                _status.setText(_statusMsgs.toString());
            }
        });

    }

    /**
     * Some simple formatting for a received sms message.
     */
    private void receivedSmsMessage(final Message m) {
        final String address = m.getAddress();
        String msg = null;

        if (m instanceof TextMessage) {
            final TextMessage tm = (TextMessage) m;
            msg = tm.getPayloadText();
        }

        final StringBuffer sb = new StringBuffer();
        sb.append("Received:");
        sb.append('\n');
        sb.append("Destination:");
        sb.append(address);
        sb.append('\n');
        sb.append("Data:");
        sb.append(msg);
        sb.append('\n');

        updateStatus(sb.toString());
    }

    private void send(final String addr, final String data) {
        _sender.send(addr, data);
    }
}
