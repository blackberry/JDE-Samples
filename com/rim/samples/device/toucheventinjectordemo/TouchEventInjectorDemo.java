/*
 * TouchEventInjectorDemo.java
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

package com.rim.samples.device.toucheventinjectordemo;

import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This sample application demonstrates the use of the EventInjector.TouchEvent
 * class to programatically inject touch screen events.
 */
public final class TouchEventInjectorDemo extends UiApplication {
    /**
     * Creates a new TouchEventInjectorDemo object
     */
    public TouchEventInjectorDemo() {
        // Check if current device has a touch screen
        if (Touchscreen.isSupported()) {
            final TouchEventInjectorScreen screen =
                    new TouchEventInjectorScreen();
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

    /**
     * Application entry point
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        final TouchEventInjectorDemo app = new TouchEventInjectorDemo();
        app.enterEventDispatcher();
    }
}
