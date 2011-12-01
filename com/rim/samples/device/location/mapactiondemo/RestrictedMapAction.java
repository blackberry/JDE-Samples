/**
 * RestrictedMapAction.java
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

package com.rim.samples.device.mapactiondemo;

import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.ui.MapAction;
import net.rim.device.api.util.MathUtilities;

/**
 * This class demonstrates how to restrict panning/zooming by implementing a
 * custom MapAction class. Panning is either disabled or enabled with a move
 * distance restriction. Zooming is either disabled or enabled with a fixed
 * allowable zoom level range.
 */
public class RestrictedMapAction extends MapAction {
    // Flag to determine if zooming is allowed
    private boolean _allowZoom = true;

    // Flag to determine if panning is allowed
    private boolean _allowPanning = true;

    // This center is the reference point for the panning restriction
    private final MapPoint _allowedCenter = new MapPoint(45.0, -75.0);

    private static final double MAX_MOVE = 0.25;

    /**
     * Toggle the state of the zoom flag
     */
    public void toggleZooming() {
        _allowZoom = !_allowZoom;
    }

    /**
     * Returns zoom allowed status
     * 
     * @return Retuns true if zooming enabled
     */
    public boolean isZoomingAllowed() {
        return _allowZoom;
    }

    /**
     * Toggle the state of the panning flag
     */
    public void togglePanning() {
        _allowPanning = !_allowPanning;
    }

    /**
     * Returns panning allowed status
     * 
     * @return Returns true if panning allowed
     */
    public boolean isPanningAllowed() {
        return _allowPanning;
    }

    /**
     * @see net.rim.device.api.lbs.maps.ui.MapAction#performSetZoom(int)
     */
    protected boolean performSetZoom(final int zoom) {
        // Restrict the zoom level to between 1 and 4 inclusive
        return super.performSetZoom(MathUtilities.clamp(1, zoom, 4));
    }

    /**
     * @see net.rim.device.api.lbs.maps.ui.MapAction#allowSetZoom(int)
     */
    protected boolean allowSetZoom(final int zoom) {
        // This will allow the application to control the lock on zooming
        return _allowZoom;
    }

    /**
     * @see net.rim.device.api.lbs.maps.ui.MapAction#performSetCenter(MapPoint)
     */
    protected boolean performSetCenter(final MapPoint newCenter) {
        // The center in this example is only allowed to move by MAX_MOVE
        // in any direction from the defined allowed center.
        final double newLat = newCenter.getLat();
        final double newLon = newCenter.getLon();
        final double allowedLat = _allowedCenter.getLat();
        final double allowedLon = _allowedCenter.getLon();

        // Clamp the new center to the imposed limits
        newCenter.setLat(MathUtilities.clamp(allowedLat - MAX_MOVE, newLat,
                allowedLat + MAX_MOVE));
        newCenter.setLon(MathUtilities.clamp(allowedLon - MAX_MOVE, newLon,
                allowedLon + MAX_MOVE));

        return super.performSetCenter(newCenter);
    }

    /**
     * @see net.rim.device.api.lbs.maps.ui.MapAction#allowSetCenter(MapPoint)
     */
    protected boolean allowSetCenter(final MapPoint newCenter) {
        return _allowPanning;
    }
}
