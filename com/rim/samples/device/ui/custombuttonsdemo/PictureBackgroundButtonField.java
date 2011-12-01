/**
 * PictureBackgroundButtonField.java
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

package com.rim.samples.device.ui.custombuttonsdemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

/**
 * Custom button field that shows how to use images as button backgrounds.
 */
public class PictureBackgroundButtonField extends Field {
    private final String _label;
    private final int _labelHeight;
    private final int _labelWidth;
    private final Font _font;

    private Bitmap _currentPicture;
    private final Bitmap _onPicture = Bitmap
            .getBitmapResource("img/pink_scribble.bmp");
    private final Bitmap _offPicture = Bitmap
            .getBitmapResource("img/blue_scribble.bmp");

    /**
     * Constructor.
     * 
     * @param text
     *            The text to be displayed on the button
     * @param style
     *            Combination of field style bits to specify display attributes
     */
    public PictureBackgroundButtonField(final String text, final long style) {
        super(style);

        _font = getFont();
        _label = text;
        _labelHeight = _font.getHeight();
        _labelWidth = _font.getAdvance(_label);
        _currentPicture = _onPicture;
    }

    /**
     * @return The text on the button
     */
    String getText() {
        return _label;
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#getPreferredHeight()
     */
    public int getPreferredHeight() {
        return _labelHeight + 4;
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#getPreferredWidth()
     */
    public int getPreferredWidth() {
        return _labelWidth + 8;
    }

    /**
     * Field implementation. Changes the picture when focus is gained.
     * 
     * @see net.rim.device.api.ui.Field#onFocus(int)
     */
    protected void onFocus(final int direction) {
        _currentPicture = _onPicture;
        invalidate();
    }

    /**
     * Field implementation. Changes picture back when focus is lost.
     * 
     * @see net.rim.device.api.ui.Field#onUnfocus()
     */
    protected void onUnfocus() {
        _currentPicture = _offPicture;
        invalidate();
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#drawFocus(Graphics, boolean)
     */
    protected void drawFocus(final Graphics graphics, final boolean on) {
        // Do nothing
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#layout(int, int)
     */
    protected void layout(final int width, final int height) {
        setExtent(Math.min(width, getPreferredWidth()), Math.min(height,
                getPreferredHeight()));
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#paint(Graphics)
     */
    protected void paint(final Graphics graphics) {
        // First draw the background colour and picture
        graphics.setColor(Color.LIGHTPINK);
        graphics.fillRect(0, 0, getWidth(), getHeight());
        graphics.drawBitmap(0, 0, getWidth(), getHeight(), _currentPicture, 0,
                0);

        // Then draw the text
        graphics.setColor(Color.BLACK);
        graphics.setFont(_font);
        graphics.drawText(
                _label,
                4,
                2,
                (int) (getStyle() & DrawStyle.ELLIPSIS | DrawStyle.HALIGN_MASK),
                getWidth() - 6);
    }

    /**
     * Overridden so that the Event Dispatch thread can catch this event instead
     * of having it be caught here..
     * 
     * @see net.rim.device.api.ui.Field#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        fieldChangeNotify(1);
        return true;
    }

}
