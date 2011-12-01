/*
 * SPPScreen.java
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

package com.rim.samples.device.bluetoothdemo;

import java.io.IOException;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.bluetooth.BluetoothSerialPortListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.DataBuffer;

/**
 * This class handles the bluetooth communication between two devices.
 * Connecting, waiting for a connection, sending and receiving messages and
 * displaying the information sent between the two devices are all handled by
 * this class.
 */
public class SPPScreen extends MainScreen implements
        BluetoothSerialPortListener {
    private RichTextField _rtf;
    private final StringBuffer _data;
    private final byte[] _receiveBuffer = new byte[1024];
    private BluetoothSerialPort _port;
    private boolean _loopback;
    private static boolean _dataSent = true;
    private String _deviceName;
    private final DataBuffer _db;

    /**
     * Constructs a serial port connection screen.
     * 
     * @param info
     *            The information describing the bluetooth serial port to
     *            connect to. If 'null' then a new serial port is opened on this
     *            device.
     */
    public SPPScreen(final BluetoothSerialPortInfo info) {
        // Fill an lk array with the 'a' character.
        Arrays.fill(_receiveBuffer, (byte) 'a');

        // Initialize the buffers
        _data = new StringBuffer();
        _db = new DataBuffer();

        try {
            if (info == null) {
                // Open a port to listen for incoming connections
                _rtf =
                        new RichTextField(
                                "Connect external device and then type something...",
                                Field.NON_FOCUSABLE);
                _port =
                        new BluetoothSerialPort(
                                "Hi there",
                                BluetoothSerialPort.BAUD_115200,
                                BluetoothSerialPort.DATA_FORMAT_PARITY_NONE
                                        | BluetoothSerialPort.DATA_FORMAT_STOP_BITS_1
                                        | BluetoothSerialPort.DATA_FORMAT_DATA_BITS_8,
                                BluetoothSerialPort.FLOW_CONTROL_NONE, 1024,
                                1024, this);
                _deviceName = "unknown";
            } else {
                // Connect to the selected device
                _rtf =
                        new RichTextField("Type something...",
                                Field.NON_FOCUSABLE);
                _port =
                        new BluetoothSerialPort(
                                info,
                                BluetoothSerialPort.BAUD_115200,
                                BluetoothSerialPort.DATA_FORMAT_PARITY_NONE
                                        | BluetoothSerialPort.DATA_FORMAT_STOP_BITS_1
                                        | BluetoothSerialPort.DATA_FORMAT_DATA_BITS_8,
                                BluetoothSerialPort.FLOW_CONTROL_NONE, 1024,
                                1024, this);
                _deviceName = info.getDeviceName();
            }
        } catch (final IOException ex) {
            BluetoothDemo.errorDialog(ex.toString());
        }

        add(_rtf);

        // Add menu items to the screen
        addMenuItem(_closeSP);
        addMenuItem(_dtr);
        addMenuItem(_dsrOn);
        addMenuItem(_dsrOff);
        addMenuItem(_enableLoopback);
        addMenuItem(_disableLoopback);
        addMenuItem(_send1k);
    }

    /**
     * Alerts the user if a device was connected or if it failed to connect
     * 
     * @param success
     *            True if the connection was successful, false otherwise
     * @see net.rim.device.api.bluetooth.BluetoothSerialPortListener#deviceConnected(boolean)
     */
    public void deviceConnected(final boolean success) {
        if (success) {
            Status.show("Bluetooth SPP connected to " + _deviceName);
        } else {
            Status.show("Bluetooth SPP failed to connect to " + _deviceName);
        }
    }

    /**
     * @see net.rim.device.api.bluetooth.BluetoothSerialPortListener#deviceDisconnected()
     */
    public void deviceDisconnected() {
        Status.show("Disconnected from " + _deviceName);
    }

    /**
     * @see net.rim.device.api.bluetooth.BluetoothSerialPortListener#dtrStateChange(boolean)
     */
    public void dtrStateChange(final boolean high) {
        Status.show("DTR: " + high);
    }

    /**
     * @see net.rim.device.api.bluetooth.BluetoothSerialPortListener#dataReceived(int)
     */
    public void dataReceived(final int length) {
        int len;
        try {
            // Read the data that arrived
            if ((len =
                    _port.read(_receiveBuffer, 0,
                            length == -1 ? _receiveBuffer.length : length)) != 0) {
                // If loopback is enabled write the data back
                if (_loopback) {
                    writeData(_receiveBuffer, 0, len);
                } else {
                    if (len == 1 && _receiveBuffer[0] == '\r') {
                        _receiveBuffer[1] = '\n';
                        ++len;
                    }
                }

                // Update the screen with the new data that arrived
                _data.append(new String(_receiveBuffer, 0, len));
                _rtf.setText(_data.toString());
            }
        } catch (final IOException ioex) {
            BluetoothDemo.errorDialog(ioex.toString());
        }
    }

    /**
     * @see net.rim.device.api.bluetooth.BluetoothSerialPortListener#dataSent()
     */
    public void dataSent() {
        // Set the _dataSent flag to true to allow more data to be written
        _dataSent = true;

        // Call sendData in case there is data waiting to be sent
        sendData();
    }

    /*
     * Invoked when a key is pressed
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     */
    public boolean keyChar(final char key, final int status, final int time) {
        // Send the key if a Bluetooth connection has been established
        if (_port != null) {
            if (key == '\n') {
                writeData((byte) '\r');
            } else {
                writeData((byte) key);
            }

            // Update the screen adding the character just pressed
            _data.append(key);
            _rtf.setText(_data.toString());
        }
        return true;
    }

    /**
     * Writes a byte to the 'send' buffer
     * 
     * @param theData
     *            The byte to write to the buffer
     */
    private void writeData(final byte theData) {
        synchronized (_db) {
            _db.write(theData);

            // Call sendData to send the data
            sendData();
        }
    }

    /**
     * Writes data from a source byte array to the 'send' buffer
     * 
     * @param theData
     *            The source data to write to the buffer
     * @param offset
     *            The off set in the source data to write to the buffer
     * @param length
     *            The length of the array of bytes to write to the buffer
     */
    private void writeData(final byte[] theData, final int offset,
            final int length) {
        synchronized (_db) {
            _db.write(theData, offset, length);

            // Call sendData to send the data
            sendData();
        }
    }

    /**
     * Sends the data currently stored in the DataBuffer to the other device
     */
    private void sendData() {
        // Ensure we have data to send
        if (_db.getArrayLength() > 0) {
            // Ensure the last write call has resulted in the sending of the
            // data
            // prior to calling write again. Calling write in sequence without
            // waiting
            // for the data to be sent can overwrite existing requests and
            // result in
            // data loss.
            if (_dataSent) {
                try {
                    // Set the _dataSent flag to false so we don't send any more
                    // data until it has been verified that this data was sent.
                    _dataSent = false;

                    synchronized (_db) {
                        // Write out the data in the DataBuffer and reset the
                        // DataBuffer
                        _port.write(_db.getArray(), 0, _db.getArrayLength());
                        _db.reset();
                    }
                } catch (final IOException ioex) {
                    // Reset _dataSent to true so we can attempt another data
                    // write
                    _dataSent = true;
                    BluetoothDemo.errorDialog("Failed to write data: "
                            + ioex.toString());
                }
            } else {
                System.out
                        .println("Can't send data right now, data will be sent after dataSent notify call.");
            }
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        if (_port != null) {
            _port.close();
        }
        super.close();
    }

    // //////////////////////////////////////////////////////////
    // Menu Items //
    // //////////////////////////////////////////////////////////

    /**
     * Closes the screen
     */
    private final MenuItem _closeSP =
            new MenuItem("Close serial port", 20, 20) {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    close();
                }
            };

    /**
     * Displays the DTR line's state
     */
    private final MenuItem _dtr = new MenuItem("Get DTR", 30, 30) {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                Status.show("DTR: " + _port.getDtr());
            } catch (final IOException ioex) {
                Status.show("");
                BluetoothDemo.errorDialog("BluetoothSerialPort#getDtr() threw "
                        + ioex.toString());
            }
        }
    };

    /**
     * Turns DSR on
     */
    private final MenuItem _dsrOn = new MenuItem("DSR on", 40, 40) {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Status.show("DSR on");
            try {
                _port.setDsr(true);
            } catch (final IOException ioex) {
                Status.show("");
                BluetoothDemo
                        .errorDialog("BluetoothSerialPort#setDsr(boolean) threw "
                                + ioex.toString());
            }
        }
    };

    /**
     * Turns DSR off
     */
    private final MenuItem _dsrOff = new MenuItem("DSR off", 50, 50) {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Status.show("DSR off");
            try {
                _port.setDsr(false);
            } catch (final IOException ioex) {
                Status.show("");
                BluetoothDemo
                        .errorDialog("BluetoothSerialPort#setDsr(boolean) threw "
                                + ioex.toString());
            }
        }
    };

    /**
     * Enables loop back
     */
    private final MenuItem _enableLoopback = new MenuItem("Enable loopback",
            60, 60) {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            _loopback = true;
        }
    };

    /**
     * Disables loop back
     */
    private final MenuItem _disableLoopback = new MenuItem("Disable loopback",
            70, 70) {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            _loopback = false;
        }
    };

    /**
     * Sends 1kb of information to the other device
     */
    private final MenuItem _send1k = new MenuItem("Send 1k", 80, 80) {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            writeData(_receiveBuffer, 0, _receiveBuffer.length);
        }
    };
}
