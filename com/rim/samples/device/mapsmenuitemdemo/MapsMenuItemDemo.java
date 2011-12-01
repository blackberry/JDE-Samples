/*
 * MapsMenuItemDemo.java
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

package com.rim.samples.device.mapsmenuitemdemo;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.blackberry.api.maps.MapView;
import net.rim.blackberry.api.menuitem.ApplicationMenuItem;
import net.rim.blackberry.api.menuitem.ApplicationMenuItemRepository;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.NumericChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample app registers an ApplicationMenuItem associated with the
 * BlackBerry Maps application on startup. When invoked from the Maps
 * application, the menu item launches a GUI version of this app to display
 * latitude, longitude and zoom level info from the provided MapView context
 * object. A MainScreen menu item invokes BlackBerry Maps application with a
 * modified MapView object.
 * 
 * The application will run in the background as a system module, without
 * displaying an icon on the BlackBerry device home screen.
 */
public final class MapsMenuItemDemo extends UiApplication {
    private static MapView _mv = new MapView();

    /**
     * Entry point for application.
     * 
     * @param args
     *            Command line arguments
     */
    public static void main(final String[] args) {
        if (args != null && args.length > 0) {
            if (args[0].equals("startup")) {
                // Register an ApplicationMenuItem on device startup.
                final ApplicationMenuItemRepository amir =
                        ApplicationMenuItemRepository.getInstance();
                final ApplicationDescriptor ad_startup =
                        ApplicationDescriptor.currentApplicationDescriptor();
                final ApplicationDescriptor ad_gui =
                        new ApplicationDescriptor(ad_startup, "gui",
                                new String[] { "gui" });
                amir.addMenuItem(ApplicationMenuItemRepository.MENUITEM_MAPS,
                        new MapsMenuItem(), ad_gui);
            } else if (args[0].equals("gui")) {
                // App was invoked by our ApplicationMenuItem. Call default
                // constructor for GUI version of the application.
                final MapsMenuItemDemo app = new MapsMenuItemDemo();

                // Make the currently running thread the application's event
                // dispatch thread and begin processing events.
                app.enterEventDispatcher();
            }
        }
    }

    /**
     * Shows a MapsMenuItemScreen when invoked.
     */
    private static class MapsMenuItem extends ApplicationMenuItem {
        // Constructor
        private MapsMenuItem() {
            // Create a new ApplicationMenuItem instance with relative menu
            // position of 20. Lower numbers correspond to higher placement
            // in the menu.
            super(20);
        }

        /**
         * Returns the name to display in a menu.
         * 
         * @return The name to display.
         */
        public String toString() {
            return "Menu Item Demo";
        }

        /**
         * Views the map in a MapMenuItemScreen.
         * 
         * @see ApplicationMenuItem#run(Object)
         */
        public Object run(final Object context) {
            if (context instanceof MapView) {
                _mv = (MapView) context;

                // Get the UiApplication instance and display the GUI screen.
                final UiApplication app = UiApplication.getUiApplication();
                app.pushScreen(new MapsMenuItemScreen(_mv));
                app.requestForeground();
            }

            return null;
        }
    }
}

/**
 * A MainScreen class for our UiApplication.
 */
final class MapsMenuItemScreen extends MainScreen {
    private final MapView _mapview;
    private final BasicEditField _latitudeField;
    private final BasicEditField _logitudeField;
    private final NumericChoiceField _zoomField;

    /**
     * Constructor
     * 
     * @param mapView
     *            The MapView context object.
     */
    MapsMenuItemScreen(final MapView mapView) {
        _mapview = mapView;

        // The int values returned by getLatitude() and getLongitude() are
        // 100,000 times
        // the values specified by WGS84.
        _latitudeField =
                new BasicEditField("Latitude:      ", _mapview.getLatitude()
                        / 100000.0 + "", 9, BasicEditField.FILTER_REAL_NUMERIC);
        _logitudeField =
                new BasicEditField("Longitude:   ", _mapview.getLongitude()
                        / 100000.0 + "", 10, BasicEditField.FILTER_REAL_NUMERIC);
        _zoomField =
                new NumericChoiceField("Zoom: ", 0, MapView.MAX_ZOOM, 1,
                        _mapview.getZoom());

        // Add GUI components.
        add(_latitudeField);
        add(_logitudeField);
        add(_zoomField);
        add(new SeparatorField());
        add(new RichTextField(
                "Edit latitude, longitude and zoom level settings and select View Map from the menu.",
                Field.NON_FOCUSABLE));
        addMenuItem(_viewMapItem);

        setTitle("Location Details Screen");
    }

    /**
     * Displays a map of a specified locaton.
     */
    private final MenuItem _viewMapItem = new MenuItem("View Map", 1000, 10) {
        public void run() {
            // Change the zoom level.
            _mapview.setZoom(_zoomField.getSelectedValue());

            try {
                final int latitude =
                        (int) (100000 * Double.parseDouble(_latitudeField
                                .getText()));
                final int longitude =
                        (int) (100000 * Double.parseDouble(_logitudeField
                                .getText()));

                if (latitude > 9000000 || latitude < -9000000
                        || longitude >= 18000000 || longitude < -18000000) {
                    throw new IllegalArgumentException();
                }

                _mapview.setLatitude(latitude);
                _mapview.setLongitude(longitude);

                // Invoke BlackBerry Maps application with provided MapView
                // object.
                Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                        new MapsArguments(_mapview));

                close();
            } catch (final RuntimeException re) {
                // An exception is thrown when any of the following occur :
                // Latitude is invalid : Valid range: [-90, 90]
                // Longitude is invalid : Valid range: [-180, 180)
                // Minus sign between 2 numbers.
                Dialog.alert("Invalid Longitude and/or Latitude");
                _latitudeField.setFocus();
            }
        }
    };

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        // If _zoomField has focus , make "Change Option" item default.
        if (getFieldWithFocus() == _zoomField) {
            menu.setDefault(1);
        }
    }

    /**
     * Clear the latitude and longitude fields when the first digit of any of
     * the fields is inputted.
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char , int , int)
     */
    public boolean keyChar(final char key, final int status, final int time) {
        final Field focusField = getFieldWithFocus();

        if (focusField instanceof BasicEditField && !focusField.isDirty()) {
            if (key >= Characters.DIGIT_ZERO && key <= Characters.DIGIT_NINE
                    || key == Characters.BACKSPACE
                    || key == Characters.HYPHEN_MINUS) {
                final BasicEditField bef = (BasicEditField) focusField;
                bef.clear(10);

                if (key != Characters.BACKSPACE) {
                    bef.setText(key + "");
                }

                bef.setDirty(true);

                return true;
            }
        }

        return super.keyChar(key, status, time);
    }

    /**
     * Prevent the save dialog from being displayed.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        return true;
    }
}
