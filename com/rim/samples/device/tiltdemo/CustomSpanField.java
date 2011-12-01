/*
 * CustomSpanField.java
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

package com.rim.samples.device.tiltdemo;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

/**
 * A class designed to demonstrate the optimization of custom fields for use
 * with touchscreen BlackBerry devices. This field displays four colored
 * rectangles spanning the width of the screen. As the display changes between
 * portrait and landscape orientations, the field is re-drawn to span the new
 * width of the screen.
 */
public final class CustomSpanField extends Field {
    /**
     * Overriding the paint method to draw four equally sized and differently
     * coloured rectangles.
     * 
     * @see net.rim.device.api.ui.Field#paint(Graphics)
     */
    protected void paint(final Graphics graphics) {
        // Each rectangle will take the entire height of the field and
        // one quarter of the width.
        final int rectHeight = getPreferredHeight();
        final int rectWidth = getPreferredWidth() / 4;

        // Paint each of the four rectangles
        graphics.drawRect(0, 0, rectWidth, rectHeight);
        graphics.setColor(Color.RED);
        graphics.fillRect(0, 0, rectWidth, rectHeight);

        graphics.drawRect(rectWidth, 0, rectWidth, rectHeight);
        graphics.setColor(Color.LIME);
        graphics.fillRect(rectWidth, 0, rectWidth, rectHeight);

        graphics.drawRect(rectWidth * 2, 0, rectWidth, rectHeight);
        graphics.setColor(Color.BLUE);
        graphics.fillRect(rectWidth * 2, 0, rectWidth, rectHeight);

        graphics.drawRect(rectWidth * 3, 0, rectWidth, rectHeight);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(rectWidth * 3, 0, rectWidth, rectHeight);

    }

    /**
     * @see net.rim.device.api.ui.Field#layout(int, int)
     */
    protected void layout(int width, int height) {
        // Calculate width
        width = Math.min(width, getPreferredWidth());

        // Calculate height
        height = Math.min(height, getPreferredHeight());

        setExtent(width, height);
    }

    /**
     * Field implementation. Ensures that the height of the field will always be
     * 1/10th the height of the display, regardless of orientation.
     * 
     * @see net.rim.device.api.ui.Field#getPreferredHeight()
     */
    public int getPreferredHeight() {
        return Display.getHeight() / 10;
    }

    /**
     * Field implementation. Ensures that the width of the field will always be
     * the entire width of the display, regardless of orientation.
     * 
     * @see net.rim.device.api.ui.Field#getPreferredWidth()
     */
    public int getPreferredWidth() {
        return Display.getWidth();
    }
}
