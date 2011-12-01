/*
 * VideoPlaybackScreen.java
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

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A screen for playing a video
 */
public class VideoPlaybackScreen extends MainScreen {
    private Player _videoPlayer;
    private VideoControl _videoControl;

    /**
     * Constructs a screen to playback the video from a specified input stream
     * 
     * @param inputStream
     *            The InputStream of the video to display
     * 
     * @throws NullPointerException
     *             Thrown if <code>inputStream</code> is null
     */
    public VideoPlaybackScreen(final InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException("'inputStream' cannot be null");
        }

        try {
            _videoPlayer =
                    javax.microedition.media.Manager.createPlayer(inputStream,
                            "video/sbv");
            initScreen();
        } catch (final Exception e) {
            Dialog.alert("Exception while initializing the playback video player\n\n"
                    + e);
        }
    }

    /**
     * Constructs the screen to playback the video from a file
     * 
     * @param file
     *            A locator string in URI syntax that points to the video file
     */
    public VideoPlaybackScreen(final String file) {
        boolean notEmpty;
        try {
            final FileConnection fconn = (FileConnection) Connector.open(file);
            notEmpty = fconn.exists() && fconn.fileSize() > 0;
            fconn.close();
        } catch (final IOException e) {
            Dialog.alert("Exception while accessing the video filesize:\n\n"
                    + e);
            notEmpty = false;
        }

        // Initialize the player if the video is not empty
        if (notEmpty) {
            try {
                _videoPlayer =
                        javax.microedition.media.Manager.createPlayer(file);
                initScreen();
            } catch (final Exception e) {
                Dialog.alert("Exception while initializing the playback video player\n\n"
                        + e);
            }
        } else {
            add(new LabelField("The video file you are trying to play is empty"));
        }
    }

    /**
     * Initializes the screen after the player has been created
     * 
     * @throws Exception
     *             Thrown if an error occurs when initializing the video player,
     *             video display or volume control
     */
    private void initScreen() throws Exception {
        _videoPlayer.realize();
        _videoPlayer.addPlayerListener(new PlayerListener() {
            /**
             * @see javax.microedition.media.PlayerListener#playerUpdate(Player,
             *      String, Object)
             */
            public void playerUpdate(final Player player, final String event,
                    final Object eventData) {
                // Alert the user and close the screen after the video has
                // finished playing.
                if (event == PlayerListener.END_OF_MEDIA) {
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    Dialog.alert("Finished playing");
                                    close();
                                }
                            });
                }
            }
        });

        // Set up the playback
        _videoControl = (VideoControl) _videoPlayer.getControl("VideoControl");
        final Field vField =
                (Field) _videoControl.initDisplayMode(
                        GUIControl.USE_GUI_PRIMITIVE,
                        "net.rim.device.api.ui.Field");
        add(vField);

        final VolumeControl vol =
                (VolumeControl) _videoPlayer.getControl("VolumeControl");
        vol.setLevel(30);
    }

    /**
     * @see net.rim.device.api.ui.Field#onVisibilityChange(boolean)
     */
    protected void onVisibilityChange(final boolean visible) {
        // If the screen becomes visible and the video player was created, then
        // start the playback.
        if (visible && _videoPlayer != null) {
            try {
                _videoPlayer.start();
            } catch (final Exception e) {
                // If starting the video fails, terminate the playback
                Dialog.alert("Exception while starting the video\n\n" + e);
                close();
            }
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public void close() {
        // Close the video player if it was created
        if (_videoPlayer != null) {
            _videoPlayer.close();
        }
        super.close();
    }
}
