/*
 * AugmentedRealityDemo.java
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

package com.rim.samples.device.augmentedrealitydemo;

import net.rim.device.api.opengles.GLUtils;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This application shows an example of overlaying a graphical object on a
 * surface backed field to create an augmented reality experience. The
 * application displays a spinning 3D cube object on top of the camera view
 * finder. The cube changes its axis of rotation when the orientation of the
 * device changes.
 */
public final class AugmentedRealityDemo extends UiApplication {

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final AugmentedRealityDemo app = new AugmentedRealityDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new AugmentedRealityDemo object
     */
    public AugmentedRealityDemo() {
        // Check whether OpenGL is supported on the current BlackBerry
        // Smartphone
        if (GLUtils.isSupported()) {
            pushScreen(new ARDemoCameraScreen());
        } else {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("This device does not support OpenGL, exiting Augmented Reality Demo application...");
                    System.exit(0);
                }
            });
        }
    }
}
