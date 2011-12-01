/*
 * AppScreen.java
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

import net.rim.device.api.bluetooth.BluetoothSerialPort;
import net.rim.device.api.bluetooth.BluetoothSerialPortInfo;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * This class is the initial loading screen for the Bluetooth Demo. It retrieves
 * the serial port information for all of the currently paired devices and lists
 * any possible connections which have a service name of "Hi there" (for
 * devices) or "COM X" (for PCs) in the menu. The user may then select the
 * device from the menu or choose to try to wait and listen for a connection
 * from a paired device.
 * 
 * Note: One of the paired devices must be listening for a connection before the
 * other device may connect.
 */
public final class AppScreen extends MainScreen {
    private BluetoothSerialPortInfo[] _portInfo;

    /**
     * Default constructor
     */
    public AppScreen() {
        setTitle("Bluetooth Sample");

        // Determine if this BlackBerry model or simulator supports Bluetooth
        if (BluetoothSerialPort.isSupported()) {
            // Get the BluetoothSerialPortInfo. Retrieves serial port
            // information for
            // the currently paired devices.
            _portInfo = BluetoothSerialPort.getSerialPortInfo();
            final int numServices = _portInfo.length;

            // Create a MenuItem for each Bluetooth device we can connect to.
            for (int count = numServices - 1; count >= 0; --count) {
                final String serviceName = _portInfo[count].getServiceName();

                // A single device can provide multiple serial port connections
                // (or services).
                // Add a menu item only to the service that corresponds to the
                // "Hi there" connection,
                // or the "COM X" connection (if we are pairing with a PC).
                if (serviceName.equals("Hi there")
                        || serviceName.equals("COM X")) {
                    final DeviceMenuItem deviceMenuItem =
                            new DeviceMenuItem("Connect to: "
                                    + _portInfo[count].getDeviceName(),
                                    _portInfo[count]);
                    addMenuItem(deviceMenuItem);
                }
            }

            final MenuItem listenItem =
                    new MenuItem(new StringProvider("Listen for connections"),
                            0x230010, 0);
            listenItem.setCommand(new Command(new CommandHandler() {
                /**
                 * Launches a connection waiting screen.
                 * 
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    UiApplication.getUiApplication().pushScreen(
                            new SPPScreen(null));
                    close(); // Close the current screen
                }
            }));

            addMenuItem(listenItem);
        } else {
            add(new LabelField("Bluetooth is not supported on this device."));
        }
    }

    /**
     * This is a custom menu item which invokes a connection to a paired device
     * when selected.
     */
    private final class DeviceMenuItem extends MenuItem {
        private final BluetoothSerialPortInfo _info;

        /**
         * Constructs the device menu item to allow the user to connect to a
         * paired device.
         * 
         * @param text
         *            The label of the menu item to display
         * @param info
         *            The Bluetooth Serial Port information of the paired device
         *            this menu item will invoke a connection to
         */
        DeviceMenuItem(final String text, final BluetoothSerialPortInfo info) {
            super(new StringProvider(text), 0x230020, 20);
            _info = info;
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * Invokes the connection to the device associated with the
                 * Bluetooth Serial Port information stored in this object.
                 * 
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    UiApplication.getUiApplication().pushScreen(
                            new SPPScreen(_info));
                    close(); // Close the current screen
                }
            }));
        }
    }
}
