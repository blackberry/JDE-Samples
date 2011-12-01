/*
 * UsbDemo.java
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

package com.rim.samples.device.usbdemo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.system.USBPort;
import net.rim.device.api.system.USBPortListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.DataBuffer;

/**
 * A sample application to demonstrate opening of the USB port for communication
 * with a desktop USB client (one that uses the COM interfaces exposed by
 * BBDevMgr.exe).
 */
public final class UsbDemo extends UiApplication {
    private static String CHANNEL = "JDE_USBClient";
    private final RichTextField _output;
    private UsbThread _usbThread;

    /**
     * Entry point for application.
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static final void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new UsbDemo().enterEventDispatcher();
    }

    /**
     * Creates a new UsbDemo object
     */
    public UsbDemo() {

        final USBScreen screen = new USBScreen();
        screen.setTitle("Usb Demo");

        _output = new RichTextField("<output>");

        screen.add(_output);

        pushScreen(screen);
    }

    /**
     * This is the main screen that allows the user to connect to a desktop
     * client.
     */
    private class USBScreen extends MainScreen {
        /**
         * Creates a new USBScreen object
         */
        private USBScreen() {
            addMenuItem(_connectGCF);
            addMenuItem(_connectLowLevel);
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            // Prevent the save dialog from being displayed
            return true;
        }

        /**
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            onExit();

            super.close();
        }

        /**
         * Connects through a GCF USB connection
         */
        private final MenuItem _connectGCF = new MenuItem("Connect (GCF)", 10,
                10) {
            public void run() {
                // Cleanup the old thread if present
                onExit();

                _usbThread = new GCFUsbThread();
                connect();
            }
        };

        /**
         * Connects through a low level USB connection.
         */
        private final MenuItem _connectLowLevel = new MenuItem(
                "Connect (low level)", 11, 11) {
            public void run() {
                // Cleanup old thread if present.
                onExit();

                _usbThread = new LowLevelUsbThread();
                connect();
            }
        };
    }

    /**
     * Provides an abstract base for the GCFUsbThread and LowLevelUsbThread to
     * use to connect to a desktop through its USB port.
     */
    private abstract class UsbThread extends Thread {
        /**
         * Provides the action of connecting to a desktop client by calling the
         * abstract methods to be implemented by subclasses.
         */
        public void run() {
            try {
                init();

                String s = read("Hello from PC");
                message("received: " + s);

                // Write back a hello string
                s = "Hello from Device";
                write(s);
                message("wrote: " + s);

                message("Sleeping to demonstrate queueing");
                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException e) {
                }
                for (int i = 0; i < 5; i++) {
                    s = read("Q packet " + (i + 1));
                    message("received: " + s);
                }

                // Wait for goodbye
                s = read("Goodbye from PC");
                message("received: " + s);

                s = "Goodbye from Device";
                write(s);
                message("wrote: " + s);

                close();
            } catch (final IOException e) {
                message(e.toString());
            }
        }

        /**
         * Initializes the connection to the USB port
         * 
         * @throws IOException
         *             Thrown if an error occurs while opening the USB port
         *             connection
         */
        abstract protected void init() throws IOException;

        /**
         * Writes to the desktop client
         * 
         * @param s
         *            The string to write out
         * @throws IOException
         *             Thrown if a write error occurs
         */
        abstract protected void write(String s) throws IOException;

        /**
         * Reads from the desktop client and returns the read string only if it
         * matches the given string.
         * 
         * @param match
         *            The specified string to match with the data read in
         * @return The data read in if it matches the specified string, null
         *         otherwise
         * @throws IOException
         *             Thrown if a read error occurs
         */
        abstract protected String read(String match) throws IOException;

        /**
         * Closes the USB connection
         */
        abstract public void close();
    }

    /**
     * The GCF prepends the length of the data written to the outputstream on
     * each transaction. Your server code should expect two bytes length data
     * prior to the actual data when the client is using the GCF.
     */
    private final class GCFUsbThread extends UsbThread {
        private DataInputStream _dis;
        private DataOutputStream _dos;
        private StreamConnection _sc;

        /**
         * @see UsbThread#init()
         */
        protected void init() throws IOException {
            // Using the connector interface
            // - no support for maxRxSize and maxTxSize changes
            // - size is set to 1024 bytes
            synchronized (this) {
                _sc =
                        (StreamConnection) Connector.open("comm:USB;channel="
                                + CHANNEL);

                _dis = _sc.openDataInputStream();
                _dos = _sc.openDataOutputStream();
            }
            message("Channel Open: " + CHANNEL);
        }

        /**
         * @see UsbThread#write(String)
         */
        protected void write(final String s) throws IOException {
            synchronized (this) {
                _dos.writeUTF(s);
                _dos.flush();
            }
        }

        /**
         * @see UsbThread#read(String)
         */
        protected String read(final String match) throws IOException {
            boolean loop;
            final int len = match.length();
            final byte[] data = new byte[len];
            do {
                loop = false;
                try {
                    synchronized (this) {
                        _dis.read(data, 0, len);
                    }
                } catch (final IOException e) {
                    message(e.toString());
                    loop = true;
                    close();
                    init();
                }
            } while (loop);

            final String s = new String(data);

            return s.equals(match) ? s : null;
        }

        /**
         * @see UsbThread#close()
         */
        public void close() {
            message("closing connections...");

            try {
                if (_dis != null) {
                    _dis.close();
                }
            } catch (final IOException e) {
            }
            try {
                if (_dos != null) {
                    _dos.close();
                }
            } catch (final IOException e) {
            }
            try {
                if (_sc != null) {
                    _sc.close();
                }
            } catch (final IOException e) {
            }

            message("connections closed");
        }

        // Default constructor
        public GCFUsbThread() {
        }
    }

    /**
     * This thread implements a low level USB connector through the USBPort
     * class.
     * 
     * Note: This method is not portable. See the USBPort javadocs for details.
     * 
     * @see net.rim.device.api.system.USBPort
     */
    private final class LowLevelUsbThread extends UsbThread implements
            USBPortListener, SystemListener2 {
        private boolean _dataAvailable;
        private Vector _readQueue;
        private int _channel;
        private USBPort _port;
        private boolean _waiting;
        private boolean _connected;
        private boolean _abort;

        /**
         * @see UsbThread#init()
         */
        protected void init() throws IOException {
            _readQueue = new Vector();

            // Register the app for callbacks.
            // NOTE: These are here for demonstration, but
            // they can be used in GCF thread if needed.

            UsbDemo.this.addIOPortListener(this);
            UsbDemo.this.addSystemListener(this);

            message("using low level usb interface");

            // Register the channel
            _channel = USBPort.registerChannel(CHANNEL, 16380, 16380);

            message("Registering channel: " + CHANNEL);

            synchronized (this) {
                // Wait for a channel
                if (_port == null) {
                    _waiting = true;
                    try {
                        this.wait();
                    } catch (final InterruptedException e) {
                    }
                    _waiting = false;
                }
            }
        }

        /**
         * @see UsbThread#write(String)
         */
        protected void write(final String s) throws IOException {
            _port.write(s.getBytes());
        }

        /**
         * @see UsbThread#read(String)
         */
        protected String read(final String match) throws IOException {
            do {
                if (_abort) {
                    // Disconnect happened, so reopen the channel
                    close();
                    init();
                    _abort = false;
                }
                synchronized (this) {
                    if (!_dataAvailable) {
                        // wait for data
                        _waiting = true;
                        try {
                            this.wait();
                        } catch (final InterruptedException e) {
                        }
                        _waiting = false;
                    }

                    _dataAvailable = false;
                }
            } while (_abort); // Check for disconnect

            // Loop through the queue of data and extract each item
            for (;;) {
                byte[] data = null;

                synchronized (this) {
                    if (_readQueue.isEmpty()) {
                        // The read queue is empty, we didn't find our match
                        message("did not recieve match for " + match);
                        break;
                    } else {
                        data = (byte[]) _readQueue.firstElement();

                        // Ensure we always remove the correct object
                        _readQueue.removeElementAt(0);

                        if (!_readQueue.isEmpty()) {
                            _dataAvailable = true;
                        }
                    }
                }

                final String s = new String(data);

                if (s.equals(match)) {
                    return s;
                }
            }
            return null;
        }

        // Wakeup the thread so it can reconnect the channel
        public void abort() {
            _abort = true;
            if (_waiting) {
                synchronized (this) {
                    this.notify();
                }
            }
        }

        /**
         * @see UsbThread#close()
         */
        public void close() {
            _connected = false;
            try {
                message("closing connections...");

                // Close port if not already closed
                if (_port != null) {
                    _port.close();
                }
                _port = null;

                // Deregister the channel
                USBPort.deregisterChannel(_channel);
                message("connections closed");

            } catch (final IOException e) {
                message(e.toString());
            }
        }

        // USBPortListener methods
        // --------------------------------------------------
        /**
         * @see net.rim.device.api.system.USBPortListener#getChannel()
         */
        public int getChannel() {
            return _channel;
        }

        /**
         * @see net.rim.device.api.system.USBPortListener#connectionRequested()
         */
        public void connectionRequested() {
            message("lowlevelusb: Connection requested!");

            try {
                synchronized (this) {
                    _port = new USBPort(_channel);
                    _connected = true;
                    this.notify();
                }
            } catch (final IOException e) {
                message(e.toString());
            }
        }

        /**
         * @see net.rim.device.api.system.USBPortListener#dataNotSent()
         */
        public void dataNotSent() {
            message("lowlevelusb: Data not sent");
        }

        /**
         * @see net.rim.device.api.system.IOPortListener#connected()
         */
        public void connected() {
            message("lowlevelusb: connected");
        }

        /**
         * @see net.rim.device.api.system.IOPortListener#disconnected()
         */
        public void disconnected() {
            message("lowlevelusb: disconnected");
            if (_connected) {
                abort();
            }
        }

        /**
         * @see net.rim.device.api.system.IOPortListener#receiveError(int)
         */
        public void receiveError(final int error) {
            message("lowlevelusb: Got rxError: " + error);
        }

        /**
         * @see net.rim.device.api.system.IOPortListener#dataReceived(int)
         */
        public void dataReceived(int length) {
            final DataBuffer db = new DataBuffer();
            int len;

            try {
                final byte[] receiveBuffer = new byte[16380];

                if (length == -1) // Length not available
                {
                    length = receiveBuffer.length;
                }

                // If something was read then add it to the data buffer to
                // send back later.
                synchronized (this) {
                    len = _port.read(receiveBuffer, 0, length);
                }
                if (len != 0) {
                    db.write(receiveBuffer, 0, len);
                    final String data = new String(receiveBuffer, 0, len);
                    message("lowlevelusb: " + data);
                }

                // Wakeup read()
                synchronized (this) {
                    _readQueue.addElement(db.toArray());
                    _dataAvailable = true;
                    this.notify();
                }
            } catch (final IOException ex) {
                throw new RuntimeException(ex.getMessage());
            }
        }

        /**
         * @see net.rim.device.api.system.IOPortListener#dataSent()
         */
        public void dataSent() {
            // Not implemented
        }

        /**
         * @see net.rim.device.api.system.IOPortListener#patternReceived(byte[])
         */
        public void patternReceived(final byte[] pattern) {
            message("lowlevelusb: Got pattern " + new String(pattern)); // +
                                                                        // pattern[0]
                                                                        // + " "
                                                                        // +
                                                                        // pattern[1]
                                                                        // + " "
                                                                        // +
                                                                        // pattern[2]
                                                                        // + " "
                                                                        // +
                                                                        // pattern[3]
                                                                        // );
        }

        // SystemListener2 methods
        // --------------------------------------------------

        // Used to detect a connection change
        public void usbConnectionStateChange(final int state) {
            switch (state) {
            // USB cable is connected
            case USB_STATE_CABLE_CONNECTED:
                message("Cable connected");
                break;
            // USB cable is disconnected
            case USB_STATE_CABLE_DISCONNECTED:
                message("Cable disconnected");
                if (_connected) {
                    abort();
                }
                break;
            }
        }

        // Not implemented in this demo but need to be defined
        public void backlightStateChange(final boolean on) {
        }

        public void cradleMismatch(final boolean mismatch) {
        }

        public void fastReset() {
        }

        public void powerOffRequested(final int reason) {
        }

        // SystemListener methods
        // --------------------------------------------------

        // Not implemented in this demo but need to be defined
        public void batteryGood() {
        }

        public void batteryLow() {
        }

        public void batteryStatusChange(final int status) {
        }

        public void powerOff() {
        }

        public void powerUp() {
        }

        // Default constructor
        public LowLevelUsbThread() {
        }
    }

    /**
     * Connects the USB to the desktop
     */
    private void connect() {
        _usbThread.start();
    }

    /**
     * Displays a message by appending it to the screen
     * 
     * @param msg
     *            The message to display
     */
    private void message(final String msg) {
        invokeLater(new Runnable() {
            public void run() {
                _output.setText(_output.getText() + "\n" + msg);
            }
        });
    }

    /**
     * Closes the USB thread
     */
    public void onExit() {
        if (_usbThread != null) {
            _usbThread.close();
        }
    }
}
