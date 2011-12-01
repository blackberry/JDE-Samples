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
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.picker.FilePicker;

/**
 * A sample application to demonstrate the FilePicker class. The application's
 * GUI screen contains a button to display a FilePicker control. A check box is
 * used to enable or disable a filter which will filter files displayed in by
 * file extension as chosen in a choice field. An additional choice field allows
 * the end user to specify a view in which to open the FilePicker.
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
            FilePicker.Listener {
        private final LabelField _labelField;
        private final FilePicker _filePicker;
        private final ButtonField _buttonField;
        private final ObjectChoiceField _filterTextChoiceField;
        private final ObjectChoiceField _viewChoiceField;

        /**
         * Creates a new FilePickerDemoScreen object
         */
        FilePickerDemoScreen() {
            setTitle("File Picker Demo");

            // Get the FilePicker instance
            _filePicker = FilePicker.getInstance();
            _filePicker.setTitle("Choose file");

            // Initialize a check box for toggling the file filter
            final CheckboxField checkBox =
                    new CheckboxField("Filter", false, Field.FIELD_HCENTER);
            checkBox.setChangeListener(new FieldChangeListener() {
                /**
                 * @see FieldChangeListener#fieldChanged(Field, int)
                 */
                public void fieldChanged(final Field field, final int context) {
                    if (checkBox.getChecked()) {
                        // Set the FilePicker to filter by extension
                        FilePickerDemoScreen.this.setFilter(true);
                        _filterTextChoiceField.setEditable(true);
                        _viewChoiceField.setSelectedIndex(0);
                        _viewChoiceField.setEditable(false);
                    } else {
                        // Disable the filter
                        FilePickerDemoScreen.this.setFilter(false);
                        _filterTextChoiceField.setEditable(false);
                        _viewChoiceField.setEditable(true);
                    }
                }
            });
            add(checkBox);

            // Add a choice field for specifying file filter type
            String[] choices = new String[] { "All", "MP3", "JPG", "MPG" };
            _filterTextChoiceField =
                    new ObjectChoiceField("Filter extension: ", choices, 0);
            add(_filterTextChoiceField);
            _filterTextChoiceField.setChangeListener(new FieldChangeListener() {
                /**
                 * @see FieldChangeListener#fieldChanged(Field, int)
                 */
                public void fieldChanged(final Field field, final int context) {
                    FilePickerDemoScreen.this.setFilter(true);
                }
            });
            _filterTextChoiceField.setEditable(false);

            // Initialize a choice field for highlight style selection
            choices =
                    new String[] { "Default", "Pictures", "Ring Tones",
                            "Music", "Videos", "Voice Notes" };
            _viewChoiceField =
                    new ObjectChoiceField("View: ", choices,
                            FilePicker.VIEW_ALL);
            _viewChoiceField.setChangeListener(new FieldChangeListener() {
                /**
                 * @see FieldChangeListener#fieldChanged(Field, int)
                 */
                public void fieldChanged(final Field field, final int context) {
                    // Change the view type for the FilePicker
                    final int index = _viewChoiceField.getSelectedIndex();
                    switch (index) {
                    case FilePicker.VIEW_ALL:
                        _filePicker.setTitle("Choose file");
                        _filePicker.setView(FilePicker.VIEW_ALL);
                        break;
                    case FilePicker.VIEW_PICTURES:
                        _filePicker.setTitle("Choose picture");
                        _filePicker.setView(FilePicker.VIEW_PICTURES);
                        break;
                    case FilePicker.VIEW_RINGTONES:
                        _filePicker.setTitle("Choose ringtone");
                        _filePicker.setView(FilePicker.VIEW_RINGTONES);
                        break;
                    case 3:
                        _filePicker.setTitle("Choose music");
                        _filePicker.setView(FilePicker.VIEW_MUSIC);
                        break;
                    case 4:
                        _filePicker.setTitle("Choose video");
                        _filePicker.setView(FilePicker.VIEW_VIDEOS);
                        break;
                    case 5:
                        _filePicker.setTitle("Choose voice note");
                        _filePicker.setView(FilePicker.VIEW_VOICE_NOTES);
                        break;
                    }
                }
            });
            add(_viewChoiceField);

            add(new SeparatorField());

            // Add a button to display the FilePicker
            _buttonField =
                    new ButtonField("Choose File", Field.FIELD_HCENTER
                            | ButtonField.CONSUME_CLICK);
            _buttonField.setChangeListener(new FieldChangeListener() {
                /**
                 * @see FieldChangeListener#fieldChanged(Field, int)
                 */
                public void fieldChanged(final Field field, final int context) {
                    _filePicker.show();
                }
            });
            add(_buttonField);

            // Create a label field that displays the file chosen
            _labelField = new LabelField();
            add(_labelField);

            // Make this class a FilePicker listener
            _filePicker.setListener(this);
        }

        /**
         * Enables or disables a filter for the FilePicker
         * 
         * @param enabled
         *            True if displayed files should be filtered by extension.
         *            False otherwise.
         */
        private void setFilter(final boolean enabled) {
            if (enabled) {
                try {
                    String path = null;

                    // Set the filter and default directory for the FilePicker
                    switch (_filterTextChoiceField.getSelectedIndex()) {
                    case 0:
                        _filePicker.setFilter(null);
                        _filePicker.setPath(null);
                        break;
                    case 1:
                        _filePicker.setFilter(".mp3");
                        path = System.getProperty("fileconn.dir.music");
                        break;
                    case 2:
                        _filePicker.setFilter(".jpg");
                        path = System.getProperty("fileconn.dir.photos");
                        break;
                    case 3:
                        _filePicker.setFilter(".mpg");
                        path = System.getProperty("fileconn.dir.videos");
                        break;
                    }

                    if (path != null) {
                        // Set the directory to open the FilePicker in if the
                        // directory exists.
                        final FileConnection fconn =
                                (FileConnection) Connector.open(path);
                        if (fconn.exists()) {
                            _filePicker.setPath(path);
                        }
                    }
                } catch (final Exception ioe) {
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    Dialog.alert("Connector.open() threw "
                                            + ioe.toString());
                                }
                            });
                }
            } else {
                _filePicker.setFilter(null);
                _filePicker.setPath(null);
            }
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
         * @see MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            // Suppress the save dialog
            return true;
        }
    }
}
