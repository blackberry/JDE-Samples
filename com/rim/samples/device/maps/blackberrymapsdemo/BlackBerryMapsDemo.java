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

package com.rim.samples.device.maps.blackberrymapsdemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

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

    /**
     * Creates a new BlackBerryMapsDemo object
     */
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

    /**
     * Creates a new BlackBerryMapsDemoScreen object
     */
    public BlackBerryMapsDemoScreen() {
        setTitle("BlackBerry Maps Demo");

        _app = (BlackBerryMapsDemo) UiApplication.getUiApplication();

        final RichTextField rtf =
                new RichTextField("Select an option from the menu.",
                        Field.NON_FOCUSABLE);
        add(rtf);

        // Displays an InvokeContactScreen
        final MenuItem invokeContactItem =
                new MenuItem(new StringProvider("Invoke Contact"), 0x230010, 0);
        invokeContactItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final InvokeContactScreen invokeContactScreen =
                        new InvokeContactScreen();
                _app.pushScreen(invokeContactScreen);
            }
        }));

        // Displays an InvokeDefaultScreen
        final MenuItem invokeDefaultItem =
                new MenuItem(new StringProvider("Invoke Default"), 0x230020, 1);
        invokeDefaultItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final InvokeDefaultScreen invokeDefaultScreen =
                        new InvokeDefaultScreen();
                _app.pushScreen(invokeDefaultScreen);
            }
        }));

        // Displays an InvokeLocationDocumentScreen
        final MenuItem invokeLocationDocumentItem =
                new MenuItem(new StringProvider("Invoke Location Document"),
                        0x230030, 2);
        invokeLocationDocumentItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final InvokeLocationDocumentScreen invokeLocationDocumentScreen =
                        new InvokeLocationDocumentScreen();
                _app.pushScreen(invokeLocationDocumentScreen);
            }
        }));

        // Displays an InvokeMapViewScreen
        final MenuItem invokeMapViewItem =
                new MenuItem(new StringProvider("Invoke Map View"), 0x230040, 3);
        invokeMapViewItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final InvokeMapViewScreen invokeMapViewScreen =
                        new InvokeMapViewScreen();
                _app.pushScreen(invokeMapViewScreen);
            }
        }));

        addMenuItem(invokeContactItem);
        addMenuItem(invokeDefaultItem);
        addMenuItem(invokeLocationDocumentItem);
        addMenuItem(invokeMapViewItem);
    }
}
