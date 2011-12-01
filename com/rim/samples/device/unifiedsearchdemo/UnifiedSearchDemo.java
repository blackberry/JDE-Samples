/*
 * UnifiedSearchDemo.java
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

package com.rim.samples.device.unifiedsearchdemo;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * A sample application demonstrating how to publish and search for searchable
 * content using the Unified Search Framework. This application extends
 * EntityBasedSearchableProvider in the UnifiedSearchDemoSearchable class which
 * allows the application to publish content to the Unified Search Framework
 * using AppContentManager.
 * 
 * The main UI screen for this application provides controls which add
 * UnifiedSearchDemoDataObjects objects to the application and
 * UnifiedSearchDemoEntity objects encapsulating the data to the Unified Search
 * Framework. An additional screen allows a user to type keywords into an input
 * field while search results from the Unified Search Framework are displayed.
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
            final UnifiedSearchDemoPublisher publisher =
                    new UnifiedSearchDemoPublisher();

            // Push a UI screen onto the display stack
            pushScreen(new UnifiedSearchDemoPublisherScreen(publisher));
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
