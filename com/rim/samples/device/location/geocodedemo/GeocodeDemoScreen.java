/*
 * GeocodeDemoScreen.java
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

package com.rim.samples.device.geocodedemo;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A MainScreen class representing the entry screen for the Geocode Demo
 * application
 */
public final class GeocodeDemoScreen extends MainScreen {
    /**
     * Creates a new GeocodeDemoScreen object and initializes UI components
     */
    public GeocodeDemoScreen() {
        setTitle("Geocode Demo");

        final FullWidthButton geocodeButton =
                new FullWidthButton("Geocode Screen");
        geocodeButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field,
             *      int)
             */
            public void fieldChanged(final Field field, final int context) {
                UiApplication.getUiApplication()
                        .pushScreen(new GeocodeScreen());

            }
        });

        final FullWidthButton reverseGeocodeButton =
                new FullWidthButton("Reverse Geocode Screen");
        reverseGeocodeButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field,
             *      int)
             */
            public void fieldChanged(final Field field, final int context) {
                UiApplication.getUiApplication().pushScreen(
                        new ReverseGeocodeScreen());
            }
        });

        add(geocodeButton);
        add(reverseGeocodeButton);
    }

    /**
     * A button which uses the full width of the screen
     */
    private final class FullWidthButton extends ButtonField {
        /**
         * Creates a new FullWidthButton object with provided label text
         * 
         * @param label
         *            Label text to display on the button
         */
        public FullWidthButton(final String label) {
            this(label, ButtonField.CONSUME_CLICK | ButtonField.NEVER_DIRTY);
        }

        /**
         * Creates a new FullWidthButton object with provided label text and
         * style
         * 
         * @param label
         *            Label text to display on the button
         * @param style
         *            Style for the button
         */
        public FullWidthButton(final String label, final long style) {
            super(label, style);
        }

        /**
         * * @see ButtonField#getPreferredWidth()
         */
        public int getPreferredWidth() {
            return Display.getWidth();
        }
    }
}
