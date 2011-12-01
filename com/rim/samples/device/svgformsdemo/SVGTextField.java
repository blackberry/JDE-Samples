/*
 * SVGTextField.java
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

package com.rim.samples.device.svgformsdemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.Touchscreen;
import net.rim.device.api.ui.component.TextField;

/**
 * A Custom text field that is the base for the SVG - Textbox
 */
class SVGTextField extends TextField {
    // Reference to the current text panel
    private final TextPanel _myTextPanel;

    /**
     * Constructs a new SVGTextField
     * 
     * @param textPanel
     *            The TextPanel that holds the SVGTextField
     */
    SVGTextField(final TextPanel textPanel) {
        _myTextPanel = textPanel;
    }

    /**
     * @see Field#keyChar(char, int, int)
     */
    public boolean keyChar(final char key, final int status, final int time) {
        super.keyChar(key, status, time);
        _myTextPanel.setText(this.getText());
        return true;
    }

    /**
     * @see TextField#navigationClick(int, int)
     */
    protected boolean navigationClick(final int status, final int time) {
        if (Touchscreen.isSupported()) {
            return false; // Ignore click events - centralize touch event
                          // handling in touchEvent() method
        }

        if (_myTextPanel.isTextBoxActive()) {
            _myTextPanel.removeTextField();
        }
        return true;
    }

    /**
     * @see TextField#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        if (message.getEvent() == TouchEvent.CLICK) {
            if (_myTextPanel.isTextBoxActive()) {
                _myTextPanel.removeTextField();
            }
        }
        return super.touchEvent(message);
    }
}
