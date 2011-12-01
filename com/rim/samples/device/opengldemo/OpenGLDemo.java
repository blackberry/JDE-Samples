/*
 * OpenGLDemo.java
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

package com.rim.samples.device.opengldemo;

import net.rim.device.api.opengles.GLUtils;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application demonstrating the leveraging of OpenGL APIs on a
 * BlackBerry Smartphone. The application does not incorporate any user
 * interaction, but simply renders a 3D representation of a spinning cube on a
 * plain black background.
 */
public final class OpenGLDemo extends UiApplication {
    /**
     * Entry point for this application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final OpenGLDemo app = new OpenGLDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new OpenGLDemo object
     */
    public OpenGLDemo() {
        // Check whether OpenGL is supported on the current BlackBerry
        // Smartphone
        if (GLUtils.isSupported()) {
            final OpenGLScreen screen = new OpenGLScreen(new CubeRenderer());
            pushScreen(screen);
        } else {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("This device does not support OpenGL, exiting OpenGL Demo application...");
                    System.exit(0);
                }
            });
        }
    }
}
