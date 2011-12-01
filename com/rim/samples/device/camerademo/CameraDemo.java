/*
 * CameraDemo.java
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

package com.rim.samples.device.camerademo;

import java.util.Vector;

import javax.microedition.amms.control.camera.ZoomControl;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.amms.control.camera.EnhancedFocusControl;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.input.InputSettings;
import net.rim.device.api.ui.input.NavigationDeviceSettings;
import net.rim.device.api.util.StringProvider;
import net.rim.device.api.util.StringUtilities;

/**
 * A sample application used to demonstrate the VideoControl.getSnapshot()
 * method. This application can take snapshots using the BlackBerry device's
 * camera. Swiping the trackpad in a north or south direction will zoom the
 * viewfinder in and out.
 */
public final class CameraDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final CameraDemo demo = new CameraDemo();
        demo.enterEventDispatcher();
    }

    /**
     * Creates a new CameraDemo object
     */
    public CameraDemo() {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert("Click the trackball or screen to take a picture. Zoom in or out by swiping the trackpad up or down. You can change the image settings by selecting 'Encoding Settings' from the menu.");
            }
        });
        final CameraScreen screen = new CameraScreen();
        pushScreen(screen);
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

/**
 * A UI screen to display the camera display and buttons
 */
final class CameraScreen extends MainScreen {
    /** The camera's video controller */
    private VideoControl _videoControl;

    /** The field containing the feed from the camera */
    private Field _videoField;

    /** An array of valid snapshot encodings */
    private EncodingProperties[] _encodings;

    private int _indexOfEncoding = 0;

    private EnhancedFocusControl _efc;
    private ZoomControl _zoomControl;

    /**
     * Constructor - initializes the camera and creates the UI.
     */
    public CameraScreen() {
        // Set the title of the screen
        setTitle("Camera Demo");

        // Initialize the camera object and video field
        initializeCamera();

        // Initialize the list of possible encodings
        initializeEncodingList();

        // If the field was constructed successfully, create the UI
        if (_videoField != null) {
            createUI();
            addMenuItem(_encodingMenuItem);
            addMenuItem(_toggleAutoFocusMenuItem);
        }
        // If not, display an error message to the user
        else {
            add(new RichTextField("Error connecting to camera."));
        }

        // Allow the screen to capture trackpad swipes
        final InputSettings settings =
                NavigationDeviceSettings.createEmptySet();
        settings.set(NavigationDeviceSettings.DETECT_SWIPE, 1);
        addInputSettings(settings);
    }

    /**
     * @see net.rim.device.api.ui.Field#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent event) {
        if (event.getEvent() == TouchEvent.GESTURE) {
            final TouchGesture gesture = event.getGesture();

            // Handle only trackpad swipe gestures
            if (gesture.getEvent() == TouchGesture.NAVIGATION_SWIPE) {
                final int direction = gesture.getSwipeDirection();

                Application.getApplication().invokeLater(new Runnable() {
                    public void run() {
                        // Determine the direction of the swipe
                        if (direction == TouchGesture.SWIPE_NORTH) {
                            _zoomControl.setDigitalZoom(ZoomControl.NEXT);
                        } else if (direction == TouchGesture.SWIPE_SOUTH) {
                            _zoomControl.setDigitalZoom(ZoomControl.PREVIOUS);
                        }
                    }
                });

                return true;
            }
        }

        return false;
    }

    /**
     * Displays the various encoding choices available
     */
    private final MenuItem _encodingMenuItem = new MenuItem(new StringProvider(
            "Encoding Settings"), 0x230010, 0) {
        public void run() {
            final EncodingPropertiesScreen s =
                    new EncodingPropertiesScreen(_encodings, CameraScreen.this,
                            _indexOfEncoding);
            UiApplication.getUiApplication().pushModalScreen(s);
        }
    };

    /**
     * Toggles auto focus for the camera
     */
    private final MenuItem _toggleAutoFocusMenuItem = new MenuItem(
            new StringProvider("Toggle Auto-focus"), 0x230020, 0) {
        public void run() {
            try {
                if (_efc != null) {
                    if (_efc.isAutoFocusLocked()) {
                        _efc.stopAutoFocus();
                    } else {
                        _efc.startAutoFocus();
                    }
                } else {
                    CameraDemo
                            .errorDialog("ERROR: Focus control not initialized.");
                }
            } catch (final Exception e) {
                CameraDemo.errorDialog("ERROR " + e.getClass() + ":  "
                        + e.getMessage());
            }
        }
    };

    /**
     * Takes a picture with the selected encoding settings
     */
    public void takePicture() {
        try {
            // A null encoding indicates that the camera should
            // use the default snapshot encoding.
            String encoding = null;

            if (_encodings != null && _encodings.length > 0) {
                // Use the user-selected encoding
                encoding = _encodings[_indexOfEncoding].getFullEncoding();
            }

            // Retrieve the raw image from the VideoControl and
            // create a screen to display the image to the user.
            createImageScreen(_videoControl.getSnapshot(encoding));
        } catch (final Exception e) {
            CameraDemo.errorDialog("ERROR " + e.getClass() + ":  "
                    + e.getMessage());
        }
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * Initializes the Player, VideoControl and VideoField
     */
    private void initializeCamera() {
        try {
            // Create a player for the Blackberry's camera
            final Player player = Manager.createPlayer("capture://video");

            // Set the player to the REALIZED state (see Player javadoc)
            player.realize();

            // Grab the video control and set it to the current display
            _videoControl = (VideoControl) player.getControl("VideoControl");

            if (_videoControl != null) {
                // Create the video field as a GUI primitive (as opposed to a
                // direct video, which can only be used on platforms with
                // LCDUI support.)
                _videoField =
                        (Field) _videoControl.initDisplayMode(
                                GUIControl.USE_GUI_PRIMITIVE,
                                "net.rim.device.api.ui.Field");
                _videoControl.setDisplayFullScreen(true);
                _videoControl.setVisible(true);
            }

            // Set the player to the STARTED state (see Player javadoc)
            player.start();

            // Enable auto-focus for the camera
            _efc =
                    (EnhancedFocusControl) player
                            .getControl("net.rim.device.api.amms.control.camera.EnhancedFocusControl");

            // Enable zoom for the camera
            _zoomControl =
                    (ZoomControl) player
                            .getControl("javax.microedition.amms.control.camera.ZoomControl");
        } catch (final Exception e) {
            CameraDemo.errorDialog("ERROR " + e.getClass() + ":  "
                    + e.getMessage());
        }
    }

    /**
     * Initialize the list of encodings
     */
    private void initializeEncodingList() {
        try {
            // Retrieve the list of valid encodings
            final String encodingString =
                    System.getProperty("video.snapshot.encodings");

            // Extract the properties as an array of word
            final String[] properties =
                    StringUtilities.stringToKeywords(encodingString);

            // The list of encodings
            final Vector encodingList = new Vector();

            // Strings representing the three properties of an encoding as
            // returned by System.getProperty().
            final String encoding = "encoding";
            final String width = "width";
            final String height = "height";

            EncodingProperties temp = null;

            for (int i = 0; i < properties.length; ++i) {
                if (properties[i].equals(encoding)) {
                    if (temp != null && temp.isComplete()) {
                        // Add a new encoding to the list if it has been
                        // properly set.
                        encodingList.addElement(temp);
                    }
                    temp = new EncodingProperties();

                    // Set the new encoding's format
                    ++i;
                    temp.setFormat(properties[i]);
                } else if (properties[i].equals(width)) {
                    // Set the new encoding's width
                    ++i;
                    temp.setWidth(properties[i]);
                } else if (properties[i].equals(height)) {
                    // Set the new encoding's height
                    ++i;
                    temp.setHeight(properties[i]);
                }
            }

            // If there is a leftover complete encoding, add it
            if (temp != null && temp.isComplete()) {
                encodingList.addElement(temp);
            }

            // Convert the Vector to an array for later use
            _encodings = new EncodingProperties[encodingList.size()];
            encodingList.copyInto(_encodings);
        } catch (final Exception e) {
            // Something is wrong, indicate that there are no encoding options
            _encodings = null;
            CameraDemo.errorDialog(e.toString());
        }
    }

    /**
     * Adds the VideoField to the screen
     */
    private void createUI() {
        // Add the video field to the screen
        add(_videoField);
    }

    /**
     * Create a screen used to display a snapshot
     * 
     * @param raw
     *            A byte array representing an image
     */
    private void createImageScreen(final byte[] raw) {
        // Create image to be displayed
        final EncodedImage encodedImage =
                EncodedImage.createEncodedImage(raw, 0, raw.length);

        // Initialize the screen
        final ImageScreen imageScreen = new ImageScreen(raw, encodedImage);

        // Push screen to display it to the user
        UiApplication.getUiApplication().pushScreen(imageScreen);

    }

    /**
     * Sets the index of the encoding in the 'encodingList' Vector
     * 
     * @param index
     *            The index of the encoding in the 'encodingList' Vector
     */
    public void setIndexOfEncoding(final int index) {
        _indexOfEncoding = index;
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        final boolean handled = super.invokeAction(action);

        if (!handled) {
            if (action == ACTION_INVOKE) {
                takePicture();
                return true;
            }
        }

        return handled;
    }
}
