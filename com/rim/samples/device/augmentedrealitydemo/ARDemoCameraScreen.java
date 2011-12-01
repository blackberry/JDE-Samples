/*
 * ARDemoCameraScreen.java
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

import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.ComponentCanvas;
import net.rim.device.api.ui.container.MainScreen;

/**
 * MainScreen subclass for the Augmented Reality Demo application. This screen
 * positions a rotating 3D cube on top of the camera view finder. The cube
 * changes its axis of rotation when the orientation of the device changes.
 */
public final class ARDemoCameraScreen extends MainScreen {
    private Field _cameraField;
    private Player _player;
    private DemoGLField _glField;
    private static final int GL_FIELD_HEIGHT = Display.getHeight() / 3;
    private static final int GL_FIELD_WIDTH = GL_FIELD_HEIGHT;
    private static final int GL_FRAME_RATE = 60;

    /**
     * Creates a new ARDemoCameraScreen object
     */
    public ARDemoCameraScreen() {
        // Set the title of the screen
        setTitle("Augmented Reality Demo");

        // Initialize the camera object and camera field
        initializeCamera();

        // If the camera field was constructed successfully, create the UI
        if (_cameraField != null) {
            // Add the camera field to a new ComponentCanvas
            final ComponentCanvas compCanvas =
                    new ComponentCanvas(Display.getWidth(), Display.getHeight());
            compCanvas.add(_cameraField, 0, 0);

            // Initialize GLField
            _glField = new DemoGLField(GL_FIELD_WIDTH, GL_FIELD_HEIGHT);
            _glField.setTargetFrameRate(GL_FRAME_RATE);

            // Add the GLField to the canvas
            compCanvas.add(_glField, 100, 100);

            // Add the ComponentCanvas to the screen
            add(compCanvas);
        } else {
            add(new RichTextField("Error connecting to camera"));
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        if (_player != null) {
            try {
                _player.close();
            } catch (final Exception e) {
            }
        }

        _glField.cleanUp();

        super.close();
    }

    /**
     * Initializes the Player, VideoControl and camera field
     */
    private void initializeCamera() {
        try {
            // Create a player for the camera view finder
            _player =
                    javax.microedition.media.Manager
                            .createPlayer("capture://video");

            // Set the player to the REALIZED state
            _player.realize();

            // Get the video control
            final VideoControl videoControl =
                    (VideoControl) _player.getControl("VideoControl");

            if (videoControl != null) {
                // Create the video field as a GUI primitive
                _cameraField =
                        (Field) videoControl.initDisplayMode(
                                GUIControl.USE_GUI_PRIMITIVE,
                                "net.rim.device.api.ui.Field");
                videoControl.setDisplayFullScreen(true);
                videoControl.setVisible(true);
            }

            // Set the player to the STARTED state
            _player.start();
        } catch (final Exception e) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("ERROR " + e.getClass() + ":  "
                            + e.getMessage());
                }
            });
        }
    }
}
