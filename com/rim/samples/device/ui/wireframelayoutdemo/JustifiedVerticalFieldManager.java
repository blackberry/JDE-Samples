/*
 * JustifiedVerticalFieldManager.java
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

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

/**
 * A Manager class which divides its available height between two fields
 */
public class JustifiedVerticalFieldManager extends Manager {
    private final Field _topField;
    private final Field _bottomField;
    private final boolean _giveTopFieldPriority;

    /**
     * Creates a new JustifiedVerticalFieldManager
     * 
     * @param topField
     *            Field to be positioned at the top
     * @param bottomField
     *            Field to be positioned at the bottom
     * @param giveTopFieldPriority
     *            True is the top field is to be given all of its preferred
     *            width, false otherwise
     */
    public JustifiedVerticalFieldManager(final Field topField,
            final Field bottomField, final boolean giveTopFieldPriority) {
        super(USE_ALL_HEIGHT);

        _topField = topField;
        _bottomField = bottomField;

        add(_topField);
        add(_bottomField);

        _giveTopFieldPriority = giveTopFieldPriority;
    }

    /**
     * @see Manager#sublayout(int, int)
     */
    protected void sublayout(final int width, final int height) {
        Field firstField;
        Field secondField;
        if (_giveTopFieldPriority) {
            firstField = _topField;
            secondField = _bottomField;
        } else {
            firstField = _bottomField;
            secondField = _topField;
        }

        int maxWidth = 0;

        final int firstFieldLeftMargin = firstField.getMarginLeft();
        final int firstFieldRightMargin = firstField.getMarginRight();
        final int secondFieldLeftMargin = secondField.getMarginLeft();
        final int secondFieldRightMargin = secondField.getMarginRight();

        final int bottomFieldMarginBottom = _bottomField.getMarginBottom();
        final int topFieldMarginTop = _topField.getMarginTop();

        int availableHeight = height;
        availableHeight -= topFieldMarginTop;
        availableHeight -=
                Math.max(_topField.getMarginBottom(), _bottomField
                        .getMarginTop());
        availableHeight -= bottomFieldMarginBottom;

        layoutChild(firstField, width - firstFieldLeftMargin
                - firstFieldRightMargin, availableHeight);
        maxWidth =
                Math.max(maxWidth, firstFieldLeftMargin + firstField.getWidth()
                        + firstFieldRightMargin);
        availableHeight -= firstField.getHeight();

        layoutChild(secondField, width - secondFieldLeftMargin
                - secondFieldRightMargin, availableHeight);

        maxWidth =
                Math.max(maxWidth, secondFieldLeftMargin
                        + secondField.getWidth() + secondFieldRightMargin);
        availableHeight -= secondField.getHeight();

        setPositionChild(_topField, 0, topFieldMarginTop);
        setPositionChild(_bottomField, 0, height - _bottomField.getHeight()
                - bottomFieldMarginBottom);

        setExtent(width, height);
    }
}
