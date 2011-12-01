/*
 * NWMVideoScreen.java
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

package com.rim.samples.device.nativewindowmanagerdemo;

import java.io.InputStream;

import javax.microedition.media.Player;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.media.control.AdvancedVideoControl;
import net.rim.device.api.media.control.StreamingBufferControl;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.AbsoluteFieldManager;
import net.rim.device.api.ui.container.ComponentCanvas;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;

/**
 * MainScreen class for the Native Window Manager Demo application. This screen
 * overlays functional play and pause buttons on top of a video field.
 */
public final class NWMVideoScreen extends MainScreen {
    private Player _player;
    private static final int VIDEO_WIDTH = 300;
    private static final int VIDEO_HEIGHT = 250;
    private static final int MARGIN = 40;

    /**
     * Creates a new NWMVideoScreen object
     */
    public NWMVideoScreen() {
        setTitle("Native Window Manager Demo");

        final int displayWidth = Display.getWidth();
        final int displayHeight = Display.getHeight();

        // Add an AbsoluteFieldManager to contain a video field
        final AbsoluteFieldManager absoluteFieldManager =
                new AbsoluteFieldManager();
        absoluteFieldManager.setBackground(BackgroundFactory
                .createSolidBackground(Color.KHAKI));
        add(absoluteFieldManager);

        // Set up video field
        final Field videoField = initializeVideo();

        if (videoField != null) {
            // Calculate position of video field and add to manager
            final int videoFieldX = (displayWidth - VIDEO_WIDTH) / 2;
            final int videoFieldY = (displayHeight - VIDEO_HEIGHT) / 2;
            absoluteFieldManager.add(videoField, videoFieldX, videoFieldY);

            // Create a ComponentCanvas for overlaying fields on the video
            final ComponentCanvas componentCanvas =
                    new ComponentCanvas(VIDEO_WIDTH + MARGIN, VIDEO_HEIGHT
                            + MARGIN);

            // Calculate the position of the ComponentCanvas
            // and add to the manager.
            final int canvasX = (displayWidth - (VIDEO_WIDTH + MARGIN)) / 2;
            final int canvasY = (displayHeight - (VIDEO_HEIGHT + MARGIN)) / 2;
            absoluteFieldManager.add(componentCanvas, canvasX, canvasY);

            // Button for pausing the video
            final ButtonField pauseButton =
                    new ButtonField("Pause", ButtonField.CONSUME_CLICK
                            | ButtonField.NEVER_DIRTY);
            pauseButton.setCommand(new Command(new CommandHandler() {
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object object) {
                    // Pause the video
                    try {
                        _player.stop();
                    } catch (final Exception e) {
                        NWMDemo.errorDialog(e.toString());
                    }
                }
            }));

            final Image pauseImage =
                    ImageFactory.createImage(Bitmap
                            .getBitmapResource("pause.png"));
            pauseButton.setImage(pauseImage);
            componentCanvas.add(pauseButton, 0, VIDEO_HEIGHT - 15);

            // Button to start/re-start the video
            final ButtonField playButton =
                    new ButtonField("Play", ButtonField.CONSUME_CLICK
                            | ButtonField.NEVER_DIRTY);
            playButton.setCommand(new Command(new CommandHandler() {
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object object) {
                    // Start the video
                    try {
                        _player.start();
                    } catch (final Exception e) {
                        NWMDemo.errorDialog(e.toString());
                    }
                }
            }));

            final Image playImage =
                    ImageFactory.createImage(Bitmap
                            .getBitmapResource("play.png"));
            playButton.setImage(playImage);
            componentCanvas
                    .add(playButton, VIDEO_WIDTH - 80, VIDEO_HEIGHT - 15);
        }
    }

    /**
     * Creates a Player and returns a Field object containing an
     * AdvancedVideoControl
     * 
     * @return A UI Field object containing an AdvancedVideoControl
     */
    private Field initializeVideo() {
        Field videoField = null;

        try {
            // Create player from input stream
            final InputStream is =
                    getClass().getResourceAsStream("/media/BlackBerry.mp4");
            _player =
                    javax.microedition.media.Manager.createPlayer(is,
                            "video/mp4");

            // Realize the player
            _player.realize();

            // Cause playback to begin as soon as possible
            // once start()is called on the Player.
            final StreamingBufferControl sbc =
                    (StreamingBufferControl) _player
                            .getControl("net.rim.device.api.media.control.StreamingBufferControl");
            sbc.setBufferTime(0);

            // Obtain video control
            final AdvancedVideoControl vControl =
                    (AdvancedVideoControl) _player
                            .getControl("net.rim.device.api.media.control.AdvancedVideoControl");

            // Initialize the video control and get the video field
            videoField =
                    (Field) vControl.initDisplayMode(
                            AdvancedVideoControl.USE_GUI_ADVANCED,
                            "net.rim.device.api.ui.Field");

            // Set the video to be a size other than full screen.
            // This must be done after calling initDisplayMode().
            vControl.setDisplaySize(VIDEO_WIDTH, VIDEO_HEIGHT);

            vControl.setVisible(true);
        } catch (final Exception e) {
            NWMDemo.errorDialog(e.toString());
        }

        return videoField;
    }

    /**
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        if (_player != null) {
            _player.close();
        }

        super.close();
    }
}
