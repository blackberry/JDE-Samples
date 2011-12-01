/*
 * SpeedBumpScreen.java
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

package com.rim.samples.device.ui.uitoolkitdemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Adjustment;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.ScrollView;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This class demonstrates how the Adjustment class can be used to create
 * "speed bumps" within a Manager. Three bitmaps, each representing a "page",
 * are added to a scrollable horizontal manager. An Adjustment object is used to
 * control the amount of horizontal movement required before the manager will
 * actually scroll to the adjacent page.
 */
public final class SpeedBumpScreen extends MainScreen {
    private static final int ARC_WIDTH = 80;
    private static final int ARC_HEIGHT = 80;
    private static final int ROUND_RECT_WIDTH_PAD = 20;
    private static final int ROUND_RECT_HEIGHT_PAD = 40;

    /**
     * Creates a new SpeedBumpScreen object
     */
    public SpeedBumpScreen() {
        super(ScrollView.NO_VERTICAL_SCROLL);

        setTitle("Speed Bump Screen");

        final int displayWidth = Display.getWidth();
        final int displayHeight = Display.getHeight();

        // Create scrollable manager to contain pages
        final HorizontalFieldManager manager =
                new HorizontalFieldManager(ScrollView.HORIZONTAL_SCROLL);

        // Create three pages inside the manager
        for (int i = 0; i < 3; i++) {
            final Bitmap bitmap = new Bitmap(displayWidth, displayHeight);

            final Graphics g = Graphics.create(bitmap);
            g.setBackgroundColor(Color.BLACK);
            g.clear();
            g.setColor(Color.ALICEBLUE);
            g.fillRoundRect(10, 10, displayWidth - ROUND_RECT_WIDTH_PAD,
                    displayHeight - ROUND_RECT_HEIGHT_PAD, ARC_WIDTH,
                    ARC_HEIGHT);
            g.setColor(Color.DARKGRAY);
            g.drawText(Integer.toString(i), displayWidth / 2, displayHeight / 2);

            final BitmapField field = new BitmapField(bitmap);
            manager.add(field);
        }

        // Initialize Adjustment
        final Adjustment adjustment = manager.getHorizontalAdjustment();
        adjustment.setPageSize(displayWidth);

        // Set page step so that pages must be moved 1/4 of their
        // width for the screen to scroll to the adjacent page.
        adjustment.setPageStep(Display.getWidth() / 4);

        add(manager);
    }
}
