/*
 * XMLDemo.java
 * 
 * A sample application demonstrating how to parse an XML file.
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

package com.rim.samples.device.xmldemo;

import net.rim.device.api.ui.UiApplication;

/**
 * The main class for the application.
 */
public final class XMLDemo extends UiApplication {
    /**
     * This constructor simply pushes the main screen onto the display stack.
     */
    public XMLDemo() {
        pushScreen(new XMLDemoScreen());
    }

    /**
     * Entry point for the application.
     * 
     * @param args
     *            Command-line arguments (not used).
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new XMLDemo().enterEventDispatcher();
    }
}
