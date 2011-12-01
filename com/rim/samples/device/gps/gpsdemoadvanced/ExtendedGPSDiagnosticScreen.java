/**
 * ExtendedGPSDiagnosticScreen.java
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

package com.rim.samples.device.gpsdemoadvanced;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.gps.BlackBerryCriteria;
import net.rim.device.api.gps.BlackBerryLocation;
import net.rim.device.api.gps.BlackBerryLocationProvider;
import net.rim.device.api.gps.GPSInfo;
import net.rim.device.api.gps.GPSSettings;
import net.rim.device.api.gps.LocationInfo;
import net.rim.device.api.i18n.SimpleDateFormat;
import net.rim.device.api.io.LineReader;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.EventLogger;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * Tests the use of the extended APIs included in the net.rim.device.api.gps
 * package.
 * 
 * There are several available modes the user can choose. (m) stands for
 * 'multiple fix' and (s) stands for 'single fix'
 * 
 * In Smart Mode the application operates in MS-Based mode but falls back to
 * MS-Assisted for a single fix if the LocationProvider is unable to return a
 * valid fix in (maxInvalidTime) seconds. The provider then goes back to
 * MS-Based again.
 * 
 * For any assisted modes (all those except Stand Alone or Cellsite), additional
 * helper data from a PDE server is required.
 */
public class ExtendedGPSDiagnosticScreen extends MainScreen implements
        FieldChangeListener {
    // Provides a reference to the UIApplication for faster updating
    private final UiApplication _uiApp;

    // TextField to show the logs
    private TextField _log;

    // The available modes the user can choose
    private ObjectChoiceField _primaryModeField;

    // Checkbox to enable geolocation falback
    private CheckboxField _enableGeolocationFallbackField;

    // Checkbox to enable concurrent geolocation
    private CheckboxField _enableConcurrentGeolocationField;

    // Check box to indicate whether location should be displayed on a map
    private CheckboxField _isMapLocationField;

    // Check box to specify whether to set gpsRestartInterval
    private CheckboxField _useGPSRestartIntervalField;

    // Check box to specify whether detailed satellite info is required
    private CheckboxField _isSatelliteInfoRequiredField;

    // Indicates if the device is on Verizon. If so, app credentials will be set
    // instead of PDE IP and Port.
    private CheckboxField _isVerizonField;

    // Field to choose a failover mode
    private ObjectChoiceField _failOverModeField;

    // Field to choose a mode to be used after the first fix
    private ObjectChoiceField _subsequentModeField;

    // Field to specify the zoom level used in the BlackBerry Maps application
    private BasicEditField _zoomLevelField;

    // Field to enter the preferred response time
    private BasicEditField _preferredResponseTimeField;

    // Field to enter the interval parameter for a LocationListener
    private BasicEditField _frequencyField;

    // Field to enter the timeout parameter for a LocationListener
    private BasicEditField _timeoutField;

    // Field to enter the maximum age of the returned location in seconds
    private BasicEditField _maxAgeField;

    // Field to enter the maximum number of GPS retries (using the selected
    // mode)
    // before a failover occurs.
    private BasicEditField _failOverRetriesField;

    // Field to enter the maximum wait time (in seconds) to get a fix before a
    // failover occurs
    private BasicEditField _failoverTimeoutField;

    // Field to enter the time (in seconds) the JSR179 extension will wait
    // before
    // automatically restarting GPS.
    private BasicEditField _gpsRestartIntervalField;

    // Field to enter the maximum number of GPS restarts
    private BasicEditField _gpsRestartRetriesField;

    // Field to enter the PDE IP
    private BasicEditField _pdeIPField;

    // Field to enter the PDE port
    private BasicEditField _pdePortField;

    // Displays the supported location data sources for the device
    private BasicEditField _supportedSourcesField;

    // Displays the available location data sources of the device
    private BasicEditField _availableSourcesField;

    // Displays the GPS mode used for the current location
    private BasicEditField _currentModeField;

    // Displays the number of satellites used to compute the current fix
    private BasicEditField _currentSatellitesCountField;

    // Displays the location information for the current fix
    private EditField _currentLocationField;

    // Displays the average satellite signal quality of the current fix
    private EditField _currentAverageSatelliteSignalField;

    // Displays the DataSource used to get the current fix
    private EditField _currentDataSourceField;

    // Displays an error for the current GPS fix
    private EditField _currentErrorField;

    // Displays the status of the current fix
    private EditField _currentStatusField;

    // Displays the total number of updates that have occurred
    private EditField _numberUpdatesField;

    // Displays the total number of MS-Assisted updates that have occurred
    private EditField _numberAssistedUpdatesField;

    // Displays the total number of MS-Based updates that have occurred
    private EditField _numberUnassistedUpdatesField;

    // Displays the total number of valid updates that have occurred
    private EditField _numberValidUpdatesField;

    // Displays the total number of invalid updates that have occurred
    private EditField _numberInvalidUpdatesField;

    // This Thread performs all location related work
    private LocationThread _locThread;

    // Flag indicating an immediate reset is required due to
    // TEMPORARILY_UNAVAILABLE event
    private boolean _resetNow;

    // A menu item for stopping a running test
    private final MenuItem _stopTestItem;

    private boolean _PDESet;

    /**
     * Creates a new ExtendedGPSDiagnosticScreen object
     */
    public ExtendedGPSDiagnosticScreen() {
        // Initialize the reference to the UiApplication
        _uiApp = UiApplication.getUiApplication();

        // Initialize UI components
        setTitle("Extended GPS Diagnostic Test");
        initFields();
        _isVerizonField.setChangeListener(this);

        // A MenuItem to start the diagnostic test
        final MenuItem startTestItem =
                new MenuItem(new StringProvider("Start Test"), 0x300010, 0);
        ;
        startTestItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Format the UI for output
                showOutputFields();

                // Begin test
                if (_locThread != null) {
                    if (!_locThread.isStopped()) {
                        _locThread.stop();
                    }
                }

                _log.setText("");
                log("Extended GPS API test starting");
                log("Device: " + DeviceInfo.getDeviceName());
                log("Device Software: " + DeviceInfo.getSoftwareVersion());
                log("Carrier: " + RadioInfo.getCurrentNetworkName());

                _locThread = new LocationThread();
                _locThread.start();
            }
        }));
        addMenuItem(startTestItem);
        _stopTestItem =
                new MenuItem(new StringProvider("Stop Test"), 0x300020, 1);
        _stopTestItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Stop the thread
                log("Stopping test");
                _locThread.stop();
                _locThread = null;
            }
        }));
        addMenuItem(_stopTestItem);

        // A MenuItem to display the help dialog
        final MenuItem helpItem =
                new MenuItem(new StringProvider("Help"), 0x300030, 2);
        helpItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Display a pop-up dialog with instructions for using this
                // application
                displayHelp();
            }
        }));
        addMenuItem(helpItem);
        showInputFields();
    }

    /**
     * Initializes the UI fields
     */
    private void initFields() {
        _log = new TextField();
        _log.setLabel("Log: ");
        _primaryModeField =
                new ObjectChoiceField("Mode: ", new String[] {
                        "Stand Alone(s)", "Stand Alone(m)", "Data Optimal(m)",
                        "Speed Optimal(m)", "MS-Based(m)",
                        "Accuracy Optimal(s)", "PDE Calculate(s)",
                        "Cellsite(s)", "Cellsite(m)", "Default(s)",
                        "Default(m)", "Optimal Geolocation(s)",
                        "Optimal Geolocation(m)", "Cell Geolocation(s)",
                        "Cell Geolocation(m)", "WLAN Geolocation(s)",
                        "WLAN Geolocation(m)" }, 1);
        _enableGeolocationFallbackField =
                new CheckboxField("Enable Geolocation Fallback", false);
        _enableConcurrentGeolocationField =
                new CheckboxField("Enable Concurrent Geolocation", false);
        _isMapLocationField = new CheckboxField("Map Location", false);
        _useGPSRestartIntervalField =
                new CheckboxField("GPS Restart Interval?", false);
        _isSatelliteInfoRequiredField =
                new CheckboxField("Satellite information required?", false);
        _isVerizonField = new CheckboxField("Verizon?", false);
        _failOverModeField =
                new ObjectChoiceField("Failover Mode: ",
                        new String[] { "Stand Alone", "Data Optimal",
                                "Speed Optimal", "MS-Based",
                                "Accuracy Optimal", "PDE Calculate", "None" },
                        6);
        _subsequentModeField =
                new ObjectChoiceField("Subsequent Mode: ",
                        new String[] { "Stand Alone", "Data Optimal",
                                "Speed Optimal", "MS-Based",
                                "Accuracy Optimal", "PDE Calculate", "None" },
                        6);
        _zoomLevelField = new BasicEditField("Zoom: ", "1");
        _preferredResponseTimeField =
                new BasicEditField("Pref'd Response Time (sec): ", "16", 6,
                        BasicEditField.FILTER_INTEGER);
        _frequencyField =
                new BasicEditField("Fix Frequency (sec): ", "5", 5,
                        BasicEditField.FILTER_INTEGER);
        _timeoutField =
                new BasicEditField("Timeout (sec): ", "-1", 5,
                        BasicEditField.FILTER_INTEGER);
        _maxAgeField =
                new BasicEditField("MaxAge (sec): ", "-1", 5,
                        BasicEditField.FILTER_INTEGER);
        _failOverRetriesField =
                new BasicEditField("Max Retries [0-3]: ", "0", 1,
                        BasicEditField.FILTER_INTEGER);
        _failoverTimeoutField =
                new BasicEditField("Failover Timeout [30-300]: ", "30", 3,
                        BasicEditField.FILTER_INTEGER);
        _gpsRestartIntervalField =
                new BasicEditField("Restart Interval [2-900]: ", "100", 3,
                        BasicEditField.FILTER_INTEGER);
        _gpsRestartRetriesField =
                new BasicEditField("Restart Retries [1-3]: ", "3", 1,
                        BasicEditField.FILTER_INTEGER);
        _pdeIPField = new BasicEditField("PDE IP: ", "");
        _pdePortField = new BasicEditField("PDE Port: ", "");
        _supportedSourcesField = new BasicEditField("Supported Sources: ", "-");
        _availableSourcesField = new BasicEditField("Available Sources: ", "-");
        _currentModeField = new BasicEditField("Current Mode: ", "-");
        _currentSatellitesCountField = new BasicEditField("Satellites: ", "-");
        _currentLocationField = new EditField("Location: ", "-");
        _currentAverageSatelliteSignalField =
                new EditField("Satellite Signal: ", "-");
        _currentDataSourceField = new EditField("Data Source: ", "-");
        _currentErrorField = new EditField("Error: ", "-");
        _currentStatusField = new EditField("Status: ", "-");
        _numberUpdatesField = new EditField("Total Updates: ", "0");
        _numberAssistedUpdatesField = new EditField("Assisted: ", "0");
        _numberUnassistedUpdatesField = new EditField("Unassisted: ", "0");
        _numberValidUpdatesField = new EditField("Valid Updates: ", "0");
        _numberInvalidUpdatesField = new EditField("Invalid Updates: ", "0");
    }

    /**
     * Empties the screen and adds the input fields
     */
    private void showInputFields() {
        // Remove existing UI components
        deleteAll();

        // Add all input UI components
        add(_primaryModeField);
        add(new SeparatorField());
        add(_enableGeolocationFallbackField);
        add(new SeparatorField());
        add(_enableConcurrentGeolocationField);
        add(new SeparatorField());
        add(_isVerizonField);
        add(_pdeIPField);
        add(_pdePortField);
        add(new SeparatorField());
        add(_isMapLocationField);
        add(_zoomLevelField);
        add(new SeparatorField());
        add(_preferredResponseTimeField);
        add(new SeparatorField());
        add(_frequencyField);
        add(_timeoutField);
        add(_maxAgeField);
        add(new SeparatorField());
        add(_failOverModeField);
        add(_failOverRetriesField);
        add(_failoverTimeoutField);
        add(new SeparatorField());
        add(_useGPSRestartIntervalField);
        add(_gpsRestartIntervalField);
        add(_gpsRestartRetriesField);
        add(new SeparatorField());
        add(_subsequentModeField);
        add(new SeparatorField());
        add(_isSatelliteInfoRequiredField);
        add(new SeparatorField());
    }

    /**
     * Empties the screen and adds the output fields
     */
    private void showOutputFields() {
        // Remove existing UI components
        deleteAll();

        // Add all output UI components
        add(_supportedSourcesField);
        add(new SeparatorField());
        add(_availableSourcesField);
        add(new SeparatorField());
        add(new SeparatorField());
        add(_currentModeField);
        add(new SeparatorField());
        add(_currentSatellitesCountField);
        add(new SeparatorField());
        add(_currentLocationField);
        add(new SeparatorField());
        add(_currentAverageSatelliteSignalField);
        add(new SeparatorField());
        add(_currentDataSourceField);
        add(new SeparatorField());
        add(_currentErrorField);
        add(new SeparatorField());
        add(_currentStatusField);
        add(new SeparatorField());
        add(new SeparatorField());
        add(_numberUpdatesField);
        add(new SeparatorField());
        add(_numberAssistedUpdatesField);
        add(new SeparatorField());
        add(_numberUnassistedUpdatesField);
        add(new SeparatorField());
        add(_numberValidUpdatesField);
        add(new SeparatorField());
        add(_numberInvalidUpdatesField);
        add(new SeparatorField());
        add(new SeparatorField());
        add(_log);
    }

    /**
     * Displays a message in the log EditField as well as in the device event
     * log
     * 
     * @param message
     *            The text to be logged
     */
    private void log(final String message) {
        if (message != null) {
            final String newMsg =
                    formatDate(System.currentTimeMillis()) + message + "\n";

            // Add event to device log
            EventLogger.logEvent(0x9876543212345L, newMsg.getBytes(),
                    EventLogger.ALWAYS_LOG);
            _uiApp.invokeLater(new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    // If log too long, reset log
                    if (_log.getText().length() > 1500) {
                        _log.setText("");
                    }

                    // Output message to screen
                    _log.setText(_log.getText() + newMsg);
                }
            });
        }
    }

    /**
     * Returns a String representation of a date provided in long format
     * 
     * @param date
     *            Date in long format
     * @return String representation of <code>date</code>
     */
    private String formatDate(final long date) {
        final SimpleDateFormat sdf =
                new SimpleDateFormat(SimpleDateFormat.TIME_LONG);
        final Date d = new Date(date);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(d);

        final StringBuffer buff = new StringBuffer();
        buff.append('[');
        buff.append(cal.get(Calendar.DAY_OF_MONTH));
        buff.append('-');
        buff.append(sdf.format(cal, new StringBuffer(), null).toString());
        buff.append("] ");

        return buff.toString();
    }

    /**
     * Resets the fix-related UI items to their initial value
     */
    void resetDataFields() {
        _uiApp.invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                // Clear all fields and set to default values
                _currentModeField.setText("-");
                _currentSatellitesCountField.setText("-");
                _currentLocationField.setText("-");
                _currentAverageSatelliteSignalField.setText("-");
                _currentDataSourceField.setText("-");
                _currentErrorField.setText("-");
                _currentStatusField.setText("-");
                _numberUpdatesField.setText("0");
                _numberAssistedUpdatesField.setText("0");
                _numberUnassistedUpdatesField.setText("0");
                _numberValidUpdatesField.setText("0");
                _numberInvalidUpdatesField.setText("0");
            }
        });
    }

    /**
     * @see net.rim.device.api.uiScreen#onClose()
     */
    public boolean onClose() {
        log("Closing Application");

        if (_locThread != null) {
            _locThread.stop();
            _locThread = null;
        }

        return super.onClose();
    }

    /**
     * @see net.rim.device.api.Screen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * @see net.rim.device.api.ui.MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        if (_locThread != null) {
            menu.setDefault(_stopTestItem);
        }
    }

    /**
     * @see net.rim.device.api.ui.FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _isVerizonField) {
            if (_isVerizonField.getChecked()) {
                _pdeIPField.setLabel("Client ID: ");
                _pdePortField.setLabel("Password: ");
            } else {
                _pdeIPField.setLabel("PDE IP: ");
                _pdePortField.setLabel("PDE Port: ");
            }
        }
    }

    /**
     * Displays the help dialog
     */
    private void displayHelp() {
        final InputStream stream =
                getClass().getResourceAsStream("/resource/help_extended.txt");
        final LineReader lineReader = new LineReader(stream);
        final StringBuffer help = new StringBuffer();

        for (;;) {
            try {
                help.append(new String(lineReader.readLine()));
                help.append('\n');
            } catch (final EOFException eof) {
                // We've reached the end of the file
                break;
            } catch (final IOException ioe) {
                Dialog.alert("LineReader#readLine() threw " + ioe.toString());
                return;
            }
        }

        Dialog.inform(help.toString());
    }

    /**
     * All the location related tasks are performed in this Thread
     */
    private class LocationThread extends Thread {
        /**
         * If true, the application will use a LocationListener to continually
         * provide updates with regard to location. If false, a single call to
         * LocationProvider.getLocation() will be made.
         */
        private boolean _isMultipleFixes = true;

        // Determines the mode of the LocationProvider
        private BlackBerryCriteria _bbCriteria;

        // Counter variables for valid, invalid, assisted, unassisted and total
        // updates
        private int _totalUpdates, _validUpdates, _inValidUpdates,
                _assistedUpdates, _unassistedUpdates;

        // Reference to the BlackBerryLocationProvider
        private BlackBerryLocationProvider _bbProvider;

        // Location object that holds the current fix
        private BlackBerryLocation _bbLocation;

        // Holds integer values representing supported and available location
        // data sources
        private int _supportedSourcesMask, _availableSourcesMask;

        // Indicates whether the thread has been stopped
        private boolean _isStopped;

        /**
         * @see java.lang.Thread#run()
         */
        public void run() {
            // Clear the current data
            resetDataFields();

            if (!LocationInfo.isLocationOn()) {
                LocationInfo.setLocationOn();
            }

            _supportedSourcesMask = LocationInfo.getSupportedLocationSources();
            _availableSourcesMask = LocationInfo.getAvailableLocationSources();

            _uiApp.invokeLater(new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    _supportedSourcesField
                            .setText(getDataSourceString(_supportedSourcesMask));
                    _availableSourcesField
                            .setText(getDataSourceString(_availableSourcesMask));
                }
            });

            // Setup and connect to the provider
            try {
                setupCriteria();
            } catch (final net.rim.device.api.system.UnsupportedOperationException uoe) {
                log(_primaryModeField.getChoice(_primaryModeField
                        .getSelectedIndex())
                        + " is not supported on this device\nTest stopped");
                return;
            }

            log("Criteria initialized");

            if (!_PDESet) {
                setupPDE();
            }

            log("Starting Updates: " + formatDate(System.currentTimeMillis()));

            setupProvider();
        }

        /*
         * Sets up the PDE server
         */
        private void setupPDE() {
            final String pdeIPText = _pdeIPField.getText();
            final String pdePortText = _pdePortField.getText();

            if (pdeIPText.length() > 0) {
                if (!_isVerizonField.getChecked()) {
                    log("Using PDE: " + pdeIPText + ":" + pdePortText);
                    final boolean setPDESuccess =
                            GPSSettings.setPDEInfo(pdeIPText, Integer
                                    .parseInt(pdePortText));
                    if (setPDESuccess) {
                        _PDESet = true;
                        log("setPDEInfo() successful");
                    }
                } else {
                    // Set Verizon specific settings
                    log("Using VZ Credentials: " + ";" + pdeIPText + ";"
                            + pdePortText);
                    GPSSettings.setPDEInfo(";" + pdeIPText + ";" + pdePortText,
                            0);

                    // Verizon Verizon PDE server sessions time out after
                    // 12 hours. Set up a timer to reset the connection
                    // at ~12 hour intervals.
                    final Timer timer = new Timer();

                    final TimerTask task = new TimerTask() {
                        /**
                         * @see java.util.TimerTask#run()
                         */
                        public void run() {
                            clearVerizonCredential();
                            setupPDE();
                            resetProvider();
                            setupProvider();
                        }
                    };

                    // Set period to just under 12 hours
                    final long period = 1000 * 60 * 60 * 12 - 60000;

                    // Set date to just under 12 hours from now
                    long date = new Date().getTime();
                    date = date + period;

                    timer.scheduleAtFixedRate(task, period, date);
                    _PDESet = true;
                }
            }
        }

        /**
         * Initializes criteria according to the mode selected by the user. The
         * following algorithm is used: If costAllowed = FALSE mode is Stand
         * Alone Otherwise, if costAllowed=TRUE, -if horizontalAccuracy = 0,
         * mode is Data Optimal -if horizontalAccuracy > 0, -if multiple fixes
         * requested, -if Telus, mode is MS-based -otherwise, -if powerUsage =
         * HIGH, mode is Speed Optimal; -if powerUsage != HIGH, mode is MS-based
         * -else if single fix requested, -if powerUsage = HIGH, mode is
         * Accuracy Optimal -if powerUsage != HIGH, mode is PDE Calculate -if
         * powerUsage = LOW mode is Cellsite
         */
        private void setupCriteria() {
            _bbCriteria = new BlackBerryCriteria();
            _bbCriteria.setPreferredResponseTime(Integer
                    .parseInt(_preferredResponseTimeField.getText()));

            if (_enableConcurrentGeolocationField.getChecked()) {
                _bbCriteria
                        .enableGeolocationWithGPS(BlackBerryCriteria.FASTEST_FIX_PREFERRED);
            } else if (_enableGeolocationFallbackField.getChecked()) {
                _bbCriteria.enableGeolocationWithGPS();
            }

            switch (_primaryModeField.getSelectedIndex()) {
            case 0: // Stand Alone(s)
                _isMultipleFixes = false;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_AUTONOMOUS);
                log("Primary mode set to Stand Alone");
                break;
            case 1: // Stand Alone(m)
                _isMultipleFixes = true;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_AUTONOMOUS);
                log("Primary mode set to Stand Alone");
                break;
            case 2: // Data Optimal(m)
                _isMultipleFixes = true;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_CDMA_DATA_OPTIMAL);
                log("Primary mode set to Data Optimal");
                break;
            case 3: // Speed Optimal(m)
                _isMultipleFixes = true;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_CDMA_SPEED_OPTIMAL);
                log("Primary mode set to Speed Optimal");
                break;
            case 4: // MS-Based(m)
                _isMultipleFixes = true;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_CDMA_MS_BASED);
                log("Primary mode set to MS-Based");
                break;
            case 5: // Accuracy Optimal(s)
                _isMultipleFixes = false;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_CDMA_ACCURACY_OPTIMAL);
                log("Primary mode set to Accuracy Optimal");
                break;
            case 6: // PDE Calculate(s)
                _isMultipleFixes = false;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_CDMA_MS_ASSIST);
                log("Primary mode set to PDE Calculate");
                break;
            case 7: // Cellsite(s)
                _isMultipleFixes = false;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_CELLSITE);
                log("Primary mode set to Cellsite(s)");
                break;
            case 8: // Cellsite(m)
                _isMultipleFixes = true;
                _bbCriteria.setMode(GPSInfo.GPS_MODE_CELLSITE);
                log("Primary mode set to Cellsite(m)");
                break;
            case 9: // Default(s)
                _isMultipleFixes = false;
                log("Primary mode set to Default(m)");
                break;
            case 10: // Default(m)
                _isMultipleFixes = true;
                log("Primary mode set to Default(m)");
                break;
            case 11: // Optimal Geolocation(s)
                _isMultipleFixes = false;
                _bbCriteria =
                        new BlackBerryCriteria(LocationInfo.GEOLOCATION_MODE);
                break;
            case 12: // Optimal Geolocation(m)
                _isMultipleFixes = true;
                _bbCriteria =
                        new BlackBerryCriteria(LocationInfo.GEOLOCATION_MODE);
                break;
            case 13: // Cell Geolocation(s)
                _isMultipleFixes = false;
                _bbCriteria =
                        new BlackBerryCriteria(
                                LocationInfo.GEOLOCATION_MODE_CELL);
                break;
            case 14: // Cell Geolocation(m)
                _isMultipleFixes = true;
                _bbCriteria =
                        new BlackBerryCriteria(
                                LocationInfo.GEOLOCATION_MODE_CELL);
                break;
            case 15: // WLAN Geolocation(s)
                _isMultipleFixes = false;
                _bbCriteria =
                        new BlackBerryCriteria(
                                LocationInfo.GEOLOCATION_MODE_WLAN);
                break;
            case 16: // WLAN Geolocation(m)
                _isMultipleFixes = true;
                _bbCriteria =
                        new BlackBerryCriteria(
                                LocationInfo.GEOLOCATION_MODE_WLAN);
                break;
            }

            if (_failOverModeField.getSelectedIndex() < 6) {
                final int foRetries =
                        Integer.parseInt(_failOverRetriesField.getText());
                final int foTimeout =
                        Integer.parseInt(_failoverTimeoutField.getText());

                switch (_failOverModeField.getSelectedIndex()) {
                case 0: // Stand Alone
                    _bbCriteria.setFailoverMode(GPSInfo.GPS_MODE_AUTONOMOUS,
                            foRetries, foTimeout);
                    log("failOverMode set to Stand Alone");
                    break;
                case 1: // Data Optimal
                    _bbCriteria.setFailoverMode(
                            GPSInfo.GPS_MODE_CDMA_DATA_OPTIMAL, foRetries,
                            foTimeout);
                    log("failOverMode set to Data Optimal");
                    break;
                case 2: // Speed Optimal
                    _bbCriteria.setFailoverMode(
                            GPSInfo.GPS_MODE_CDMA_SPEED_OPTIMAL, foRetries,
                            foTimeout);
                    log("failOverMode set to Speed Optimal");
                    break;
                case 3: // MS-Based
                    _bbCriteria.setFailoverMode(GPSInfo.GPS_MODE_CDMA_MS_BASED,
                            foRetries, foTimeout);
                    log("failOverMode set to MS-Based");
                    break;
                case 4: // Accuracy Optimal
                    _bbCriteria.setFailoverMode(
                            GPSInfo.GPS_MODE_CDMA_ACCURACY_OPTIMAL, foRetries,
                            foTimeout);
                    log("failOverMode set to Accuracy Optimal");
                    break;
                case 5: // PDE Calculate
                    _bbCriteria.setFailoverMode(
                            GPSInfo.GPS_MODE_CDMA_MS_ASSIST, foRetries,
                            foTimeout);
                    log("failOverMode set to PDE Calculate");
                    break;
                }
            }

            // Apply user settings
            if (_useGPSRestartIntervalField.getChecked()) {
                final int interval =
                        Integer.parseInt(_gpsRestartIntervalField.getText());
                final int maximumRetry =
                        Integer.parseInt(_gpsRestartRetriesField.getText());
                _bbCriteria.setGPSRestartInterval(interval, maximumRetry);
            }

            if (_isSatelliteInfoRequiredField.getChecked()) {
                _bbCriteria.setSatelliteInfoRequired(true, true);
            }

            if (_subsequentModeField.getSelectedIndex() < 6) {
                switch (_subsequentModeField.getSelectedIndex()) {
                case 0: // Stand Alone
                    _bbCriteria.setSubsequentMode(GPSInfo.GPS_MODE_AUTONOMOUS);
                    log("subsequentMode set to Stand Alone");
                    break;
                case 1: // Data Optimal
                    _bbCriteria
                            .setSubsequentMode(GPSInfo.GPS_MODE_CDMA_DATA_OPTIMAL);
                    log("subsequentMode set to Data Optimal");
                    break;
                case 2: // Speed Optimal
                    _bbCriteria
                            .setSubsequentMode(GPSInfo.GPS_MODE_CDMA_SPEED_OPTIMAL);
                    log("subsequentMode set to Speed Optimal");
                    break;
                case 3: // MS-Based
                    _bbCriteria
                            .setSubsequentMode(GPSInfo.GPS_MODE_CDMA_MS_BASED);
                    log("subsequentMode set to MS-Based");
                    break;
                case 4: // Accuracy Optimal
                    _bbCriteria
                            .setSubsequentMode(GPSInfo.GPS_MODE_CDMA_ACCURACY_OPTIMAL);
                    log("subsequentMode set to Accuracy Optimal");
                    break;
                case 5: // PDE Calculate
                    _bbCriteria
                            .setSubsequentMode(GPSInfo.GPS_MODE_CDMA_MS_ASSIST);
                    log("subsequentMode set to PDE Calculate");
                    break;
                }
            }
        }

        /**
         * Reset logic for LocationProvider
         */
        private void resetProvider() {
            log("Resetting LocationProvider");
            _bbProvider.setLocationListener(null, 0, 0, 0);
            _bbProvider.reset();
            _bbProvider = null;
        }

        /**
         * Reset credential logic for Verizon. The Verizon PDE session needs to
         * be refreshed every 12 hours (contact Verizon for more information).
         */
        private void clearVerizonCredential() {
            final Thread resetThread = new Thread() {
                /**
                 * @see java.lang.Thread#run()
                 */
                public void run() {
                    final BlackBerryCriteria oldBBCriteria = _bbCriteria;
                    _bbCriteria.setMode(GPSInfo.GPS_MODE_CDMA_MS_ASSIST);
                    LocationProvider tempProvider = null;

                    try {
                        tempProvider =
                                LocationProvider.getInstance(_bbCriteria);
                    } catch (final LocationException e) {
                        log(e.toString());
                    }

                    if (tempProvider != null) {
                        log("Clearing VZ credentials. Please wait...");

                        try {
                            Thread.sleep(2000);
                        } catch (final InterruptedException e) {
                            log(e.toString());
                        }

                        GPSSettings.setPDEInfo("127.0.0.1", 0);

                        try {
                            Thread.sleep(2000);
                        } catch (final InterruptedException e) {
                            log(e.toString());
                        }

                        try {
                            tempProvider.getLocation(1);
                        } catch (final Exception e) {
                            log(e.toString());
                        }

                        try {
                            Thread.sleep(15000);
                        } catch (final InterruptedException e) {
                            log(e.toString());
                        }

                        tempProvider = null;
                        log("Old Verizon session cleared");
                    }
                    _bbCriteria = oldBBCriteria;
                }
            };
            resetThread.start();
            try {
                resetThread.join();
            } catch (final InterruptedException e) {
                log(e.toString());
            }
        }

        /**
         * This method initializes the LocationProvider and sets a
         * LocationListener if <code>isMultipleFixes</code> is TRUE. Otherwise
         * it simply calls singleFixLocationUpdate() which calls
         * LocationProvider.getLocation() once to get a single fix.
         */
        private void setupProvider() {
            try {
                // Disable resetNow
                if (_resetNow) {
                    _resetNow = false;
                }

                log("setupProvider()");

                try {
                    // Sleep to ensure _bbProvider and _bbCriteria have enough
                    // time to be instantiated
                    Thread.sleep(5000);
                } catch (final InterruptedException ie) {
                    log(ie.toString());
                }

                _bbProvider =
                        (BlackBerryLocationProvider) LocationProvider
                                .getInstance(_bbCriteria);
                log("LocationProvider initialized");
                if (_bbProvider != null) {
                    if (_isMultipleFixes) {
                        final int frequency =
                                Integer.parseInt(_frequencyField.getText());
                        final int timeout =
                                Integer.parseInt(_timeoutField.getText());
                        final int maxage =
                                Integer.parseInt(_maxAgeField.getText());
                        _bbProvider.setLocationListener(new LocListener(),
                                frequency, timeout, maxage);
                        log("LocationListener started");
                    } else {
                        log("Initiating single shot GPS fix");
                        singleFixLocationUpdate();
                    }
                } else {
                    log("Provider unavailable for Criteria");
                }
            } catch (final LocationException le) {
                log(le.toString());
            }
        }

        /**
         * Gets a single fix by calling LocationProvider.getLocation(). Updates
         * the UI with the fix information. In case of a valid fix it maps the
         * fix by invoking the Maps application.
         */
        private void singleFixLocationUpdate() {
            try {
                _bbLocation =
                        (BlackBerryLocation) _bbProvider.getLocation(Integer
                                .parseInt(_timeoutField.getText()));
            } catch (final InterruptedException ie) {
                log("InterruptedException thrown by getLocation(): "
                        + ie.getMessage());
            } catch (final LocationException le) {
                log("LocationException thrown by getLocation(): "
                        + le.getMessage());
            }

            if (_bbLocation != null) {
                logLocation(_bbLocation);
            } else {
                log("Location is null");
            }
        }

        /**
         * Displays and logs information about the given location object
         * 
         * @param location
         *            location object to display and log
         */
        private void logLocation(final BlackBerryLocation location) {
            _uiApp.invokeLater(new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    _numberUpdatesField.setText(Integer
                            .toString(++_totalUpdates));
                }
            });
            if (location.isValid()) {
                _uiApp.invokeLater(new Runnable() {
                    /**
                     * @see java.lang.Runnable#run()
                     */
                    public void run() {
                        // Update UI fields to reflect new location
                        _currentModeField.setText(getGPSModeString(location
                                .getGPSMode())
                                + getLocMethodString(location
                                        .getLocationMethod()));

                        final StringBuffer buff =
                                new StringBuffer(location
                                        .getQualifiedCoordinates()
                                        .getLatitude()
                                        + " ");
                        buff.append(location.getQualifiedCoordinates()
                                .getLongitude());
                        buff.append(' ');
                        buff.append(location.getQualifiedCoordinates()
                                .getAltitude());
                        buff.append(' ');
                        _currentLocationField.setText(buff.toString());

                        _currentSatellitesCountField.setText(Integer
                                .toString(location.getSatelliteCount()));
                        _currentAverageSatelliteSignalField.setText(Integer
                                .toString(location
                                        .getAverageSatelliteSignalQuality()));
                        _currentDataSourceField
                                .setText(getDataSourceString(location
                                        .getDataSource()));
                        _currentErrorField.setText(getErrorMessage(location
                                .getError()));
                        _currentStatusField.setText(getStatusString(location
                                .getStatus()));

                        _numberValidUpdatesField.setText(Integer
                                .toString(++_validUpdates));

                        if (location.getGPSMode() != GPSInfo.GPS_MODE_AUTONOMOUS) {
                            _numberAssistedUpdatesField.setText(Integer
                                    .toString(++_assistedUpdates));
                        } else {
                            _numberUnassistedUpdatesField.setText(Integer
                                    .toString(++_unassistedUpdates));
                        }
                    }
                });

                StringBuffer buff = new StringBuffer();
                buff.append(location.getQualifiedCoordinates().getLatitude());
                buff.append(", ");
                buff.append(location.getQualifiedCoordinates().getLongitude());
                buff.append(' ');
                buff.append(location.getQualifiedCoordinates().getAltitude());

                if (_isMultipleFixes) {
                    _currentLocationField.setText("Valid single fix: "
                            + buff.toString());
                } else {
                    log("Valid multiple fix: " + buff.toString());
                }

                buff = new StringBuffer("\tGPS Mode: ");
                buff.append(getGPSModeString(location.getGPSMode()));
                buff.append(getLocMethodString(location.getLocationMethod()));
                buff.append(", Satellite Count: ");
                buff.append(Integer.toString(location.getSatelliteCount()));
                buff.append(", Signal Strength: ");
                buff.append(Integer.toString(location
                        .getAverageSatelliteSignalQuality()));
                log(buff.toString());

                buff = new StringBuffer("\tData Source: ");
                buff.append(getDataSourceString(location.getDataSource()));
                buff.append(", GPS Error: ");
                buff.append(getErrorMessage(location.getError()));
                buff.append(", Status: ");
                buff.append(getStatusString(location.getStatus()));
                log(buff.toString());

                if (_isMapLocationField.getChecked()) {
                    // Launch the Maps application to display the given location
                    displayLocationOnMap(location);
                }
            } else {
                if (_isMultipleFixes && _resetNow) {
                    resetProvider();
                    setupProvider();
                    return;
                }

                _uiApp.invokeLater(new Runnable() {
                    /**
                     * @see java.lang.Runnable#run()
                     */
                    public void run() {
                        // Update the UI to reflect invalid fix
                        _currentModeField.setText(getGPSModeString(location
                                .getGPSMode()));
                        _currentLocationField.setText("*UNKNOWN*");
                        _currentSatellitesCountField.setText(Integer
                                .toString(location.getSatelliteCount()));
                        _currentAverageSatelliteSignalField.setText(Integer
                                .toString(location
                                        .getAverageSatelliteSignalQuality()));
                        _currentDataSourceField
                                .setText(getDataSourceString(location
                                        .getDataSource()));
                        _currentErrorField.setText(getErrorMessage(location
                                .getError()));
                        _currentStatusField.setText(getStatusString(location
                                .getStatus()));

                        _numberInvalidUpdatesField.setText(Integer
                                .toString(++_inValidUpdates));
                    }
                });
                if (_isMultipleFixes) {
                    log("Invalid multiple fix");
                } else {
                    log("Invalid single fix");
                }

                StringBuffer buff = new StringBuffer("\tGPS Mode: ");
                buff.append(getGPSModeString(location.getGPSMode()));
                buff.append(getLocMethodString(location.getLocationMethod()));
                buff.append(", Satellite Count: ");
                buff.append(Integer.toString(location.getSatelliteCount()));
                buff.append(", Signal Strength: ");
                buff.append(Integer.toString(location
                        .getAverageSatelliteSignalQuality()));
                log(buff.toString());

                buff = new StringBuffer("\tData Source: ");
                buff.append(getDataSourceString(location.getDataSource()));
                buff.append(", GPS Error: ");
                buff.append(getErrorMessage(location.getError()));
                buff.append(", Status: ");
                buff.append(getStatusString(location.getStatus()));
                log(buff.toString());
            }
        }

        /**
         * Returns a String representation of a given fix status code
         * 
         * @param status
         *            Given status code
         * @return String representation of status code
         */
        private String getStatusString(final int status) {
            final StringBuffer statusString = new StringBuffer();

            switch (status) {
            case BlackBerryLocation.GPS_ERROR:
                statusString.append("*ERROR");
                break;
            case BlackBerryLocation.GPS_FIX_COMPLETE:
                statusString.append("*FIX_COMPLETE");
                break;
            case BlackBerryLocation.GPS_FIX_PARTIAL:
                statusString.append("*FIX_PARTIAL");
                break;
            case BlackBerryLocation.GPS_FIX_UNAVAILABLE:
                statusString.append("*FIX_UNAVAILABLE");
                break;
            case BlackBerryLocation.FAILOVER_MODE_ON:
                statusString.append("*FAILOVER_MODE_ON");
                break;
            case BlackBerryLocation.SUBSEQUENT_MODE_ON:
                statusString.append("*SUBSEQUENT_MODE_ON");
                break;
            }

            statusString.append("*");

            return statusString.toString();
        }

        /**
         * Returns a String representation for a given data source
         * 
         * @param source
         *            Integer representation of data source
         * @return String representation of data source
         */
        private String getDataSourceString(final int source) {
            final StringBuffer result = new StringBuffer("*");

            switch (source) {
            case GPSInfo.GPS_DEVICE_BLUETOOTH:
                result.append("GPS_DEVICE_BLUETOOTH");
                break;
            case GPSInfo.GPS_DEVICE_INTERNAL:
                result.append("GPS_DEVICE_INTERNAL");
                break;
            case LocationInfo.LOCATION_SOURCE_GEOLOCATION:
                result.append("LOCATION_SOURCE_GEOLOCATION");
                break;
            case LocationInfo.LOCATION_SOURCE_GEOLOCATION_CELL:
                result.append("LOCATION_SOURCE_GEOLOCATION_CELL");
                break;
            case LocationInfo.LOCATION_SOURCE_GEOLOCATION_WLAN:
                result.append("LOCATION_SOURCE_GEOLOCATION_WLAN");
                break;
            default:
                return "*UNKNOWN*";

            }

            result.append("*");

            return result.toString();
        }

        /**
         * Returns a String representation of the location method being used
         * 
         * @param method
         *            The location method for which to retrieve a string
         * @return location Location method String
         */
        private String getLocMethodString(final int method) {
            final StringBuffer buf = new StringBuffer();

            if ((method & Location.MTA_ASSISTED) != 0) {
                buf.append("*MTA_ASSISTED");
            }
            if ((method & Location.MTA_UNASSISTED) != 0) {
                buf.append("*MTA_UNASSISTED");
            }
            if ((method & Location.MTE_ANGLEOFARRIVAL) != 0) {
                buf.append("*MTE_ANGLEOFARRIVAL");
            }
            if ((method & Location.MTE_CELLID) != 0) {
                buf.append("*MTE_CELLID");
            }
            if ((method & Location.MTE_SATELLITE) != 0) {
                buf.append("*MTE_SATELLITE");
            }
            if ((method & Location.MTE_SHORTRANGE) != 0) {
                buf.append("*MTE_SHORTRANGE");
            }
            if ((method & Location.MTE_TIMEDIFFERENCE) != 0) {
                buf.append("*MTE_TIMEDIFFERENCE");
            }
            if ((method & Location.MTE_TIMEOFARRIVAL) != 0) {
                buf.append("*MTE_TIMEOFARRIVAL");
            }
            if ((method & Location.MTY_NETWORKBASED) != 0) {
                buf.append("*MTY_NETWORKBASED");
            }
            if ((method & Location.MTY_TERMINALBASED) != 0) {
                buf.append("*MTY_TERMINALBASED");
            }

            buf.append("*");

            if (buf.length() < 2) {
                return "";
            } else {
                return buf.toString();
            }
        }

        /**
         * Returns a String representation of a given GPS mode
         * 
         * @param mode
         *            GPS mode for which to retrieve a String
         * @return String representation of GPS mode
         */
        private String getGPSModeString(final int mode) {
            final StringBuffer modeString = new StringBuffer();

            if (mode == GPSInfo.GPS_MODE_ASSIST) {
                modeString.append("*ASSIST");
            }
            if (mode == GPSInfo.GPS_MODE_AUTONOMOUS) {
                modeString.append("*AUTONOMOUS");
            }
            if (mode == GPSInfo.GPS_MODE_BT) {
                modeString.append("*BT");
            }
            if (mode == GPSInfo.GPS_MODE_CDMA_ACCURACY_OPTIMAL) {
                modeString.append("*CDMA_ACCURACY_OPTIMAL");
            }
            if (mode == GPSInfo.GPS_MODE_CDMA_DATA_OPTIMAL) {
                modeString.append("*CDMA_DATA_OPTIMAL");
            }
            if (mode == GPSInfo.GPS_MODE_CDMA_MS_ASSIST) {
                modeString.append("*CDMA_MS_ASSIST");
            }
            if (mode == GPSInfo.GPS_MODE_CDMA_MS_BASED) {
                modeString.append("*CDMA_MS_BASED");
            }
            if (mode == GPSInfo.GPS_MODE_CDMA_SPEED_OPTIMAL) {
                modeString.append("*CDMA_SPEED_OPTIMAL");
            }
            if (mode == GPSInfo.GPS_MODE_CELLSITE) {
                modeString.append("*CELLSITE");
            }
            if (mode == GPSInfo.GPS_MODE_NONE) {
                modeString.append("*NONE");
            }
            if (mode == LocationInfo.GEOLOCATION_MODE_WLAN) {
                modeString.append("*GEOLOCATION_MODE_WLAN");
            } else if (mode == LocationInfo.GEOLOCATION_MODE_CELL) {
                modeString.append("*GEOLOCATION_MODE_CELL");
            } else if (mode == LocationInfo.GEOLOCATION_MODE) {
                modeString.append("*GEOLOCATION_MODE");
            }

            modeString.append("*");

            if (modeString.length() < 2) {
                return "";
            } else {
                return modeString.toString();
            }
        }

        /**
         * Invoke the Maps application to show the fix on a map
         * 
         * @param location
         *            The Location object to map
         */
        private void displayLocationOnMap(final Location location) {
            try {
                String lon =
                        Double.toString(location.getQualifiedCoordinates()
                                .getLongitude() * 100000);
                lon = lon.substring(0, 8);
                String lat =
                        Double.toString(location.getQualifiedCoordinates()
                                .getLatitude() * 100000);
                lat = lat.substring(0, 7);
                final StringBuffer document =
                        new StringBuffer("<lbs><location lon='");
                document.append(lon);
                document.append("' lat='");
                document.append(lat);
                document.append("' label='MyLocation' zoom='");
                document.append(_zoomLevelField.getText());
                document.append("'/></lbs>");

                Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                        new MapsArguments(MapsArguments.ARG_LOCATION_DOCUMENT,
                                document.toString()));
            } catch (final Exception e) {
                log("Unable to map Location. Please make sure that BlackBerry Maps is installed.");
            }
        }

        /**
         * Resets the BlackBerryLocationProvider and removes reference
         */
        public void stop() {
            _isStopped = true;

            // Log the statistics for the location session
            log("Stopping Updates: " + formatDate(System.currentTimeMillis()));
            log("Total Updates: " + _numberUpdatesField);
            log("Assisted Updates: " + _numberAssistedUpdatesField);
            log("Unassisted Updates: " + _numberUnassistedUpdatesField);
            log("Valid Updates: " + _numberValidUpdatesField);
            log("Invalid Updates: " + _numberInvalidUpdatesField);

            if (_bbProvider != null) {
                _bbProvider.setLocationListener(null, 0, 0, 0);
                _bbProvider.reset();
                _bbProvider = null;
            }
        }

        /**
         * Returns the running/stopped status of this thread
         * 
         * @return The running/stopped status of this thread
         */
        boolean isStopped() {
            return _isStopped;
        }

        /**
         * Returns a readable error message for a given GPS error code
         * 
         * @param err
         *            Error code
         * @return Human readable error message
         */
        private String getErrorMessage(final int err) {
            String msg = "";

            switch (err) {
            case GPSInfo.GPS_ERROR_ALMANAC_OUTDATED:
                msg = "Almanac outdated";
                break;
            case GPSInfo.GPS_ERROR_AUTHENTICATION_FAILURE:
                msg = "Authentication failed with the network";
                break;
            case GPSInfo.GPS_ERROR_CHIPSET_DEAD:
                msg = "GPS chipset dead; no fix";
                break;
            case GPSInfo.GPS_ERROR_DEGRADED_FIX_IN_ALLOTTED_TIME:
                msg = "Degraded fix; poor accuracy";
                break;
            case GPSInfo.GPS_ERROR_GPS_LOCKED:
                msg = "GPS service locked";
                break;
            case GPSInfo.GPS_ERROR_INVALID_NETWORK_CREDENTIAL:
                msg = "Invalid network credential";
                break;
            case GPSInfo.GPS_ERROR_INVALID_REQUEST:
                msg = "Request is invalid";
                break;
            case GPSInfo.GPS_ERROR_LOW_BATTERY:
                msg = "Low battery; fix cannot be obtained";
                break;
            case GPSInfo.GPS_ERROR_NETWORK_CONNECTION_FAILURE:
                msg = "Unable to connect to the data network";
                break;
            case GPSInfo.GPS_ERROR_NO_FIX_IN_ALLOTTED_TIME:
                msg = "No fix obtained in alloted time.";
                break;
            case GPSInfo.GPS_ERROR_NO_SATELLITE_IN_VIEW:
                msg =
                        "No Satellite is in view or the signal strength is too low to get a position fix";
                break;
            case GPSInfo.GPS_ERROR_NONE:
                msg = "No GPS Error";
                break;
            case GPSInfo.GPS_ERROR_PRIVACY_ACCESS_DENIED:
                msg = "Privacy setting denies getting a fix";
                break;
            case GPSInfo.GPS_ERROR_SERVICE_UNAVAILABLE:
                msg =
                        "GPS service is not available due to no cellular service or no data service or no resources, etc.";
                break;
            case GPSInfo.GPS_ERROR_TIMEOUT_DEGRADED_FIX_NO_ASSIST_DATA:
                msg = "Degraded fix (no assist data); poor accuracy";
                break;
            case GPSInfo.GPS_ERROR_TIMEOUT_NO_FIX_NO_ASSIST_DATA:
                msg = "No fix in alloted time, no assist";
                break;
            default:
                msg = "Unknown error";
                break;
            }

            return msg;
        }

        /**
         * LocationListener implementation
         */
        private class LocListener implements LocationListener {
            /**
             * @see javax.microedition.location.LocationListener#locationUpdated(LocationProvider,
             *      Location)
             */
            public void locationUpdated(final LocationProvider provider,
                    final Location location) {
                _bbLocation = (BlackBerryLocation) location;
                if (_bbLocation != null) {
                    logLocation(_bbLocation);
                } else {
                    log("Location is null");
                }
            }

            /**
             * @see javax.microedition.location.LocationListener#providerStateChanged(LocationProvider,
             *      int)
             */
            public void providerStateChanged(final LocationProvider provider,
                    final int newState) {
                switch (newState) {
                case LocationProvider.OUT_OF_SERVICE: // Triggered when a BES
                                                      // policy does not allow
                                                      // location capabilities
                    log("State Change: Out of Service");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE: // Triggered when
                                                               // the system has
                                                               // stopped
                                                               // looking for a
                                                               // fix
                    log("State Change: Temp Unavailable");
                    log("Resetting Location Provider due to TEMPORARILY UNAVAILABLE state");
                    _resetNow = true;
                    break;
                }
            }
        }
    }

}
