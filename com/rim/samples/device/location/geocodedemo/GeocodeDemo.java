/*
 * GeocodeDemo.java
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

import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * The Geocode Demo application demonstrates the Geocoder and ReverseGeocoder
 * APIs. A UI screen for geocoding allows a user to enter structured or free
 * form search criteria and displays results if the location can be resolved to
 * a latitude/longitude. A UI screen for reverse geocoding allows a user to
 * specify a latitude/longitude and displays any resulting location information.
 */
public final class GeocodeDemo extends UiApplication {
    /**
     * Creates a new GeocodeDemo object
     */
    public GeocodeDemo() {
        pushScreen(new GeocodeDemoScreen());
    }

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static final void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new GeocodeDemo().enterEventDispatcher();
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }

    /**
     * Converts a location into a String for presentation. All fields in the
     * location are returned in the String representation.
     * 
     * @param location
     *            MapLocation to convert
     * @return The location as a String
     */
    public static String composeLocation(final MapLocation location) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Name: ").append(location.getName()).append('\n');
        buffer.append("Desc: ").append(location.getDescription()).append('\n');
        buffer.append("Lat/Lon: ").append(location.getLat()).append(',')
                .append(location.getLon()).append('\n');
        buffer.append("Addr: ").append(
                location.getData(MapLocation.LBS_LOCATION_STREET_ADDRESS_KEY))
                .append('\n');
        buffer.append("City: ").append(
                location.getData(MapLocation.LBS_LOCATION_CITY_KEY)).append(
                '\n');
        buffer.append("Region: ").append(
                location.getData(MapLocation.LBS_LOCATION_REGION_KEY)).append(
                '\n');
        buffer.append("County: ").append(
                location.getData(MapLocation.LBS_LOCATION_COUNTY_KEY)).append(
                '\n');
        buffer.append("Dist: ").append(
                location.getData(MapLocation.LBS_LOCATION_DISTRICT_KEY))
                .append('\n');
        buffer.append("Country: ").append(
                location.getData(MapLocation.LBS_LOCATION_COUNTRY_KEY)).append(
                '\n');
        buffer.append("Zip: ").append(
                location.getData(MapLocation.LBS_LOCATION_POSTAL_CODE_KEY))
                .append('\n');
        buffer.append("Phone: ").append(
                location.getData(MapLocation.LBS_LOCATION_PHONE_KEY)).append(
                '\n');
        buffer.append("Fax: ").append(
                location.getData(MapLocation.LBS_LOCATION_FAX_KEY))
                .append('\n');
        buffer.append("URL: ").append(
                location.getData(MapLocation.LBS_LOCATION_URL_KEY))
                .append('\n');
        buffer.append("E-Mail: ").append(
                location.getData(MapLocation.LBS_LOCATION_EMAIL_KEY)).append(
                '\n');
        buffer.append("Merit: ").append(
                location.getData(MapLocation.LBS_POI_LOCATION_MERIT_KEY))
                .append('\n');
        buffer.append("Zoom hint: ").append(
                location.getData(MapLocation.LBS_ZOOM_HINT));

        return buffer.toString();
    }
}
