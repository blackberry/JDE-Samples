/**
 * BluetoothSerialPortDemo.java
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

package com.rim.samples.device.bluetoothserialportdemo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.UnsupportedOperationException;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The client side of a simple serial port demonstration app. This application
 * will listen for text on the serial port and render the data when it arrives.
 * Please refer to readme.txt in
 * 'samples\com\rim\samples\server\bluetoothserialportdemo'.
 */
public final class BluetoothSerialPortDemo extends UiApplication {
    // Statics
    // ------------------------------------------------------------------
    private static final int INSERT = 1;
    private static final int REMOVE = 2;
    private static final int JUST_OPEN = 3;
    private static final int CONTENTS = 4;
    private static final int NO_CONTENTS = 5;
    private static final int FIRST = 0;

    // Members
    // -------------------------------------------------------------------
    private final EditField _infoField;
    private StreamConnection _bluetoothConnection;
    private DataInputStream _din;
    private DataOutputStream _dout;

    /**
     * This class represents the main screen for the BluetoothSerialPortDemo
     * application.
     */
    private final class BluetoothDemoScreen extends MainScreen {
        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            if (_infoField.getTextLength() > 0) {
                menu.add(new MenuItem("Copy Contents", 100000, 10) {
                    public void run() {
                        Clipboard.getClipboard().put(_infoField.getText());
                    }
                });
            }

            super.makeMenu(menu, instance);
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
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            onExit();
            super.close();
        }
    }

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final BluetoothSerialPortDemo theApp = new BluetoothSerialPortDemo();
        theApp.enterEventDispatcher();
    }

    // Constructor
    private BluetoothSerialPortDemo() {
        final BluetoothDemoScreen mainScreen = new BluetoothDemoScreen();
        mainScreen.setTitle(new LabelField("Bluetooth Serial Port Demo",
                Field.USE_ALL_WIDTH));

        _infoField = new EditField(Field.READONLY);
        mainScreen.add(_infoField);

        pushScreen(mainScreen);

        invokeLater(new Runnable() {
            public void run() {
                openPort();
            }
        });
    }

    /**
     * Closes the port when exiting the application
     */
    protected void onExit() {
        closePort();
    }

    /**
     * Close the serial port
     */
    private void closePort() {
        // Close the bluetooth connection
        if (_bluetoothConnection != null) {
            try {
                _bluetoothConnection.close();
            } catch (final IOException ioe) {
            }
        }

        // Close the input stream
        if (_din != null) {
            try {
                _din.close();
            } catch (final IOException ioe) {
            }
        }

        // Close the output stream
        if (_dout != null) {
            try {
                _dout.close();
            } catch (final IOException ioe) {
            }
        }

        _bluetoothConnection = null;
        _din = null;
        _dout = null;
    }

    /**
     * Opens the serial port
     */
    private void openPort() {
        if (_bluetoothConnection != null) {
            closePort();
        }

        new InputThread().start();
    }

    /**
     * A thread which handles the bluetooth serial port communication between
     * this device and the first device listed on its 'paired devices' list.
     */
    private class InputThread extends Thread {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            // Try to connect to the first device on the list of pair devices
            try {
                final BluetoothSerialPortInfo[] info =
                        BluetoothSerialPort.getSerialPortInfo();

                if (info == null || info.length == 0) // No devices paired
                {
                    invokeAndWait(new Runnable() {
                        /**
                         * @see java.lang.Runnable#run()
                         */
                        public void run() {
                            Dialog.alert("No bluetooth serial ports available for connection.");
                            onExit();
                            System.exit(1);
                        }
                    });
                }

                // Set up the bluetooth connection
                _bluetoothConnection =
                        (StreamConnection) Connector.open(info[FIRST]
                                .toString(), Connector.READ_WRITE);

                _din = _bluetoothConnection.openDataInputStream();
                _dout = _bluetoothConnection.openDataOutputStream();

            } catch (final IOException e) // Unable to connect
            {
                invokeAndWait(new Runnable() {
                    /**
                     * @see java.lang.Runnable#run()
                     */
                    public void run() {
                        Dialog.alert("Unable to open serial port");
                        onExit();
                        System.exit(1);
                    }
                });
            } catch (final UnsupportedOperationException e) // Bluetooth not
                                                            // supported
            {
                invokeAndWait(new Runnable() {
                    /**
                     * @see java.lang.Runnable#run()
                     */
                    public void run() {
                        Dialog.alert("This handheld or simulator does not support bluetooth.");
                        onExit();
                        System.exit(1);
                    }
                });
            }

            // Read information from the opened bluetooth serial port and
            // respond to the type of action requested. Note: we flush the
            // output stream every time we want to communicate to the other
            // device to ensure the message is sent immediately.
            try {
                int type, offset, count;
                String value;

                // Send the message that this connection has just been opened
                _dout.writeInt(JUST_OPEN);
                _dout.flush();

                // Communicating with the other device indefinitely unless the
                // connection is interrupted with an exception.
                for (;;) {
                    type = _din.readInt(); // Type of operation to enact

                    if (type == INSERT) {
                        // Insert the selected text at the specified position.
                        offset = _din.readInt();
                        value = _din.readUTF();
                        insert(value, offset);
                    } else if (type == REMOVE) {
                        // Remove characters at specified position
                        offset = _din.readInt();
                        count = _din.readInt();
                        remove(offset, count);
                    } else if (type == JUST_OPEN) {
                        // Send contents to desktop.
                        value = _infoField.getText();

                        if (value == null || value.length() == 0) {
                            // Communicate that our text field is empty
                            _dout.writeInt(NO_CONTENTS);
                            _dout.flush();
                        } else {
                            // Write out the contents of the text field
                            _dout.writeInt(CONTENTS);
                            _dout.writeUTF(_infoField.getText());
                            _dout.flush();
                        }
                    } else if (type == CONTENTS) {
                        // Read in the contents and get the event lock for this
                        // application so we can update the info field.
                        final String contents = _din.readUTF();
                        synchronized (Application.getEventLock()) {
                            _infoField.setText(contents);
                        }

                    } else if (type == NO_CONTENTS) {
                        // Do nothing
                    } else {
                        // This should not happen. 'type' did not match any type
                        // which is suposed to be outputted.
                        throw new RuntimeException();
                    }
                }
            } catch (final IOException ioe) {
                invokeLater(new Runnable() {
                    /**
                     * @see java.lang.Runnable#run()
                     */
                    public void run() {
                        Dialog.alert("Problems reading from or writing to serial port.");
                        onExit();
                        System.exit(1);
                    }
                });
            }
        }
    }

    /**
     * Inserts a message into the information text field displayed on the screen
     * 
     * @param msg
     *            The message to insert
     * @param offset
     *            The position to insert the message at
     */
    private void insert(final String msg, final int offset) {
        invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                _infoField.setCursorPosition(offset);
                _infoField.insert(msg);
            }
        });
    }

    /**
     * Removes a certain amount of characters at a specified position from the
     * information text field displayed on the screen.
     * 
     * @param offset
     *            The position just to the right of the rightmost character to
     *            remove (index of rightmost character to remove + 1)
     * @param count
     *            The number of characters to the left of the offset position to
     *            remove
     */
    private void remove(final int offset, final int count) {
        invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                _infoField.setCursorPosition(offset + count);
                _infoField.backspace(count);
            }
        });
    }
}
