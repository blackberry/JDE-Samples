/*
 * SpriteGameScreen.java
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

import net.rim.device.api.opengles.GLField;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.container.FullScreen;

/**
 * This Screen sub class is a container for a SpriteGameGLField
 */
public class SpriteGameScreen extends FullScreen {
    private SpriteGameGLField _glField = null;

    /**
     * Creates a new SpriteGameScreen object
     * 
     * @param version
     *            The version of OpenGL to use
     */
    SpriteGameScreen(final int version) {
        super(Screen.DEFAULT_CLOSE);
        displayGameScreen(version);
    }

    /**
     * Displays the game screen using the specified version of OpenGL ES
     * 
     * @param glVersion
     *            The version of OpenGL to use
     */
    private void displayGameScreen(final int glVersion) {
        if (glVersion == SpriteGame.OPENGLES11) {
            _glField = new SpriteGameGLField(GLField.VERSION_1_1);
            add(_glField);
        } else if (glVersion == SpriteGame.OPENGLES20) {
            _glField = new SpriteGameGLField(GLField.VERSION_2_0);
            add(_glField);
        }

        // Set the main field to get focus so it can process input events.
        setFieldWithFocus(_glField);
        getDelegate().setFieldWithFocus(_glField);
    }

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public boolean onClose() {
        Sprite.cleanup();

        return super.onClose();
    }
}
