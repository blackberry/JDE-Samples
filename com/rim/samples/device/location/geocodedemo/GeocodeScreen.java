/*
 * GeocodeScreen.java
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

import java.util.Vector;

import net.rim.device.api.lbs.maps.MapDimensions;
import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.server.Geocoder;
import net.rim.device.api.lbs.maps.server.ServerExchangeCallback;
import net.rim.device.api.lbs.maps.server.exchange.GeocodeException;
import net.rim.device.api.lbs.maps.server.exchange.GeocodeExchange;
import net.rim.device.api.lbs.maps.server.exchange.ServerExchange;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * This screen allows a user to initiate either a free form search or a
 * structured search. The search can be designated blocking or non-blocking.
 */
public class GeocodeScreen extends MainScreen implements FieldChangeListener,
        ServerExchangeCallback {
    private final ObjectChoiceField _choiceField;

    private final Manager _freeFormManager;
    private final Manager _structuredManager;

    private final ButtonField _searchButton;

    private final BasicEditField _editFieldLatitude;
    private final BasicEditField _editFieldLongitude;
    private final LabelField _resultsField;
    private BasicEditField _freeFormEditField;
    private BasicEditField _addressEditField;
    private BasicEditField _cityEditField;
    private BasicEditField _districtEditField;
    private BasicEditField _countryEditField;
    private BasicEditField _zipEditField;

    private final CheckboxField _blockingCheckbox;

    private static final int MAP_WIDTH = 480;
    private static final int MAP_HEIGHT = 360;
    private static final int ZOOM_LEVEL = 5;
    private static final int ROTATION = 0;

    /**
     * Creates a new GeocodeScreen object and intializes UI components
     */
    public GeocodeScreen() {
        setTitle("Geocode");

        _editFieldLatitude = new BasicEditField("Latitude: ", "");
        _editFieldLongitude = new BasicEditField("Longitude: ", "");
        add(_editFieldLatitude);
        add(_editFieldLongitude);

        _blockingCheckbox = new CheckboxField("Blocking:", false);
        add(_blockingCheckbox);

        _choiceField =
                new ObjectChoiceField("Search Type: ", new String[] {
                        "Freeform", "Structured" }, 0);
        _choiceField.setChangeListener(this);
        add(_choiceField);

        add(new SeparatorField());

        _structuredManager = createStructuredManager();
        _freeFormManager = createFreeFormManager();

        // Add the free form manager as default
        add(_freeFormManager);

        add(new SeparatorField());

        _searchButton = new ButtonField("Search");
        _searchButton.setChangeListener(this);
        add(_searchButton);

        add(new SeparatorField());

        _resultsField = new LabelField();
        add(_resultsField);
    }

    /**
     * Adds UI components to a VerticalFieldManager
     * 
     * @return Manager containing UI components
     */
    private Manager createStructuredManager() {
        final VerticalFieldManager manager = new VerticalFieldManager();

        _addressEditField = new BasicEditField("Address: ", "");
        manager.add(_addressEditField);

        _cityEditField = new BasicEditField("City: ", "");
        manager.add(_cityEditField);

        _districtEditField = new BasicEditField("Province: ", "");
        manager.add(_districtEditField);

        _countryEditField = new BasicEditField("Country: ", "");
        manager.add(_countryEditField);

        _zipEditField = new BasicEditField("Postal Code: ", "");
        manager.add(_zipEditField);

        return manager;
    }

    /**
     * Adds UI components to a VerticalFieldManager
     * 
     * @return Manager containing UI components
     */
    private Manager createFreeFormManager() {
        final VerticalFieldManager manager = new VerticalFieldManager();

        _freeFormEditField = new BasicEditField("Freeform: ", "");
        manager.add(_freeFormEditField);

        return manager;
    }

    /**
     * Constructs and returns a MapLocation object
     * 
     * @return MapLocation object containing data from screen
     */
    private MapLocation createMapLocation() {
        final MapLocation mapLocation =
                new MapLocation(Double
                        .parseDouble(_editFieldLatitude.getText()), Double
                        .parseDouble(_editFieldLatitude.getText()), null, null);
        mapLocation.addData(MapLocation.LBS_LOCATION_STREET_ADDRESS_KEY,
                _addressEditField.getText());
        mapLocation.addData(MapLocation.LBS_LOCATION_CITY_KEY, _cityEditField
                .getText());
        mapLocation.addData(MapLocation.LBS_LOCATION_REGION_KEY,
                _districtEditField.getText());
        mapLocation.addData(MapLocation.LBS_LOCATION_COUNTRY_KEY,
                _countryEditField.getText());
        mapLocation.addData(MapLocation.LBS_LOCATION_POSTAL_CODE_KEY,
                _zipEditField.getText());

        return mapLocation;
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Prevent the save dialog from being displayed
        return true;
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _searchButton) {
            _resultsField.setText("");

            final String lat = _editFieldLatitude.getText().trim();
            final String lon = _editFieldLongitude.getText().trim();
            if (lat.length() > 0 && lon.length() > 0) {
                // Check if this is to be a blocking request
                final boolean blocking = _blockingCheckbox.getChecked();

                final MapPoint origin =
                        new MapPoint(Float.parseFloat(lat), Float
                                .parseFloat(lon));

                final MapDimensions dim =
                        new MapDimensions(origin, MAP_WIDTH, MAP_HEIGHT,
                                ZOOM_LEVEL, ROTATION);

                if (_choiceField.getSelectedIndex() == 0) // Use the freeform
                                                          // text
                {
                    if (blocking) {
                        // We are making a blocking request, should not be
                        // called on event thread.
                        final Thread t = new Thread(new Runnable() {
                            /**
                             * @see java.lang.Runnable#run()
                             */
                            public void run() {
                                final GeocodeExchange ex =
                                        Geocoder.getInstance().geocode(null,
                                                _freeFormEditField.getText(),
                                                dim, 0);

                                // Check if the request was successful
                                if (ex.getExceptionList().size() == 0) {
                                    requestSuccess(ex);
                                } else {
                                    requestFailure(ex);
                                }
                            }
                        });
                        t.start();
                    } else {
                        Geocoder.getInstance().geocode(this,
                                _freeFormEditField.getText(), dim, 0);
                    }
                } else // Use the structured data
                {
                    if (blocking) {
                        // We are making a blocking request, should not be
                        // called on event thread.
                        final Thread t = new Thread(new Runnable() {
                            /**
                             * @see java.lang.Runnable#run()
                             */
                            public void run() {
                                try {
                                    final GeocodeExchange ex =
                                            Geocoder.getInstance().geocode(
                                                    null, createMapLocation(),
                                                    dim, 0);

                                    // Check if the request was successful
                                    if (ex.getExceptionList().size() == 0) {
                                        requestSuccess(ex);
                                    } else {
                                        requestFailure(ex);
                                    }
                                } catch (final GeocodeException ge) {
                                    GeocodeDemo.errorDialog(ge.toString());
                                }
                            }
                        });
                        t.start();
                    } else {
                        try {
                            // Non-blocking
                            Geocoder.getInstance().geocode(this,
                                    createMapLocation(), dim, 0);
                        } catch (final GeocodeException ge) {
                            GeocodeDemo.errorDialog(ge.toString());
                        }
                    }
                }
            } else {
                Dialog.alert("Please enter both latitude and longitude coordinates");
            }

        } else if (field == _choiceField) {
            final int index = _choiceField.getSelectedIndex();
            if (index == 0) {
                // Replace the structured manager with the free form manager
                replace(_structuredManager, _freeFormManager);
            } else {
                // Delete the free form manager and insert the structured
                // manager
                delete(_freeFormManager);
                insert(_structuredManager, 4);
            }
        }
    }

    /**
     * @see net.rim.device.api.lbs.maps.server.ServerExchangeCallback#requestSuccess(ServerExchange)
     */
    public void requestSuccess(final ServerExchange exchange) {
        // Display results in the event of a successful request.

        if (exchange instanceof GeocodeExchange) {
            final GeocodeExchange geocodecExchange = (GeocodeExchange) exchange;
            final Vector results = geocodecExchange.getResults();

            final StringBuffer text = new StringBuffer();

            for (int i = 0; i < results.size(); i++) {
                text.append(GeocodeDemo.composeLocation((MapLocation) results
                        .elementAt(i)));
            }

            synchronized (Application.getEventLock()) {
                _resultsField.setText(text.toString());
            }
        }
    }

    /**
     * @see net.rim.device.api.lbs.maps.server.ServerExchangeCallback#requestFailure(ServerExchange)
     */
    public void requestFailure(final ServerExchange exchange) {
        // Display any exceptions that were thrown in the event
        // of a request failure.

        final Vector exList = exchange.getExceptionList();
        final StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < exList.size(); i++) {
            final Exception ex = (Exception) exList.elementAt(i);
            if (buffer.length() == 0) {
                buffer.append("Exception List").append('\n');
            }

            if (ex instanceof GeocodeException) {
                final GeocodeException gcex = (GeocodeException) ex;
                buffer.append("Geocode Exception code: " + gcex.getErrorCode())
                        .append('\n');
            } else {
                buffer.append("Exception: " + ex).append('\n');
            }
        }

        synchronized (Application.getEventLock()) {
            _resultsField.setText(buffer.toString());
        }
    }

    /**
     * @see net.rim.device.api.lbs.maps.server.ServerExchangeCallback#requestHalted()
     */
    public void requestHalted() {
        _resultsField.setText("Request halted");
    }
}
