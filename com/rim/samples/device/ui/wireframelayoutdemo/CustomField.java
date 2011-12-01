/*
 * CustomField.java
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

package com.rim.samples.device.ui.wireframelayoutdemo;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.BackgroundFactory;

/**
 * A Field class which has a predefined height and definable background color
 * and text.
 */
public class CustomField extends Field {
    private static final int FIELD_HEIGHT = 40;

    private final String _text;

    /**
     * Creates a new CustomField object with provided color and text
     * 
     * @param color
     *            Background color for this field
     * @param text
     *            Text to display in this field
     */
    CustomField(final int color, final String text) {
        super(Field.FOCUSABLE);

        _text = text;

        // Set the background color for this field
        final Background background =
                BackgroundFactory.createSolidBackground(color);
        setBackground(background);
    }

    /**
     * @see Field#layout(int, int)
     */
    protected void layout(int width, int height) {
        // Calculate width
        width = Math.min(width, getPreferredWidth());

        // Calculate height
        height = Math.min(height, getPreferredHeight());

        setExtent(width, height);
    }

    /**
     * @see Field#getPreferredHeight()
     */
    public int getPreferredHeight() {
        return FIELD_HEIGHT;
    }

    /**
     * @see Field#getPreferredWidth()
     */
    public int getPreferredWidth() {
        return Display.getWidth();
    }

    /**
     * @see Field#paint(Graphics)
     */
    protected void paint(final Graphics graphics) {
        final int rectHeight = getPreferredHeight();
        final int rectWidth = getPreferredWidth();

        graphics.drawRect(0, 0, rectWidth, rectHeight);
        graphics.drawText(_text, 0, 0);
    }
}
