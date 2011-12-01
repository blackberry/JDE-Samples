/*
 * VideoRecordingSetupScreen.java
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

package com.rim.samples.device.videorecordingdemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A screen that allows a user to choose the encoding used to record videos and
 * the file system to record to.
 */
public final class VideoRecordingSetupScreen extends MainScreen implements
        FieldChangeListener {
    private static String FILE_SYSTEM_URI_HEADER = "file:///";

    private final ObjectChoiceField _encodings;
    private final ObjectChoiceField _fileSystems;
    private final ButtonField _launchRecorder;

    /**
     * Constructs a screen to setup the recording
     * 
     * @param encodings
     *            The list of video encodings this device supports
     * @param fileSystems
     *            The list of file systems the user can record video files to
     * 
     * @throws IllegalArgumentException
     *             Thrown if <code>encodings</code> or <code>fileSystems</code>
     *             is null or empty
     */
    public VideoRecordingSetupScreen(final String[] encodings,
            final String[] fileSystems) {
        if (encodings == null || encodings.length == 0 || fileSystems == null
                || fileSystems.length == 0) {
            throw new IllegalArgumentException(
                    "Encodings and file systems must be non-null and non-empty");
        }

        setTitle("Setup screen");

        _encodings = new ObjectChoiceField("Encoding:", encodings, 0);
        add(_encodings);

        _fileSystems = new ObjectChoiceField("File System", fileSystems, 0);
        add(_fileSystems);

        _launchRecorder =
                new ButtonField("Start recording", ButtonField.CONSUME_CLICK
                        | Field.FIELD_RIGHT);
        _launchRecorder.setChangeListener(this);
        add(_launchRecorder);
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _launchRecorder) {
            final String selectedEncoding =
                    (String) _encodings
                            .getChoice(_encodings.getSelectedIndex());
            final String selectedFileSystem =
                    (String) _fileSystems.getChoice(_fileSystems
                            .getSelectedIndex());
            String filePath = null;
            if (selectedFileSystem.equals("store/")) {
                filePath =
                        selectedFileSystem
                                + "home/user/videos/mmapi_rimlet.3GP";
            } else if (selectedFileSystem.equals("SDCard/")) {
                filePath =
                        selectedFileSystem
                                + "BlackBerry/videos/mmapi_rimlet.3GP";
            }

            UiApplication.getUiApplication().pushScreen(
                    new VideoRecordingScreen(selectedEncoding,
                            FILE_SYSTEM_URI_HEADER + filePath));
            close();
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }
}
