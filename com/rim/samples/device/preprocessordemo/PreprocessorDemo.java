/*
 * PreprocessorDemo.java
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

package com.rim.samples.device.preprocessordemo;

import net.rim.device.api.ui.UiApplication;

/**
 * 
 * This sample demonstrates how to use the RAPC preprocessor. The
 * PreprocessorDemoScreen contains a text field to allow a user to enter text.
 * The "Clear Field" menu item is used to clear the text field and is
 * automatically added to the PreprocessorDemoScreen when the text field is
 * added to the screen. If the device supports touch gestures, a swipe gesture
 * will also cause the text field to be cleared. To ensure that this application
 * is compatible with devices which do not include touch screen specific APIs,
 * the preprocessor will only compile the sections of code pertinent to the
 * touch screen API if the TOUCH_API tag is defined. Otherwise only the
 * "Clear Field" MenuItem will allow a user to clear the text field.
 * 
 * See the "readme.txt" file for details on how to use the preprocessor.
 * 
 * Note: The "//#preprocess" directive must be the first line (with no preceding
 * characters or whitespace) in all files requiring preprocessing.
 * 
 * Note: At least one tag must be defined for the preprocessor to engage. In
 * this demo the "PREPROCESSOR" tag will serve this purpose and should not be
 * removed.
 * 
 * To test the demo in JDE 4.7 or later: 1. Ensure the TOUCH_API tag is defined
 * as outlined in the "readme.txt" file. 2. Build the project and run the demo.
 * Note that both the swipe gesture and the "Clear Field" MenuItem may be used
 * to clear the text field. 3. Remove the TOUCH_API tag as outlined in the
 * "readme.txt" file. 4. Build the project and run the demo. Note that only the
 * "Clear Field" MenuItem is used to clear the text field.
 * 
 * To test the demo in JDE 4.6.x or earlier: 1. Ensure the TOUCH_API tag is
 * defined as outlined in the "readme.txt" file. 2. Build the project. Note that
 * the project does not build successfully beacause the TouchEvent and
 * TouchGesture classes are not supported in JDE 4.6.x and earlier. 3. Remove
 * the TOUCH_API tag as outlined in the "readme.txt" file. 4. Build the project.
 * Note that the demo builds successfully.
 * 
 */
public final class PreprocessorDemo extends UiApplication {
    /**
     * Creates a new PreprocessorDemo object
     */
    public PreprocessorDemo() {
        final PreprocessorDemoScreen screen = new PreprocessorDemoScreen();
        pushScreen(screen);
    }

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new PreprocessorDemo().enterEventDispatcher();
    }
}
