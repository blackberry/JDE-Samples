/*
 * PhoneMultiLineDemo.java
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

package com.rim.samples.device.phonemultilinedemo;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/*
 * A phone application that demonstrates the use of the Phone Multiline APIs. 
 * This application allows you to switch between multiple lines on your phone, 
 * and make calls with all the available lines. It also determines the type 
 * of phone lines available, namely Mobile and PBX. 
 *
 * To run the demo using a simulator configured with multiple phone lines, you
 * will need to edit the simulator profile.
 * Step 1: Select Edit -> Preferences -> Simulator -> Network. 
 * Step 2: Click the 'Add' button located on the right side of the Phone Number text area.
 * Step 3: Enter a phone number and click OK.
 * Step 4: Repeat step 2 and 3. 
 */
public final class PhoneMultiLineDemo extends UiApplication {
    // Constructor
    public PhoneMultiLineDemo() {
        pushScreen(new PhoneMultiLineScreen(this));
    }

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line args (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final PhoneMultiLineDemo app = new PhoneMultiLineDemo();
        app.enterEventDispatcher();
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void messageDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }
}
