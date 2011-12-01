/**
 * CustomButtonField.java
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

package com.rim.samples.device.custombuttonsdemo;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

/**
 * CustomButtonField - class which creates button fields of various shapes.
 * Demonstrates how to create custom ui fields.
 */
public class CustomButtonField extends Field implements DrawStyle {
    static final int RECTANGLE = 1;
    static final int TRIANGLE = 2;
    static final int OCTAGON = 3;
    static final int FIXED_WIDTH = 4;
    static final int FULLSCREEN = 5;
    static final int COLOUR_BACKGROUND = 6;

    private final String _label;
    private final int _shape;
    private Font _font;
    private int _labelHeight;
    private int _labelWidth;

    /**
     * Constructs a button with specified label, and default style and shape.
     */
    public CustomButtonField(final String label) {
        this(label, RECTANGLE, 0);
    }

    /**
     * Constructs a button with specified label and shape, and default style.
     */
    public CustomButtonField(final String label, final int shape) {
        this(label, shape, 0);
    }

    /**
     * Constructs a button with specified label and style, and default shape.
     */
    public CustomButtonField(final String label, final long style) {
        this(label, RECTANGLE, style);
    }

    /**
     * Constructs a button with specified label, shape, and style.
     */
    public CustomButtonField(final String label, final int shape,
            final long style) {
        super(style);

        _label = label;
        _shape = shape;
        _font = getFont();
        _labelHeight = _font.getHeight();
        _labelWidth = _font.getAdvance(_label);
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
     * @see net.rim.device.api.ui.Field#getPreferredWidth()
     */
    public int getPreferredWidth() {
        switch (_shape) {
        case TRIANGLE:
            if (_labelWidth < _labelHeight) {
                return _labelHeight << 2;
            } else {
                return _labelWidth << 1;
            }

        case OCTAGON:
            if (_labelWidth < _labelHeight) {
                return _labelHeight + 4;
            } else {
                return _labelWidth + 8;
            }

        case FIXED_WIDTH:
            return _font.getAdvance(" ") * 35; // Always set to 35 spaces wide

        case FULLSCREEN:
            return Display.getWidth();

        default:
            return _labelWidth + 8;

        }
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#getPreferredHeight()
     */
    public int getPreferredHeight() {
        switch (_shape) {
        case TRIANGLE:
            if (_labelWidth < _labelHeight) {
                return _labelHeight << 1;
            } else {
                return _labelWidth;
            }

        case OCTAGON:
            return getPreferredWidth();

        default:
            return _labelHeight + 4;
        }
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#drawFocus(Graphics, boolean)
     */
    protected void drawFocus(final Graphics graphics, final boolean on) {
        switch (_shape) {
        case TRIANGLE:
            final int w = getWidth();
            final int h = w >> 1;

            for (int i = h - 1; i >= 2; --i) {
                graphics.invert(i, h - i, w - (i << 1), 1);
            }

            break;

        case OCTAGON:
            int x3 = getWidth();
            final int x = 5 * x3 / 17;
            int x2 = x3 - x;
            x3 = x3 - 1;
            x2 = x2 - 1;

            graphics.invert(1, x, getWidth() - 2, x2 - x + 1);

            for (int i = 1; i < x; ++i) {
                graphics.invert(1 + i, x - i, getWidth() - (i + 1 << 1), 1);
                graphics.invert(1 + i, x2 + i, getWidth() - (i + 1 << 1), 1);
            }

            break;

        default:
            graphics.invert(1, 1, getWidth() - 2, getHeight() - 2);
            break;
        }
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#layout(int, int)
     */
    protected void layout(int width, int height) {
        // Update the cached font - in case it has been changed.
        _font = getFont();
        _labelHeight = _font.getHeight();
        _labelWidth = _font.getAdvance(_label);

        // Calc width.
        width = Math.min(width, getPreferredWidth());

        // Calc height.
        height = Math.min(height, getPreferredHeight());

        // Set dimensions.
        setExtent(width, height);
    }

    /**
     * Field implementation.
     * 
     * @see net.rim.device.api.ui.Field#paint(Graphics)
     */
    protected void paint(final Graphics graphics) {
        int textX, textY, textWidth;
        final int w = getWidth();
        int h = getHeight();

        switch (_shape) {
        case TRIANGLE:
            h = w >> 1;
            final int m = (w >> 1) - 1;

            graphics.drawLine(0, h - 1, m, 0);
            graphics.drawLine(m, 0, w - 1, h - 1);
            graphics.drawLine(0, h - 1, w - 1, h - 1);

            textWidth = Math.min(_labelWidth, h);
            textX = w - textWidth >> 1;
            textY = h >> 1;
            break;

        case OCTAGON:
            final int x = 5 * w / 17;
            final int x2 = w - x - 1;
            final int x3 = w - 1;

            graphics.drawLine(0, x, 0, x2);
            graphics.drawLine(x3, x, x3, x2);
            graphics.drawLine(x, 0, x2, 0);
            graphics.drawLine(x, x3, x2, x3);
            graphics.drawLine(0, x, x, 0);
            graphics.drawLine(0, x2, x, x3);
            graphics.drawLine(x2, 0, x3, x);
            graphics.drawLine(x2, x3, x3, x2);

            textWidth = Math.min(_labelWidth, w - 6);
            textX = w - textWidth >> 1;
            textY = w - _labelHeight >> 1;
            break;

        case COLOUR_BACKGROUND:
            graphics.setColor(Color.LIGHTBLUE);
            graphics.fillRect(0, 0, w, h);
            graphics.setColor(Color.BLACK);

            textX = 4;
            textY = 2;
            textWidth = w - 6;
            break;

        default:
            graphics.drawRect(0, 0, w, h);

            textX = 4;
            textY = 2;
            textWidth = w - 6;
            break;
        }
        graphics.drawText(_label, textX, textY, (int) (getStyle()
                & DrawStyle.ELLIPSIS | DrawStyle.HALIGN_MASK), textWidth);
    }

    /**
     * Overridden so that the Event Dispatch thread can catch this event instead
     * of having it be caught here.
     * 
     * @see net.rim.device.api.ui.Field#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        fieldChangeNotify(0);
        return true;
    }

}
