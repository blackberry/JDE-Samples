/*
 * UnifiedSearchDemo.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.unifiedsearchdemo;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application demonstrating how to publish and search for searchable
 * content using the Unified Search Framework. This application implements
 * EntityBasedSearchable in the PointOfInterestSearchable class which allows the
 * application to publish content to the Unified Search Framework using
 * AppContentManager.
 * 
 * The main UI screen for this application provides controls which add
 * PointOfInterest objects to the application and PointOfInterestEntity objects
 * encapsulating the POIs to the Unified Search Framework. An additional screen
 * allows a user to type keywords into an input field while search results from
 * the Unified Search Framework are displayed.
 */
public class UnifiedSearchDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final UnifiedSearchDemo app = new UnifiedSearchDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new UnifiedSearchDemo object
     */
    public UnifiedSearchDemo() {
        try {
            final PointOfInterestPublisher poiPublisher =
                    new PointOfInterestPublisher();

            // Push a UI screen onto the display stack
            pushScreen(new UnifiedSearchDemoPublisherScreen(poiPublisher));
        } catch (final PublisherException pe) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert(pe.toString());
                    System.exit(0);
                }
            });
        }
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
