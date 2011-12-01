/*
 * TrackpadGesturesScreen.java
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

package com.rim.samples.device.ui.uitoolkitdemo;

import net.rim.device.api.system.Application;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.input.InputSettings;
import net.rim.device.api.ui.input.NavigationDeviceSettings;

/**
 * A class demonstrating the use of the Touch APIs to recognize trackpad
 * gestures
 */
class TrackpadGesturesScreen extends MainScreen {
    LabelField _log;

    /**
     * Creates a new TrackPadGesturesScreen object
     */
    public TrackpadGesturesScreen() {
        // Tell the screen to handle trackpad swipes
        final InputSettings settings =
                NavigationDeviceSettings.createEmptySet();
        settings.set(NavigationDeviceSettings.DETECT_SWIPE, 1);
        addInputSettings(settings);

        // Create label fields for directions/display of gestures
        final LabelField directions =
                new LabelField(
                        "Swipe the trackpad. Swipe direction will be displayed on screen.");
        _log = new LabelField("");

        // Add fields to screen
        add(directions);
        add(_log);
    }

    /**
     * @see net.rim.device.api.ui.Field#touchEvent(TouchEvent)
     */
    protected boolean touchEvent(final TouchEvent event) {
        if (event.getEvent() == TouchEvent.GESTURE) {
            final TouchGesture gesture = event.getGesture();

            // Handle only trackpad swipe gestures
            if (gesture.getEvent() == TouchGesture.NAVIGATION_SWIPE) {
                final int direction = gesture.getSwipeDirection();

                Application.getApplication().invokeLater(new Runnable() {
                    /**
                     * @see Runnable#run()
                     */
                    public void run() {
                        // Determine the cardinal direction of the swipe
                        String cardinalDirection = "";
                        if (direction == TouchGesture.SWIPE_NORTH) {
                            cardinalDirection = "North";
                        } else if (direction == (TouchGesture.SWIPE_NORTH | TouchGesture.SWIPE_EAST)) {
                            cardinalDirection = "North-East";
                        } else if (direction == TouchGesture.SWIPE_EAST) {
                            cardinalDirection = "East";
                        } else if (direction == (TouchGesture.SWIPE_EAST | TouchGesture.SWIPE_SOUTH)) {
                            cardinalDirection = "South-East";
                        } else if (direction == TouchGesture.SWIPE_SOUTH) {
                            cardinalDirection = "South";
                        } else if (direction == (TouchGesture.SWIPE_SOUTH | TouchGesture.SWIPE_WEST)) {
                            cardinalDirection = "South-West";
                        } else if (direction == TouchGesture.SWIPE_WEST) {
                            cardinalDirection = "West";
                        } else if (direction == (TouchGesture.SWIPE_WEST | TouchGesture.SWIPE_NORTH)) {
                            cardinalDirection = "North-West";
                        }

                        // Output swipe direction to screen
                        _log.setText("Swipe direction: " + cardinalDirection);
                    }
                });

                return true;
            }
        }

        return false;
    }
}
