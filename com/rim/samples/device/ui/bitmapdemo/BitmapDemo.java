/*
 * BitmapDemo.java
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

package com.rim.samples.device.ui.bitmapdemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.StringProvider;

/**
 * A sample application to demonstrate Bitmap scaling and Bitmap animation
 */
public class BitmapDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final BitmapDemo app = new BitmapDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new BitmapDemo object
     */
    public BitmapDemo() {
        pushScreen(new BitmapDemoScreen());
    }

    /**
     * MainScreen class for the BitmapDemo application
     */
    static class BitmapDemoScreen extends MainScreen {
        private static final String LABEL_X = " x ";

        /**
         * Creates a new BitmapDemoScreen object
         */
        BitmapDemoScreen() {
            setTitle("Bitmap Demo");

            // Create a Bitmap from a project resource
            final Bitmap bitmapOrig = Bitmap.getBitmapResource("rim.png");

            // Create a Bitmap of arbitrary size
            final int scaledX = 175;
            final int scaledY = 50;
            Bitmap bitmapScaled = new Bitmap(scaledX, scaledY);

            // Scale the original Bitmap into the new Bitmap using
            // a Lanczos filter.
            bitmapOrig.scaleInto(bitmapScaled, Bitmap.FILTER_LANCZOS);

            // Display the original Bitmap on the screen
            final BitmapField bitmapFieldOrig =
                    new BitmapField(bitmapOrig, Field.FOCUSABLE);
            final StringBuffer strBuff = new StringBuffer("Original - ");
            strBuff.append(bitmapOrig.getWidth());
            strBuff.append(LABEL_X);
            strBuff.append(bitmapOrig.getHeight());
            add(new LabelField(strBuff.toString()));
            add(bitmapFieldOrig);

            add(new SeparatorField());

            // Display the scaled Bitmap on the screen
            final BitmapField bitmapFieldScaled1 =
                    new BitmapField(bitmapScaled, Field.FOCUSABLE);
            strBuff.delete(0, strBuff.length());
            strBuff.append("\nScaled - ");
            strBuff.append(bitmapScaled.getWidth());
            strBuff.append(LABEL_X);
            strBuff.append(bitmapScaled.getHeight());
            strBuff.append(" - FILTER_LANCZOS - Aspect ratio not preserved");
            add(new LabelField(strBuff.toString()));
            add(bitmapFieldScaled1);

            add(new SeparatorField());

            // Redefine the scaled Bitmap
            bitmapScaled = new Bitmap(scaledX, scaledY);

            // Scale the original Bitmap into the new Bitmap using
            // a bilinear filter and maintaining aspect ratio.
            bitmapOrig.scaleInto(bitmapScaled, Bitmap.FILTER_BILINEAR,
                    Bitmap.SCALE_TO_FILL);

            // Display the newly scaled Bitmap on the screen
            final BitmapField bitmapFieldScaled2 =
                    new BitmapField(bitmapScaled, Field.FOCUSABLE);
            strBuff.delete(0, strBuff.length());
            strBuff.append("\nScaled - ");
            strBuff.append(bitmapScaled.getWidth());
            strBuff.append(LABEL_X);
            strBuff.append(bitmapScaled.getHeight());
            strBuff.append(" - FILTER_BILINEAR - Aspect ratio preserved");
            add(new LabelField(strBuff.toString()));
            add(bitmapFieldScaled2);

            add(new SeparatorField());

            // Redefine the scaled Bitmap
            bitmapScaled = new Bitmap(scaledX, scaledY);

            // Calculate fragment dimensions
            final int fragmentWidth = bitmapOrig.getWidth() >> 1; // >> 1
                                                                  // equivalent
                                                                  // to / 2
            final int fragmentHeight = bitmapOrig.getHeight() >> 1; // >> 1
                                                                    // equivalent
                                                                    // to / 2

            // Scale a fragment of the original Bitmap into the new Bitmap
            // using a box filter.
            bitmapOrig.scaleInto(0, 0, fragmentWidth, fragmentHeight,
                    bitmapScaled, 0, 0, bitmapScaled.getWidth(), bitmapScaled
                            .getHeight(), Bitmap.FILTER_BOX);

            // Display the newly scaled Bitmap on the screen
            final BitmapField bitmapFieldScaled3 =
                    new BitmapField(bitmapScaled, Field.FOCUSABLE);
            strBuff.delete(0, strBuff.length());
            strBuff.append("\nScaled fragment ");
            strBuff.append(fragmentWidth);
            strBuff.append(LABEL_X);
            strBuff.append(fragmentHeight);
            strBuff.append(" into ");
            strBuff.append(bitmapScaled.getWidth());
            strBuff.append(LABEL_X);
            strBuff.append(bitmapScaled.getHeight());
            strBuff.append(" - FILTER_BOX");
            add(new LabelField(strBuff.toString()));
            add(bitmapFieldScaled3);

            // Add a menu item to display an animation in a popup screen
            final MenuItem showAnimation =
                    new MenuItem(new StringProvider("Show Animation"),
                            0x230010, 0);
            showAnimation.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    // Create an EncodedImage object to contain an animated
                    // gif resource.
                    final EncodedImage encodedImage =
                            EncodedImage
                                    .getEncodedImageResource("animation.gif");

                    // Create a BitmapField to contain the animation
                    final BitmapField bitmapFieldAnimation = new BitmapField();
                    bitmapFieldAnimation.setImage(encodedImage);

                    // Push a popup screen containing the BitmapField onto the
                    // display stack.
                    UiApplication.getUiApplication().pushScreen(
                            new BitmapDemoPopup(bitmapFieldAnimation));
                }
            }));

            addMenuItem(showAnimation);
        }

        /**
         * A popup screen for displaying an animated image
         */
        private static class BitmapDemoPopup extends PopupScreen {
            /**
             * Creates a new BitmapDemoPopup object
             * 
             * @param bitmapField
             *            <code>BitmapField</code> to display inside this popup
             */
            public BitmapDemoPopup(final BitmapField bitmapField) {
                super(new VerticalFieldManager());
                add(bitmapField);
            }

            /**
             * @see Screen#keyChar(char, int, int)
             */
            protected boolean keyChar(final char c, final int status,
                    final int time) {
                if (c == Characters.ESCAPE) {
                    close();
                }

                return super.keyChar(c, status, time);
            }
        }
    }
}
