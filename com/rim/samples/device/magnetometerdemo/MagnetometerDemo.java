/*
 * MagnetometerDemo.java
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

package com.rim.samples.device.magnetometerdemo;

import net.rim.device.api.system.MagnetometerSensor;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This application uses the MagnetometerSensor API to create a simple compass
 * and magnetic field strength reader. Using a MagnetometerListener
 * implementation, data is captured from the magnetometer and displayed on the
 * screen as it comes in. The user can also take a data snapshot for a point in
 * time by invoking a button on the screen.
 */
class MagnetometerDemo extends UiApplication {
    /**
     * Entry point for the application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String args[]) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final MagnetometerDemo app = new MagnetometerDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new MagnetometerDemo object
     */
    public MagnetometerDemo() {
        // Check if the device has a MagnetometerSensor
        if (MagnetometerSensor.isSupported()) {
            final MagnetometerDemoScreen screen =
                    new MagnetometerDemoScreen(this);
            pushScreen(screen);
        } else // No MagnetometerSensor on the device
        {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("Magnetometer not present on this device");
                    System.exit(0);
                }
            });
        }
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }
}
