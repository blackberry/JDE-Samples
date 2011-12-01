/*
 * TitleBarScreen.java
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

package com.rim.samples.device.ui.uitoolkitdemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.component.StandardTitleBar;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A class to demonstrate the StandardTitleBar API
 */
public final class TitleBarScreen extends MainScreen {
    /**
     * Creates a new TitleBarScreen object
     */
    public TitleBarScreen() {
        // Initialize and configure title bar
        final StandardTitleBar titleBar = new StandardTitleBar();

        titleBar.addTitle("<title goes here>");

        final Bitmap bitmap = Bitmap.getBitmapResource("logo.jpg");
        if (bitmap != null) {
            titleBar.addIcon(bitmap);
        }

        titleBar.addClock();

        titleBar.addNotifications();

        titleBar.setPropertyValue(StandardTitleBar.PROPERTY_BATTERY_VISIBILITY,
                StandardTitleBar.BATTERY_VISIBLE_ALWAYS);
        titleBar.setPropertyValue(StandardTitleBar.PROPERTY_WIFI_VISIBILITY,
                StandardTitleBar.PROPERTY_VALUE_ON);
        titleBar.setPropertyValue(
                StandardTitleBar.PROPERTY_CELLULAR_VISIBILITY,
                StandardTitleBar.PROPERTY_VALUE_ON);

        // Set title bar for this screen
        setTitleBar(titleBar);
    }
}
