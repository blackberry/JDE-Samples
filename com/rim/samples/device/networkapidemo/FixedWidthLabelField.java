/**
 * FixedWidthLabelField.java
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

package com.rim.samples.device.networkapidemo;

import net.rim.device.api.ui.component.LabelField;

/**
 * A custom LabelField that has a fixed width
 */
public class FixedWidthLabelField extends LabelField {
    private final int _width;

    /**
     * Creates a new FixedWidthLabelField object
     * 
     * @param text
     *            The text for this label
     * @param width
     *            The width for this label
     */
    public FixedWidthLabelField(final String text, final int width) {
        super(text);
        _width = width;
    }

    /**
     * @see LabelField#getPreferredWidth()
     */
    public int getPreferredWidth() {
        return _width;
    }

    /**
     * @see LabelField#layout(int, int)
     */
    protected void layout(int width, int height) {
        width = getPreferredWidth();
        height = getPreferredHeight();
        super.layout(width, height);
        super.setExtent(width, height);
    }
}
