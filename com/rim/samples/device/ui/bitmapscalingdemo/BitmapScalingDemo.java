/*
 * BitmapScalingDemo.java
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

package com.rim.samples.device.ui.bitmapscalingdemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A sample application to demonstrate Bitmap scaling
 */
public class BitmapScalingDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final BitmapScalingDemo app = new BitmapScalingDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new BitmapScalingDemo object
     */
    public BitmapScalingDemo() {
        pushScreen(new BitmapScalingDemoScreen());
    }

    /**
     * MainScreen class for the BitmapScalingDemo application
     */
    static class BitmapScalingDemoScreen extends MainScreen {
        private static String LABEL_X = " x ";

        /**
         * Creates a new BitmapScalingDemoScreen object
         */
        BitmapScalingDemoScreen() {
            setTitle("Bitmap Scaling Demo");

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
        }
    }
}
