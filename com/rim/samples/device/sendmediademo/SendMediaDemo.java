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
import javax.microedition.media.control.GUIControl;
import javax.microedition.media.control.VideoControl;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample shows how registering an application as a content handler for
 * image or video mime types results in menu items and/or dialog buttons being
 * available within various screens to invoke the application as a handler for
 * send actions.
 * 
 * In the case of image types, this application creates an input stream using
 * the URL associated with the Invocation object obtained from the
 * ContentHandlerServer and reads the image data which it then uses to display
 * the image on the BlackBerry screen. Similarly, when encountering a video mime
 * type, the application creates a video player to render the file pointed to by
 * the URL.
 */
class SendMediaDemo extends UiApplication implements RequestListener {
    private static final String ID = "com.rim.samples.device.sendmediademo";
    private static final String CLASSNAME =
            "com.rim.samples.device.sendmediademo.SendMediaDemo";

    private Player _player;
    private Field _videoField;
    VideoControl _vc;

    /**
     * Entry point for app
     * 
     * @param args
     *            Command line arguments
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0) {
            if (args[0].equals("startup")) {
                // Register ourselves as a content handler on startup
                register();
            }
        } else {
            final SendMediaDemo app = new SendMediaDemo();
            app.enterEventDispatcher();
        }
    }

    /**
     * Registers this application as a content handler for image files
     */
    private static void register() {
        final String[] types =
                { "image/bmp", "image/png", "image/jpeg", "video/3gpp",
                        "video/mp4" };
        final String[] suffixes = { ".bmp", ".png", ".jpg", ".3GP", ".mp4" };
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
            System.out.print(che.toString());
        } catch (final ClassNotFoundException cnfe) {
            System.out.print(cnfe.toString());
        }
    }

    // Constructor
    private SendMediaDemo() {
        try {
            // Get access to the ContentHandlerServer for this application and
            // register as a listener.
            final ContentHandlerServer contentHandlerServer =
                    Registry.getServer(CLASSNAME);
            contentHandlerServer.setListener(this);
        } catch (final ContentHandlerException che) {
            System.out.println(che.toString());
        }
    }

    /**
     * RequestListener implementation
     * 
     * @param server
     *            The content handler server from which to request Invocation
     *            objects
     */
    public void invocationRequestNotify(final ContentHandlerServer server) {
        final Invocation invoc = server.getRequest(false);
        if (invoc != null) {
            final String type = invoc.getType();

            if (type.equals("image/bmp") || type.equals("image/png")
                    || type.equals("image/jpeg")) {
                final byte[] data = getData(invoc.getURL());
                displayImage(data);
            } else if (type.equals("video/3gpp") || type.equals("video/mp4")) {
                initVideo(invoc.getURL());
                if (_videoField != null) {
                    displayVideo();
                }
            } else {
                System.exit(0);
            }

            server.finish(invoc, Invocation.OK);
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
            System.out.println(pe.toString());
        }
    }

    /**
     * Creates and initializes a video player
     * 
     * @param url
     *            The URL of the video file to play
     */
    private void initVideo(final String url) {
        try {
            _player = javax.microedition.media.Manager.createPlayer(url);
            _player.realize();

            _vc = (VideoControl) _player.getControl("VideoControl");
            if (_vc != null) {
                _videoField =
                        (Field) _vc.initDisplayMode(
                                GUIControl.USE_GUI_PRIMITIVE,
                                "net.rim.device.api.ui.Field");
                _vc.setVisible(true);
            }
        } catch (final MediaException pe) {
            System.out.println(pe.toString());
        } catch (final IOException ioe) {
            System.out.println(ioe.toString());
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
            System.out.println(e.toString());
        }
        return data;
    }

    /**
     * Creates a screen and displays the image
     * 
     * @param data
     *            The data representing the image to be rendered
     */
    private void displayImage(final byte[] data) {
        // Create image field
        final Bitmap image = Bitmap.createBitmapFromBytes(data, 0, -1, 5);
        final BitmapField imageField =
                new BitmapField(image, Field.FIELD_HCENTER);

        // Create and display screen
        final MainScreen screen =
                new MainScreen(net.rim.device.api.ui.Manager.NO_VERTICAL_SCROLL);
        screen.setTitle("Send Media Demo");
        screen.add(imageField);
        pushScreen(screen);
    }

    /**
     * Creates a video screen and starts the video player
     */
    private void displayVideo() {
        // Create and display screen
        final VideoMainScreen screen = new VideoMainScreen();
        screen.setTitle("Send Media Demo");
        screen.add(_videoField);
        pushScreen(screen);

        try {
            // Start media player
            _player.start();
        } catch (final MediaException pe) {
            System.out.println(pe.toString());
        }
    }

    /**
     * A main screen in which to play video files
     */
    class VideoMainScreen extends MainScreen {
        // Constructor
        VideoMainScreen() {
            super(net.rim.device.api.ui.Manager.NO_VERTICAL_SCROLL);
        }

        /**
         * @see net.rim.device.api.ui.Manager#sublayout(int,int)
         */
        protected void sublayout(final int width, final int height) {

            setVideoSize(Display.getWidth(), Display.getHeight());
            super.sublayout(width, height);
        }

        /**
         * @see net.rim.device.api.ui.Screen#onClose()
         */
        public boolean onClose() {
            _player.close();
            return super.onClose();
        }
    }
}
