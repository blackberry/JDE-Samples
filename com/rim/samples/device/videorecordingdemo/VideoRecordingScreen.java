/*
 * VideoRecordingScreen.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This screen allows the user to record videos to a file or to a stream and
 * enables the user to open the VideoPlaybackScreen to play the recorded video.
 */
public class VideoRecordingScreen extends MainScreen {
    private boolean _pendingCommit;
    private boolean _committed;
    private boolean _recording;

    private String _videoFile;

    private Player _player;
    private VideoControl _videoControl;
    private RecordControl _recordControl;
    private boolean _displayVisible;
    private boolean _recordToStream;

    private ObjectChoiceField _recordLocation;

    private ByteArrayOutputStream _outStream;

    /**
     * Constructs a screen to display and record the video being captured from
     * the device's camera.
     * 
     * @param encoding
     *            The non-null video encoding to be used when recording video to
     *            a file
     * @param fileSystem
     *            The file system to record the video file to, <code>null</code>
     *            if no file system was chosen
     * 
     * @throws NullPointerException
     *             Thrown if <code>encoding</code> or <code>filePath</code> is
     *             null.
     */
    public VideoRecordingScreen(final String encoding, final String filePath) {
        if (encoding == null) {
            throw new NullPointerException("Video encoding can not be null");
        }
        if (filePath == null) {
            throw new NullPointerException("File path can not be null");
        }

        try {
            // Start capturing video from the camera
            _player =
                    javax.microedition.media.Manager
                            .createPlayer("capture://video?" + encoding);
            _player.start();

            _videoControl = (VideoControl) _player.getControl("VideoControl");
            _recordControl =
                    (RecordControl) _player.getControl("RecordControl");

            // Initialize the video display
            final Field videoField =
                    (Field) _videoControl.initDisplayMode(
                            GUIControl.USE_GUI_PRIMITIVE,
                            "net.rim.device.api.ui.Field");

            try {
                _videoControl.setDisplaySize(Display.getWidth(), Display
                        .getHeight());
            } catch (final MediaException me) {
                // setDisplaySize is not supported
            }

            add(videoField);

            final int choice =
                    Dialog.ask(Dialog.D_YES_NO, "Record to stream?", 1);
            if (choice == Dialog.YES) {
                _recordToStream = true;
                _outStream = new ByteArrayOutputStream();
            } else {
                _videoFile = filePath;
            }

            startRecord();
        } catch (final Exception e) {
            // Dispose of the player if it was created
            if (_player != null) {
                _player.close();
            }
            _player = null;

            deleteAll();
            removeAllMenuItems();

            VideoRecordingDemo.errorDialog(e.toString());
        }
    }

    /**
     * Commits the current recording
     */
    private final MenuItem _commit = new MenuItem("Commit recording", 0, 0) {
        public void run() {
            commitRecording();
        }
    };

    /**
     * Plays the recording
     */
    private final MenuItem _playRecording =
            new MenuItem("Play recording", 0, 0) {
                public void run() {
                    // Create the playback screen from the chosen video source
                    VideoPlaybackScreen playbackScreen;

                    if (_recordToStream) {
                        playbackScreen =
                                new VideoPlaybackScreen(
                                        new ByteArrayInputStream(_outStream
                                                .toByteArray()));
                    } else {
                        playbackScreen = new VideoPlaybackScreen(_videoFile);
                    }

                    // Hide the video feed since we cannot display video from
                    // the camera
                    // and video from a file at the same time.
                    _videoControl.setVisible(false);
                    _displayVisible = false;

                    UiApplication.getUiApplication().pushScreen(playbackScreen);

                }
            };

    /**
     * Resets the recording
     */
    private final MenuItem _reset = new MenuItem("Reset recording", 0, 0) {
        public void run() {
            try {
                _recordControl.reset();
            } catch (final Exception e) {
                VideoRecordingDemo.errorDialog("RecordControl#reset threw "
                        + e.toString());
            }
        }
    };

    /**
     * Shows the video display
     */
    private final MenuItem _showDisplay = new MenuItem("Show display", 0, 0) {
        public void run() {
            _videoControl.setVisible(true);
            _displayVisible = true;
        }
    };

    /**
     * Hides the video display
     */
    private final MenuItem _hideDisplay = new MenuItem("Hide display", 0, 0) {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            _videoControl.setVisible(false);
            _displayVisible = false;
        }
    };

    private final MenuItem _startRecord =
            new MenuItem("Start recording", 0, 0) {
                public void run() {
                    startRecord();
                }
            };

    private final MenuItem _stopRecord = new MenuItem("Stop recording", 0, 0) {
        public void run() {
            stopRecord();
        }
    };

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public boolean onClose() {
        // Stop capturing video from the camera
        if (_player != null) {
            _player.close();
        }

        return super.onClose();
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save prompt
        return true;
    }

    /**
     * Creates the menu based on the current recording state
     * 
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        if (_recording) {
            menu.add(_stopRecord);
        } else {
            menu.add(_startRecord);
        }

        // If currently recording video, allow the user to commit
        // and reset the current recording.
        if (_pendingCommit) {
            menu.add(_commit);
            menu.add(_reset);
        }
        if (_committed) {
            // Commit is complete, allow playback of the recording
            menu.add(_playRecording);
        }

        // Add menu item for hiding or showing display depending on
        // current display status.
        if (_displayVisible) {
            menu.add(_hideDisplay);
        } else {
            menu.add(_showDisplay);
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        switch (action) {
        case ACTION_INVOKE: // Trackball click
            if (_recording) {
                final int response =
                        Dialog.ask(Dialog.D_YES_NO,
                                "Recording paused.  Commit recording?",
                                Dialog.YES);
                if (response == Dialog.YES) {
                    this.commitRecording();
                }
            }
            return true; // We've consumed the event
        }
        return super.invokeAction(action);
    }

    /**
     * @see net.rim.device.api.ui.Field#onVisibilityChange(boolean)
     */
    protected void onVisibilityChange(final boolean visible) {
        // If this screen is visible and the video player exists,
        // display the captured video.
        if (visible && _player != null) {
            _videoControl.setVisible(true);
            _displayVisible = true;
        }
    }

    /**
     * Starts the recording of video from the camera
     */
    private void startRecord() {
        try {
            // If the recording is not pending a commit, then we need to set
            // the location to commit to
            if (!_pendingCommit) {
                if (_recordToStream) {
                    _outStream.reset();
                    _recordControl.setRecordStream(_outStream);
                } else {
                    _recordControl.setRecordLocation(_videoFile);
                }
                _pendingCommit = true;
                _committed = false;
            }

            _recordControl.startRecord();
            _recording = true;
        } catch (final Exception e) {
            VideoRecordingDemo.errorDialog(e.toString());
        }
    }

    /**
     * Stops the recording of video from the camera
     */
    private void stopRecord() {
        try {
            _recordControl.stopRecord();
            _recording = false;

        } catch (final Exception e) {
            VideoRecordingDemo.errorDialog("RecordControl#stopRecord() threw "
                    + e.toString());
        }
    }

    /**
     * Commits the current recording
     */
    private void commitRecording() {
        try {
            _recordControl.commit();

            // Reset the recording settings
            _pendingCommit = false;
            _committed = true;
            _recording = false;

            // Alert the user that the recording has been committed
            Dialog.alert("Committed");
        } catch (final Exception e) {
            VideoRecordingDemo.errorDialog("RecordControl#commit() threw "
                    + e.toString());
        }
    }
}
