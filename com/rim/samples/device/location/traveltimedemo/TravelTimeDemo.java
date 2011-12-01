/*
 * TravelTimeDemo.java
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

package com.rim.samples.device.traveltimedemo;

import java.util.Vector;

import javax.microedition.location.Coordinates;
import javax.microedition.location.Location;
import javax.microedition.location.LocationProvider;

import net.rim.device.api.i18n.DateFormat;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.lbs.maps.model.MapLocation;
import net.rim.device.api.lbs.maps.server.Geocoder;
import net.rim.device.api.lbs.maps.server.exchange.GeocodeExchange;
import net.rim.device.api.lbs.travel.TravelTime;
import net.rim.device.api.lbs.travel.TravelTimeEstimator;
import net.rim.device.api.system.Application;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * This application consists of a single screen which allows a user to enter a
 * destination address as a free-form string and then obtain the travel time
 * between the current location and the specified address.
 * 
 * The code demonstrates how to use the Travel Time API in conjunction with the
 * GPS Location API and the Geocoder API.
 * 
 * Note that to run this application in the simulator, you must tell the
 * simulator where it is by selecting 'Simulator > GPS Location' and entering a
 * latitude and longitude.
 */
public final class TravelTimeDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final TravelTimeDemo theApp = new TravelTimeDemo();
        theApp.enterEventDispatcher();
    }

    /**
     * Creates a new TravelTimeDemo object
     */
    public TravelTimeDemo() {
        pushScreen(new TravelTimeDemoScreen());
    }
}

/**
 * Main screen for the Travel Time Demo application
 */
final class TravelTimeDemoScreen extends MainScreen {
    private static final int DEPART_NOW = 0;
    private static final int DEPART_AT = 1;
    private static final int ARRIVE_AT = 2;

    private final LabelField _statusField;
    private final BasicEditField _addressField;
    private final LabelField _startLabel;
    private final LabelField _endLabel;
    private final LabelField _elapsedLabel;
    private final LabelField _distanceLabel;
    private final ObjectChoiceField _choiceField;
    private final DateField _dateField;

    /**
     * Creates a new TravelTimeDemoScreen
     */
    public TravelTimeDemoScreen() {
        setTitle("Travel Time Demo");

        // Initialize status field
        _statusField = new LabelField("");
        setStatus(_statusField);

        // Free form address entry field
        _addressField =
                new BasicEditField("Destination: ", "", 500,
                        TextField.NO_NEWLINE);
        add(_addressField);

        // Start/end time selection
        final VerticalFieldManager vfm = new VerticalFieldManager();
        _choiceField =
                new ObjectChoiceField("When:", new Object[] { "Depart Now",
                        "Depart At", "Arrive At" }, 0);
        _choiceField.setChangeListener(new FieldChangeListener() {
            /**
             * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field,
             *      int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (context == ChoiceField.CONTEXT_CHANGE_OPTION) {
                    final int idx = _choiceField.getSelectedIndex();
                    if (idx == DEPART_NOW) {
                        _dateField.setEnabled(false);
                    } else {
                        _dateField.setEnabled(true);
                        _dateField.setFocus();
                    }
                }
            }
        });
        vfm.add(_choiceField);

        final DateFormat dateFormat =
                DateFormat.getInstance(DateFormat.DATETIME_DEFAULT);
        _dateField = new DateField("", System.currentTimeMillis(), dateFormat);
        _dateField.setEnabled(false);
        vfm.add(_dateField);

        add(vfm);

        // Initialize a button for intitiating a travel time query
        final HorizontalFieldManager hfm =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        final ButtonField travelButton =
                new ButtonField("Get Travel Time", ButtonField.CONSUME_CLICK
                        | ButtonField.NEVER_DIRTY);
        hfm.add(travelButton);
        add(hfm);
        travelButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                findTravelTime();
            }
        });

        // Add labels to display travel time results
        _startLabel = new LabelField();
        add(_startLabel);
        _endLabel = new LabelField();
        add(_endLabel);
        _elapsedLabel = new LabelField();
        add(_elapsedLabel);
        _distanceLabel = new LabelField();
        add(_distanceLabel);
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Supress the save dialog
        return true;
    }

    /**
     * Performs the work of obtaining the starting and ending points and
     * requesting a travel time estimate.
     */
    private void findTravelTime() {
        // Ensure that an address has been entered
        final String address = _addressField.getText();
        if (address == null || address.length() == 0) {
            Dialog.alert("Address field cannot be empty");
            return;
        }

        // Clear the results fields
        _startLabel.setText("");
        _endLabel.setText("");
        _elapsedLabel.setText("");
        _distanceLabel.setText("");

        // Determine whether an arrival or departure estimate is desired and
        // read the appropriate time.
        final int choice = _choiceField.getSelectedIndex();
        final long startTime;
        final long endTime;

        if (choice == DEPART_NOW) {
            startTime = TravelTime.START_NOW;
            endTime = 0;
        } else if (choice == DEPART_AT) {
            startTime = _dateField.getDate();
            endTime = 0;
        } else {
            startTime = 0;
            endTime = _dateField.getDate();
        }

        // To obtain the starting point, we must obtain the latitude and
        // longitude corresponding to the specified address. This done using
        // the geocode() method of the Geocoder API. Since we are not passing
        // a callback object to this method this will be a blocking method and
        // cannot be called on the same thread as the event dispatcher (i.e. the
        // main thread). Similarly the GPS location is obtained using a blocking
        // method and cannot be called on the event dispatch thread. Therefore,
        // a separate thread is created to host the geocoding and GPS locating.
        // Since a separate thread is already being created, the synchronous
        // Travel Time API method is the most straightforward and appropriate to
        // use in this case.
        final Thread travelTimeThread = new Thread() {
            public void run() {
                try {
                    // Attempt to geocode the destination address
                    showStatus("Geocoding destination address...");
                    final GeocodeExchange ex =
                            Geocoder.getInstance().geocode(null, address, null,
                                    0);
                    final Vector results = ex.getResults();

                    if (results.size() == 0) {
                        throw new Exception("Could not geocode address");
                    }

                    final MapLocation mapLocation =
                            (MapLocation) results.elementAt(0);
                    final Coordinates endPoint =
                            new Coordinates(mapLocation.getLat(), mapLocation
                                    .getLon(), 0);

                    // Obtain the coordinates for the current location
                    showStatus("Finding current location...");
                    final LocationProvider provider =
                            LocationProvider.getInstance(null);
                    if (provider == null) {
                        throw new IllegalStateException(
                                "no LocationProvider available");
                    }
                    final Location location = provider.getLocation(-1);
                    final Coordinates startPoint =
                            location.getQualifiedCoordinates();

                    // Obtain the travel time between the two points
                    showStatus("Obtaining travel time estimate...");
                    final TravelTimeEstimator estimator =
                            TravelTimeEstimator.getInstance();
                    TravelTime travelTime;
                    if (endTime > 0) {
                        travelTime =
                                estimator.requestDepartureEstimate(startPoint,
                                        endPoint, endTime, null);
                    } else {
                        travelTime =
                                estimator.requestArrivalEstimate(startPoint,
                                        endPoint, startTime, null);
                    }
                    showStatus("Done");
                    showResults(travelTime);
                } catch (final Exception e) {
                    showStatus("");

                    // Use invokeLater() so that the dialog will be spawned
                    // on the event thread.
                    Application.getApplication().invokeLater(new Runnable() {
                        public void run() {
                            Dialog.alert(e.getClass().getName() + "\n\n"
                                    + e.getMessage());
                        }
                    });
                }
            }
        };
        travelTimeThread.start();
    }

    /**
     * Updates the status field
     * 
     * @param message
     *            Text to display
     */
    private void showStatus(final String message) {
        // Use invokeLaterAndWait() so that the update occurs on the event
        // thread
        Application.getApplication().invokeAndWait(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                _statusField.setText(message);
            }
        });
    }

    /**
     * Updates the results fields with results of a travel time query
     * 
     * @param travelTime
     *            A TravelTime object containing results of a query
     */
    private void showResults(final TravelTime travelTime) {
        // Use invokeLater() so that the update occurs on the event thread
        Application.getApplication().invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                // Convert form the elapsed time in milliseconds to an
                // hour:minute:seconds format.
                long value = travelTime.getElapsedTime() / 1000;
                final long seconds = value % 60;
                value /= 60;
                final long minutes = value % 60;
                final long hours = value / 60;

                final StringBuffer buffer = new StringBuffer();
                buffer.append(hours);
                buffer.append(':');

                if (minutes < 10) {
                    buffer.append('0');
                }

                buffer.append(minutes);
                buffer.append(':');

                if (seconds < 10) {
                    buffer.append('0');
                }

                buffer.append(seconds);

                final DateFormat dateFormatter =
                        new SimpleDateFormat(DateFormat.DATETIME_DEFAULT);

                String dateStr =
                        dateFormatter.formatLocal(travelTime.getStartTime());
                String msg = "Start Time: " + dateStr;
                _startLabel.setText(msg);

                dateStr = dateFormatter.formatLocal(travelTime.getEndTime());
                msg = "End Time: " + dateStr;
                _endLabel.setText(msg);

                msg = "Travel Time (h:m:s): " + buffer.toString();
                _elapsedLabel.setText(msg);

                // Convert the distance from meters to kilometers
                final double distance = travelTime.getDistance() / 1000.0;
                msg = "Distance (km): " + Double.toString(distance);
                _distanceLabel.setText(msg);
            }
        });
    }
}
