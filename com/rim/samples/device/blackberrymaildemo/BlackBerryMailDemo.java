/*
 * BlackBerryMailDemo.java
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

package com.rim.samples.device.blackberrymaildemo;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/*
 * This sample demonstrates how to access email on a BlackBerry.  It shows the following:
 * 
 * - Accessing all email service records (email accounts) available on a BlackBerry.
 * - Accessing all email folders and messages for an email account.
 * - Displaying the content of an email message.  Both plain text and HTML content 
 *   is displayed (if available within the message).
 * - Saving and sending email messages.
 *     
 * The sample can be tested using the BlackBerry email server simulator or 
 * synching the BlackBerry Desktop Manager to the BlackBerry Smartphone simulator
 * device. See the "BlackBerry Java Development Environment Development Guide"
 * for more information on the BlackBerry email server simulator.
 */
public final class BlackBerryMailDemo extends UiApplication {
    /**
     * Entry point for this application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new BlackBerryMailDemo().enterEventDispatcher();
    }

    /**
     * Creates a new BlackBerryMailDemo object
     */
    public BlackBerryMailDemo() {
        pushScreen(new BlackBerryMailDemoScreen("BlackBerry Mail Demo"));
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }
}
