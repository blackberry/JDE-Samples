/*
 * UiToolkitDemo.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.ui.uitoolkitdemo;

import net.rim.device.api.ui.UiApplication;

/**
 * A sample application demonstrating various user interface API's
 */
public final class UiToolkitDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final UiToolkitDemo app = new UiToolkitDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new UiToolkitDemo object
     */
    public UiToolkitDemo() {
        pushScreen(new UiToolkitDemoScreen());
    }
}
