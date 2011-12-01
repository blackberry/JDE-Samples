/*
 * InvertedScrollingScreen.java
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

package com.rim.samples.device.ui.wireframelayoutdemo;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * A screen containing a fixed field at the bottom, and a scrollable manager
 * containg a number of fields at the top.
 */
public class InvertedScrollingScreen extends MainScreen {
    private static final int[] colors = new int[] { Color.LIGHTBLUE,
            Color.LIGHTSKYBLUE };

    /**
     * Creates a new InvertedScrollingScreen object
     */
    public InvertedScrollingScreen() {
        super(NO_VERTICAL_SCROLL);

        setTitle("Inverted Scrolling Screen");

        // Create a scrollable VerticalFieldManager
        final VerticalFieldManager topScrollingManager =
                new VerticalFieldManager(VERTICAL_SCROLL | VERTICAL_SCROLLBAR);

        // Add fields to the scrollable region, alternating background color
        for (int i = 0; i < 15; ++i) {
            final int color = colors[i % 2];
            topScrollingManager.add(new CustomField(color, i + 1
                    + " - This field is in the scrolling region"));
        }

        // Create edit field for input
        final TextField bottomInputField =
                new BasicEditField("Enter text: ", "");

        // Add the VerticalFieldManager and input field to a
        // JustifiedVerticalFieldManager
        final JustifiedVerticalFieldManager jvfm =
                new JustifiedVerticalFieldManager(topScrollingManager,
                        bottomInputField, false);

        // Add the JustifiedVerticalFieldManager to the screen
        add(jvfm);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }
}
