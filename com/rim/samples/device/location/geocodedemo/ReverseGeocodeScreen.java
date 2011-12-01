/*
 * ReverseGeocodeScreen.java
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

import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.lbs.maps.model.MapPoint;
import net.rim.device.api.lbs.maps.server.ReverseGeocoder;
import net.rim.device.api.lbs.maps.server.ServerExchangeCallback;
import net.rim.device.api.lbs.maps.server.exchange.ReverseGeocodeException;
import net.rim.device.api.lbs.maps.server.exchange.ReverseGeocodeExchange;
import net.rim.device.api.lbs.maps.server.exchange.ServerExchange;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Screen that allows user to type in lat/lon pairs and see the reverse geocoded
 * result along with other attributes of the location found.
 */
public final class ReverseGeocodeScreen extends MainScreen implements
        FieldChangeListener, ServerExchangeCallback {
    private final BasicEditField _editFieldLatitude;
    private final BasicEditField _editFieldLongitude;
    private final BasicEditField _bearingEditField;

    private final ButtonField _searchButton;
    private final ObjectChoiceField _choiceField;
    private final LabelField _resultsField;
    private final CheckboxField _blockingCheckBox;

    private final AdminChoice[] choices = new AdminChoice[] {
            new AdminChoice("Address", ReverseGeocodeExchange.ADDRESS),
            new AdminChoice("Country", ReverseGeocodeExchange.COUNTRY),
            new AdminChoice("Province/State",
                    ReverseGeocodeExchange.PROVINCE_STATE),
            new AdminChoice("City", ReverseGeocodeExchange.CITY),
            new AdminChoice("Postal Code", ReverseGeocodeExchange.POSTAL),
            new AdminChoice("Out Of Bounds", 100),
            new AdminChoice("Below Bounds", -100) };

    /**
     * Creates a new ReverseGeocodeScreen object and initializes UI elements
     */
    public ReverseGeocodeScreen() {
        setTitle("Reverse Geocode");

        _editFieldLatitude = new BasicEditField("Latitude: ", "");
        _editFieldLongitude = new BasicEditField("Longitude: ", "");
        _blockingCheckBox = new CheckboxField("Blocking:", false);
        _bearingEditField =
                new BasicEditField("Bearing: ", "-1", 4,
                        BasicEditField.FILTER_INTEGER);

        add(_editFieldLatitude);
        add(_editFieldLongitude);
        add(_blockingCheckBox);
        add(_bearingEditField);

        _choiceField = new ObjectChoiceField("Admin Level:", choices, 0);
        add(_choiceField);

        _searchButton = new ButtonField("Search");
        _searchButton.setChangeListener(this);
        add(_searchButton);

        add(new SeparatorField());

        _resultsField = new LabelField();
        add(_resultsField);
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
    public void fieldChanged(final Field field, final int actionId) {
        if (field == _searchButton) {
            _resultsField.setText("");

            final String lat = _editFieldLatitude.getText().trim();
            final String lon = _editFieldLongitude.getText().trim();

            if (lat.length() > 0 && lon.length() > 0) {
                try {
                    final MapPoint origin =
                            new MapPoint(Float.parseFloat(lat), Float
                                    .parseFloat(lon));
                    final AdminChoice choice =
                            (AdminChoice) _choiceField.getChoice(_choiceField
                                    .getSelectedIndex());

                    final ReverseGeocoder reverseGeocoder =
                            ReverseGeocoder.getInstance();

                    if (_blockingCheckBox.getChecked()) {
                        // We are making a blocking request, should not be
                        // called on event thread.
                        final Thread t = new Thread(new Runnable() {
                            /**
                             * @see java.lang.Runnable#run()
                             */
                            public void run() {
                                try {
                                    // This will check the cache first before
                                    // making
                                    // a request to the server. Passing null as
                                    // the
                                    // first argument to reverseGeocode()
                                    // results in
                                    // a blocking call.
                                    final ReverseGeocodeExchange ex =
                                            reverseGeocoder
                                                    .reverseGeocode(
                                                            null,
                                                            origin,
                                                            choice.getValue(),
                                                            Integer.parseInt(_bearingEditField
                                                                    .getText()),
                                                            0);

                                    UiApplication.getUiApplication()
                                            .invokeLater(new Runnable() {
                                                /**
                                                 * @see java.lang.Runnable#run()
                                                 */
                                                public void run() {
                                                    _resultsField
                                                            .setText("Blocking method");
                                                }
                                            });

                                    // In this case we did not provide a
                                    // callback so we need to initiate
                                    // the processing of the reverse geocoding
                                    // result ourselves.
                                    if (ex.getExceptionList().size() == 0) {
                                        requestSuccess(ex);
                                    } else {
                                        requestFailure(ex);
                                    }
                                } catch (final ReverseGeocodeException rge) {
                                    GeocodeDemo.errorDialog(rge.toString());
                                }
                            }
                        });
                        t.start();
                    } else {
                        try {
                            // This will check the cache first before making
                            // a request to the server.
                            ReverseGeocoder.getInstance().reverseGeocode(
                                    this,
                                    origin,
                                    choice.getValue(),
                                    Integer.parseInt(_bearingEditField
                                            .getText()), 0);

                            _resultsField.setText("Non-blocking method");
                        } catch (final ReverseGeocodeException rge) {
                            GeocodeDemo.errorDialog(rge.toString());
                        }
                    }
                } catch (final NumberFormatException nfe) {
                    GeocodeDemo.errorDialog(nfe.toString());
                }
            } else {
                Dialog.alert("Please enter both latitude and longitude coordinates");
            }
        }
    }

    /**
     * @see net.rim.device.api.lbs.maps.server.ServerExchangeCallback#requestSuccess(ServerExchange)
     */
    public void requestSuccess(final ServerExchange exchange) {
        // Display results in the event of a successful request.

        if (exchange instanceof ReverseGeocodeExchange) {
            final ReverseGeocodeExchange reverseGeocodeExchange =
                    (ReverseGeocodeExchange) exchange;
            final Vector results = reverseGeocodeExchange.getResults();

            final StringBuffer text = new StringBuffer();

            for (int i = 0; i < results.size(); i++) {
                text.append(GeocodeDemo.composeLocation((MapLocation) results
                        .elementAt(i)));
            }

            synchronized (Application.getEventLock()) {
                _resultsField.setText(_resultsField.getText() + '\n'
                        + text.toString());
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

            if (ex instanceof ReverseGeocodeException) {
                final ReverseGeocodeException rgex =
                        (ReverseGeocodeException) ex;
                buffer.append(
                        " ReverseGeocode Exception code: "
                                + rgex.getErrorCode()).append('\n');
            } else {
                buffer.append(" Exception: " + ex).append('\n');
            }
        }

        synchronized (Application.getEventLock()) {
            _resultsField.setText(_resultsField.getText() + '\n'
                    + buffer.toString());
        }
    }

    /**
     * @see net.rim.device.api.lbs.maps.server.ServerExchangeCallback#requestHalted()
     */
    public void requestHalted() {
        GeocodeDemo.errorDialog("Request halted");
    }

    /**
     * Represents a choice of administration boundary level as defined in the
     * <code>net.rim.device.api.lbs.maps.server.exchangeReverseGeocodeExchange</code>
     * class.
     */
    private final static class AdminChoice {
        private final String _name;
        private final int _value;

        public AdminChoice(final String name, final int value) {
            _name = name;
            _value = value;
        }

        /**
         * Retrieves the value associated with this AdminChoice object
         * 
         * @return The value associated with this AdminChoice object
         */
        public int getValue() {
            return _value;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            // Return the name associated with this AdminChoice object
            return _name;
        }
    }
}
