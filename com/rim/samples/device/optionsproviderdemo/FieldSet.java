/*
 * FieldSet.java
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

package com.rim.samples.device.blackberry.options;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.Background;
import net.rim.device.api.ui.decor.Border;

/**
 * Defines a vertical field manager for a set of fields
 */
public class FieldSet extends VerticalFieldManager {
    private final String _title;
    private final Border _titleBorder;
    private final Border _contentBorder;

    /**
     * Creates a new instance of FieldSet
     * 
     * @param title
     *            The title of the FieldSet. It will be displayed as the title.
     * @param titleBorder
     *            The border for the title section of the FieldSet
     * @param contentBorder
     *            The border for the content section of the FieldSet
     * @param style
     *            A border style for the whole set. A style constant defined in
     *            {@link Border}.
     */
    public FieldSet(final String title, final Border titleBorder,
            final Border contentBorder, final long style) {
        super(style);

        _title = title;
        _titleBorder = titleBorder;
        _contentBorder = contentBorder;
    }

    /**
     * Applies the font and borders to the FieldSet by creating a new instance
     * of FieldSetBorder
     */
    protected void applyFont() {
        setBorder(new FieldSetBorder(_title, getFont(), _titleBorder,
                _contentBorder), true);
    }

    /**
     * Constructs and paints the borders for the FieldSet
     */
    private static class FieldSetBorder extends Border {
        private final String _title;
        private final Font _font;
        private final Border _titleBorder;
        private final Border _contentBorder;

        private final Background _titleBackground;

        private final int _titleAreaHeight;
        private final int _titleBorderTopAndBottom;
        private final int _titleBorderLeftAndRight;

        private final XYRect _paintRect = new XYRect();

        /**
         * Constructs a new FieldSetBorder instance
         * 
         * @param title
         *            The title of the FieldSet
         * @param font
         *            The font that is going to be used for the FieldSet
         * @param titleBorder
         *            Border instance for the title section of the FieldSet
         * @param contentBorder
         *            Border instance for the content section of the FieldSet
         */
        public FieldSetBorder(final String title, final Font font,
                final Border titleBorder, final Border contentBorder) {
            super(getComposedBorderEdges(font, titleBorder, contentBorder), 0);

            _title = title;
            _font = font;
            _titleBorder = titleBorder;
            _contentBorder = contentBorder;

            _titleBackground = titleBorder.getBackground();

            _titleAreaHeight =
                    titleBorder.getTop() + font.getHeight()
                            + titleBorder.getBottom();
            _titleBorderTopAndBottom =
                    titleBorder.getTop() + titleBorder.getBottom();
            _titleBorderLeftAndRight =
                    titleBorder.getLeft() + titleBorder.getRight();
        }

        /**
         * Creates a new XYEdes object that is the length and width of the
         * overall FieldSet
         * 
         * @param font
         *            The font that is used in the FieldSet
         * @param titleBorder
         *            The border for the title section of the FieldSet
         * @param contentBorder
         *            The border for the content section of the FieldSet
         * @return A new XYEdges object that is the length and width of the
         *         overall FieldSet
         * @throws IllegalArgumentException
         *             if the titleBorder and contentBorder have different left
         *             or right edges.
         */
        public static XYEdges getComposedBorderEdges(final Font font,
                final Border titleBorder, final Border contentBorder) {
            if (titleBorder.getLeft() != contentBorder.getLeft()
                    || titleBorder.getRight() != contentBorder.getRight()) {
                throw new IllegalArgumentException(
                        "borders must have matching left and right edges");
            }

            return new XYEdges(titleBorder.getTop() + font.getHeight()
                    + titleBorder.getBottom() + contentBorder.getTop(),
                    contentBorder.getRight(), contentBorder.getBottom(),
                    contentBorder.getLeft());
        }

        /**
         * Paints the screen
         * 
         * @see net.rim.device.api.ui.decor.Border#paint(Graphics, XYRect)
         */
        public void paint(final Graphics graphics, final XYRect rect) {
            // paint() is always called from the event thread so we don't have
            // to worry about concurrent access to _paintRect
            _paintRect.set(rect.x, rect.y, rect.width, _titleAreaHeight);
            _titleBorder.paint(graphics, _paintRect);

            if (_titleBackground != null) {
                _paintRect.x += _titleBorder.getLeft();
                _paintRect.y += _titleBorder.getTop();
                _paintRect.width -= _titleBorderLeftAndRight;
                _paintRect.height -= _titleBorderTopAndBottom;
                _titleBackground.draw(graphics, _paintRect);
            }

            _paintRect.set(rect.x, rect.y + _titleAreaHeight, rect.width,
                    rect.height - _titleAreaHeight);
            _contentBorder.paint(graphics, _paintRect);

            final Font oldFont = graphics.getFont();
            try {
                graphics.setFont(_font);
                graphics.drawText(_title, rect.x + _titleBorder.getLeft(),
                        rect.y + _titleBorder.getTop(), DrawStyle.ELLIPSIS,
                        rect.width - _titleBorderLeftAndRight);
            } finally {
                graphics.setFont(oldFont);
            }
        }

        /**
         * Gets the current background of the contentBorder
         * 
         * @return The Background that the contentBorder is using
         */
        public Background getBackground() {
            return _contentBorder.getBackground();
        }

    }

}
