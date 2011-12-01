/*
 * AttachmentDemo.java
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

package com.rim.samples.device.attachmentdemo;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application to demonstrate how to programmatically download email
 * attachments and send email with attachments. A custom menu item is available
 * from the AttachmentDemoScreen to allow users to initiate file downloads. The
 * AttachmentAction class retrieves all email messages and extracts those that
 * contain attachments. If a user so chooses, only filesof type msword or png
 * will be downloaded.
 * 
 * To demonstrate the sample:
 * 
 * An email containing attachments should have been received before running the
 * application. Attachment resources are provided in the 'attachments' folder in
 * this project. The application can be tested using the BlackBerry Email Server
 * Simulator or by synchronizing the BlackBerry Desktop Manager to the
 * BlackBerry Smartphone simulator.
 * 
 * If you are using BlackBerry Desktop Manager to sync to the Blackberry
 * Smartphone simulator, follow these configuration steps: 1. Open the desktop
 * manager and select the desired profile under 'Email Settings -> Advanced ->
 * Profile Settings. 2. In the BlackBerry Java Development Environment select
 * 'Edit -> Preferences -> Simulator - > Memory'. Enable the checkbox titled
 * 'Use PC filesystem for SD Card files'. In the text field titled 'PC
 * filesystem path for SD Card files' type 'C:\SDCard'. 3. Activate and build
 * the Attachment Demo application and launch the BlackBerry Smartphone
 * simulator. Select 'USB cable attached' from the 'Simulate' menu. 4. Allow
 * Enterprise Activation to complete 5. Navigate to the 'Downloads' folder and
 * select 'Attachment Demo'.
 * 
 * To download attachments: 1. Open the menu and select 'Download Attachments'.
 * 2. A dialog appears prompting the downloading of all or only png and msword
 * type attachments. 3. Select 'No' to download all attachments, select 'Yes' to
 * download only files of type msword or png. 4. The screen displays the
 * downloading result(s).
 * 
 * Checking results: 1. Navigate to 'C:\SDCard' on your PC. 2. png files are
 * located in 'C:\SDCard\BlackBerry\pictures\'. 3. msword files are located in
 * 'C:\SDCard\BlackBerry\documents\'.
 * 
 * To send attachments: 1. Select 'Send Attachments' from the menu. 3. Navigate
 * to the file to be attached and select it. 4. A dialog appears prompting for
 * the recipient email address. 5. Enter the email address and then click OK.
 */

public final class AttachmentDemo extends UiApplication {
    /**
     * Creates a new AttachmentDemo object
     */
    public AttachmentDemo() {
        pushScreen(new AttachmentDemoScreen(this));
    }

    /**
     * Entry point for this application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final AttachmentDemo demo = new AttachmentDemo();
        demo.enterEventDispatcher();
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
