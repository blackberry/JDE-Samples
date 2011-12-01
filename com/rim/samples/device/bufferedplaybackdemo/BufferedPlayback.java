/*
 * BufferedPlayback.java
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

package com.rim.samples.device.bufferedplaybackdemo;

import javax.microedition.media.Manager;
import javax.microedition.media.Player;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Provides a GUI interface for buffered media playback from a remotely streamed
 * source.
 */
public final class BufferedPlayback extends UiApplication {
    /**
     * Entry point for the application.
     * 
     * @param args
     *            Not used.
     */
    public static void main(final String[] args) {
        final BufferedPlayback app = new BufferedPlayback();
        app.enterEventDispatcher();
    }

    /**
     * Creates the main screen and pushes it.
     */
    BufferedPlayback() {
        final BufferedPlaybackScreen screen = new BufferedPlaybackScreen();
        pushScreen(screen);
    }

    /**
     * The main screen of the application.
     */
    private static final class BufferedPlaybackScreen extends MainScreen
            implements FieldChangeListener {
        /** A field used to enter the URL of the remote media file. */
        private final BasicEditField _urlField;

        /** A field used to enter the MIME type of the remote media file. */
        private final BasicEditField _mimeField;

        /**
         * A field used to display the number of bytes that have been loaded.
         */
        private final TextField _loadStatusField;

        /** A field used to display the current status of the media player. */
        private final TextField _playStatusField;

        /**
         * A field which contains the minimum number of bytes that must be
         * buffered before the media file will begin playing.
         */
        private final BasicEditField _startBufferField;

        /**
         * A field which contains the minimum forward byte buffer which must be
         * maintained in order for the video to keep playing. If the forward
         * buffer falls below this number, the playback will pause until the
         * buffer increases.
         */
        private final BasicEditField _pauseBytesField;

        /**
         * A field which contains the minimum forward byte buffer required to
         * resume playback after a pause.
         */
        private final BasicEditField _resumeBytesField;

        /** A field which contains the maximum byte size of a single read. */
        private final BasicEditField _readLimitField;

        /** A button which starts the HTTP request and media playback. */
        private final ButtonField _startPlayingButton;

        /** A button which stops the HTTP request and media playback. */
        private final ButtonField _stopPlayingButton;

        /** A button which erases current request and playback progress. */
        private final ButtonField _resetField;

        /** A stream for the resource we are retrieving. */
        private LimitedRateStreamingSource _source;

        /** A player for the media stream. */
        private Player _player;

        /** A thread which creates and starts the Player. */
        private PlayerThread _playerThread;

        /**
         * Constructor, creates the GUI.
         */
        BufferedPlaybackScreen() {
            // Set the title of the window.
            setTitle("Buffered Playback Demo");

            // Create and add the field for the URL to be retrieved.
            _urlField = new BasicEditField("Media URL: ", "");
            add(_urlField);

            // Create and add the field for the MIME type of the remote file.
            _mimeField =
                    new BasicEditField("Mime: ", "audio/mpeg", 10,
                            Field.NON_FOCUSABLE);
            add(_mimeField);

            // Create the START, STOP and RESET buttons.
            _startPlayingButton =
                    new ButtonField("Play", ButtonField.CONSUME_CLICK);
            _stopPlayingButton =
                    new ButtonField("Stop", ButtonField.CONSUME_CLICK);
            _resetField = new ButtonField("Reset", ButtonField.CONSUME_CLICK);
            _startPlayingButton.setChangeListener(this);
            _stopPlayingButton.setChangeListener(this);
            _resetField.setChangeListener(this);

            // Add the player control buttons to the screen.
            final HorizontalFieldManager buttonlist =
                    new HorizontalFieldManager();
            buttonlist.add(_startPlayingButton);
            buttonlist.add(_stopPlayingButton);
            buttonlist.add(_resetField);
            add(buttonlist);

            // Create and add the field with the load progress.
            _loadStatusField =
                    new TextField("Load: ", "0 Bytes", 10, Field.NON_FOCUSABLE);
            add(_loadStatusField);

            // Create and add the field with the player status.
            _playStatusField =
                    new TextField("Play: ", "Stopped", 10, Field.NON_FOCUSABLE);
            add(_playStatusField);

            // Create and add the field with the starting buffer.
            _startBufferField =
                    new BasicEditField("Starting Buffer: ", "200000", 10,
                            BasicEditField.FILTER_INTEGER | Field.NON_FOCUSABLE);
            add(_startBufferField);

            // Create and add the field with the minimum pause buffer.
            _pauseBytesField =
                    new BasicEditField("Pause At: ", "64000", 10,
                            BasicEditField.FILTER_INTEGER | Field.NON_FOCUSABLE);
            add(_pauseBytesField);

            // Create and add the field with the minimum resume buffer.
            _resumeBytesField =
                    new BasicEditField("Resume At: ", "128000", 10,
                            BasicEditField.FILTER_INTEGER | Field.NON_FOCUSABLE);
            add(_resumeBytesField);

            // Create and add the field with the read limit.
            _readLimitField =
                    new BasicEditField("Read Limit: ", "32000", 10,
                            BasicEditField.FILTER_INTEGER | Field.NON_FOCUSABLE);
            add(_readLimitField);
        }

        /**
         * A common listener for all three player controls.
         * 
         * @param field
         *            The field that changed.
         * @param context
         *            Information specifying the origin of the change.
         */
        public void fieldChanged(final Field field, final int context) {
            try {
                // If the START button was pressed, begin playback.
                if (field == _startPlayingButton) {
                    // The player does not exist, we must initialize it.
                    if (_player == null) {
                        // Create a stream using the remote file.
                        _source =
                                new LimitedRateStreamingSource(_urlField
                                        .getText());

                        // Set the attributes of the stream using the
                        // information from the GUI fields.
                        _source.setContentType(_mimeField.getText());
                        _source.setStartBuffer(Integer
                                .parseInt(_startBufferField.getText()));
                        _source.setReadLimit(Integer.parseInt(_readLimitField
                                .getText()));
                        _source.setResumeBytes(Integer
                                .parseInt(_resumeBytesField.getText()));
                        _source.setPauseBytes(Integer.parseInt(_pauseBytesField
                                .getText()));
                        _source.setLoadStatus(_loadStatusField);
                        _source.setPlayStatus(_playStatusField);

                        // Acquire the UI lock.
                        UiApplication.getUiApplication().invokeLater(
                                new Runnable() {
                                    public void run() {
                                        // Update the player status.
                                        _playStatusField.setText("Started");
                                    }
                                });

                        // Create and run the player's thread.
                        _playerThread = new PlayerThread();
                        _playerThread.start();
                    }
                    // The player already exists, simply resume it.
                    else {
                        _player.start();
                    }
                }
                // If the STOP button was pressed:
                else if (field == _stopPlayingButton) {
                    // Acquire the UI lock.
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    // Update the status fields.
                                    _playStatusField.setText("Stopped");
                                }
                            });

                    if (_player != null) {
                        // Stop the player.
                        _player.stop();
                    }
                }
                // If the RESET button was pressed:
                else if (field == _resetField) {
                    // Acquire the UI lock.
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    // Update the status fields.
                                    _loadStatusField.setText("0 Bytes");
                                    _playStatusField.setText("Stopped");
                                }
                            });

                    // Destroy the Player and streams.
                    destroy();
                }
            } catch (final Exception e) {
                System.err.println(e.getMessage());
            }

        }

        /**
         * Called when the application exits, ensures that all attributes are
         * destroyed correctly.
         */
        public void close() {
            try {
                // Destroy the Player and streams.
                destroy();
            } catch (final Exception e) {
                System.err.println(e.getMessage());
            } finally {
                super.close();
                System.exit(0);
            }
        }

        /**
         * Prevent the save dialog from being displayed.
         * 
         * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            return true;
        }

        /**
         * Destroy the Player and streams.
         * 
         * @throws Exception
         */
        private void destroy() throws Exception {
            // Destroy the player.
            if (_player != null) {
                _player.stop();
                _player = null;
            }

            // Destroy the stream.
            if (_source != null) {
                _source.stop();
                _source.disconnect();
                _source = null;
            }
        }

        /**
         * A thread for the media player.
         */
        private class PlayerThread extends Thread {
            /**
             * Create and start the player.
             */
            public void run() {
                try {
                    _player = Manager.createPlayer(_source);
                    _player.start();
                } catch (final Exception e) {
                    // Acquire the UI lock.
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    _playStatusField.setText("Stopped");
                                    Dialog.alert("Error: " + e.getMessage());
                                }
                            });
                }
            }
        }
    }
}
