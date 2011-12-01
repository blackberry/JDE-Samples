/*
 * OpenVGDemoScreen.java
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

package com.rim.samples.device.openvgdemo;

import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A subclass of MainScreen that allows a user to switch the display between
 * this application's two VGField implementations.
 */
public class OpenVGDemoScreen extends MainScreen {
    // VGField for the path example
    private final ParsedPathVGField _pathField;

    // VGField for the animated image example
    private final CreateImageVGField _createImageField;

    // Indicates which field is currently displayed
    private boolean _drawPathFieldLoaded;

    /**
     * Constructs a new OpenVGDemoScreen object
     */
    public OpenVGDemoScreen() {
        // Create the two VGField examples
        _pathField = new ParsedPathVGField();
        _createImageField = new CreateImageVGField();

        // Add the path field to begin
        add(_pathField);
        _drawPathFieldLoaded = true;
    }

    /**
     * Toggles the display between the two VGField examples
     */
    private void swapFields() {
        if (_drawPathFieldLoaded) {
            replace(_pathField, _createImageField);
            _drawPathFieldLoaded = false;
        } else {
            replace(_createImageField, _pathField);
            _drawPathFieldLoaded = true;
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        if (action == ACTION_INVOKE) {
            swapFields();
            return true;
        }

        return super.invokeAction(action);
    }

    /**
     * @see net.rim.device.api.ui.Screen#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent message) {
        final TouchGesture touchGesture = message.getGesture();

        if (touchGesture != null) {
            final int event = touchGesture.getEvent();

            if (event == TouchGesture.TAP) {
                swapFields();

                return true;
            }
        }

        return super.touchEvent(message);
    }
}
