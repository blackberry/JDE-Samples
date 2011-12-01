/**
 * HelloWorld.java 
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

package com.rim.samples.device.helloworlddemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This class extends the UiApplication class, providing a graphical user
 * interface.
 */
public class HelloWorldDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final HelloWorldDemo theApp = new HelloWorldDemo();
        theApp.enterEventDispatcher();
    }

    /**
     * Creates a new HelloWorldDemo object
     */
    public HelloWorldDemo() {
        // Push a screen onto the UI stack for rendering.
        pushScreen(new HelloWorldScreen());
    }
}

/**
 * A class extending the MainScreen class, which provides default standard
 * behavior for BlackBerry GUI applications.
 */
final class HelloWorldScreen extends MainScreen {
    /**
     * Creates a new HelloWorldScreen object
     */
    HelloWorldScreen() {
        // Set the displayed title of the screen
        setTitle("Hello World Demo");

        // Add a read only text field (RichTextField) to the screen. The
        // RichTextField is focusable by default. Here we provide a style
        // parameter to make the field non-focusable.
        add(new RichTextField("Hello World!", Field.NON_FOCUSABLE));
    }

    /**
     * Displays a dialog box to the user with the text "Goodbye!" when the
     * application is closed.
     * 
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        // Display a farewell message before closing the application
        Dialog.alert("Goodbye!");
        super.close();
    }
}
