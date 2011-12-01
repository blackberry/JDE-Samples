/*
 * PhoneScreenDemo.java
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

package com.rim.samples.device.phonescreendemo;

import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.phonegui.PhoneScreen;
import net.rim.device.api.system.Application;

/**
 * This application demonstrates injecting custom data into both incoming and
 * active call screens. It runs concurrently with PhoneScreenDemo2 to send data
 * to the phone screens. Both applications are auto-run on system start-up.
 */
public final class PhoneScreenDemo extends Application {
    /**
     * Constructs a new PhoneScreenDemo object
     */
    public PhoneScreenDemo() {
        Phone.addPhoneListener(new PhoneScreenDataSender());
    }

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line args (not used)
     * @throws UnsupportedOperationException
     *             if PhoneScreen API not supported on current device
     */
    public static void main(final String[] args) {
        if (PhoneScreen.isSupported()) {
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            new PhoneScreenDemo().enterEventDispatcher();
        } else {
            throw new UnsupportedOperationException(
                    "Could not start Phone Screen Demo. Phone ScreenAPI not supported on this device");
        }
    }
}
