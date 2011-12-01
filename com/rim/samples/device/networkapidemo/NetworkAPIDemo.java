/**
 * NetworkAPIDemo.java
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

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A sample application to demonstrate the Network API.
 * 
 * Clicking the "Transport Info" button shows network transports availability
 * and coverage. Obtaining a connection through available transports can be
 * tested by clicking the "Connection Tests" button. The "UDP Client" button
 * displays a screen which acts as a UDP client for the UDP server component
 * found in the com.rim.samples.server.udpdemo directory.
 */
public final class NetworkAPIDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new NetworkAPIDemo().enterEventDispatcher();
    }

    /**
     * Creates a new NetworkAPIDemo object
     */
    public NetworkAPIDemo() {
        // Display the main screen
        pushScreen(new NetworkAPIDemoScreen());
    }

    /**
     * The main application screen showing the "Transport Info" and
     * "Connection Tests" buttons.
     */
    private static class NetworkAPIDemoScreen extends MainScreen implements
            FieldChangeListener {
        private final ButtonField _transportInfoBtn;
        private final ButtonField _connectionTestsBtn;
        private final ButtonField _udpBtn;
        private final UiApplication _uiApp;

        /**
         * Creates a new NetworkAPIDemoScreen object
         */
        NetworkAPIDemoScreen() {
            // Initialize UI components
            setTitle("Network API Demo");
            _transportInfoBtn =
                    new ButtonField("Transport Info", ButtonField.NEVER_DIRTY
                            | Field.FIELD_HCENTER | ButtonField.CONSUME_CLICK);
            _connectionTestsBtn =
                    new ButtonField("Connection Tests", ButtonField.NEVER_DIRTY
                            | Field.FIELD_HCENTER | ButtonField.CONSUME_CLICK);
            _udpBtn =
                    new ButtonField("UDP Client", ButtonField.NEVER_DIRTY
                            | Field.FIELD_HCENTER | ButtonField.CONSUME_CLICK);

            _transportInfoBtn.setChangeListener(this);
            _connectionTestsBtn.setChangeListener(this);
            _udpBtn.setChangeListener(this);

            // Add components to screen
            add(_transportInfoBtn);
            add(_connectionTestsBtn);
            add(_udpBtn);

            _uiApp = UiApplication.getUiApplication();
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            if (field == _transportInfoBtn) {
                doTransportInfoBtn();
            } else if (field == _connectionTestsBtn) {
                doConnectBestEffortBtn();
            } else if (field == _udpBtn) {
                doUDPBtn();
            }
        }

        /**
         * Displays a TransportInfoScreen
         */
        private void doTransportInfoBtn() {
            _uiApp.pushScreen(new TransportInfoScreen());
        }

        /**
         * Displays a ConnectionTestsScreen
         */
        private void doConnectBestEffortBtn() {
            _uiApp.pushScreen(new ConnectionTestsScreen());
        }

        /**
         * Displays a UDPClientScreen
         */
        private void doUDPBtn() {
            _uiApp.pushScreen(new UDPClientScreen());
        }
    }
}
