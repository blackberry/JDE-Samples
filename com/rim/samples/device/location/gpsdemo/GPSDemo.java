/**
 * GPSDemo.java
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

package com.rim.samples.device.gpsdemo;

import java.util.Vector;

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.gps.GPSInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringProvider;

/**
 * This application acts as a simple travel computer, recording route
 * coordinates, speed and altitude. Recording begins as soon as the application
 * is invoked.
 */
public class GPSDemo extends UiApplication {
    // Represents the number of updates over which altitude is calculated, in
    // seconds
    private static final int GRADE_INTERVAL = 5;

    // com.rim.samples.device.gpsdemo.GPSDemo.ID
    private static final long ID = 0x5d459971bb15ae7aL;

    // Represents period of the position query, in seconds
    private static int _interval = 1;

    private static Vector _previousPoints;
    private static float[] _altitudes;
    private static float[] _horizontalDistances;
    private static PersistentObject _store;

    // Initialize or reload the persisted WayPoints
    static {
        _store = PersistentStore.getPersistentObject(ID);

        if (_store.getContents() == null) {
            _previousPoints = new Vector();
            _store.setContents(_previousPoints);
        }

        _previousPoints = (Vector) _store.getContents();
    }

    private long _startTime;
    private float _wayHorizontalDistance;
    private float _horizontalDistance;
    private float _verticalDistance;
    private double _latitude;
    private double _longitude;
    private final EditField _status;
    private LocationProvider _locationProvider;
    private final GPSDemoScreen _screen;

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new GPSDemo().enterEventDispatcher();
    }

    /**
     * Create a new GPSDemo object
     */
    public GPSDemo() {
        // Used by WayPoints, represents the time since the last waypoint
        _startTime = System.currentTimeMillis();

        _altitudes = new float[GRADE_INTERVAL];
        _horizontalDistances = new float[GRADE_INTERVAL];

        _screen = new GPSDemoScreen();
        _screen.setTitle("GPS Demo");

        _status = new EditField(Field.NON_FOCUSABLE);
        _screen.add(_status);

        // Attempt to start the location listening thread
        if (startLocationUpdate()) {
            _screen.setState(_locationProvider.getState());
        }

        // Render the screen
        pushScreen(_screen);
    }

    /**
     * Update the GUI with the data just received
     * 
     * @param msg
     *            The message to display
     */
    private void updateLocationScreen(final String msg) {
        invokeLater(new Runnable() {
            public void run() {
                _status.setText(msg);
            }
        });
    }

    /**
     * Invokes the Location API with Standalone criteria
     * 
     * @return True if the <code>LocationProvider</code> was successfully
     *         started, false otherwise
     */
    private boolean startLocationUpdate() {
        boolean returnValue = false;

        if (!(GPSInfo.getDefaultGPSMode() == GPSInfo.GPS_MODE_NONE)) {
            try {
                final Criteria criteria = new Criteria();
                criteria.setCostAllowed(false);

                _locationProvider = LocationProvider.getInstance(criteria);

                if (_locationProvider != null) {
                    /*
                     * Only a single listener can be associated with a provider,
                     * and unsetting it involves the same call but with null.
                     * Therefore, there is no need to cache the listener
                     * instance request an update every second.
                     */
                    _locationProvider.setLocationListener(
                            new LocationListenerImpl(), _interval, -1, -1);
                    returnValue = true;
                } else {
                    invokeLater(new Runnable() {
                        public void run() {
                            Dialog.alert("Failed to obtain a location provider, exiting...");
                            System.exit(0);
                        }
                    });
                }

            } catch (final LocationException le) {
                invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert("Failed to instantiate LocationProvider object, exiting..."
                                + le.toString());
                        System.exit(0);
                    }
                });
            }
        } else {
            invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert("GPS is not supported on this device, exiting...");
                    System.exit(0);
                }
            });
        }

        return returnValue;
    }

    /**
     * Marks a WayPoint in the persistent store. Calculations are based on all
     * data collected since the previous way point, or from the start of the
     * application if no previous WayPoints exist.
     */
    private void markPoint() {
        final long current = System.currentTimeMillis();
        final WayPoint point =
                new WayPoint(_latitude, _longitude, _startTime, current,
                        _wayHorizontalDistance, _verticalDistance);

        addWayPoint(point);

        // Reset the WayPoint variables
        _startTime = current;
        _wayHorizontalDistance = 0;
        _verticalDistance = 0;
    }

    /**
     * View the various saved WayPoints
     */
    private void viewPreviousPoints() {
        final PointScreen pointScreen = new PointScreen(_previousPoints);
        pushScreen(pointScreen);
    }

    /**
     * Adds a new WayPoint and commits the set of saved waypoints to persistent
     * store.
     * 
     * @param point
     *            The WayPoint to add
     */
    private synchronized static void addWayPoint(final WayPoint point) {
        _previousPoints.addElement(point);
        commit();
    }

    /**
     * Removes a WayPoint from the set of saved points and commits the modifed
     * set to persistent store
     * 
     * @param point
     *            The WayPoint to remove
     */
    synchronized static void removeWayPoint(final WayPoint point) {
        _previousPoints.removeElement(point);
        commit();
    }

    /**
     * Commits the WayPoint set to persistent store
     */
    private static void commit() {
        _store.setContents(_previousPoints);
        _store.commit();
    }

    /**
     * Implementation of the LocationListener interface. Listens for updates to
     * the device location and displays the results.
     */
    private class LocationListenerImpl implements LocationListener {

        /**
         * @see javax.microedition.location.LocationListener#locationUpdated(LocationProvider,Location)
         */
        public void locationUpdated(final LocationProvider provider,
                final Location location) {
            if (location.isValid()) {
                final float heading = location.getCourse();
                _longitude = location.getQualifiedCoordinates().getLongitude();
                _latitude = location.getQualifiedCoordinates().getLatitude();
                final float altitude =
                        location.getQualifiedCoordinates().getAltitude();
                final float speed = location.getSpeed();

                // Horizontal distance for current Location
                final float horizontalDistance = speed * _interval;
                _horizontalDistance += horizontalDistance;

                // Horizontal distance for WayPoint
                _wayHorizontalDistance += horizontalDistance;

                // Distance over the current interval
                float totalDist = 0;

                // Moving average grade
                for (int i = 0; i < GRADE_INTERVAL - 1; ++i) {
                    _altitudes[i] = _altitudes[i + 1];
                    _horizontalDistances[i] = _horizontalDistances[i + 1];
                    totalDist = totalDist + _horizontalDistances[i];
                }

                _altitudes[GRADE_INTERVAL - 1] = altitude;
                _horizontalDistances[GRADE_INTERVAL - 1] = speed * _interval;
                totalDist =
                        totalDist + _horizontalDistances[GRADE_INTERVAL - 1];
                final float grade =
                        totalDist == 0.0F ? Float.NaN
                                : (_altitudes[4] - _altitudes[0]) * 100
                                        / totalDist;

                // Running total of the vertical distance gain
                final float altGain =
                        _altitudes[GRADE_INTERVAL - 1]
                                - _altitudes[GRADE_INTERVAL - 2];

                if (altGain > 0) {
                    _verticalDistance = _verticalDistance + altGain;
                }

                // Information to be displayed on the device
                final StringBuffer sb = new StringBuffer();
                sb.append("Longitude: ");
                sb.append(_longitude);
                sb.append("\n");
                sb.append("Latitude: ");
                sb.append(_latitude);
                sb.append("\n");
                sb.append("Altitude: ");
                sb.append(altitude);
                sb.append(" m");
                sb.append("\n");
                sb.append("Heading relative to true north: ");
                sb.append(heading);
                sb.append("\n");
                sb.append("Speed : ");
                sb.append(speed);
                sb.append(" m/s");
                sb.append("\n");
                sb.append("Grade : ");
                if (Float.isNaN(grade)) {
                    sb.append(" Not available");
                } else {
                    sb.append(grade + " %");
                }

                GPSDemo.this.updateLocationScreen(sb.toString());
            }
        }

        /**
         * @see javax.microedition.location.LocationListener#providerStateChanged(LocationProvider,
         *      int)
         */
        public void providerStateChanged(final LocationProvider provider,
                final int newState) {
            if (newState == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                provider.reset();
            }
            _screen.setState(newState);
        }
    }

    /**
     * The main screen to display the current GPS information
     */
    private final class GPSDemoScreen extends MainScreen {
        TextField _statusTextField;

        /**
         * Create a new GPSDemoScreen object
         */
        GPSDemoScreen() {
            // Initialize UI
            _statusTextField = new TextField(Field.NON_FOCUSABLE);
            _statusTextField.setLabel("GPS Status: ");
            add(_statusTextField);
            final RichTextField instructions =
                    new RichTextField("Waiting for location update...",
                            Field.NON_FOCUSABLE);
            add(instructions);

            // Menu Item to add the current location to the list of WayPoints
            final MenuItem markWayPoint =
                    new MenuItem(new StringProvider("Mark waypoint"), 0x230010,
                            0);
            markWayPoint.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    GPSDemo.this.markPoint();
                }
            }));

            // Menu Item to view the marked WayPoints
            final MenuItem viewWayPoints =
                    new MenuItem(new StringProvider("View waypoints"),
                            0x230020, 1);
            viewWayPoints.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    GPSDemo.this.viewPreviousPoints();
                }
            }));
            addMenuItem(markWayPoint);
            addMenuItem(viewWayPoints);
        }

        /**
         * Display the state of the GPS service
         * 
         * @param newState
         *            The state to display
         */
        public void setState(final int newState) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    switch (newState) {
                    case LocationProvider.AVAILABLE:
                        _statusTextField.setText("Available");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        _statusTextField.setText("Out of Service");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        _statusTextField.setText("Temporarily Unavailable");
                        break;
                    }
                }
            });
        }

        /**
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            if (_locationProvider != null) {
                _locationProvider.reset();
                _locationProvider.setLocationListener(null, -1, -1, -1);
            }

            super.close();
        }
    }

    /**
     * A class to represent a way point, a marker on a journey or a point of
     * interest.
     */
    static class WayPoint implements Persistable {
        long _startTime;
        long _endTime;
        double _latitude;
        double _longitude;
        float _distance;
        float _verticalDistance;

        /**
         * Creates a new WayPoint object
         * 
         * @param latitude
         *            Latitude of waypoint
         * @param longitude
         *            Longitude of waypoint
         * @param startTime
         *            Time at start of update
         * @param endTime
         *            Time at end of update
         * @param distance
         *            Distance travelled
         * @param verticalDistance
         *            Vertical distance travelled
         */
        WayPoint(final double latitude, final double longitude,
                final long startTime, final long endTime, final float distance,
                final float verticalDistance) {
            _startTime = startTime;
            _endTime = endTime;
            _distance = distance;
            _verticalDistance = verticalDistance;
            _latitude = latitude;
            _longitude = longitude;
        }
    }
}
