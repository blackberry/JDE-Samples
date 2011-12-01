/**
 * PIMDemo.java
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

package com.rim.samples.device.blackberry.pim;

import net.rim.device.api.ui.UiApplication;

/**
 * Sample to demonstrate functionality of Personal Information Management (PIM)
 * API's. EventScreen class allows an event to be saved and alerts invitees via
 * email. ContactListScreen class displays a list of potential invitees.
 * ContactScreen screen allows additional contacts to be added to the Address
 * Book.
 */
public final class PIMDemo extends UiApplication {
    // Constants
    // ----------------------------------------------------------------
    // private static String ARG_STARTUP = "startup";

    // Members
    // ------------------------------------------------------------------
    private final EventScreen _eventScreen;

    // Entry point for application
    public static void main(final String[] args) {
        new PIMDemo().enterEventDispatcher();
    }

    // Constructor
    private PIMDemo() {
        // Create a new EventScreen and push screen onto stack.
        _eventScreen = new EventScreen();
        pushScreen(_eventScreen);
    }
}
