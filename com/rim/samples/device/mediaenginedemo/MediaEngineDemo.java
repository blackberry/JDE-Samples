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

package com.rim.samples.device.mediaengine;

import java.io.IOException;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
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
 * A simple example of the MediaEngine API's. PME files in this sample are
 * version 1.2 In Plazmic Composer version 3.0.0.21 export content as SVG. Open
 * SVG file in a text editor and change height and width attributes within the
 * svg tag to pixel values. If content includes png files you may need to modify
 * the paths in the xlink:href attributes within the image tags as well.
 * Finally, use the command line utility to transcode the SVG to PME format (eg.
 * C:\projectDir>svgc -pme 12 filename.svg).
 */
public final class MediaEngineDemo extends UiApplication {
    private RichTextField _statusField;
    private final MediaDisplayScreen _display;

    /**
     * Entry point for application.
     */
    public static void main(final String[] args) {
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
     * The intro screen, hosting any status info.
     */
    private final class MediaSampleScreen extends MainScreen {
        /**
         * Contructor
         */
        public MediaSampleScreen() {
            final LabelField title =
                    new LabelField("Media Engine Demo", DrawStyle.ELLIPSIS
                            | Field.USE_ALL_WIDTH);
            setTitle(title);

            _statusField =
                    new RichTextField("Please select a PME from the Menu.");
            add(_statusField);
        }

        /**
         * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
         */
        public void makeMenu(final Menu menu, final int instance) {
            // Invoke some content using the jar://url
            menu.add(new MenuItem("PME 1", 5, 5) {
                public void run() {
                    // Check for optimal screen dimensions.
                    if (Display.getHeight() != 240 || Display.getWidth() != 320) {
                        Dialog.alert("Sample has been optimized for a 320 x 240 display. "
                                + "This device has a "
                                + Display.getWidth()
                                + " x " + Display.getHeight() + " display.");
                    }
                    _statusField.setText("Loading, please wait...");

                    // Play media in a separate thread.
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
            menu.add(new MenuItem("PME 2", 6, 6) {
                public void run() {
                    // Check for optimal screen dimensions.
                    if (Display.getHeight() != 240 || Display.getWidth() != 320) {
                        Dialog.alert("Sample has been optimized for a 320 x 240 display. "
                                + "This device has a "
                                + Display.getWidth()
                                + " x " + Display.getHeight() + " display.");

                    }
                    _statusField.setText("Loading, please wait...");

                    // Play media after all pending events have been processed.
                    UiApplication.getUiApplication().invokeLater(
                            new Runnable() {
                                public void run() {
                                    playMedia("jar:///weather.pme");
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
                System.out.print(ioe.toString());
            } catch (final MediaException me) {
                final String msg =
                        "Error during media loading: " + me.getCode()
                                + me.getMessage();
                System.out.println(msg);
                _statusField.setText(_statusField.getText() + msg);
            }

            /* parent. */_display.init((Field) player.getUI(), player);
            pushScreen(/* parent. */_display);
        }
    }

    /**
     * A MainScreen to display pme content.
     */
    static private final class MediaDisplayScreen extends MainScreen {
        private Field _current;
        private MediaPlayer _player;

        public MediaDisplayScreen() {
        }

        public void init(final Field f, final MediaPlayer player) {
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
                final String msg =
                        "Error during media playback: " + me.getCode()
                                + me.getMessage();
                System.out.println(msg);
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
