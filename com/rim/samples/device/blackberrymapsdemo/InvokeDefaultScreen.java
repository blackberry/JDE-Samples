/*
 * InvokeDefaultScreen.java
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

package com.rim.samples.device.blackberrymapsdemo;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This example invokes the BlackBerry Map application with a MapArgument object
 * constructed with no arguments. The resulting map view will be the last map
 * view displayed by BlackBerry Maps or the default map view if BlackBerry Maps
 * is being run for the first time.
 */

public final class InvokeDefaultScreen extends MainScreen {
    // Constructor
    public InvokeDefaultScreen() {
        setTitle("Invoke Default");

        final LabelField instructions =
                new LabelField(
                        "Select 'View Map' from the menu to see the default map view.");
        add(instructions);

        addMenuItem(viewMapItem);
    }

    /**
     * Displays the default map
     */
    private final MenuItem viewMapItem = new MenuItem("View Map", 1000, 10) {
        // Invoke maps application with default map
        public void run() {
            Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, new MapsArguments());
        }
    };
}
