/*
 * LocationPickerDemo.java
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

package com.rim.samples.device.ui.locationpickerdemo;

import java.util.Enumeration;

import javax.microedition.location.Landmark;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.gps.GPSInfo;
import net.rim.device.api.lbs.picker.ContactsLocationPicker;
import net.rim.device.api.lbs.picker.EnterLocationPicker;
import net.rim.device.api.lbs.picker.GPSLocationPicker;
import net.rim.device.api.lbs.picker.GeotaggedPhotoPicker;
import net.rim.device.api.lbs.picker.LocationPicker;
import net.rim.device.api.lbs.picker.MapsLocationPicker;
import net.rim.device.api.lbs.picker.RecentLocationPicker;
import net.rim.device.api.lbs.picker.SuggestedLocationPicker;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A sample application to demonstrate the Location Picker API
 */
public class LocationPickerDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final LocationPickerDemo app = new LocationPickerDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new LocationPickerDemo object
     */
    public LocationPickerDemo() {
        pushScreen(new LocationPickerDemoScreen());
    }

    /**
     * MainScreen class for the LocationPickerDemo application
     */
    static class LocationPickerDemoScreen extends MainScreen implements
            LocationPicker.Listener, FieldChangeListener {
        private final LocationPicker _locationPicker;
        private final ButtonField _buttonField;
        private final LabelField _nameLabel;
        private final LabelField _descLabel;
        private final LabelField _coordLabel;
        private boolean _mapsPresent = false;

        /**
         * Creates a new LocationPickerDemoScreen object
         */
        LocationPickerDemoScreen() {
            // Initialize screen
            setTitle("Location Picker Demo");
            _buttonField =
                    new ButtonField("Choose location", ButtonField.NEVER_DIRTY
                            | ButtonField.CONSUME_CLICK);
            _buttonField.setChangeListener(this);
            add(_buttonField);
            _nameLabel = new LabelField();
            _descLabel = new LabelField();
            _coordLabel = new LabelField();
            add(_nameLabel);
            add(_descLabel);
            add(_coordLabel);

            // Define suggested locations
            final Landmark[] landmarks =
                    new Landmark[] {
                            new Landmark("New York", "Times Square",
                                    new QualifiedCoordinates(40.757682,
                                            -73.98571, Float.NaN, Float.NaN,
                                            Float.NaN), null),
                            new Landmark("New York", "Central Park",
                                    new QualifiedCoordinates(40.783333,
                                            -73.966667, Float.NaN, Float.NaN,
                                            Float.NaN), null) };

            int arraySize = 7; // We will create an array of pickers with a max
                               // size of seven

            // Check for BlackBerry Maps support
            LocationPicker.Picker mapsLocationPicker = null;
            try {
                mapsLocationPicker = MapsLocationPicker.getInstance();
                _mapsPresent = true;
            } catch (final IllegalStateException ise) {
                arraySize--; // No maps, reduce array size
            }

            // Check for GPS support
            final boolean gpsSupported = GPSInfo.getGPSDataSource() != null;
            if (!gpsSupported) {
                arraySize--; // No GPS, reduce array size
            }

            // Define an array containing individual picker types.
            final LocationPicker.Picker[] locationPickersArray =
                    new LocationPicker.Picker[arraySize];
            locationPickersArray[--arraySize] =
                    EnterLocationPicker.getInstance(false);
            locationPickersArray[--arraySize] =
                    SuggestedLocationPicker.getInstance("App specific...",
                            landmarks);
            locationPickersArray[--arraySize] =
                    RecentLocationPicker.getInstance();
            locationPickersArray[--arraySize] =
                    ContactsLocationPicker.getInstance(false);
            locationPickersArray[--arraySize] =
                    GeotaggedPhotoPicker.getInstance();

            if (_mapsPresent) {
                // Blackberry Maps is present on the device, add a
                // MapsLocationPicker
                locationPickersArray[--arraySize] = mapsLocationPicker;
            }

            if (gpsSupported) {
                // GPS is supported, add a GPSLocationPicker
                locationPickersArray[--arraySize] =
                        GPSLocationPicker.getInstance();
            }

            // Get a LocationPicker instance containing individual picker types
            // and make this class a location picker listener.
            _locationPicker = LocationPicker.getInstance(locationPickersArray);
            final Enumeration globalPickers =
                    _locationPicker.getGlobalLocationPickers();

            while (globalPickers.hasMoreElements()) {
                _locationPicker
                        .addLocationPicker((LocationPicker.Picker) globalPickers
                                .nextElement());
            }

            _locationPicker.setListener(this);
        }

        /**
         * @see LocationPicker.Listener#locationPicked(LocationPicker.Picker,
         *      Landmark)
         */
        public void locationPicked(final LocationPicker.Picker picker,
                final Landmark location) {
            if (picker instanceof LocationPicker) {
                final LocationPicker locationPicker = (LocationPicker) picker;
                locationPicker.close();
            }
            if (location != null) {
                _nameLabel.setText("Location name: " + location.getName());
                _descLabel.setText("Description: " + location.getDescription());

                final QualifiedCoordinates coordinates =
                        location.getQualifiedCoordinates();
                if (coordinates != null) {
                    final StringBuffer buff = new StringBuffer("Coordinates: ");
                    final double latitude = coordinates.getLatitude();
                    final double longitude = coordinates.getLongitude();
                    buff.append("Latitude:");
                    buff.append(latitude);
                    buff.append(", Longitude: ");
                    buff.append(longitude);
                    _coordLabel.setText(buff.toString());
                }

                // Invoke the BlackBerry Maps application with the
                // chosen location.
                if (_mapsPresent) {
                    final Landmark[] landmark = { location };
                    final MapsArguments mapsArgs = new MapsArguments(landmark);
                    Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, mapsArgs);
                }
            }
        }

        /**
         * @see FieldChangeListener#fieldChanged(Field, int)
         */
        public void fieldChanged(final Field field, final int context) {
            if (field == _buttonField) {
                // Display the location picker
                _locationPicker.show();
            }
        }
    }
}
