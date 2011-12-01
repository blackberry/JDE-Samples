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

import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Landmark;
import javax.microedition.location.LocationProvider;

import net.rim.device.api.lbs.Locator;
import net.rim.device.api.lbs.travel.TravelTime;
import net.rim.device.api.lbs.travel.TravelTimeEstimator;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This application consists of a single screen which allows a user to enter an
 * address as a free-form string and then obtain the travel time between the
 * users's current location and the specified address.
 * 
 * The code demonstrates how to use the Travel Time API in conjunction with the
 * GPS Location API and the Locator geocoding service.
 * 
 * Note that to run this application in a BlackBerry Smartphone simulator, you
 * need to simulate a GPS location by selecting 'Simulator > GPS Location' and
 * specifying latitude and longitude coordinates.
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
        // Render the screen
        pushScreen(new TravelTimeScreen());
    }
}

/**
 * Main screen for the application
 */
final class TravelTimeScreen extends MainScreen {
    private final LabelField _statusField;
    private final BasicEditField _addressField;
    private final LabelField _timeLabel;
    private final LabelField _distanceLabel;

    /**
     * Creates a new TravelTimeScreen object
     */
    public TravelTimeScreen() {
        super(DEFAULT_CLOSE | DEFAULT_MENU);

        setTitle(new LabelField("Travel Time Demo", Field.USE_ALL_WIDTH
                | DrawStyle.HCENTER));

        _statusField =
                new LabelField("", Field.USE_ALL_WIDTH | DrawStyle.HCENTER);
        _statusField.setPadding(3, 0, 3, 0);
        setStatus(_statusField);

        // Add a BasicEditField for text entry
        _addressField =
                new BasicEditField("Address: ", "", 500, TextField.NO_NEWLINE);
        _addressField.setPadding(5, 5, 10, 5);
        add(_addressField);

        // Add a button for initiating travel time query
        final ButtonField travelButton =
                new ButtonField("Get Travel Time", ButtonField.CONSUME_CLICK);
        travelButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field arg0, final int arg1) {
                findTravelTime();
            }
        });
        add(travelButton);

        // Add labels to display travel time results
        _timeLabel = new LabelField();
        _timeLabel.setPadding(10, 5, 0, 5);
        add(_timeLabel);
        _distanceLabel = new LabelField();
        _distanceLabel.setPadding(5, 5, 0, 5);
        add(_distanceLabel);
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        // Suppress the save dialog
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
        _timeLabel.setText("");
        _distanceLabel.setText("");

        // To obtain the starting point, the Locator API is called via the
        // geocode() method, returning latitude and longitude. This is a
        // blocking method. Similarly, the GPS location is obtained using
        // another blocking method (LocationProvider.getLocation()) and
        // cannot be called on the event dispatch thread. Therefore, a
        // separate thread (travelTimeThread) is created to host these
        // requests.
        final Thread travelTimeThread = new Thread() {
            public void run() {
                try {
                    // Obtain the coordinates for the current location
                    showStatus("Finding current location...");

                    // Set an autonomous criteria
                    final Criteria autoCriteria = new Criteria();
                    autoCriteria.setCostAllowed(false);
                    final LocationProvider provider =
                            LocationProvider.getInstance(autoCriteria);

                    if (provider == null) {
                        throw new IllegalStateException(
                                "no LocationProvider available");
                    }

                    final int timeout = 300;
                    final Coordinates startPoint =
                            provider.getLocation(timeout)
                                    .getQualifiedCoordinates();

                    // Obtain the coordinates corresponding to
                    // the destination address.
                    showStatus("Finding address coordinates...");
                    final Landmark[] landmarks =
                            Locator.geocode(address.replace('\n', ' '), null);
                    if (landmarks == null) {
                        throw new Exception("Locator could not geocode address");
                    }
                    final Coordinates endPoint =
                            landmarks[0].getQualifiedCoordinates();

                    // Obtain the travel time between the two points.
                    showStatus("Obtaining travel time estimate...");
                    final TravelTimeEstimator estimator =
                            TravelTimeEstimator.getInstance();

                    // The requestArrivalEstimate() method is synchronous, but
                    // we use it as we are
                    // already executing in a separate thread. An asynchronous
                    // implementation would
                    // also specify a TimeTravelListener in the method call to
                    // respond when the
                    // estimate was received.
                    final TravelTime travelTime =
                            estimator.requestArrivalEstimate(startPoint,
                                    endPoint, TravelTime.START_NOW, null);
                    showStatus("Finished travel time request");
                    showResults(travelTime);
                } catch (final Exception e) {
                    showStatus(e.getClass().getName() + "\n\n" + e.getMessage());
                }
            }
        };
        travelTimeThread.start();
    }

    /**
     * Set the text of the status field.
     * 
     * @param message
     *            The status to be displayed
     */
    private void showStatus(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                TravelTimeScreen.this._statusField.setText(message);
            }
        });
    }

    /**
     * Update the GUI with the travel time data
     * 
     * @param travelTime
     *            The <code>TravelTime</code> object to display information for
     */
    private void showResults(final TravelTime travelTime) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                // Convert form the elapsed time in milliseconds to an
                // hour:minute:seconds format.
                long value = travelTime.getElapsedTime() / 1000;
                final long seconds = value % 60;
                value /= 60;
                final long minutes = value % 60;
                final long hours = value / 60;

                final StringBuffer buffer =
                        new StringBuffer("Travel Time (h:m:s): ");
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

                TravelTimeScreen.this._timeLabel.setText(buffer.toString());

                // Convert the distance from meters to kilometers
                final double distance = travelTime.getDistance() / 1000.0;

                TravelTimeScreen.this._distanceLabel.setText("Distance (km): "
                        + Double.toString(distance));
            }
        });
    }
}
