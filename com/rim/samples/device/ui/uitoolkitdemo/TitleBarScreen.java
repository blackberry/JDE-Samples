/*
 * TitleBarScreen.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
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
