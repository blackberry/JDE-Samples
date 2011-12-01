/*
 * BlackBerryMapsDemo.java
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

package com.rim.samples.device.blackberrymapsdemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample features a main screen including menu items which display
 * respective screens highlighting specific aspects of the BlackBerry Maps
 * API's. Each of these screens offer menu items that will invoke the BlackBerry
 * Maps application in some manner. See the GPS and BlackBerry Maps Development
 * Guide for more information.
 */
public final class BlackBerryMapsDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final BlackBerryMapsDemo app = new BlackBerryMapsDemo();
        app.enterEventDispatcher();
    }

    // Constructor
    public BlackBerryMapsDemo() {
        final BlackBerryMapsDemoScreen screen = new BlackBerryMapsDemoScreen();
        pushScreen(screen);
    }
}

/**
 * The main screen for the application. Additional screens can be launched via
 * menu items.
 */
final class BlackBerryMapsDemoScreen extends MainScreen {
    BlackBerryMapsDemo _app;

    // Constructor
    BlackBerryMapsDemoScreen() {
        setTitle("BlackBerry Maps Demo");

        _app = (BlackBerryMapsDemo) UiApplication.getUiApplication();

        final RichTextField rtf =
                new RichTextField("Select an option from the menu.",
                        Field.NON_FOCUSABLE);
        add(rtf);

        addMenuItem(invokeContactItem);
        addMenuItem(invokeDefaultItem);
        addMenuItem(invokeLocationDocumentItem);
        addMenuItem(invokeMapViewItem);
    }

    // ///////////////////////
    // / MenuItem classes ///
    // ///////////////////////

    /**
     * Displays an InvokeContactScreen
     */
    private final MenuItem invokeContactItem = new MenuItem("Invoke Contact",
            0, 0) {
        public void run() {
            final InvokeContactScreen invokeContactScreen =
                    new InvokeContactScreen();
            _app.pushScreen(invokeContactScreen);
        }
    };

    /**
     * Displays an InvokeDefaultScreen
     */
    private final MenuItem invokeDefaultItem = new MenuItem("Invoke Default",
            0, 0) {
        public void run() {
            final InvokeDefaultScreen invokeDefaultScreen =
                    new InvokeDefaultScreen();
            _app.pushScreen(invokeDefaultScreen);
        }
    };

    /**
     * Displays an InvokeLocationDocumentScreen
     */
    private final MenuItem invokeLocationDocumentItem = new MenuItem(
            "Invoke Location Document", 0, 0) {
        public void run() {
            final InvokeLocationDocumentScreen invokeLocationDocumentScreen =
                    new InvokeLocationDocumentScreen();
            _app.pushScreen(invokeLocationDocumentScreen);
        }
    };

    /**
     * Displays an InvokeMapViewScreen
     */
    private final MenuItem invokeMapViewItem = new MenuItem("Invoke Map View",
            0, 0) {
        public void run() {
            final InvokeMapViewScreen invokeMapViewScreen =
                    new InvokeMapViewScreen();
            _app.pushScreen(invokeMapViewScreen);
        }
    };
}
