/*
 * LocatorScreen.java
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

import javax.microedition.location.AddressInfo;
import javax.microedition.location.Landmark;

import net.rim.device.api.lbs.Locator;
import net.rim.device.api.lbs.LocatorException;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * This class makes use of the Locator API. This screen allows users to look up
 * locations using a number of different criteria. If a match or multiple
 * matches are found, the user can choose which one to display (see
 * LookupMatchesScreen) and the location will be displayed on the embedded map.
 */
final class LocatorScreen extends MainScreen {
    private final EmbeddedMapDemo.EmbeddedMapDemoScreen _mainScreen;

    private final VerticalFieldManager _vfm;
    private final BasicEditField _lookupField;
    private final BasicEditField _lookupStreetField;
    private final BasicEditField _lookupPostalCodeField;
    private final BasicEditField _lookupCityField;
    private final BasicEditField _lookupStateField;
    private final BasicEditField _lookupCountryField;
    private final ButtonField _lookupButtonField;
    private final LabelField _statusLabelField;

    /**
     * Constructor.
     * 
     * @param screen
     *            - A pointer to the main screen.
     */
    LocatorScreen(final EmbeddedMapDemo.EmbeddedMapDemoScreen screen) {
        setTitle("Location Search");
        _mainScreen = screen;

        // Initialize the field manager and all fields
        _vfm =
                new VerticalFieldManager(Field.USE_ALL_WIDTH
                        | Manager.VERTICAL_SCROLLBAR | Manager.VERTICAL_SCROLL);

        _lookupField =
                new BasicEditField("General Lookup: ", "",
                        EmbeddedMapDemo.MAX_NUM_OF_CHARS, TextField.NO_NEWLINE);
        _lookupStreetField =
                new BasicEditField("Street Lookup: ", "",
                        EmbeddedMapDemo.MAX_NUM_OF_CHARS, TextField.NO_NEWLINE);
        _lookupPostalCodeField =
                new BasicEditField("Postal Code Lookup: ", "",
                        EmbeddedMapDemo.MAX_NUM_OF_CHARS, TextField.NO_NEWLINE);
        _lookupCityField =
                new BasicEditField("City Lookup: ", "",
                        EmbeddedMapDemo.MAX_NUM_OF_CHARS, TextField.NO_NEWLINE);
        _lookupStateField =
                new BasicEditField("State Lookup: ", "",
                        EmbeddedMapDemo.MAX_NUM_OF_CHARS, TextField.NO_NEWLINE);
        _lookupCountryField =
                new BasicEditField("Country Lookup: ", "",
                        EmbeddedMapDemo.MAX_NUM_OF_CHARS, TextField.NO_NEWLINE);
        _lookupButtonField =
                new ButtonField("Search", Field.FIELD_HCENTER
                        | ButtonField.CONSUME_CLICK);
        _lookupButtonField.setChangeListener(_lookupListener);
        _statusLabelField = new LabelField("");

        // Add fields to the manager
        _vfm.add(_lookupField);
        _vfm.add(new LabelField(" -- OR -- "));
        _vfm.add(_lookupStreetField);
        _vfm.add(_lookupPostalCodeField);
        _vfm.add(_lookupCityField);
        _vfm.add(_lookupStateField);
        _vfm.add(_lookupCountryField);
        _vfm.add(_lookupButtonField);
        _vfm.add(_statusLabelField);

        // Add the manager and menu items to the screen
        add(_vfm);
        addMenuItem(_search);
        addMenuItem(_clear);
    }

    /**
     * Update the _statusLabel label with a set prefix and the given text.
     * 
     * @param text
     *            - the text to be displayed
     */
    private void setStatus(final String text) {
        _statusLabelField.setText("Locator Status: " + text);
    }

    /**
     * Starts a new thread running LocatorRunnable to do a search.
     */
    private void lookup() {
        // Cancel any existing searches since each application
        // can only run one search at a time.
        Locator.cancel();

        if (_lookupField.getText().length() != 0) {
            final Thread locatorThread =
                    new Thread(new LocatorRunnable(_lookupField.getText()));
            locatorThread.start();
        } else if (!(_lookupStreetField.getText().length() == 0
                && _lookupPostalCodeField.getText().length() == 0
                && _lookupCityField.getText().length() == 0
                && _lookupStateField.getText().length() == 0 && _lookupCountryField
                .getText().length() == 0)) {
            // Construct and pass an AddressInfo object
            final AddressInfo address = new AddressInfo();
            address.setField(AddressInfo.STREET, _lookupStreetField.getText());
            address.setField(AddressInfo.POSTAL_CODE, _lookupPostalCodeField
                    .getText());
            address.setField(AddressInfo.CITY, _lookupCityField.getText());
            address.setField(AddressInfo.STATE, _lookupStateField.getText());
            address.setField(AddressInfo.COUNTRY, _lookupCountryField.getText());

            final Thread locatorThread =
                    new Thread(new LocatorRunnable(address));
            locatorThread.start();
        } else {
            Dialog.inform("Please fill in either the general lookup field"
                    + " or at least one location lookup field.");
            _lookupField.setFocus();
        }
    }

    /**
     * Prevent the save dialog from being displayed.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        return true;
    }

    /**
     * Menu item to initiate a search. See lookup().
     */
    private final MenuItem _search = new MenuItem("Search", 100, 10) {
        public void run() {
            lookup();
        }
    };

    /**
     * Menu item to clear all of the edit fields.
     */
    private final MenuItem _clear = new MenuItem("Clear all fields", 100, 10) {
        public void run() {
            _lookupField.clear(10);
            _lookupStreetField.clear(10);
            _lookupPostalCodeField.clear(10);
            _lookupCityField.clear(10);
            _lookupStateField.clear(10);
            _lookupCountryField.clear(10);
        }
    };

    /**
     * Listener for _lookupButton. See lookup().
     */
    private final FieldChangeListener _lookupListener =
            new FieldChangeListener() {
                public void fieldChanged(final Field f, final int context) {
                    lookup();
                }
            };

    /**
     * Runnable class that allows for landmark searching and displaying.
     */
    private class LocatorRunnable implements Runnable {
        private final String _search;
        private final AddressInfo _address;

        /**
         * Constructor using the freeform string lookup.
         * 
         * @param field
         *            - the ExtendedMapField that will hold the location
         * @param location
         *            - the String location to search for
         */
        LocatorRunnable(final String location) {
            _search = location;
            _address = null;
        }

        /**
         * Constructor using an AddressInfo object for lookup.
         * 
         * @param field
         *            - the ExtendedMapField that will hold the location
         * @param address
         *            - the AddressInfo object to search for
         */
        LocatorRunnable(final AddressInfo address) {
            _search = null;
            _address = address;
        }

        /**
         * Will update the _statusLabel field with progress as it continues.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            Landmark[] landmarkArray;
            final UiApplication ui = UiApplication.getUiApplication();

            try {
                ui.invokeLater(new StatusRunnable("Working..."));

                // Search for the location and then parse the required info
                if (_search == null) {
                    landmarkArray = Locator.geocode(_address, null);
                } else {
                    landmarkArray = Locator.geocode(_search, null);
                }

                // If a landmark was found, create, add, and display location
                if (landmarkArray != null && landmarkArray.length > 0) {
                    if (landmarkArray.length == 1) {
                        final Landmark landmark = landmarkArray[0];

                        ui.invokeLater(new Runnable() {
                            public void run() {
                                _mainScreen.addAndDisplayLocation(landmark);
                                close();
                            }
                        });

                    } else {
                        final LookupMatchesScreen lookupMatchesScreen =
                                new LookupMatchesScreen(landmarkArray,
                                        _mainScreen);
                        ui.invokeLater(new Runnable() {
                            public void run() {
                                UiApplication.getUiApplication().pushScreen(
                                        lookupMatchesScreen);
                                close();
                            }
                        });
                    }
                    ui.invokeLater(new StatusRunnable("Done."));
                }

                else {
                    ui.invokeLater(new StatusRunnable("No results."));
                }

            } catch (final LocatorException e) {
                ui.invokeLater(new StatusRunnable(e.getMessage()));
            }
        }
    }

    /**
     * Runnable class that allows us to update the status label
     */
    private class StatusRunnable implements Runnable {
        String _message;

        /**
         * Constructor that sets the text to be displayed
         * 
         * @param text
         *            - to be displayed in status label
         */
        StatusRunnable(final String text) {
            _message = text;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            setStatus(_message);
        }
    }
}
