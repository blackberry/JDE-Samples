/*
 * BluetoothJSR82Demo.java
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

package com.rim.samples.device.bluetoothjsr82demo;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A sample application that demonstrates the JSR 82 API by establishing a
 * Bluetooth connection between two BlackBerry smartphone devices through a
 * given protocol.
 * 
 * This application uses the JSR 82 API to allow a device to act as a server or
 * a client in a Bluetooth connection. The server waits for a connection from a
 * client while the client inquires as to the available devices and chooses
 * which device to connect to. The server is identified by a service name
 * (labeled as SERVICE_NAME), which can be retrieved in accordance with the JSR
 * 82 specifications.
 */
public class BluetoothJSR82Demo extends UiApplication {
    static final int SPP_UUID = 0x8889, OPP_UUID = 0x1105, L2CAP_UUID = 0x8888;

    private static final int CLIENT_CHOICE = 0, SERVER_CHOICE = 1;
    private static final String[] CHOICES = { "Client", "Server", "Exit" };
    private static final String[] DEMO_CHOICES = { "SPP", "OPP", "L2CAP",
            "Exit" };
    private static final int[] DEMO_UUIDS = { SPP_UUID, OPP_UUID, L2CAP_UUID,
            -1 };

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final BluetoothJSR82Demo app = new BluetoothJSR82Demo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new BluetoothJSR82Demo object
     */
    public BluetoothJSR82Demo() {
        // If the device supports JSR 82 then ask the user if they want to set
        // up a client or server.
        if (System.getProperty("bluetooth.api.version") != null) {
            initializeMode();
        } else {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    Dialog.alert("JSR 82 Bluetooth APIs not present");
                    System.exit(0);
                }
            });
        }
    }

    /**
     * Prompts the user to set up bluetooth communication as a client or a
     * server and begins to set up the connection.
     */
    private void initializeMode() {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                final int choice =
                        Dialog.ask("Select Mode", CHOICES, CLIENT_CHOICE);
                if (choice == CLIENT_CHOICE) {
                    final int uuid =
                            Dialog.ask("Select Profile", DEMO_CHOICES,
                                    DEMO_UUIDS, SPP_UUID);
                    if (uuid == -1) {
                        System.exit(0);
                    } else {
                        pushScreen(new ClientScreen(uuid));
                    }
                } else if (choice == SERVER_CHOICE) {
                    final int uuid =
                            Dialog.ask("Select Demo", DEMO_CHOICES, DEMO_UUIDS,
                                    SPP_UUID);
                    if (uuid == -1) {
                        System.exit(0);
                    } else {
                        pushScreen(new ServerScreen(uuid));
                    }
                } else {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                Dialog.alert(message);
            }
        });
    }

    /**
     * MainScreen class for the BluetoothJSR82Demo application
     */
    static class BluetoothJSR82DemoScreen extends MainScreen {
        /**
         * Creates a new BluetoothJSR82DemoScreen object
         */
        BluetoothJSR82DemoScreen() {
            setTitle("Bluetooth JSR82 Demo");
        }
    }
}
