/*
 * SVGScriptingDemo.java
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

package com.rim.samples.device.svg.svgscriptingdemo;

import net.rim.device.api.ui.UiApplication;

/**
 * This sample application demonstrates the embedding of javascript into SVG
 * files to allow for dynamic and interactive content. The Java classes in this
 * sample are used for the set up and display of the SVG content, while the core
 * of the scripting logic is in the "sample.svg" file.
 */
public class SVGScriptingDemo extends UiApplication {
    /**
     * Creates a new SVGScriptingDemo object
     */
    public SVGScriptingDemo() {
        pushScreen(new SVGScriptingDemoScreen());
    }

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        new SVGScriptingDemo().enterEventDispatcher();
    }
}
