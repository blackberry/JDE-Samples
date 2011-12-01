/**
 * SocketDemo.java
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

package com.rim.samples.device.socketdemo;

import net.rim.device.api.ui.UiApplication;

/**
 * This sample enables client/server communication using a simple implementation
 * of TCP sockets. The client application allows the user to select direct TCP
 * as the connection method. If direct TCP is not selected, a proxy TCP
 * connection is opened using the BlackBerry MDS Connection Service. The server
 * application can be found in com/rim/samples/server/socketdemo.
 */
public class SocketDemo extends UiApplication {
    SocketDemoScreen _screen;

    /**
     * Entry point for application.
     * 
     * @param Command
     *            line arguments.
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final SocketDemo app = new SocketDemo();
        app.enterEventDispatcher();
    }

    // Constructor
    public SocketDemo() {
        // Create a new screen for the application.
        _screen = new SocketDemoScreen();

        // Push the screen onto the UI stack for rendering.
        pushScreen(_screen);
    }

    /**
     * Provides access to this application's UI screen
     * 
     * @return This application's UI screen.
     */
    SocketDemoScreen getScreen() {
        return _screen;
    }
}
