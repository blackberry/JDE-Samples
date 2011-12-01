/**
 *  GPSDemo.java
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Persistable;

/**
 * This application acts as a simple travel computer, recording route
 * coordinates, speed and altitude. Recording begins as soon as the application
 * is invoked. When using the BlackBerry Smartphone Simulator you will need to
 * select 'GPS location' from the 'Simulate' menu and specify GPS locations or
 * generate a route. The server application included in the JDE will use the
 * co-ordinates it recieves to construct route, speed and altitude plots. These
 * plots will be saved as JPEG files in the 'samples' directory of the JDE
 * installation.
 */
public class GPSDemo extends UiApplication {

    // Constants
    // -----------------------------------------------------------------------------------------------------------------
    private static final int GRADE_INTERVAL = 5; // Seconds - represents the
                                                 // number of updates over which
                                                 // alt is calculated.
    private static final long ID = 0x5d459971bb15ae7aL; // com.rim.samples.device.gpsdemo.GPSDemo.ID
    private static final int CAPTURE_INTERVAL = 5; // We record a location every
                                                   // 5 seconds.
    private static final int SENDING_INTERVAL = 30; // The interval in seconds
                                                    // after which the
                                                    // information is sent to
                                                    // the server.

    // When running this application, select options from the menu and replace
    // <server name here>
    // with the name of the computer which is running the GPSServer application
    // found in
    // com.rim.samples.server, typically the local machine. Alternatively, the
    // _hostName variable
    // can be hard-coded below with no need to further modify the server name
    // while running the application.

    private static String _hostName = "<server name here>:5555"; // E.g
                                                                 // "ComputerName.DomainName:5555".
    private static int _interval = 1; // Seconds - this is the period of
                                      // position query.
    private static Vector _previousPoints;
    private static float[] _altitudes;
    private static float[] _horizontalDistances;
    private static PersistentObject _store;

    /**
     * Initialize or reload our persistent store.
     */
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
    private ListField _listField;
    private final EditField _status;
    private final StringBuffer _messageString;
    private String _oldmessageString;
    private LocationProvider _locationProvider;
    private ServerConnectThread _serverConnectThread;

    /**
     * Instantiate the new application object and enter the event loop.
     * 
     * @param args
     *            No args are supported for this application.
     */
    public static void main(final String[] args) {
        new GPSDemo().enterEventDispatcher();
    }

    // Constructor
    public GPSDemo() {
        // Used by waypoints, represents the time since the last waypoint.
        _startTime = System.currentTimeMillis();
        _altitudes = new float[GRADE_INTERVAL];
        _horizontalDistances = new float[GRADE_INTERVAL];
        _messageString = new StringBuffer();

        final GPSDemoScreen screen = new GPSDemoScreen();
        screen.setTitle(new LabelField("GPS Demo", Field.USE_ALL_WIDTH));

        _status = new EditField();
        screen.add(_status);

        // Try to start the GPS thread that listens for updates.
        if (startLocationUpdate()) {
            // If successful, start the thread that communicates with the
            // server.
            startServerConnectionThread();
        }

        // Render our screen.
        pushScreen(screen);
    }

    /**
     * Update the GUI with the data just received.
     */
    private void updateLocationScreen(final String msg) {
        invokeLater(new Runnable() {
            public void run() {
                _status.setText(msg);
            }
        });
    }

    // Menu items
    // --------------------------------------------------------------------------------------------

    // Cache the markwaypoint menu item for reuse.
    private final MenuItem _markWayPoint = new MenuItem("Mark Waypoint", 110,
            10) {
        public void run() {
            GPSDemo.this.markPoint();
        }
    };

    // Cache the view waypoints menu item for reuse.
    private final MenuItem _viewWayPoints = new MenuItem("View Waypoints", 110,
            10) {
        public void run() {
            GPSDemo.this.viewPreviousPoints();
        }
    };

    // Cache the options menu item for reuse.
    private final MenuItem _options = new MenuItem("Options", 110, 10) {
        public void run() {
            GPSDemo.this.viewOptions();
        }
    };

    // Cache the options menu item for reuse.
    private final MenuItem _close = new MenuItem("Close", 110, 10) {
        public void run() {
            System.exit(0);
        }
    };

    /**
     * Invokes the Location API with the default criteria.
     * 
     * @return True if the Location Provider was successfully started; false
     *         otherwise.
     */
    private boolean startLocationUpdate() {
        boolean retval = false;

        try {
            _locationProvider = LocationProvider.getInstance(null);

            if (_locationProvider == null) {
                // We would like to display a dialog box indicating that GPS
                // isn't supported,
                // but because the event-dispatcher thread hasn't been started
                // yet, modal
                // screens cannot be pushed onto the display stack. So delay
                // this operation
                // until the event-dispatcher thread is running by asking it to
                // invoke the
                // following Runnable object as soon as it can.
                final Runnable showGpsUnsupportedDialog = new Runnable() {
                    public void run() {
                        Dialog.alert("GPS is not supported on this platform, exiting...");
                        System.exit(1);
                    }
                };

                invokeLater(showGpsUnsupportedDialog); // Ask event-dispatcher
                                                       // thread to display
                                                       // dialog ASAP.
            } else {
                // Only a single listener can be associated with a provider, and
                // unsetting it
                // involves the same call but with null, therefore, no need to
                // cache the listener
                // instance request an update every second.
                _locationProvider.setLocationListener(
                        new LocationListenerImpl(), _interval, 1, 1);
                retval = true;
            }
        } catch (final LocationException le) {
            System.err
                    .println("Failed to instantiate the LocationProvider object, exiting...");
            System.err.println(le);
            System.exit(0);
        }
        return retval;
    }

    /**
     * Invokes a separate thread used to send data to the server.
     */
    private void startServerConnectionThread() {
        _serverConnectThread = new ServerConnectThread(_hostName);
        _serverConnectThread.start();
    }

    /**
     * Marks a point in the persistent store, calculations are based on all data
     * collected since the previous way point, or from the start of the
     * application of no previous waypoints exist.
     */
    private void markPoint() {
        final long current = System.currentTimeMillis();
        final WayPoint p =
                new WayPoint(_startTime, current, _wayHorizontalDistance,
                        _verticalDistance);

        addWayPoint(p);

        // Reset the waypoint vars.
        _startTime = current;
        _wayHorizontalDistance = 0;
        _verticalDistance = 0;
    }

    /**
     * View the various options for this application.
     */
    public void viewOptions() {
        final OptionScreen optionScreen = new OptionScreen();
        pushScreen(optionScreen);
    }

    /**
     * View the various saved waypoints.
     */
    private void viewPreviousPoints() {
        final PointScreen pointScreen = new PointScreen(_previousPoints);
        pushScreen(pointScreen);
    }

    /**
     * Adds a new WayPoint and commits the set of saved waypoints to flash.
     * 
     * @param p
     *            The point to add.
     */
    /* package */synchronized static void addWayPoint(final WayPoint p) {
        _previousPoints.addElement(p);
        commit();
    }

    /**
     * Removes a waypoint from the set of saved points and commits the modifed
     * set to flash.
     * 
     * @param p
     *            The point to remove.
     */
    /* package */synchronized static void removeWayPoint(final WayPoint p) {
        _previousPoints.removeElement(p);
        commit();
    }

    /**
     * Commit the waypoint set to flash.
     */
    private static void commit() {
        _store.setContents(_previousPoints);
        _store.commit();
    }

    /**
     * Rounds off a given double to the provided number of decimal places.
     * 
     * @param d
     *            The double to round off.
     * @param decimal
     *            The number of decimal places to retain.
     * @return A double with the number of decimal places specified.
     */
    private static double round(final double d, int decimal) {
        double powerOfTen = 1;

        while (decimal-- > 0) {
            powerOfTen *= 10.0;
        }

        final double d1 = d * powerOfTen;
        final int d1asint = (int) d1; // Clip the decimal portion away and cache
                                      // the cast, this is a costly
                                      // transformation.
        final double d2 = d1 - d1asint; // Get the remainder of the double.

        // Is the remainder > 0.5? if so, round up, otherwise round down (lump
        // in .5 with > case for simplicity).
        return d2 >= 0.5 ? (d1asint + 1) / powerOfTen : d1asint / powerOfTen;
    }

    /**
     * Implementation of the LocationListener interface.
     */
    private class LocationListenerImpl implements LocationListener {
        // Members
        // ----------------------------------------------------------------------------------------------
        private int captureCount;
        private int sendCount;

        // Methods
        // ----------------------------------------------------------------------------------------------
        /**
         * @see javax.microedition.location.LocationListener#locationUpdated(LocationProvider,Location)
         */
        public void locationUpdated(final LocationProvider provider,
                final Location location) {
            if (location.isValid()) {
                final float heading = location.getCourse();
                final double longitude =
                        location.getQualifiedCoordinates().getLongitude();
                final double latitude =
                        location.getQualifiedCoordinates().getLatitude();
                final float altitude =
                        location.getQualifiedCoordinates().getAltitude();
                final float speed = location.getSpeed();

                // Horizontal distance to send to server.
                final float horizontalDistance = speed * _interval;
                _horizontalDistance += horizontalDistance;

                // Horizontal distance for this waypoint.
                _wayHorizontalDistance += horizontalDistance;

                // Distance over the current interval.
                float totalDist = 0;

                // Moving average grade.
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

                // Running total of the vertical distance gain.
                final float altGain =
                        _altitudes[GRADE_INTERVAL - 1]
                                - _altitudes[GRADE_INTERVAL - 2];

                if (altGain > 0) {
                    _verticalDistance = _verticalDistance + altGain;
                }

                captureCount += _interval;

                // If we're mod zero then it's time to record this data.
                captureCount %= CAPTURE_INTERVAL;

                // Information to be sent to the server.
                if (captureCount == 0) {
                    // Minimize garbage creation by appending only character
                    // primitives, no extra String objects created that way.
                    _messageString.append(round(longitude, 4));
                    _messageString.append(';');
                    _messageString.append(round(latitude, 4));
                    _messageString.append(';');
                    _messageString.append(round(altitude, 2));
                    _messageString.append(';');
                    _messageString.append(_horizontalDistance);
                    _messageString.append(';');
                    _messageString.append(round(speed, 2));
                    _messageString.append(';');
                    _messageString.append(System.currentTimeMillis());
                    _messageString.append(':');
                    sendCount += CAPTURE_INTERVAL;
                    _horizontalDistance = 0;
                }

                // If we're mod zero then it's time to send.
                sendCount %= SENDING_INTERVAL;

                synchronized (this) {
                    if (sendCount == 0 && _messageString.length() != 0) {
                        _serverConnectThread.sendUpdate(_messageString
                                .toString());
                        _messageString.setLength(0);
                    }
                }

                // Information to be displayed on the device.
                final StringBuffer sb = new StringBuffer();
                sb.append("Longitude: ");
                sb.append(longitude);
                sb.append("\n");
                sb.append("Latitude: ");
                sb.append(latitude);
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

        public void providerStateChanged(final LocationProvider provider,
                final int newState) {
            // Not implemented.
        }
    }

    private final class GPSDemoScreen extends MainScreen {

        // Constructor
        public GPSDemoScreen() {
            final RichTextField instructions =
                    new RichTextField("Waiting for location update...");
            this.add(instructions);

            addMenuItem(_markWayPoint);
            addMenuItem(_viewWayPoints);
            addMenuItem(_options);
        }

        /**
         * @see net.rim.device.api.ui.Screen#close()
         */
        public void close() {
            if (_locationProvider != null) {
                _locationProvider.reset();
                _locationProvider.setLocationListener(null, -1, -1, -1);
            }
            if (_serverConnectThread != null) {
                _serverConnectThread.stop();
            }

            super.close();
        }
    }

    /**
     * Options for the application.
     */
    private class OptionScreen extends MainScreen {
        private final BasicEditField _serverAddressField;

        public OptionScreen() {
            super();

            final LabelField title =
                    new LabelField("Options", DrawStyle.ELLIPSIS
                            | Field.USE_ALL_WIDTH);
            setTitle(title);

            _serverAddressField =
                    new BasicEditField("Server Address: ", GPSDemo._hostName,
                            128, Field.EDITABLE);
            add(_serverAddressField);

            addMenuItem(_save);
            addMenuItem(_cancel);
        }

        private final MenuItem _cancel = new MenuItem("Cancel", 300000, 10) {
            public void run() {
                OptionScreen.this.close();
            }
        };

        private final MenuItem _save = new MenuItem("Save", 200000, 10) {
            public void run() {
                final String hostname = _serverAddressField.getText();
                GPSDemo._hostName = hostname;
                GPSDemo.this._serverConnectThread.setHost(hostname);
                GPSDemo.this.popScreen(OptionScreen.this);
            }
        };

    }

    /**
     * WayPoint describes a way point, a marker on a journey or point of
     * interest. WayPoints are persistable.
     * 
     * package
     */
    static class WayPoint implements Persistable {
        public long _startTime;
        public long _endTime;
        public float _distance;
        public float _verticalDistance;

        public WayPoint(final long startTime, final long endTime,
                final float distance, final float verticalDistance) {
            _startTime = startTime;
            _endTime = endTime;
            _distance = distance;
            _verticalDistance = verticalDistance;
        }
    }

    /**
     * ServerConnectThread is responsible for sending data periodically to the
     * server. This thread runs for the lifetime of the application instance,
     * data is queued up for send. Failed transactions are retried at the next
     * send period.
     */
    private final class ServerConnectThread extends Thread {
        // Members
        // ------------------------------------------------------------------------------------------------

        private InputStreamReader _in;
        private OutputStreamWriter _out;
        private String _url;
        private String _message;
        private boolean _go;
        private final Vector _data;
        private int _delay;
        static private final int DEFAULT = 5000; // Retry delay for
                                                 // communication with the
                                                 // server after a failed
                                                 // attempt.

        public ServerConnectThread(final String host) {
            setHost(host);
            _go = true;
            _data = new Vector();
            _delay = DEFAULT; // 5 second backoff to start.
        }

        public synchronized void setHost(final String host) {
            _url = "socket://" + host; // + ";deviceside=false"; // Don't use
                                       // direct TCP.
            this.notify(); // Notify the thread method so that any pending data
                           // can be resent.
        }

        public synchronized void stop() {
            _go = false;
        }

        public synchronized void sendUpdate(final String data) {
            _data.addElement(data);
            this.notify();
        }

        private int increaseDelay(final int delay) {
            if (delay >= 24 * 3600 * 1000) {
                return 24 * 3600 * 1000; // 24 hr max backoff.
            } else {
                return delay << 1; // Otherwise just double the current delay.
            }
        }

        public void run() {
            boolean error = false;

            while (_go) {
                String data = null;
                synchronized (this) {
                    if (_data.size() == 0 || error) {
                        try {
                            this.wait(_delay);
                        } catch (final InterruptedException e) {
                        }
                    }

                    if (!_go) {
                        break; // Check for exit status.
                    }

                    if (_data.size() == 0) {
                        continue; // Nothing to do.
                    }

                    data = (String) _data.elementAt(0); // Pop the first
                                                        // element.
                    _data.removeElement(data);
                }

                StreamConnection connection = null;

                try {
                    connection =
                            (StreamConnection) Connector.open(_url,
                                    Connector.READ_WRITE, false);
                    _in = new InputStreamReader(connection.openInputStream());
                    _out =
                            new OutputStreamWriter(connection
                                    .openOutputStream());

                    // Write the data to the server.
                    _out.write(data, 0, data.length());
                    _out.write('z'); // Write a terminator.

                    // Wait for an acknowledgement, in this case an 'R'
                    // character, for 'Received'.
                    final char c = (char) _in.read();

                    // Debug
                    System.out
                            .println("GPSDemo: Debug: exchange(): received acknowledgement char:"
                                    + c);
                    _delay = DEFAULT; // Reset the backoff delay .
                    error = false; // Clear any error.
                } catch (final IOException e) {
                    error = true;
                    _delay = increaseDelay(_delay);
                    GPSDemo.this.updateLocationScreen(e.toString());

                    // Push this data back on the stack, it's still pending.
                    synchronized (this) {
                        _data.insertElementAt(data, 0);
                    }
                } finally {
                    try {
                        if (_in != null) {
                            _in.close();
                        }
                        if (_out != null) {
                            _out.close();
                        }
                        if (connection != null) {
                            connection.close();
                        }
                    } catch (final IOException ioe) {
                        // No-op - we don't care on close.
                    }
                }
            }
        }
    }
}
