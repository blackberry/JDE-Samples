/**
 * MapLocation.java
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

package com.rim.samples.device.embeddedmapdemo;

import javax.microedition.location.Coordinates;

final class MapLocation extends Coordinates {
    private String _name;
    private boolean _isVisible; // Display marker at this location ?

    MapLocation(final double latitude, final double longitude, final String name) {
        super(latitude, longitude, 0);

        _name = name;

        // _isVisible = false; (set by default) // Do not display marker at
        // location until it has been selected by the user.
    }

    String getName() {
        return _name;
    }

    void setName(final String name) {
        _name = name;
    }

    boolean isVisible() {
        return _isVisible;
    }

    // Add a marker at location.
    void setVisible(final boolean isVisible) {
        _isVisible = isVisible;
    }

    public String toString() {
        return _name;
    }
}
