/*
 * SearchScreen.java
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
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * A Screen class using a JustifiedHorizontalFieldManager to position an edit
 * field and a choice field at the top of the screen, and displaying a
 * scrollable VerticalFieldManager at the bottom
 */
public class SearchScreen extends MainScreen {
    private static final int[] colors = new int[] { Color.LIGHTBLUE,
            Color.LIGHTSKYBLUE };

    /**
     * Creates a new SearchScreen object
     */
    public SearchScreen() {
        super(NO_VERTICAL_SCROLL);

        setTitle("Search Screen");

        // Create input field and choice field and add to a
        // JustifiedHorizontalFieldManager
        final BasicEditField searchInput = new BasicEditField("Search: ", "");
        final String[] choices = new String[] { "Value 1", "Value 2" };
        final ObjectChoiceField searchProvider =
                new ObjectChoiceField("", choices, 0, Field.FIELD_RIGHT);
        final JustifiedHorizontalFieldManager jhfm =
                new JustifiedHorizontalFieldManager(searchInput,
                        searchProvider, false);

        // Add the JustifiedHorizontalFieldManager to the screen
        add(jhfm);

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

        // Add the VerticalFieldManager to the screen
        add(scrollingRegion);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }
}
