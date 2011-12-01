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
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

final class AppScreen extends MainScreen {

    private BluetoothSerialPortInfo[] _portInfo;

    AppScreen() {
        setTitle("Bluetooth Sample");

        // Determine if this BlackBerry model or simulator supports Bluetooth.
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
                if (serviceName.indexOf("Hi") != -1
                        || serviceName.indexOf("COM") != -1) {
                    final DeviceMenuItem deviceMenuItem =
                            new DeviceMenuItem("Connect to: "
                                    + _portInfo[count].getDeviceName(),
                                    _portInfo[count]);
                    addMenuItem(deviceMenuItem);
                }
            }

            addMenuItem(_listenItem);
        } else {
            add(new LabelField(
                    "Bluetooth is not supported on this BlackBerry or simulator."));
        }
    }

    // //////////////////////////////////////////////////////////
    // Menu Items //
    // //////////////////////////////////////////////////////////
    private final MenuItem _listenItem = new MenuItem("Listen for connections",
            30, 30) {
        public void run() {
            UiApplication.getUiApplication().pushScreen(new SPPScreen(null));
            close(); // close the current screen
        }
    };

    // //////////////////////////////////////////////////////////
    // Custom Item //
    // //////////////////////////////////////////////////////////
    private final class DeviceMenuItem extends MenuItem {
        private final BluetoothSerialPortInfo _info;

        DeviceMenuItem(final String text, final BluetoothSerialPortInfo info) {
            super(text, 20, 20);
            _info = info;
        }

        public void run() {
            UiApplication.getUiApplication().pushScreen(new SPPScreen(_info));
            close(); // close the current screen
        }
    };
}
