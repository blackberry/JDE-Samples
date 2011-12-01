/*
 * FilePickerDemo.java
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

package com.rim.samples.device.ui.filepickerdemo;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.picker.FilePicker;

/**
 * A sample application to demonstrate the FilePicker class
 */
public class FilePickerDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final FilePickerDemo app = new FilePickerDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new FilePickerDemo object
     */
    public FilePickerDemo() {
        pushScreen(new FilePickerDemoScreen());
    }

    /**
     * MainScreen class for the FilePickerDemo application
     */
    static class FilePickerDemoScreen extends MainScreen implements
            FilePicker.Listener, FieldChangeListener {
        private final LabelField _labelField;
        private final FilePicker _filePicker;
        private final ButtonField _buttonField;

        /**
         * Creates a new FilePickerDemoScreen object
         */
        FilePickerDemoScreen() {
            // Initialize screen
            setTitle("File Picker Demo");
            _buttonField =
                    new ButtonField("Choose File", ButtonField.CONSUME_CLICK
                            | ButtonField.NEVER_DIRTY);
            _buttonField.setChangeListener(this);
            add(_buttonField);
            _labelField = new LabelField();
            add(_labelField);

            // Get the FilePicker instance
            _filePicker = FilePicker.getInstance();

            // Set the file picker to only display mp3 files
            _filePicker.setFilter(".mp3");

            try {
                // Obtain the default system music directory to open
                // the file picker in.
                final String path = System.getProperty("fileconn.dir.music");

                // Set the directory to open the file picker in if the
                // directory exists
                final FileConnection fconn =
                        (FileConnection) Connector.open(path);
                if (fconn.exists()) {
                    _filePicker.setPath(path);
                }
            } catch (final Exception ioe) {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert("Connector.open() threw " + ioe.toString());
                    }
                });
            }

            // Make this class a file picker listener
            _filePicker.setListener(this);
        }

        /**
         * @see FilePicker.Listener#selectionDone(String)
         */
        public void selectionDone(final String selection) {
            if (selection != null && selection.length() > 0) {
                // Display the chosen file on the screen
                _labelField.setText("File selected: " + selection);
            }
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            if (field == _buttonField) {
                _filePicker.show();
            }
        }
    }
}
