/*
 * JustifiedHorizontalFieldManager.java
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
 * A Manager class which divides its available width between two fields
 */
public class JustifiedHorizontalFieldManager extends Manager {
    private final Field _leftField;
    private final Field _rightField;
    private final boolean _giveLeftFieldPriority;

    /**
     * Creates a new JustifiedHorizontalFieldManager
     * 
     * @param leftField
     *            Field to be positioned on the left
     * @param rightField
     *            Field to be positioned on the right
     * @param giveLeftFieldPriority
     *            True is the left field is to be given all of its preferred
     *            width, false otherwise
     */
    public JustifiedHorizontalFieldManager(final Field leftField,
            final Field rightField, final boolean giveLeftFieldPriority) {
        super(USE_ALL_WIDTH);
        _leftField = leftField;
        _rightField = rightField;

        add(_leftField);
        add(_rightField);

        _giveLeftFieldPriority = giveLeftFieldPriority;
    }

    /**
     * @see Manager#sublayout(int, int)
     */
    protected void sublayout(int width, int height) {
        Field firstField;
        Field secondField;
        if (_giveLeftFieldPriority) {
            firstField = _leftField;
            secondField = _rightField;
        } else {
            firstField = _rightField;
            secondField = _leftField;
        }

        int maxHeight = 0;

        final int leftFieldLeftMargin = _leftField.getMarginLeft();
        final int leftFieldRightMargin = _leftField.getMarginRight();
        final int rightFieldLeftMargin = _rightField.getMarginLeft();
        final int rightFieldRightMargin = _rightField.getMarginRight();

        final int firstFieldMarginBottom = firstField.getMarginBottom();
        final int firstFieldMarginTop = firstField.getMarginTop();
        final int secondFieldMarginBottom = secondField.getMarginBottom();
        final int secondFieldMarginTop = secondField.getMarginTop();

        int availableWidth = width;
        availableWidth -= leftFieldLeftMargin;
        availableWidth -= Math.max(leftFieldRightMargin, rightFieldLeftMargin);
        availableWidth -= rightFieldRightMargin;

        layoutChild(firstField, availableWidth, height - firstFieldMarginTop
                - firstFieldMarginBottom);
        maxHeight =
                Math.max(maxHeight, firstFieldMarginTop
                        + firstField.getHeight() + firstFieldMarginBottom);
        availableWidth -= firstField.getWidth();

        layoutChild(secondField, availableWidth, height - secondFieldMarginTop
                - secondFieldMarginBottom);
        maxHeight =
                Math.max(maxHeight, secondFieldMarginTop
                        + secondField.getHeight() + secondFieldMarginBottom);
        availableWidth -= secondField.getWidth();

        if (!isStyle(Field.USE_ALL_HEIGHT)) {
            height = maxHeight;
        }
        if (!isStyle(Field.USE_ALL_WIDTH)) {
            width -= availableWidth;
        }

        setPositionChild(_leftField, leftFieldLeftMargin, 0);
        setPositionChild(_rightField, width - _rightField.getWidth()
                - rightFieldRightMargin, 0);

        setExtent(width, height);
    }
}
