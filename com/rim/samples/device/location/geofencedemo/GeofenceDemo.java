/*
 * GeofenceDemo.java
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

package com.rim.samples.device.geofencedemo;

import javax.microedition.location.Coordinates;

import net.rim.device.api.gps.BlackBerryLocation;
import net.rim.device.api.lbs.maps.MapConstants;
import net.rim.device.api.lbs.maps.MapDimensions;
import net.rim.device.api.lbs.maps.MapFactory;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.ui.MapAction;
import net.rim.device.api.lbs.maps.ui.MapField;
import net.rim.device.api.lbs.maps.ui.RichMapField;
import net.rim.device.api.location.Geofence;
import net.rim.device.api.location.GeofenceListener;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A sample application to demonstrate the Geofencing API. The app displays a
 * map centered on a pre-defined origin which is also used as the center point
 * for a specified region enclosed by a 'geofence'.
 */
public final class GeofenceDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final GeofenceDemo app = new GeofenceDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new GeofenceDemo object
     */
    public GeofenceDemo() {
        pushScreen(new GeofenceDemoScreen());
    }

    /**
     * MainScreen class for the GeofenceDemo application
     */
    static final class GeofenceDemoScreen extends MainScreen implements
            GeofenceListener {
        private static final int ZOOM_LEVEL = 4;
        private static final int MAP_HEIGHT = Display.getHeight() - 100;
        private static final int GEOFENCE_RADIUS = 500;
        private static final double ORIGIN_LATITUDE = 43.47583;
        private static final double ORIGIN_LONGITUDE = -80.54019;

        private final RichMapField _richMapField;
        private final LabelField _geofenceInfoField;

        /**
         * Creates a new GeofenceDemoScreen object
         */
        GeofenceDemoScreen() {
            setTitle("Geofence Demo");

            _richMapField = MapFactory.getInstance().generateRichMapField();

            // Allow map to get focus but not be interactive unless clicked
            final MapAction mapAction = _richMapField.getAction();
            mapAction.enableOperationMode(MapConstants.MODE_SHARED_FOCUS);

            // Set the size of the map field
            final MapField mapField = _richMapField.getMapField();
            mapField.setDimensions(new MapDimensions(Display.getWidth(),
                    MAP_HEIGHT));

            add(_richMapField);

            // Establish a Geofence around a circular region defined by a
            // defined origin and radius. A Geofence can also be defined
            // as a polygon. The specified GeofenceListener will be notified
            // when the BlackBerry device enters or exits the perimeter of the
            // geofenced region.
            final Geofence geofence = new Geofence();
            final Coordinates coordinates =
                    new Coordinates(ORIGIN_LATITUDE, ORIGIN_LONGITUDE,
                            Float.NaN);
            geofence.monitorPerimeter(this, "RIM Campus", coordinates,
                    GEOFENCE_RADIUS, 0, -1);

            // Add a field to display status
            final StringBuffer strBuffer =
                    new StringBuffer("Geofence enabled for latitude ");
            strBuffer.append(coordinates.getLatitude());
            strBuffer.append(" and longitude ");
            strBuffer.append(coordinates.getLongitude());
            strBuffer.append(" with ");
            strBuffer.append(GEOFENCE_RADIUS);
            strBuffer.append(" meter radius");
            _geofenceInfoField = new LabelField(strBuffer.toString());
            add(_geofenceInfoField);
        }

        /**
         * @see net.rim.device.api.ui.Screen#onUiEngineAttached(boolean)
         */
        protected void onUiEngineAttached(final boolean attached) {
            super.onUiEngineAttached(attached);

            if (attached) {
                final MapAction mapAction =
                        _richMapField.getMapField().getAction();

                // Set center of map near to desired origin and zoom to desired
                // level
                mapAction.setCenterAndZoom(new MapPoint(ORIGIN_LATITUDE,
                        ORIGIN_LONGITUDE), ZOOM_LEVEL);

                _richMapField.setFocus();
            }
        }

        /**
         * @see net.rim.device.api.location.GeofenceListener#perimeterEntered(String,
         *      BlackBerryLocation)
         */
        public void perimeterEntered(final String tag,
                final BlackBerryLocation location) {
            final Coordinates coordinates = location.getQualifiedCoordinates();

            // Move the map to the current location
            final MapAction mapAction = _richMapField.getMapField().getAction();
            mapAction.setCenter(new MapPoint(coordinates));

            final double lat = 100000.0 * coordinates.getLatitude();
            final double lng = 100000.0 * coordinates.getLongitude();

            // Update the status field
            final StringBuffer strBuffer = new StringBuffer("Welcome to ");
            strBuffer.append(tag);
            strBuffer.append(" Lat: ");
            strBuffer.append(lat);
            strBuffer.append(" Long: ");
            strBuffer.append(lng);

            synchronized (Application.getEventLock()) {
                _geofenceInfoField.setText(strBuffer.toString());
            }
        }

        /**
         * @see net.rim.device.api.location.GeofenceListener#perimeterExited(String,
         *      BlackBerryLocation)
         */
        public void perimeterExited(final String tag,
                final BlackBerryLocation location) {
            final int lat =
                    (int) (100000.0 * location.getQualifiedCoordinates()
                            .getLatitude());
            final int lng =
                    (int) (100000.0 * location.getQualifiedCoordinates()
                            .getLongitude());

            // Move the map to the current location
            final MapAction mapAction = _richMapField.getMapField().getAction();
            mapAction.setCenter(new MapPoint(lat, lng));

            // Update the status field
            final StringBuffer strBuffer = new StringBuffer(tag);
            strBuffer.append(": Good bye, come again!");
            strBuffer.append(" Lat: ");
            strBuffer.append(lat);
            strBuffer.append(" Long: ");
            strBuffer.append(lng);

            synchronized (Application.getEventLock()) {
                _geofenceInfoField.setText(strBuffer.toString());
            }
        }

        /**
         * @see net.rim.device.api.location.GeofenceListener#errorOccurred(int)
         */
        public void errorOccurred(final int errorCode) {
            String errorString = "";

            switch (errorCode) {
            case Geofence.LOCATION_DISALLOWED_BY_IT_POLICY:
                errorString = " - Location disallowed by IT policy";
                break;
            case Geofence.LOCATION_DISALLOWED_BY_USER:
                errorString = " - Location disallowed by user";
                break;
            case Geofence.TEMPORARILY_UNAVAILABLE_AUTO_RESTART:
                errorString = " - Temporarily unavailable, auto restart";
                break;
            case Geofence.OUT_OF_SERVICE:
                errorString = " - Out of service";
                break;
            case Geofence.LOW_BATTERY:
                errorString = " - Low battery";
                break;
            default:
                errorString = " - Unknown error";
            }

            synchronized (Application.getEventLock()) {
                _geofenceInfoField.setText("Error: " + errorCode + errorString);
            }
        }
    }
}
