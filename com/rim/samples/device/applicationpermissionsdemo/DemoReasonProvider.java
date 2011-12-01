/*
 * DemoReasonProvider.java
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

package com.rim.samples.device.applicationpermissionsdemo;

import net.rim.device.api.applicationcontrol.ApplicationPermissions;
import net.rim.device.api.applicationcontrol.ReasonProvider;

/**
 * This class implements the ReasonProvider interface in order to provide
 * detailed permission request messages for the user.
 * 
 * To test ReasonProvider functionality, when prompted, set the demo's
 * application permissions to "prompt" and then run those tests. When the pop-up
 * asking for permission appears, click "Details from the vendor..." to view
 * your messages. The messages will only appear when access is being requested.
 * 
 */
class DemoReasonProvider implements ReasonProvider {

    /**
     * Gets the message of this listener according to the type of permission.
     * Must be implemented for this interface.
     * 
     * @param permissionID
     *            : the ID of the permission requested. Must be in the class
     *            net.rim.device.api.applicationcontrol.ApplicationPermissions
     * @return The string to be displayed.
     * @see net.rim.device.api.applicationcontrol.ReasonProvider#getMessage(int)
     */
    public String getMessage(final int permissionID) {

        // General message for other permissions
        String message =
                "ReasonProviderDemo recieved permissionID: " + permissionID;

        // Set specific messages for specific permission IDs
        switch (permissionID) {

        case ApplicationPermissions.PERMISSION_INPUT_SIMULATION:
            message = "Sample message for PERMISSION_INPUT_SIMULATION";
            break;

        case ApplicationPermissions.PERMISSION_PHONE:
            message = "Sample message for PERMISSION_PHONE";
            break;

        case ApplicationPermissions.PERMISSION_DEVICE_SETTINGS:
            message = "Sample message for PERMISSION_DEVICE_SETTINGS";
            break;

        case ApplicationPermissions.PERMISSION_EMAIL:
            message = "Sample message for PERMISSION_EMAIL";
            break;
        }

        return message;
    }

}
