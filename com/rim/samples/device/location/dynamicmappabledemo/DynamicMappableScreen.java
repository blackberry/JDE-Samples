/*
 * DynamicMappableScreen.java
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

package com.rim.samples.device.dynamicmappabledemo;

import net.rim.device.api.lbs.maps.MapDimensions;
import net.rim.device.api.lbs.maps.MapFactory;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.ui.RichMapField;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.container.MainScreen;

/**
 * MainScreen class for the Dynamic Mappable Demo
 */
public class DynamicMappableScreen extends MainScreen {
    public static final double ORIGIN_LATITUDE = 45.0;
    public static final double ORIGIN_LONGITUDE = -75.0;

    private static final int ZOOM_LEVEL = 4;

    private final RichMapField _map;

    /**
     * Creates a new DynamicMappableScreen object
     */
    public DynamicMappableScreen() {
        _map = MapFactory.getInstance().generateRichMapField();

        // Set the size of the map field
        _map.getMapField().setDimensions(
                new MapDimensions(Display.getWidth(), Display.getHeight()));

        // Add any dynamic mappables to the model
        UpdateManager.getInstance().addMappablesToModel(_map.getModel());

        // Add the map to the screen
        add(_map);
    }

    /**
     * @see net.rim.device.api.ui.Screen#onUiEngineAttached(boolean)
     */
    protected void onUiEngineAttached(final boolean attached) {
        super.onUiEngineAttached(attached);
        if (attached) {
            // Set the location of the map to some random location
            _map.getMapField().getAction().setCenter(
                    new MapPoint(ORIGIN_LATITUDE, ORIGIN_LONGITUDE));

            // Set the zoom level of the map
            _map.getMapField().getAction().setZoom(ZOOM_LEVEL);

            // Now that we know the map is up and running, start the "service"
            UpdateManager.getInstance().startService();
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public boolean onClose() {
        try {
            // Clean up any resources used
            _map.close();

        } catch (final Exception e) {
        }

        return super.onClose();
    }
}
