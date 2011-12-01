/*
 * ImageScreen.java
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

import java.io.IOException;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.extension.container.ZoomScreen;
import net.rim.device.api.util.StringProvider;

/**
 * A screen to display an image taken with the camera demo
 */
public final class ImageScreen extends ZoomScreen {
    private static final String FILE_NAME = System
            .getProperty("fileconn.dir.photos")
            + "IMAGE";
    private static final String EXTENSION = ".bmp";

    private static int _counter; // A counter for the number of snapshots taken

    private final SaveMenuItem _saveMenuItem;

    /**
     * Creates a new ImageScreen object
     * 
     * @param raw
     *            A byte array representing an image
     * @param image
     *            Image to display
     */
    public ImageScreen(final byte[] raw, final EncodedImage image) {
        super(image);

        _saveMenuItem = new SaveMenuItem("Save", raw);
        addMenuItem(_saveMenuItem);

        // Initialize the zoom screen to be zoomed all the way out
        setViewableArea(0, 0, 0);
    }

    /**
     * @see ZoomScreen#zoomedOutNearToFit()
     */
    public void zoomedOutNearToFit() {
        close();
    }

    /**
     * A MenuItem class to save the displayed image as a file
     */
    private final class SaveMenuItem extends MenuItem {
        private final byte[] _raw;

        /**
         * Creates a new SaveMenuItem
         * 
         * @param text
         *            Label text for the menu item
         * @param raw
         *            Raw image data
         */
        SaveMenuItem(final String text, final byte[] raw) {
            super(new StringProvider(text), 0x230010, 0);

            _raw = raw;
            setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    try {
                        // Create connection to a file that may or
                        // may not exist.
                        FileConnection file =
                                (FileConnection) Connector.open(FILE_NAME
                                        + _counter + EXTENSION);

                        // If the file exists, increment the counter and try
                        // again until we have a filename for a file that hasn't
                        // been created yet.
                        while (file.exists()) {
                            file.close();
                            ++_counter;
                            file =
                                    (FileConnection) Connector.open(FILE_NAME
                                            + _counter + EXTENSION);
                        }

                        // We know the file doesn't exist yet, so create it
                        file.create();

                        // Write the image to the file
                        final OutputStream out = file.openOutputStream();
                        out.write(_raw);

                        // Close the connections
                        out.close();
                        file.close();

                        // Inform the user where the file has been saved
                        Dialog.inform("Saved to " + FILE_NAME + _counter
                                + EXTENSION);

                        // Remove the save menu item, as the file has
                        // already been saved.
                        ImageScreen.this.removeMenuItem(_saveMenuItem);

                        // Don't close the screen directly, leave it open to
                        // allow the user to send the image.
                    } catch (final IOException ioe) {
                        CameraDemo.errorDialog("ERROR " + ioe.getClass()
                                + ":  " + ioe.getMessage());
                    }
                }
            }));
        }
    }
}
