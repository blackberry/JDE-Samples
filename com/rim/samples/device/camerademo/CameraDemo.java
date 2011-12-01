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

import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringUtilities;

/**
 * A sample application used to demonstrate the VideoControl.getSnapshot()
 * method. Creates a custom camera which can take snapshots from the
 * Blackberry's camera.
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
     * Constructs a new CameraDemo object
     */
    public CameraDemo() {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert("Click the trackball or screen to take a picture. You can change the image settings by selecting 'Encoding Settings' from the menu.");
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

    /**
     * Constructor. Initializes the camera and creates the UI.
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
        }
        // If not, display an error message to the user
        else {
            add(new RichTextField("Error connecting to camera."));
        }
    }

    /**
     * Displays the various encoding choices available
     */
    private final MenuItem _encodingMenuItem = new MenuItem(
            "Encoding Settings", 10, 100) {
        public void run() {
            final EncodingPropertiesScreen s =
                    new EncodingPropertiesScreen(_encodings, CameraScreen.this,
                            _indexOfEncoding);
            UiApplication.getUiApplication().pushModalScreen(s);
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

            if (_encodings != null) {
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
     * Prevent the save dialog from being displayed
     * 
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
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

            // Strings representing the four properties of an encoding as
            // returned by System.getProperty().
            final String encoding = "encoding";
            final String width = "width";
            final String height = "height";
            final String quality = "quality";

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
                } else if (properties[i].equals(quality)) {
                    // Set the new encoding's quality
                    ++i;
                    temp.setQuality(properties[i]);
                }
            }

            // If there is a leftover complete encoding, add it.
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
        // Initialize the screen
        final ImageScreen imageScreen = new ImageScreen(raw);

        // Push this screen to display it to the user
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
            switch (action) {
            case ACTION_INVOKE: // Trackball click
            {
                takePicture();
                return true;
            }
            }
        }
        return handled;
    }
}
