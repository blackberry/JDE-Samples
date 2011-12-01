/*
 * BasicScrollingScreen.java
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
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * A screen containing a fixed field at the top, and a scrollable manager
 * containg a number of fields below.
 */
public class BasicScrollingScreen extends MainScreen {
    private static final int[] colors = new int[] { Color.LIGHTBLUE,
            Color.LIGHTSKYBLUE };

    /**
     * Creates a new BasicScrollingScreen object
     */
    public BasicScrollingScreen() {
        super(NO_VERTICAL_SCROLL | USE_ALL_WIDTH);

        setTitle("Basic Scrolling Screen");

        // Add a field which will not scroll
        add(new CustomField(Color.WHITE, "This field does not scroll"));

        // Create a scrollable VerticalFieldManager
        final VerticalFieldManager scrollingRegion =
                new VerticalFieldManager(USE_ALL_HEIGHT | VERTICAL_SCROLL
                        | VERTICAL_SCROLLBAR);

        // Add fields to the scrollable region, alternating background color
        for (int i = 0; i < 15; ++i) {
            final int color = colors[i % 2];
            scrollingRegion.add(new CustomField(color, i + 1
                    + " - This field is in the scrolling region"));
        }

        // Add the VerticalfieldManager to the screen
        add(scrollingRegion);
    }
}
