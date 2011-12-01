/**
 * EmbeddedMapDemo.java
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

import java.util.Vector;

import javax.microedition.location.Landmark;
import javax.microedition.location.QualifiedCoordinates;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.TouchGesture;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * This sample demonstrates the embedding of a MapField in a BlackBerry UI
 * application. The LocatorScreen class shows how to use the Locator API to
 * search for locations and display them on the map.
 */
final class EmbeddedMapDemo extends UiApplication {
    static final String INITIAL_NAME = "RIM Mississauga";
    static final double INITIAL_LATITUDE = 43.66455;
    static final double INITIAL_LONGITUDE = -79.59473;
    static final int MAX_NUM_OF_CHARS = 255;

    private final Vector _mapLocations; // Stored locations.
    private final MapLocation _initialLocation;
    private final EmbeddedMapDemoScreen _mainScreen;

    /**
     * Entry point for the application.
     * 
     * @param args
     *            - N/A
     */
    public static void main(final String[] args) {
        final EmbeddedMapDemo app = new EmbeddedMapDemo();
        app.enterEventDispatcher();
    }

    // Constructor
    EmbeddedMapDemo() {
        _mapLocations = new Vector();
        _initialLocation =
                new MapLocation(INITIAL_LATITUDE, INITIAL_LONGITUDE,
                        INITIAL_NAME);
        _mapLocations.addElement(_initialLocation);

        _mainScreen = new EmbeddedMapDemoScreen();
        pushScreen(_mainScreen);
    }

    /**
     * This class is the main screen for the application. It holds the embedded
     * map as well as labels and buttons for other features.
     */
    final class EmbeddedMapDemoScreen extends MainScreen {
        private final VerticalFieldManager _vfm;

        private final LabelField _instructionsField1;
        private final LabelField _instructionsField2;
        private final ExtendedMapField _mapField;
        private final BasicEditField _nameField;
        private final SaveButtonField _saveField;

        // Constructor
        private EmbeddedMapDemoScreen() {
            // Initialize UI components
            setTitle("Embedded Map Demo");

            _vfm =
                    new VerticalFieldManager(Field.USE_ALL_WIDTH
                            | Manager.VERTICAL_SCROLLBAR
                            | Manager.VERTICAL_SCROLL);

            _instructionsField1 = new LabelField("Click map to (de)activate");
            _instructionsField2 =
                    new LabelField("Press 'i' to zoom in, 'o' to zoom out");
            _mapField = new ExtendedMapField(_initialLocation, _mapLocations);
            _nameField =
                    new BasicEditField("Name: ", _initialLocation.getName(),
                            MAX_NUM_OF_CHARS, TextField.NO_NEWLINE);
            _saveField =
                    new SaveButtonField("Save", Field.FIELD_HCENTER
                            | ButtonField.CONSUME_CLICK);

            _vfm.add(_instructionsField1);
            _vfm.add(_instructionsField2);
            _vfm.add(_mapField);
            _vfm.add(_nameField);
            _vfm.add(_saveField);

            add(_vfm);

            addMenuItem(_favouriteLocationsItem);
            addMenuItem(_locatorItem);
            addMenuItem(_saveLocationItem);
            addMenuItem(_resetMapItem);
        }

        /**
         * Menu item to go to the saved locations screen.
         */
        private final MenuItem _favouriteLocationsItem = new MenuItem(
                "Favourite Locations", 110, 10) {
            public void run() {
                if (_mapLocations.size() == 0) {
                    Dialog.inform("No saved locations");
                } else {
                    final SavedLocationsScreen savedLocationsScreen =
                            new SavedLocationsScreen(_mapLocations, _mainScreen);
                    UiApplication.getUiApplication().pushScreen(
                            savedLocationsScreen);
                }
            }
        };

        /**
         * Menu item to look up a location.
         */
        private final MenuItem _locatorItem = new MenuItem("Location Search",
                110, 11) {
            public void run() {
                final LocatorScreen locatorScreen =
                        new LocatorScreen(_mainScreen);
                UiApplication.getUiApplication().pushScreen(locatorScreen);
            }
        };

        /**
         * Menu item to save the location.
         */
        private final MenuItem _saveLocationItem = new MenuItem(
                "Save Location", 110, 12) {
            public void run() {
                saveOrUpdateLocation();
            }
        };

        /**
         * Menu item to reset the map (including saved locations).
         */
        private final MenuItem _resetMapItem = new MenuItem("Reset Map", 110,
                13) {
            public void run() {
                _mapField.resetMap();
                _mapLocations.removeAllElements();
                _mapLocations.addElement(_initialLocation);
                displayLocation(0);
            }
        };

        /**
         * Clears the name field.
         */
        void clearEditFields() {
            _nameField.clear(10);
        }

        /**
         * Update the name field to reflect the newly updated map location.
         * 
         * @param index
         *            - the index of the map location
         */
        void displayLocation(final int index) {
            final MapLocation mapLocation =
                    (MapLocation) _mapLocations.elementAt(index);

            _nameField.setText(mapLocation.getName());

            _mapField.displayLocation(index);
            _mapField.setFocus();
            _mapField.activatePan();
        }

        void displayLocation(final MapLocation mapLocation) {
            final int index = _mapLocations.indexOf(mapLocation, 0);
            displayLocation(index);
        }

        /**
         * Add to the favourite locations list and display the location
         * described by the given landmark.
         * 
         * @param landmark
         *            - object describing the location to add and display
         */
        void addAndDisplayLocation(final Landmark landmark) {
            final QualifiedCoordinates cords =
                    landmark.getQualifiedCoordinates();

            final MapLocation location =
                    new MapLocation(cords.getLatitude(), cords.getLongitude(),
                            landmark.getName());

            // Check if the location exists in the favourites, if not,
            // create and add.
            final int index = _mapField.checkForLocation(location);
            if (index != -1) {
                displayLocation(index);
            } else {
                _mapLocations.addElement(location);
                displayLocation(location);
            }
        }

        /**
         * Save the current location. If the location has already been saved,
         * update the entry with the new name.
         */
        void saveOrUpdateLocation() {
            final String name = _nameField.getText().trim();

            if (name.length() == 0) {
                Dialog.inform("Name is required");
                _nameField.setFocus();
                return;
            }

            MapLocation found;
            final MapLocation mapLocation =
                    new MapLocation(_mapField.getLatitude() / 100000.0,
                            _mapField.getLongitude() / 100000.0, name);
            final int index = _mapField.checkForLocation(mapLocation);

            if (index == -1) {
                _mapLocations.addElement(mapLocation);
                displayLocation(mapLocation);
            } else {
                found = (MapLocation) _mapLocations.elementAt(index);
                found.setName(name);

                displayLocation(index);
            }
        }

        /**
         * @see Manager#sublayout(int, int)
         */
        protected void sublayout(final int width, final int height) {
            _mapField.setPreferredSize(Display.getWidth() - 10, (int) (Display
                    .getHeight() * 0.65));
            super.sublayout(width, height);
        }

        /**
         * @see Field#touchEvent(TouchEvent)
         */
        protected boolean touchEvent(final TouchEvent message) {
            boolean isConsumed = false;

            if (_mapField.isClicked()) {
                final TouchGesture touchGesture = message.getGesture();
                if (touchGesture != null) {
                    // If the user has performed a swipe gesture we will move
                    // the
                    // map accordingly.
                    if (touchGesture.getEvent() == TouchGesture.SWIPE) {
                        // Retrieve the swipe magnitude so we know how
                        // far to move the map.
                        final int magnitude = touchGesture.getSwipeMagnitude();

                        // Move the map in the direction of the swipe.
                        switch (touchGesture.getSwipeDirection()) {
                        case TouchGesture.SWIPE_NORTH:
                            _mapField.move(0, -magnitude);
                            break;
                        case TouchGesture.SWIPE_SOUTH:
                            _mapField.move(0, magnitude);
                            break;
                        case TouchGesture.SWIPE_EAST:
                            _mapField.move(-magnitude, 0);
                            break;
                        case TouchGesture.SWIPE_WEST:
                            _mapField.move(magnitude, 0);
                            break;
                        }
                        isConsumed = true; // We've consumed the touch event.
                    }
                }
            }
            return isConsumed;
        }

        /**
         * @see net.rim.device.api.ui.Field#navigationMovement(int, int, int,
         *      int)
         */
        protected boolean navigationMovement(final int dx, final int dy,
                final int status, final int time) {
            if (_vfm.getFieldWithFocus() == _mapField) {
                final boolean handled =
                        _mapField.navigationMovement(dx, dy, status, time);

                if (handled) {
                    clearEditFields();
                }

                return handled;
            }

            return super.navigationMovement(dx, dy, status, time);
        }

        /**
         * Prevent the save dialog from being displayed.
         * 
         * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
         */
        public boolean onSavePrompt() {
            return true;
        }

        private class SaveButtonField extends ButtonField implements
                FieldChangeListener {
            /**
             * Constructor.
             */
            private SaveButtonField(final String label, final long style) {
                super(label, style);
                setChangeListener(this);
            }

            /**
             * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field,
             *      int)
             */
            public void fieldChanged(final Field field, final int context) {
                saveOrUpdateLocation();
            }
        }
    }
}
