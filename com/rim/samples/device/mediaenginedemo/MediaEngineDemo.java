/**
 * MediaEngineDemo.java
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

package com.rim.samples.device.mediaenginedemo;

import java.io.IOException;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.MediaManager;
import net.rim.plazmic.mediaengine.MediaPlayer;

/**
 * A simple example of the Media Engine API's. The demo uses a
 * net.rim.plazmic.mediaengine.MediaManager object to create a media object from
 * a pme file embedded in this project. The media object is then rendered using
 * a net.rim.plazmic.mediaengine.MediaPlayer object. The MediaListenerImpl class
 * implements the MediaListener interface so the application can recieve media
 * event notifications. For information on creating PME content, please refer to
 * the Plazmic Composer User Guide, available at www.plazmic.com.
 */
public final class MediaEngineDemo extends UiApplication {
    private RichTextField _statusField;
    private final MediaDisplayScreen _display;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final MediaEngineDemo app = new MediaEngineDemo();
        app.enterEventDispatcher();
    }

    /**
     * Constructor
     */
    public MediaEngineDemo() {
        _display = new MediaDisplayScreen();
        pushScreen(new MediaSampleScreen());
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
     * The intro screen, hosting any status info.
     */
    private final class MediaSampleScreen extends MainScreen {
        /**
         * Contructor
         */
        MediaSampleScreen() {
            final LabelField title = new LabelField("Media Engine Demo");
            setTitle(title);

            _statusField =
                    new RichTextField("Please select a PME from the Menu.");
            add(_statusField);
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        public void makeMenu(final Menu menu, final int instance) {
            // Invoke some content using the jar://url
            menu.add(new MenuItem("PME 1", 0, 0) {
                public void run() {
                    // Check for optimal screen dimensions.
                    if (Display.getHeight() != 320 || Display.getWidth() != 480) {
                        Dialog.alert("Sample has been optimized for a 480 x 320 display. "
                                + "This device has a "
                                + Display.getWidth()
                                + " x " + Display.getHeight() + " display.");
                    }
                    _statusField.setText("Loading, please wait...");

                    // Play media after all pending events have been processed.
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    playMedia("jar:///runner.pme");
                                    _statusField
                                            .setText("Please select a PME from the Menu.");
                                }
                            });
                }
            });

            // Invoke some content using the jar:///url
            menu.add(new MenuItem("PME 2", 1, 0) {
                public void run() {
                    // Check for optimal screen dimensions.
                    if (Display.getHeight() != 320 || Display.getWidth() != 480) {
                        Dialog.alert("Sample has been optimized for a 480 x 320 display. "
                                + "This device has a "
                                + Display.getWidth()
                                + " x " + Display.getHeight() + " display.");

                    }
                    _statusField.setText("Loading, please wait...");

                    // Play media after all pending events have been processed.
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    playMedia("jar:///planets.pme");
                                    _statusField
                                            .setText("Please select a PME from the Menu.");
                                }
                            });
                }
            });

            super.makeMenu(menu, instance);
        }

        /**
         * Play some media.
         */
        private void playMedia(final String mediaUrl) {
            final MediaPlayer player = new MediaPlayer();
            final MediaManager manager = new MediaManager();
            final MediaListenerImpl listener = new MediaListenerImpl();
            player.addMediaListener(listener);
            manager.addMediaListener(listener);

            try {
                final Object media = manager.createMedia(mediaUrl);
                player.setMedia(media);

            } catch (final IOException ioe) {
                errorDialog("MediaManager#createMedia() threw "
                        + ioe.toString());
            } catch (final MediaException me) {
                final String msg =
                        "Error during media loading: " + me.getCode()
                                + me.getMessage();
                _statusField.setText(_statusField.getText() + msg);
                errorDialog(msg);
            }

            _display.init((Field) player.getUI(), player);
            pushScreen(_display);
        }
    }

    /**
     * A MainScreen to display pme content.
     */
    static private final class MediaDisplayScreen extends MainScreen {
        private Field _current;
        private MediaPlayer _player;

        // Constructor
        MediaDisplayScreen() {
        }

        /**
         * Initializes the player
         * 
         * @param f
         *            The field to display the player with
         * @param player
         *            The media player to play the media with
         */
        private void init(final Field f, final MediaPlayer player) {
            if (_player != null) {
                _player.close();
            }

            _player = player;

            if (_current != null) {
                delete(_current);
            }

            _current = f;
            add(f);
        }

        /**
         * Overrides onUiEngineAttached() method in superclass.
         * 
         * @param attached
         *            True is it is an attach event, false if it is a detach
         *            event.
         */
        public void onUiEngineAttached(final boolean attached) {
            try {
                _player.start();
            } catch (final MediaException me) {
                errorDialog("MediaPlayer#start() threw " + me.toString());
            }
        }

        /**
         * @see net.rim.device.api.ui.Screen#onClose()
         */
        public boolean onClose() {
            final boolean retval = super.onClose();
            if (retval) {
                _player.close();
            }
            return retval;
        }

        /**
         * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
         */
        public boolean
                keyChar(final char key, final int status, final int time) {
            boolean retval = false;

            switch (key) {
            case Characters.ESCAPE:
                UiApplication.getUiApplication().popScreen(this);
                _player.close();
                retval = true;
                break;
            }

            return retval;
        }
    }
}
