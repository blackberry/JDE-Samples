/*
 * TouchDemo.java
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

package com.rim.samples.device.touchdemo;

import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample drawing application highlighting the capabilities of a touch screen
 * BlackBerry smartphone. The application's MainScreen class captures touch
 * events and uses SVG to draw lines between touch points as a user traces lines
 * on the screen. Menu items allow a user to erase the canvas or change the
 * color and width of the lines being drawn.
 */
public class TouchDemo extends UiApplication {
    /**
     * Entry point for this application.
     * 
     * @param args
     *            Command line arguments
     */
    public static void main(final String[] args) {
        new TouchDemo().enterEventDispatcher();
    }

    /**
     * Constructor
     */
    private TouchDemo() {
        if (Touchscreen.isSupported()) {
            final TouchDemoScreen screen = new TouchDemoScreen();
            pushScreen(screen);
        } else {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("This application requires a touch screen device.");
                    System.exit(0);
                }
            });
        }
    }
}
