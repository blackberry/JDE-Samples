/*
 * EmbeddedMediaDemo.java
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

package com.rim.samples.device.embeddedmediademo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

import net.rim.device.api.media.control.StreamingBufferControl;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A simple showcase of the javax.microedition.media API's. We are embedding a
 * media player within a standard UI field and allowing the user to start and
 * pause the media player and adjust the audio volume. Label fields at the
 * bottom of the screen indicate current volume level, total playing time of the
 * media, and elapsed time when media is paused.
 */
public class EmbeddedMediaDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new EmbeddedMediaDemo().enterEventDispatcher();
    }

    /**
     * Creates a new EmbeddedMediaDemo object
     */
    public EmbeddedMediaDemo() {
        final EmbeddedMediaScreen screen = new EmbeddedMediaScreen();
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
 * A UI screen to display a media player
 */
final class EmbeddedMediaScreen extends MainScreen implements
        FieldChangeListener, PlayerListener {
    private Player _player;
    private VolumeControl _volumeControl;
    private final RichTextField _statusField;
    private ButtonField _controlButton;
    private Field _videoField;
    private HorizontalFieldManager _hfm1;
    private HorizontalFieldManager _hfm2;

    private LabelField _currentTime;
    private LabelField _duration;
    private LabelField _volumeDisplay;

    private TimerUpdateThread _timerUpdateThread;

    /**
     * Creates a new EmbeddedMediaScreen object
     */
    EmbeddedMediaScreen() {
        setTitle("Embedded Media Demo");

        _statusField = new RichTextField("Loading media, please wait...");
        add(_statusField);

        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                initializeMedia();

                // If initialization was successful...
                if (_videoField != null) {
                    createUI();

                    updateVideoSize();
                } else {
                    _statusField.setText("Error: Could not load media");
                }
            }
        });
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        final int playerState = _player.getState();

        if (playerState == Player.PREFETCHED || playerState == Player.REALIZED) {
            try {
                // Start/resume the media player
                _player.start();

                _timerUpdateThread = new TimerUpdateThread();
                _timerUpdateThread.start();
            } catch (final MediaException pe) {
                EmbeddedMediaDemo.errorDialog("Player#start() threw "
                        + pe.toString());
            }
        } else if (playerState == Player.STARTED) {
            try {
                // Stop/pause the media player
                _player.stop();

                _timerUpdateThread.stop();
            } catch (final MediaException pe) {
                EmbeddedMediaDemo.errorDialog("Player#stop() threw "
                        + pe.toString());
            }
        }
    }

    /**
     * @see net.rim.device.api.ui.Manager#sublayout(int,int)
     */
    protected void sublayout(final int width, final int height) {
        super.sublayout(width, height);
        updateVideoSize();
    }

    /**
     * Initializes UI Components and adds them to the screen
     */
    private void createUI() {
        delete(_statusField);

        _hfm1 = new HorizontalFieldManager(Field.FIELD_HCENTER);
        _controlButton =
                new ButtonField("Start", ButtonField.NEVER_DIRTY
                        | ButtonField.CONSUME_CLICK);
        _controlButton.setChangeListener(this);
        _hfm1.add(_controlButton);

        _hfm2 = new HorizontalFieldManager(Field.FIELD_HCENTER);
        _currentTime = new LabelField("-");
        _duration = new LabelField("- s");
        _volumeDisplay =
                new LabelField("Volume : " + _volumeControl.getLevel());
        _hfm2.add(_currentTime);
        _hfm2.add(new LabelField(" / "));
        _hfm2.add(_duration);
        _hfm2.add(new LabelField("\t\t"));
        _hfm2.add(_volumeDisplay);

        add(_videoField);
        add(_hfm1);
        add(_hfm2);
    }

    /**
     * Creates a Player based on a specified URL and provides a VolumeControl
     * object.
     */
    private void initializeMedia() {
        try {
            // For the purpose of this sample we are supplying a URL to a media
            // file
            // that is included in the project itself. See the
            // javax.microedition.media.Manager javadoc for more information on
            // handling data residing on a server.

            final InputStream is =
                    getClass().getResourceAsStream("/media/BlackBerry.mp4");
            _player =
                    javax.microedition.media.Manager.createPlayer(is,
                            "video/mp4");
            _player.addPlayerListener(this);
            _player.realize();

            // Cause playback to begin as soon as possible once start()
            // is called on the Player.
            final StreamingBufferControl sbc =
                    (StreamingBufferControl) _player
                            .getControl("net.rim.device.api.media.control.StreamingBufferControl");
            sbc.setBufferTime(0);

            final VideoControl vc =
                    (VideoControl) _player.getControl("VideoControl");
            if (vc != null) {
                _videoField =
                        (Field) vc.initDisplayMode(
                                GUIControl.USE_GUI_PRIMITIVE,
                                "net.rim.device.api.ui.Field");
                vc.setVisible(true);
            }

            _volumeControl =
                    (VolumeControl) _player.getControl("VolumeControl");

        } catch (final MediaException pe) {
            EmbeddedMediaDemo.errorDialog(pe.toString());
        } catch (final IOException ioe) {
            EmbeddedMediaDemo.errorDialog("Manager.createPlayer() threw "
                    + ioe.toString());
        }
    }

    /**
     * Updates the video size according to the current screen dimensions
     * 
     * @param screenWidth
     *            The screen's width.
     * @param screenHeight
     *            The screen's height.
     */
    private void updateVideoSize() {
        if (_player != null) {
            try {
                final VideoControl vc =
                        (VideoControl) _player.getControl("VideoControl");
                if (vc != null) {
                    final net.rim.device.api.ui.Manager manager =
                            getMainManager();
                    final int videoHeight =
                            manager.getHeight() - _hfm1.getHeight()
                                    - _hfm2.getHeight();
                    final int videoWidth = manager.getWidth();
                    vc.setDisplaySize(videoWidth, videoHeight);
                }
            } catch (final Exception e) {
                EmbeddedMediaDemo
                        .errorDialog("VideoControl#setDisplayDize() threw "
                                + e.toString());
            }
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#keyControl(char,int,int)
     */
    protected boolean
            keyControl(final char c, final int status, final int time) {
        // Capture volume control key press and adjust volume accordingly
        switch (c) {
        case Characters.CONTROL_VOLUME_DOWN:
            _volumeControl.setLevel(_volumeControl.getLevel() - 10);
            return true;

        case Characters.CONTROL_VOLUME_UP:
            _volumeControl.setLevel(_volumeControl.getLevel() + 10);
            return true;
        }

        return super.keyControl(c, status, time);
    }

    /**
     * @see javax.microedition.media.PlayerListener#playerUpdate(Player,String,Object)
     */
    public void playerUpdate(final Player player, final String event,
            final Object eventData) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                if (event.equals(VOLUME_CHANGED)) {
                    _volumeDisplay.setText("Volume : "
                            + _volumeControl.getLevel());
                } else if (event.equals(STARTED)) {
                    _currentTime.setText(" ");
                    _controlButton.setLabel("Pause");
                } else if (event.equals(STOPPED)) {
                    _controlButton.setLabel("Start");
                } else if (event.equals(DURATION_UPDATED)) {
                    _duration.setText(_player.getDuration() / 1000000 + " s");
                } else if (event.equals(END_OF_MEDIA)) {
                    _controlButton.setLabel("Start");
                    _timerUpdateThread.stop();
                }
            }
        });
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        final boolean handled = super.invokeAction(action);

        if (!handled) {
            if (action == ACTION_INVOKE) {
                // Suppress the menu
                return true;
            }
        }
        return handled;
    }

    /**
     * A thread which acts as a timer to update the screen
     */
    private class TimerUpdateThread extends Thread {
        private boolean _threadCanRun;

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            _threadCanRun = true;
            while (_threadCanRun) {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        _currentTime.setText(String.valueOf(_player
                                .getMediaTime() / 1000000));
                    }
                });

                try {
                    Thread.sleep(500L);
                } catch (final InterruptedException e) {
                }
            }
        }

        /**
         * Sets an internal flag such that {@link #run()} will stop as soon as
         * possible
         */
        public void stop() {
            _threadCanRun = false;
        }
    }
}
