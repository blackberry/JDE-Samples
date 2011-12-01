//#preprocessor

/*
 * PreprocessorDemoScreen.java
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

package com.rim.samples.device.preprocessordemo;

/**
 * Import the TouchEvent and TouchGesture classes if TOUCH_API exists. 
 * 
 * Note: net.rim.device.api.ui.*; could be used instead of the preprocessor
 *       directive since the package includes the TouchEvent and TouchGesture
 *       classes. If these classes are supported in the JDE version then they 
 *       will be included by the package import. However, the directive serves
 *       as an example of how to import packages which are exclusive to certain
 *       JDE versions without fragmenting the code across multiple code bases.
 */

//#ifdef TOUCH_API

import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This screen displays a text field which may be cleared by the user. The
 * preprocessor is used to determine whether to include the swipe gesture as an
 * option to clear the text.
 */
public final class PreprocessorDemoScreen extends MainScreen {
    private final BasicEditField _textArea;

    /**
     * Creates a new PreprocessorDemoScreen object
     */
    public PreprocessorDemoScreen() {
        setTitle("Preprocessor Demo");

        // Indicate how the user may clear the text

        // #ifdef TOUCH_API
        add(new RichTextField(
                "Swipe the screen or use the menu to clear the text"));
        // #else
        add(new RichTextField("Use the menu to clear the text"));
        // #endif

        add(new SeparatorField());

        _textArea = new BasicEditField("Text: ", "");
        add(_textArea);
    }

    // #ifdef TOUCH_API

    /**
     * Clears the text field if a swipe touch gesture is detected
     * 
     * @see net.rim.device.api.ui.Screen#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent event) {
        if (event.getEvent() == TouchEvent.GESTURE
                && event.getGesture().getEvent() == TouchGesture.SWIPE) {
            _textArea.setText("");
            return true;
        }

        return super.touchEvent(event);
    }

    // #endif

    /**
     * @see net.rim.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        return true; // Prevent the save dialog from appearing
    }
}
