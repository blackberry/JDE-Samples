/**
 * GPSDemoAdvanced.java
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

package com.rim.samples.device.gpsdemoadvanced;

import net.rim.device.api.system.EventLogger;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This application acts as a diagnostic tool for the GPS functionality as
 * provided by both the core and extended GPS APIs. When the application
 * launches, the user will be able to select either the core or extended API
 * diagnostic test.
 */
public class GPSDemoAdvanced extends UiApplication implements
        FieldChangeListener {
    private final ButtonField _coreTestButton;
    private final ButtonField _extendedTestButton;

    /**
     * Create a new GPSDemoAdvanced object
     */
    public GPSDemoAdvanced() {
        final MainScreen screen = new MainScreen();

        // Initialize UI components
        screen.setTitle("GPS Advanced Demo");
        final LabelField apiChoiceMessage =
                new LabelField("Please select an API to test:",
                        Field.USE_ALL_WIDTH | Field.FIELD_HCENTER);
        _coreTestButton =
                new ButtonField("Core GPS API Test", ButtonField.NEVER_DIRTY
                        | ButtonField.CONSUME_CLICK);
        _extendedTestButton =
                new ButtonField("Extended GPS API Test",
                        ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK);
        _coreTestButton.setChangeListener(this);
        _extendedTestButton.setChangeListener(this);

        screen.add(apiChoiceMessage);
        screen.add(_coreTestButton);
        screen.add(_extendedTestButton);
        pushScreen(screen);
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        // Push a screen depending on which button was invoked
        if (field == _coreTestButton) {
            pushScreen(new CoreGPSDiagnosticScreen());
        } else if (field == _extendedTestButton) {
            pushScreen(new ExtendedGPSDiagnosticScreen());
        }
    }

    /**
     * Entry point for the application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        EventLogger.register(0x9876543212345L, "GPSAdvancedDemo",
                EventLogger.VIEWER_STRING);
        final GPSDemoAdvanced app = new GPSDemoAdvanced();
        app.enterEventDispatcher();
    }
}
