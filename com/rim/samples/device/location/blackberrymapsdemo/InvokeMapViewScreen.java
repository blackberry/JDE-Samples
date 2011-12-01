/*
 * InvokeMapViewScreen.java
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

package com.rim.samples.device.maps.blackberrymapsdemo;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.blackberry.api.maps.MapView;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * This example uses of a MapView object when invoking the the BlackBerry Maps
 * application.
 */
public final class InvokeMapViewScreen extends MainScreen {
    /**
     * Creates a new InvokeMapViewScreen object
     */
    InvokeMapViewScreen() {
        setTitle("Invoke Map View");

        final LabelField instructions =
                new LabelField(
                        "Select 'View Map' from the menu.  The MapView object is set to Zoom Level 3. Location is Ottawa, ON, Canada at Latitude 45.42349, Longitude -75.69792");
        add(instructions);

        // Invokes BlackBerry Maps application using a MapView object
        final MenuItem viewMapItem =
                new MenuItem(new StringProvider("View Map"), 0x230010, 0);
        viewMapItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final MapView mapview = new MapView();
                mapview.setLatitude(4542349);
                mapview.setLongitude(-7569792);
                mapview.setZoom(3);

                // Invoke maps application with specified MapView
                Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                        new MapsArguments(mapview));
            }
        }));

        addMenuItem(viewMapItem);
    }
}
