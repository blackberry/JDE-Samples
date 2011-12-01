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

import javax.microedition.amms.control.camera.FlashControl;
import javax.microedition.amms.control.camera.ZoomControl;
import javax.microedition.amms.control.imageeffect.ImageEffectControl;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.RecordControl;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RadioButtonField;
import net.rim.device.api.ui.component.RadioButtonGroup;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringProvider;

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
    private FlashControl _flashControl;
    private ZoomControl _zoomControl;
    private ImageEffectControl _effectControl;
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

        _commit =
                new MenuItem(new StringProvider("Commit recording"), 0x230010,
                        0);
        _commit.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                commitRecording();
            }
        }));

        _playRecording =
                new MenuItem(new StringProvider("Play recording"), 0x230020, 0);
        _playRecording.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Create the playback screen from the chosen video source
                VideoPlaybackScreen playbackScreen;

                if (_recordToStream) {
                    playbackScreen =
                            new VideoPlaybackScreen(new ByteArrayInputStream(
                                    _outStream.toByteArray()));
                } else {
                    playbackScreen = new VideoPlaybackScreen(_videoFile);
                }

                // Hide the video feed since we cannot display video from the
                // camera
                // and video from a file at the same time.
                _videoControl.setVisible(false);
                _displayVisible = false;

                UiApplication.getUiApplication().pushScreen(playbackScreen);

            }
        }));

        _reset =
                new MenuItem(new StringProvider("Reset recording"), 0x230030, 0);
        _reset.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                try {
                    _recordControl.reset();
                } catch (final Exception e) {
                    VideoRecordingDemo.errorDialog("RecordControl#reset threw "
                            + e.toString());
                }
            }
        }));

        _showDisplay =
                new MenuItem(new StringProvider("Show display"), 0x230040, 0);
        _showDisplay.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                _videoControl.setVisible(true);
                _displayVisible = true;
            }
        }));

        _hideDisplay =
                new MenuItem(new StringProvider("Hide display"), 0x230050, 0);
        _hideDisplay.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                _videoControl.setVisible(false);
                _displayVisible = false;
            }
        }));

        _startRecord =
                new MenuItem(new StringProvider("Start recording"), 0x230060, 0);
        _startRecord.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                startRecord();
            }
        }));

        _stopRecord =
                new MenuItem(new StringProvider("Stop recording"), 0x230070, 0);
        _stopRecord.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                stopRecord();
            }
        }));

        _toggleFlash =
                new MenuItem(new StringProvider("Toggle flash"), 0x230080, 0);
        _toggleFlash.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                int newMode;
                switch (_flashControl.getMode()) {
                case FlashControl.OFF:
                    newMode = FlashControl.FORCE;
                    break;
                default:
                    newMode = FlashControl.OFF;
                }

                try {
                    _flashControl.setMode(newMode);
                } catch (final Exception e) {
                }
            }
        }));

        _chooseImageEffect =
                new MenuItem(new StringProvider("Choose effect"), 0x230090, 0);
        _chooseImageEffect.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                String currentEffect = null;
                if (_effectControl.isEnabled()) {
                    final String preset = _effectControl.getPreset();
                    if (preset != null) {
                        currentEffect = preset;
                    }
                }

                final ImageEffectDialog chooseDialog =
                        new ImageEffectDialog(currentEffect);
                UiApplication.getUiApplication().pushModalScreen(chooseDialog);

                final String preset = chooseDialog.getImageEffectPreset();
                if (preset == null) {
                    // No preset chosen, turn off effects
                    _effectControl.setEnabled(false);
                } else {
                    // Turn on the chosen effect
                    _effectControl.setPreset(preset);
                    _effectControl.setEnabled(true);
                }
            }
        }));

        try {
            // Start capturing video from the camera
            _player =
                    javax.microedition.media.Manager
                            .createPlayer("capture://video?" + encoding);
            _player.start();

            _videoControl = (VideoControl) _player.getControl("VideoControl");
            _recordControl =
                    (RecordControl) _player.getControl("RecordControl");
            _flashControl =
                    (FlashControl) _player
                            .getControl("javax.microedition.amms.control.camera.FlashControl");
            _zoomControl =
                    (ZoomControl) _player
                            .getControl("javax.microedition.amms.control.camera.ZoomControl");
            _effectControl =
                    (ImageEffectControl) _player
                            .getControl("javax.microedition.amms.control.imageeffect.ImageEffectControl");

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
     * A MenuItem to commit the current recording
     */
    private final MenuItem _commit;

    /**
     * A MenuItem to play the recording
     */
    private final MenuItem _playRecording;

    /**
     * A MenuItem to reset the recording
     */
    private final MenuItem _reset;

    /**
     * A MenuItem to show the video display
     */
    private final MenuItem _showDisplay;

    /**
     * A MenuItem to hide the video display
     */
    private final MenuItem _hideDisplay;

    /**
     * A MenuItem to start the recording of video
     */
    private final MenuItem _startRecord;

    /**
     * A MenuItem to stop the recording of video
     */
    private final MenuItem _stopRecord;

    /**
     * A MenuItem to turn flash on or off
     */
    private final MenuItem _toggleFlash;

    /**
     * A MenuItem to allow users to choose a filter effect
     */
    private final MenuItem _chooseImageEffect;

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
            menu.add(_toggleFlash);
            menu.add(_chooseImageEffect);
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
        if (action == ACTION_INVOKE) {
            if (_recording) {
                final int response =
                        Dialog.ask(Dialog.D_YES_NO,
                                "Recording paused.  Commit recording?",
                                Dialog.YES);
                if (response == Dialog.YES) {
                    this.commitRecording();
                }
            }

            return true;
        }

        return super.invokeAction(action);
    }

    /**
     * @see net.rim.device.api.ui.Screen#navigationMovement(int, int, int, int)
     */
    protected boolean navigationMovement(final int dx, final int dy,
            final int status, final int time) {
        if (dy < 0) {
            // Upwards move
            _zoomControl.setDigitalZoom(ZoomControl.NEXT);
            return true;
        } else if (dy > 0) {
            // Downwards move
            _zoomControl.setDigitalZoom(ZoomControl.PREVIOUS);
            return true;
        } else {
            return false;
        }
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

    /**
     * A popup dialog class which allows the user to pick an image effect to use
     */
    private final static class ImageEffectDialog extends PopupScreen {
        private final RadioButtonGroup _options;

        /**
         * Creates a new ImageEffectDialog object
         * 
         * @param effect
         *            The name of the effect currently in use by the video
         *            recording Player
         */
        public ImageEffectDialog(final String effect) {
            super(new VerticalFieldManager());

            // Create radio buttons for the image effect choices
            _options = new RadioButtonGroup();
            final RadioButtonField none =
                    new RadioButtonField("None", _options, effect == null);
            final RadioButtonField greyscale =
                    new RadioButtonField("Greyscale", _options,
                            (effect != null && effect.equals("monochrome")));
            final RadioButtonField sepia =
                    new RadioButtonField("Sepia", _options,
                            (effect != null && effect.equals("sepia")));

            // Add fields to the dialog
            add(new RichTextField("Choose Effect", Field.NON_FOCUSABLE));
            add(new SeparatorField());
            add(none);
            add(greyscale);
            add(sepia);
        }

        /**
         * @see net.rim.device.api.ui.Screen.invokeAction(int)
         */
        protected boolean invokeAction(final int action) {
            final boolean result = super.invokeAction(action);

            if (action == ACTION_INVOKE) {
                close();
                return true;
            } else {
                return result;
            }
        }

        /**
         * @see net.rim.device.api.ui.Screen.keyChar(char, int, int)
         */
        protected boolean
                keyChar(final char c, final int status, final int time) {
            final boolean result = super.keyChar(c, status, time);
            switch (c) {
            case Characters.ENTER:
                close();
                return true;
            default:
                return result;
            }
        }

        /**
         * Returns the image effect currently selected by the user in this
         * dialog
         * 
         * @return The selected image effect preset name
         */
        public String getImageEffectPreset() {
            switch (_options.getSelectedIndex()) {
            case 1:
                return "monochrome";
            case 2:
                return "sepia";
            default:
                return null;
            }
        }
    }
}
