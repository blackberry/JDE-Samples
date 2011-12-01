/*
 * SendMediaDemo.java
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

package com.rim.samples.device.sendmediademo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.content.ActionNameMap;
import javax.microedition.content.ContentHandler;
import javax.microedition.content.ContentHandlerException;
import javax.microedition.content.ContentHandlerServer;
import javax.microedition.content.Invocation;
import javax.microedition.content.Registry;
import javax.microedition.content.RequestListener;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

import net.rim.device.api.media.MediaActionHandler;
import net.rim.device.api.media.control.StreamingBufferControl;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample shows how registering an application as a content handler for
 * various mime types results in menu items and/or dialog buttons being
 * available within various screens to invoke the application as a handler for
 * send actions.
 * 
 * In the case of image types, this application creates an input stream using
 * the URL associated with the Invocation object obtained from the
 * ContentHandlerServer and reads the image data which it then uses to display
 * the image on the BlackBerry screen. Similarly, when encountering a video mime
 * type, the application creates a video player to render the file pointed to by
 * the URL. In the case of audio types, the application prompts the user to play
 * the specified file. If the user chooses to play the file, the application
 * plays the audio in the background and responds to media key presses.
 */
public final class SendMediaDemo extends UiApplication implements
        RequestListener, PlayerListener, MediaActionHandler {
    private static final String ID = "com.rim.samples.device.sendmediademo";
    private static final String CLASSNAME =
            "com.rim.samples.device.sendmediademo.SendMediaDemo";

    private Player _videoPlayer;
    private Player _audioPlayer;
    private VolumeControl _audioVolumeControl;
    private Field _videoField;
    private VideoControl _vc;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0) {
            if (args[0].equals("startup")) {
                // Register this application as a content handler on startup
                register();
            }
        } else {
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            final SendMediaDemo app = new SendMediaDemo();
            app.enterEventDispatcher();
        }
    }

    /**
     * Registers this application as a content handler for image, video, and
     * audio files
     */
    private static void register() {
        final String[] types =
                { "image/bmp", "image/png", "image/jpeg", "video/3gpp",
                        "video/mp4", "audio/mp4", "audio/amr", "audio/mpeg" };
        final String[] suffixes =
                { "bmp", "png", "jpg", "3GP", "mp4", "m4A", "amr", "mp3" };

        final String[] actions = { ContentHandler.ACTION_SEND };
        final String[] actionNames = { "Send to demo app" };
        final ActionNameMap[] actionNameMaps =
                { new ActionNameMap(actions, actionNames, "en") };

        // Get access to the registry
        final Registry registry = Registry.getRegistry(CLASSNAME);

        try {
            // Register as a content handler
            registry.register(CLASSNAME, types, suffixes, actions,
                    actionNameMaps, ID, null);
        } catch (final ContentHandlerException che) {
            System.out.println("Registry#register() threw " + che.toString());
        } catch (final ClassNotFoundException cnfe) {
            System.out.println("Registry#register() threw " + cnfe.toString());
        }
    }

    /**
     * Creates a new SendMediaDemo object
     */
    public SendMediaDemo() {
        try {
            // Get access to the ContentHandlerServer for this application and
            // register as a listener.
            final ContentHandlerServer contentHandlerServer =
                    Registry.getServer(CLASSNAME);
            contentHandlerServer.setListener(this);
        } catch (final ContentHandlerException che) {
            errorDialog("Registry.getServer(String) threw " + che.toString());
        }
    }

    /**
     * RequestListener implementation
     * 
     * @param server
     *            The content handler server from which to request Invocation
     *            objects
     * 
     * @see javax.microedition.content.RequestListener#invocationRequestNotify(ContentHandlerServer)
     */
    public void invocationRequestNotify(final ContentHandlerServer server) {
        final Invocation invoc = server.getRequest(false);
        if (invoc != null) {
            if (getActiveScreen() == null) {
                final String type = invoc.getType();

                final DemoMainScreen screen = new DemoMainScreen();
                screen.setTitle("Send Media Demo");

                if (type.equals("audio/mp4") || type.equals("audio/amr")
                        || type.equals("audio/mpeg")) {
                    screen.setType(DemoMainScreen.AUDIO_TYPE);

                    // Play an audio file
                    if (_audioPlayer == null) {
                        final String url = invoc.getURL();
                        screen.add(new LabelField("Playing audio file: " + url));

                        initAudio(url);
                    }
                }

                if (type.equals("image/bmp") || type.equals("image/png")
                        || type.equals("image/jpeg")) {
                    screen.setType(DemoMainScreen.IMAGE_TYPE);

                    // Get data from URL
                    final byte[] data = getData(invoc.getURL());

                    // Create image field
                    final Bitmap image =
                            Bitmap.createBitmapFromBytes(data, 0, -1, 5);
                    final BitmapField imageField =
                            new BitmapField(image, Field.FIELD_HCENTER);

                    screen.add(imageField);
                } else if (type.equals("video/3gpp")
                        || type.equals("video/mp4")) {
                    screen.setType(DemoMainScreen.VIDEO_TYPE);

                    // Play a video
                    initVideo(invoc.getURL());
                    if (_videoField != null) {
                        screen.add(_videoField);

                        try {
                            // Start video player
                            _videoPlayer.start();
                        } catch (final MediaException pe) {
                            errorDialog("Player#start() threw " + pe.toString());
                        }
                    }
                }

                pushScreen(screen);
            }

            server.finish(invoc, Invocation.OK);
        }
    }

    /**
     * PlayerListener implementation
     * 
     * @see javax.microedition.media.PlayerListener#playerUpdate(Player,String,Object)
     */
    public void playerUpdate(final Player player, final String event,
            final Object eventData) {
        if (event.equals(PlayerListener.END_OF_MEDIA)) {
            if (player == _audioPlayer) {
                // We've finished playing the audio file, close the player and
                // update screen
                _audioPlayer.close();
            }

            final Screen screen = getActiveScreen();
            if (screen instanceof MainScreen) {
                final Field field = screen.getField(0);
                if (field instanceof LabelField) {
                    final LabelField labelField = (LabelField) field;
                    invokeLater(new Runnable() {
                        public void run() {
                            labelField.setText("End of media reached.");
                        }
                    });
                }
            }
        }
    }

    /**
     * Creates and initializes an audio player
     * 
     * @param url
     *            The URL of the audio file to play
     */
    private void initAudio(final String url) {
        // Register this class as a MediaActionHandler so we can respond
        // to media key presses.
        addMediaActionHandler(this);

        final Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    // Create and start an audio player
                    _audioPlayer =
                            javax.microedition.media.Manager.createPlayer(url);
                    _audioPlayer.addPlayerListener(SendMediaDemo.this);
                    _audioPlayer.realize();

                    // Cause playback to begin as soon as possible once start()
                    // is called on the Player.
                    final StreamingBufferControl sbc =
                            (StreamingBufferControl) _audioPlayer
                                    .getControl("net.rim.device.api.media.control.StreamingBufferControl");
                    sbc.setBufferTime(0);

                    _audioPlayer.start();

                    // Create a volume control
                    _audioVolumeControl =
                            (VolumeControl) _audioPlayer
                                    .getControl("VolumeControl");
                } catch (final MediaException me) {
                    errorDialog(me.toString());
                } catch (final IOException ioe) {
                    errorDialog("Manager.createPlayer(String) threw"
                            + ioe.toString());
                }
            }
        });
        thread.start();
    }

    /**
     * MediaActionHandler implementation
     * 
     * @see net.rim.device.api.media.MediaActionHandler#mediaAction(int, int,
     *      Object)
     */
    public boolean mediaAction(final int action, final int source,
            final Object context) {
        if (_audioVolumeControl != null) {
            final int volumeLevel = _audioVolumeControl.getLevel();

            // Respond to media key presses. Here we will allow the user to
            // adjust the volume or mute the audio entirely.
            switch (action) {
            case MediaActionHandler.MEDIA_ACTION_VOLUME_UP:
                _audioVolumeControl.setLevel(volumeLevel + 10);
                break;
            case MediaActionHandler.MEDIA_ACTION_VOLUME_DOWN:
                _audioVolumeControl.setLevel(volumeLevel - 10);
                break;
            case MediaActionHandler.MEDIA_ACTION_PLAYPAUSE_TOGGLE:
                if (_audioVolumeControl.isMuted()) {
                    _audioVolumeControl.setMute(false);
                } else {
                    _audioVolumeControl.setMute(true);
                }
                break;
            }
        }

        return true;
    }

    /**
     * Creates and initializes a video player
     * 
     * @param url
     *            The URL of the video file to play
     */
    private void initVideo(final String url) {
        try {
            // Create a video player
            _videoPlayer = javax.microedition.media.Manager.createPlayer(url);
            _videoPlayer.realize();

            // Cause playback to begin as soon as possible once start()
            // is called on the Player.
            final StreamingBufferControl sbc =
                    (StreamingBufferControl) _videoPlayer
                            .getControl("net.rim.device.api.media.control.StreamingBufferControl");
            sbc.setBufferTime(0);

            _vc = (VideoControl) _videoPlayer.getControl("VideoControl");
            if (_vc != null) {
                _videoField =
                        (Field) _vc.initDisplayMode(
                                GUIControl.USE_GUI_PRIMITIVE,
                                "net.rim.device.api.ui.Field");
                _vc.setVisible(true);
            }
        } catch (final MediaException me) {
            errorDialog(me.toString());
        } catch (final IOException ioe) {
            errorDialog("Manager#createPlayer(String) threw " + ioe.toString());
        }
    }

    /**
     * Sets the size of the video
     * 
     * @param width
     *            Width of the video display
     * @param height
     *            Height of the video display
     */
    private void setVideoSize(final int width, final int height) {
        try {
            if (_vc != null) {
                _vc.setDisplaySize(width, height);
            }
        } catch (final MediaException pe) {
            errorDialog("VideoControl#setDisplaySize(int, int) threw "
                    + pe.toString());
        }
    }

    /**
     * Returns a byte array containing data representing the image at the
     * specified URL
     * 
     * @param url
     *            The location of the image to display
     * @return The image data
     */
    private byte[] getData(final String url) {
        byte[] data = new byte[0];
        try {
            final FileConnection file = (FileConnection) Connector.open(url);
            final int fileSize = (int) file.fileSize();
            data = new byte[fileSize];
            final InputStream inputStream = file.openInputStream();
            inputStream.read(data);
        } catch (final Exception e) {
            errorDialog(e.toString());
        }
        return data;
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
     * A MainScreen subclass for displaying either an image, a video, or a
     * status field to indicate an audio file is being played.
     */
    final private class DemoMainScreen extends MainScreen {
        public final static int AUDIO_TYPE = 0;
        public final static int VIDEO_TYPE = 1;
        public final static int IMAGE_TYPE = 2;

        private int _mediaType = -1;

        /**
         * Creates a new DemoMainScreen object
         */
        DemoMainScreen() {
            super(net.rim.device.api.ui.Manager.NO_VERTICAL_SCROLL);
        }

        /**
         * Sets the media type for this screen
         * 
         * @param mediaType
         *            The media type of which to associate this screen
         */
        public void setType(final int mediaType) {
            _mediaType = mediaType;
        }

        /**
         * @see net.rim.device.api.ui.Manager#sublayout(int,int)
         */
        protected void sublayout(final int width, final int height) {
            if (_mediaType == VIDEO_TYPE) {
                setVideoSize(Display.getWidth(), Display.getHeight());
            }

            super.sublayout(width, height);
        }

        /**
         * @see net.rim.device.api.ui.Screen#onClose()
         */
        public boolean onClose() {
            try {
                // Deregister this application as a listener
                final ContentHandlerServer contentHandlerServer =
                        Registry.getServer(CLASSNAME);
                contentHandlerServer.setListener(null);
            } catch (final ContentHandlerException che) {
                errorDialog(che.toString());
            }

            if (_videoPlayer != null) {
                _videoPlayer.close();
            }

            if (_audioPlayer != null) {
                _audioPlayer.close();
            }

            return super.onClose();
        }
    }
}
