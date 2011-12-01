/*
 * UpdatableMappable.java
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

import net.rim.device.api.lbs.maps.model.DynamicMappable;
import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.model.MappableChangeEvent;
import net.rim.device.api.lbs.maps.model.MappableEventManager;

/**
 * A Mappable that is also a Dynamic Mappable which can be auto updated as
 * needed
 */
public class UpdatableMappable extends MapLocation implements DynamicMappable {
    private final MappableEventManager _eventManager;
    private final MapPoint _mapPointOldState;

    /**
     * Creates a new UpdatableMappable object
     * 
     * @param lat
     *            Initial latitude for the UpdatableMappable
     * @param lon
     *            Initial longtitude for the UpdatableMappable
     * @param name
     *            Name for the UpdatableMappable
     * @param description
     *            Description for the UpdatableMappable
     */
    public UpdatableMappable(final double lat, final double lon,
            final String name, final String description) {
        super(lat, lon, name, description);

        _eventManager = new MappableEventManager();

        _mapPointOldState = new MapPoint(lat, lon);
    }

    /**
     * @see net.rim.device.api.lbs.maps.model.MapPoint#setLon(double)
     */
    public void setLon(final double lon) {
        // Record the old value before it's changed. This way we can
        // notify the MapField of both the old and new values in the
        // update() method.
        _mapPointOldState.setLon(getLon());

        super.setLon(lon);
    }

    /**
     * @see net.rim.device.api.lbs.maps.model.MapPoint#setLat(double)
     */
    public void setLat(final double lat) {
        // Record the old value before it's changed. This way we can
        // notify the MapField of both the old and new values in the
        // update() method.
        _mapPointOldState.setLat(getLat());

        super.setLat(lat);
    }

    /**
     * Forces the mappable to be updated
     */
    public void update() {
        final MappableChangeEvent event = new MappableChangeEvent();

        // The old value only needs to be a simple MapPoint to record the
        // difference in latitude and longitude.
        event.setOldState(_mapPointOldState);

        // By sending the current object as the new state we ensure any changes
        // in the position are respected, we also keep object creation down.
        // However, if it is important that the information in the new state
        // is synchronous it may be better to send a copy of the data so it
        // isn't overwritten while being read by the MapField.
        event.setNewState(this);

        _eventManager.triggerEvent(event);
    }

    /**
     * @see net.rim.device.api.lbs.maps.model.DynamicMappable#getEventManager()
     */
    public MappableEventManager getEventManager() {
        return _eventManager;
    }
}
