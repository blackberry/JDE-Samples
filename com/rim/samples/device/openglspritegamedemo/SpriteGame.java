/*
 * SpriteGame.java
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

package com.rim.samples.device.openglspritegamedemo;

import net.rim.device.api.opengles.GLUtils;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * The UiApplication class for the sprite game. This application demonstrates
 * how you can use OpenGL 1.0 or 2.0 to create a simple game.
 */
public class SpriteGame extends UiApplication {
    final static int OPENGLES11 = 0;
    final static int OPENGLES20 = 1;

    /**
     * Creates a new SpriteGame object
     */
    SpriteGame() {
        // Make sure that the device supports OpenGL ES.
        // If it doesn't, display a dialog and exit the application.
        if (!GLUtils.isSupported()) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("This device does not support OpenGL ES. Closing application...");
                    System.exit(0);
                }
            });
        } else {
            final SpriteGameSplashScreen splashScreen =
                    new SpriteGameSplashScreen();
            pushScreen(splashScreen);
        }
    }

    /**
     * Entry point for the application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        final SpriteGame app = new SpriteGame();
        app.enterEventDispatcher();
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
