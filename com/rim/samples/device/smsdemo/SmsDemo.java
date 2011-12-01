/**
 * SmsDemo.java
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

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

import net.rim.device.api.io.DatagramBase;
import net.rim.device.api.io.DatagramConnectionBase;
import net.rim.device.api.io.SmsAddress;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.system.SMSParameters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A demo application for sending and receiving SMS messages. Running this
 * program on a BlackBerry smartphone simulator requires the associated server
 * component found in the com.rim.samples.server.smsdemo package under the
 * samples directory in your JDE installation folder. This application
 * demonstrates sending and receiving SMS messages on both CDMA and GSM enabled
 * BlackBerry smartphones. CDMA devices require port 0 to be specified in the
 * call to Connector.open(). When running on a GSM device, this application
 * gives the user the option of the SMS message being routed to the inbox of the
 * SMS and MMS application as well as being received by this application's
 * listening thread.
 */
public class SmsDemo extends UiApplication {
    private static final int MAX_PHONE_NUMBER_LENGTH = 32;

    private static String NON_ZERO_PORT_NUMBER = "3590";

    private EditField _sendText;
    private EditField _address;
    private EditField _status;
    private ListeningThread _listener;
    private SendThread _sender;
    private Connection _conn;
    private String _port = "0";

    // Cached for improved performance
    private final StringBuffer _statusMsgs = new StringBuffer();

    /**
     * Determines whether the currently active WAF is CDMA
     * 
     * @return True if currently active WAF is CDMA, otherwise false
     */
    private static boolean isCDMA() {
        return (RadioInfo.getActiveWAFs() & RadioInfo.WAF_CDMA) == RadioInfo.WAF_CDMA;
    }

    /**
     * Sends an SMS message
     */
    private final MenuItem _sendMenuItem = new MenuItem("Send", 100, 10) {
        public void run() {
            final String text = _sendText.getText();
            final String addr = _address.getText();

            if (addr.length() == 0) {
                Dialog.alert("Destination field cannot be blank");
                _address.setFocus();
            } else if (text.length() == 0) {
                Dialog.alert("Message field cannot be blank");
                _sendText.setFocus();
            } else {
                _sender.send(addr, text, _port);
            }
        }
    };

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final SmsDemo sms = new SmsDemo();
        sms.enterEventDispatcher();
    }

    /**
     * This thread listens for any incoming messages
     */
    private class ListeningThread extends Thread {
        private boolean _stop;

        /**
         * Stops this thread from listening for messages
         */
        private synchronized void stop() {
            _stop = true;

            try {
                if (_conn != null) {
                    _conn.close();
                }
            } catch (final IOException ioe) {
            }
        }

        /**
         * Listens for incoming messages until stop() is called
         * 
         * @see #stop()
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                _conn = Connector.open("sms://:" + _port);
                for (;;) {
                    if (_stop) {
                        return;
                    }
                    final MessageConnection msgConn = (MessageConnection) _conn;
                    final Message m = msgConn.receive();

                    receivedSmsMessage(m);
                }
            } catch (final IOException ioe) {
                updateStatus(ioe.toString());
            }
        }
    }

    /**
     * A simple abstraction of an SMS message, used by the SendThread class
     */
    private static final class SmsMessage {
        private final String _address;
        private final String _port;
        private final String _msg;

        /**
         * Creates a SMS message
         * 
         * @param address
         *            The address of the recipient of the SMS message
         * @param msg
         *            The message to send
         */
        public SmsMessage(final String address, final String msg,
                final String port) {
            _address = address;
            _port = port;
            _msg = msg;
        }

        /**
         * Returns a Message object representing this SMS message
         * 
         * @param mc
         *            The MessageConnection source with which to create the
         *            Message from
         * @return The Message object representing the SMS message
         */
        public Message toMessage(final MessageConnection mc) {
            // If the user chose to have messages routed to the inbox (port =
            // 0),
            // we need to specify an address without a port number.
            final String addressString =
                    "//"
                            + _address
                            + (_port.equals(NON_ZERO_PORT_NUMBER) ? ":" + _port
                                    : "");

            final TextMessage m =
                    (TextMessage) mc.newMessage(MessageConnection.TEXT_MESSAGE,
                            addressString);
            m.setPayloadText(_msg);

            return m;
        }

        /**
         * Returns a Datagram object representing this SMS message
         * 
         * @param datagramConnectionBase
         *            The DatagramConnectionBase object with which to create the
         *            Datagram from
         * @return The Datagram object representing the SMS message
         */
        public Datagram toDatagram(
                final DatagramConnectionBase datagramConnectionBase)
                throws IOException {
            DatagramBase datagram = null;
            final byte[] data = _msg.getBytes("ISO-8859-1");
            datagram = (DatagramBase) datagramConnectionBase.newDatagram();
            final SmsAddress smsAddress = new SmsAddress("//" + _address);
            final SMSPacketHeader smsPacketHeader = smsAddress.getHeader();
            smsPacketHeader
                    .setMessageCoding(SMSParameters.MESSAGE_CODING_ISO8859_1);
            datagram.setAddressBase(smsAddress);
            datagram.write(data, 0, data.length);

            return datagram;
        }
    }

    /**
     * A thread to manage outbound transactions
     */
    private class SendThread extends Thread {
        private boolean _stopped = false;

        // Create a vector of SmsMessage objects with an initial capacity of 5.
        // For this implementation it is unlikely that more than 5 msgs will be
        // queued at any one time.
        private final Vector _msgs = new Vector(5);

        /**
         * Queues message send requests to send later
         * 
         * @param address
         *            The address to send the message to
         * @param msg
         *            The message to send
         */
        public void send(final String address, final String msg,
                final String port) {
            final SmsMessage message = new SmsMessage(address, msg, port);
            synchronized (this._msgs) {
                if (!this._stopped) {
                    this._msgs.addElement(message);
                    this._msgs.notifyAll();
                }
            }
        }

        /**
         * Stops this thread from sending any more messages
         */
        public void stop() {
            synchronized (this._msgs) {
                this._stopped = true;
                this._msgs.notifyAll();
                this._msgs.removeAllElements();

                try {
                    if (_conn != null) {
                        _conn.close();
                    }
                } catch (final IOException ioe) {
                }
            }
        }

        /**
         * Sends any queued messages until stop() is called
         * 
         * @see #stop()
         * @see java.lang.Runnable#run()
         */
        public void run() {
            while (true) {
                final SmsMessage smsMessage;
                synchronized (this._msgs) {
                    if (this._stopped) {
                        return;
                    } else if (this._msgs.isEmpty()) {
                        try {
                            this._msgs.wait();
                        } catch (final InterruptedException ie) {
                            return;
                        }
                    }

                    if (this._stopped) {
                        return;
                    } else {
                        smsMessage = (SmsMessage) this._msgs.elementAt(0);
                        this._msgs.removeElementAt(0);
                    }
                }
                try {
                    if (isCDMA()) {
                        final DatagramConnectionBase dcb =
                                (DatagramConnectionBase) _conn;
                        dcb.send(smsMessage.toDatagram(dcb));
                    } else {
                        final MessageConnection mc = (MessageConnection) _conn;
                        mc.send(smsMessage.toMessage(mc));
                    }
                } catch (final IOException ioe) {
                    updateStatus(ioe.toString());
                }
            }
        }
    }

    /**
     * This screen acts as the main screen to allow the user to send and receive
     * messages.
     */
    private class SmsDemoScreen extends MainScreen {
        /**
         * Default constructor
         */
        private SmsDemoScreen() {
            setTitle("SMS Demo");

            _address =
                    new EditField("Destination:", "", MAX_PHONE_NUMBER_LENGTH,
                            BasicEditField.FILTER_PHONE);
            add(_address);
            _sendText = new EditField("Message:", "");
            add(_sendText);
            add(new SeparatorField());

            _status = new EditField(Field.NON_FOCUSABLE);
            add(_status);

            addMenuItem(_sendMenuItem);
        }

        /**
         * Prevent the save dialog from being displayed
         * 
         * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            return true;
        }

        /**
         * Closes the application
         * 
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            _listener.stop();
            _sender.stop();

            super.close();
        }
    }

    /**
     * Default constructor
     */
    public SmsDemo() {
        invokeLater(new Runnable() {

            public void run() {
                if (!isCDMA()) {
                    final int result =
                            Dialog.ask(Dialog.D_YES_NO,
                                    "Send messages to inbox?", Dialog.YES);
                    if (!(result == Dialog.YES)) {
                        // If user chooses to not have message routed to inbox,
                        // we need to specify an arbitrary non-zero port number.
                        _port = NON_ZERO_PORT_NUMBER;
                    }
                }

                _listener = new ListeningThread();
                _listener.start();

                _sender = new SendThread();
                _sender.start();
            }
        });

        final SmsDemoScreen screen = new SmsDemoScreen();
        pushScreen(screen);
    }

    /**
     * Update the GUI with the data just received
     * 
     * @param msg
     *            The new status message to display on screen
     */
    private void updateStatus(final String msg) {
        System.err.println(msg);

        invokeLater(new Runnable() {
            /**
             * Updates the GUI's status message
             * 
             * @see java.lang.Runnable#run()
             */
            public void run() {

                // Clear the string buffer
                _statusMsgs.delete(0, _statusMsgs.length());

                _statusMsgs.append(_status.getText());
                _statusMsgs.append('\n');
                _statusMsgs.append(msg);
                _status.setText(_statusMsgs.toString());
            }
        });

    }

    /**
     * Some simple formatting for a received SMS message
     * 
     * @param m
     *            The message just received
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
}
