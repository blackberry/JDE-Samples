/*
 * VideoRecordingDemo.java
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

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * This application demonstrates how to capture and record video using the JSR
 * 135 Multi Media API (MMAPI). Video can be recorded from the camera to either
 * a file or to an output stream. This sample also demonstrates how to playback
 * the recorded video using the Multi Media API.
 */
public class VideoRecordingDemo extends UiApplication {
    private static final int ANY_ENCODING = 0;

    public static final int MIN_FILE_SYSTEM_SIZE = 1024 * 50; // 50 MB

    /**
     * Creates a new VideoRecordingDemo object
     */
    public VideoRecordingDemo() {
        // Check if video recording is enabled on this device
        final String[] encodings = VideoRecordingSetup.getVideoEncodings();
        if (encodings != null && encodings.length > 0) {
            if (Touchscreen.isSupported()) {
                Ui.getUiEngineInstance().setAcceptableDirections(
                        Display.DIRECTION_NORTH);
            }

            // Check if any file systems exist with adequate space to record to.
            final String[] fileSystems =
                    VideoRecordingSetup.getFileSystems(MIN_FILE_SYSTEM_SIZE);
            if (fileSystems.length > 0) {
                pushScreen(new VideoRecordingSetupScreen(encodings, fileSystems));
            } else {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert("No sufficiently large file systems detected!\n\nWithout an appropriate file system this demo cannot record video to a file.");
                        pushScreen(new VideoRecordingScreen(
                                encodings[ANY_ENCODING], null));
                    }
                });
            }
        } else {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("This device is not capable of recording video");
                    System.exit(0);
                }
            });
        }
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

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        new VideoRecordingDemo().enterEventDispatcher();
    }
}
