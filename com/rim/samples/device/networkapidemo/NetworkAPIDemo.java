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

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A sample application to demonstrate the Network API.
 * 
 * Clicking the "Transport Info" button shows network transports availability
 * and coverage. Obtaining a connection through available transports can be
 * tested by clicking the "Connection Tests" button.
 */
public class NetworkAPIDemo extends UiApplication {
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
        // Button that triggers a display of network transports' availability
        // and coverage information screen
        private final FullWidthButton _transportInfoBtn;
        // Button that triggers a display of network connection test screen
        private final FullWidthButton _connectionTestsBtn;

        /**
         * Creates a new NetworkAPIDemoScreen object
         */
        public NetworkAPIDemoScreen() {
            // Sets screen's title to "Network API Demo"
            setTitle(new LabelField("Network API Demo", DrawStyle.ELLIPSIS
                    | Field.USE_ALL_WIDTH));

            // "Transport Info" button
            _transportInfoBtn =
                    new FullWidthButton("Transport Info",
                            ButtonField.CONSUME_CLICK);
            // "Connection Tests" button
            _connectionTestsBtn =
                    new FullWidthButton("Connection Tests",
                            ButtonField.CONSUME_CLICK);

            _transportInfoBtn.setChangeListener(this);
            _connectionTestsBtn.setChangeListener(this);

            add(_transportInfoBtn);
            add(_connectionTestsBtn);
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            // Prevent the save dialog from being displayed
            return true;
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            if (field == _transportInfoBtn) {
                doTransportInfoBtn();
            } else if (field == _connectionTestsBtn) {
                doConnectBestEffortBtn();
            }
        }

        /**
         * Displays a TransportInfoScreen
         */
        private void doTransportInfoBtn() {
            UiApplication.getUiApplication().pushScreen(
                    new TransportInfoScreen());
        }

        /**
         * Displays a ConnectionTestsScreen
         */
        private void doConnectBestEffortBtn() {
            UiApplication.getUiApplication().pushScreen(
                    new ConnectionTestsScreen());
        }
    }
}
