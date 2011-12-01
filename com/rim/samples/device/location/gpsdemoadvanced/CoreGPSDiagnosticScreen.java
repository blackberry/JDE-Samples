/**
 * CoreGPSDiagnosticScreen.java
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

import javax.microedition.location.Criteria;
import javax.microedition.location.Location;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationListener;
import javax.microedition.location.LocationProvider;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.gps.GPSSettings;
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
 * A screen for testing the use of the core APIs included in the
 * javax.microedition.location package.
 * 
 * There are several available modes the user can choose. (m) stands for
 * 'multiple fix' and (s) stands for 'single fix'.
 * 
 * In Smart Mode the application operates in MS-Based mode but falls back to
 * MS-Assisted for a single fix if the LocationProvider is unable to return a
 * valid fix in (maxInvalidTime) seconds. The provider then goes back to
 * MS-Based again.
 * 
 * For any assisted modes (all those except Stand Alone or Cellsite), additional
 * helper data from a PDE server is required.
 */
public final class CoreGPSDiagnosticScreen extends MainScreen implements
        FieldChangeListener {
    // This number represents the maximum number of provider resets performed
    // before falling back to a single assisted fix. Only applies to Smart Mode.
    private static final int FALL_BACK_COUNTER_THRESHOLD = 2;

    // Provides a reference to the UiApplication for faster updating
    private final UiApplication _uiApp;

    private ObjectChoiceField _modeField;

    // Check box to indicate whether location should be displayed on a map
    private CheckboxField _enableMapLocationField;

    // Field to specify if the device uses Verizon as a carrier. If so, app
    // credentials will be set instead of PDE IP and Port.
    private CheckboxField _isVerizonField;

    // Field to specify the zoom level used in the BlackBerry Maps application
    private BasicEditField _zoomLevelField;

    // Field to enter desired horizontal accuracy. This value may be ignored in
    // the case that the selected mode requires a specific horizontal accuracy
    // value.
    private BasicEditField _horizontalAccuracyField;

    // Field to enter preferred response time
    private BasicEditField _preferredResponseTimeField;

    // Field to enter the interval parameter for a LocationListener
    private BasicEditField _frequencyField;

    // Field to enter the timeout parameter for a LocationListener
    private BasicEditField _timeoutField;

    // Field to enter the maximum age of the returned location in seconds
    private BasicEditField _maxAgeField;

    // Field to enter how long consecutive invalid fixes can occur until a
    // LocationProvider reset is performed.
    private BasicEditField _maxInvalidTimeField;

    // Displays the value returned by Location.getLocationMethod()
    private BasicEditField _currentModeField;

    // Displays the number of satellites used to compute the current fix
    private BasicEditField _currentSatelliteCountField;

    // Field to enter the PDE IP
    private BasicEditField _pdeIPField;

    // Field to enter the PDE port
    private BasicEditField _pdePortField;

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

    // Displays the location information for the current fix
    private EditField _currentLocationField;

    // Displays the time of the last valid fix
    private EditField _lastValidFixField;

    // Displays the time of the last LocationProvider reset
    private EditField _lastResetField;

    // EditField to show the log messages generated during the test
    private TextField _log;

    // A Thread that performs all location related work
    private LocationThread _locThread;

    // A menu item for stopping a running test
    private final MenuItem _stopTestItem;

    private boolean _PDESet;

    /**
     * Creates a new CoreGPSDiagnosticScreen object
     */
    public CoreGPSDiagnosticScreen() {
        // Initialize reference to UiApplication
        _uiApp = UiApplication.getUiApplication();

        // Initialize UI components
        setTitle("Core GPS Diagnostic Test");
        initFields();
        _isVerizonField.setChangeListener(this);

        // A MenuItem to start the diagnostic test
        final MenuItem startTestItem =
                new MenuItem(new StringProvider("Start Test"), 0x300010, 0);
        startTestItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Format the UI for output
                showOutputFields();

                if (_locThread != null) {
                    if (!_locThread.isStopped()) {
                        _locThread.stop();
                    }
                }

                _log.setText("");
                log("Core GPS API test starting");
                log("Device: " + DeviceInfo.getDeviceName());
                log("Device Software: " + DeviceInfo.getSoftwareVersion());
                log("Carrier: " + RadioInfo.getCurrentNetworkName());

                // Begin test
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
        _modeField =
                new ObjectChoiceField("Mode: ", new String[] {
                        "Stand Alone(s)", "Stand Alone(m)", "Data Optimal(m)",
                        "Speed Optimal(m)", "MS-Based(m)",
                        "Accuracy Optimal(s)", "PDE Calculate(s)",
                        "Cellsite(s)", "Cellsite(m)", "AFLT(s)",
                        "SmartMode(m)", "Default(s)", "Default(m)",
                        "NULL Criteria(s)", "NULL Criteria(m)" }, 1);
        _enableMapLocationField = new CheckboxField("Map Location", false);
        _isVerizonField = new CheckboxField("Verizon?", false);
        _zoomLevelField = new BasicEditField("Zoom: ", "1");
        _horizontalAccuracyField =
                new BasicEditField("Horizontal Accuracy (meters): ", "100", 6,
                        BasicEditField.FILTER_INTEGER);
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
        _maxInvalidTimeField =
                new BasicEditField("Max Invalid Time (sec): ", "90", 5,
                        BasicEditField.FILTER_INTEGER);
        _currentModeField = new BasicEditField("Current Mode: ", "-");
        _currentSatelliteCountField = new BasicEditField("Satellites: ", "-");
        _pdeIPField = new BasicEditField("PDE IP: ", "");
        _pdePortField = new BasicEditField("PDE Port: ", "");
        _numberUpdatesField = new EditField("Total Updates: ", "0");
        _numberAssistedUpdatesField = new EditField("Assisted: ", "0");
        _numberUnassistedUpdatesField = new EditField("Unassisted: ", "0");
        _numberValidUpdatesField = new EditField("Valid Updates: ", "0");
        _numberInvalidUpdatesField = new EditField("Invalid Updates: ", "0");
        _currentLocationField = new EditField("Location: ", "-");
        _lastValidFixField = new EditField("Last Valid Fix: ", "-");
        _lastResetField = new EditField("Last Reset: ", "-");
        _log = new TextField();
        _log.setLabel("Log: ");
    }

    /**
     * Clears the screen and adds the UI input fields
     */
    private void showInputFields() {
        // Remove all current fields
        deleteAll();

        // Populate UI
        add(_modeField);
        add(new SeparatorField());
        add(_enableMapLocationField);
        add(_zoomLevelField);
        add(new SeparatorField());
        add(_horizontalAccuracyField);
        add(_preferredResponseTimeField);
        add(new SeparatorField());
        add(_frequencyField);
        add(_timeoutField);
        add(_maxAgeField);
        add(new SeparatorField());
        add(_maxInvalidTimeField);
        add(new SeparatorField());
        add(_isVerizonField);
        add(_pdeIPField);
        add(_pdePortField);
        add(new SeparatorField());
    }

    /**
     * Clears the screen and adds the UI output fields
     */
    private void showOutputFields() {
        // Remove all current fields
        deleteAll();

        // Populate UI
        add(_currentModeField);
        add(new SeparatorField());
        add(_currentLocationField);
        add(new SeparatorField());
        add(_currentSatelliteCountField);
        add(new SeparatorField());
        add(new SeparatorField());
        add(_lastValidFixField);
        add(new SeparatorField());
        add(_lastResetField);
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
                    dateFormatter(System.currentTimeMillis()) + message + "\n";

            // Add event to device log
            EventLogger.logEvent(0x9876543212345L, newMsg.getBytes(),
                    EventLogger.ALWAYS_LOG);

            _uiApp.invokeLater(new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    // If log is too long, reset
                    if (_log.getText().length() > 1000) {
                        _log.setText("");
                    }

                    // Print message to log field
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
    private String dateFormatter(final long date) {
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
     * Resets the fix-related UI items to their initial values
     */
    private void resetDataFields() {
        _uiApp.invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                // Replace all entered values with defaults
                _numberUpdatesField.setText("0");
                _numberAssistedUpdatesField.setText("0");
                _numberUnassistedUpdatesField.setText("0");
                _numberValidUpdatesField.setText("0");
                _numberInvalidUpdatesField.setText("0");
                _currentLocationField.setText("-");
                _currentSatelliteCountField.setText("-");
                _locThread._lastValid = System.currentTimeMillis();
                _lastValidFixField
                        .setText(dateFormatter(_locThread._lastValid));
                _lastResetField.setText(dateFormatter(_locThread._lastReset));
                _currentModeField.setText("-");
            }
        });
    }

    /**
     * All location related tasks are performed in this Thread
     */
    private final class LocationThread extends Thread {
        /**
         * If true, the app will use a LocationListener to continually provide
         * updates with regard to location. If false, a single call to
         * LocationProvider.getLocation() will be made.
         */
        private boolean _isMultipleFixes = true;

        /*
         * If > FALL_BACK_COUNTER_THRESHOLD, a single fallBack fix will be
         * computed in MS-Assisted mode. This is only used in SmartMode.
         */
        private int _fallBackCounter = FALL_BACK_COUNTER_THRESHOLD;

        /*
         * In SmartMode the application operates mostly in MS-Based mode but
         * falls back to MS-Assisted for a single fix if the LocationProvider is
         * unable to return a valid fix after the number of resets reaches
         * FALL_BACK_COUNTER_THRESHOLD. If the number of resets exceeds
         * FALL_BACK_COUNTER_THRESHOLD, a single assisted fix will be computed.
         * A reset is performed if the provider fails to return a valid fix for
         * more than "maxInvalidTimeField.getText()" seconds. The provider then
         * goes back to MS-Based mode again.
         */
        private boolean _isSmartMode;

        // Determines the mode of the LocationProvider
        private Criteria _criteria;

        // Arguments passed to LocationProvider.setLocationListener()
        private final int _frequency, _timeout, _maxage;

        // Counter variables for valid, invalid, assisted, unassisted and total
        // updates
        private int _totalUpdates, _validUpdates, _inValidUpdates,
                _assistedUpdates, _unassistedUpdates;

        // Reference to the LocationProvider
        private LocationProvider _provider;

        // Variable to store the date/time (in long format) of the last valid
        // fix
        private long _lastValid = System.currentTimeMillis();

        // Variable to store the date/time (in long format) of the last
        // LocationProvider reset
        private long _lastReset = System.currentTimeMillis();

        // Location object that holds the current location at a given time
        private Location _location = null;

        // Indicates whether the thread has been stopped
        private boolean _isStopped;

        /**
         * Creates a new LocationThread object. Initializes frequency, timeout
         * and max age counters and sets PDE parameters according to the PDE IP
         * and PORT provided by the user.
         */
        LocationThread() {
            _frequency = Integer.parseInt(_frequencyField.getText());
            _timeout = Integer.parseInt(_timeoutField.getText());
            _maxage = Integer.parseInt(_maxAgeField.getText());
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
         * This method validates the value in the _horizontalAccuracy EditField.
         * If mustBeZero is true then the value is set to 0. Otherwise, the user
         * specified value is validated i.e. if the user sets a value < = 0 the
         * value is force set to a default value of 100.
         * 
         * @param mustBeZero
         *            True if the value must be 0, false otherwise
         */
        private void validateHorizontalAccuracy(final boolean mustBeZero) {
            _uiApp.invokeLater(new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    final int value =
                            Integer.parseInt(_horizontalAccuracyField.getText());
                    if (!mustBeZero) {
                        if (value <= 0) {
                            _horizontalAccuracyField.setText("100");
                        }
                    } else {
                        _horizontalAccuracyField.setText("0");
                    }
                }
            });
        }

        /**
         * @see java.lang.Thread#run()
         */
        public void run() {
            resetDataFields();
            setupCriteria();

            log("Criteria initialized");

            if (!_PDESet) {
                setupPDE();
            }

            log("Starting Updates: "
                    + dateFormatter(System.currentTimeMillis()));
            setupProvider();
        }

        /**
         * Resets logic for most carriers (excludes Verizon)
         */
        private void resetProvider() {
            // Set to indicate that the provider has been reset
            _lastReset = System.currentTimeMillis();
            _uiApp.invokeLater(new Runnable() {
                /**
                 * @see java.lang.Runnable#run()
                 */
                public void run() {
                    // Indicate in the UI that the provider is being reset
                    _lastResetField.setText(dateFormatter(_lastReset));
                }
            });
            log("Resetting LocationProvider");
            _provider.setLocationListener(null, 0, 0, 0);
            _provider.reset();
            _provider = null;
        }

        /**
         * Resets credential logic for Verizon. Also refreshes the Verizon PDE
         * session which needs to be done every 12 hours (contact Verizon).
         */
        private void clearVerizonCredential() {
            final Thread resetThread = new Thread() {
                /**
                 * @see java.lang.Thread#run()
                 */
                public void run() {
                    LocationProvider tempProvider = null;

                    try {
                        tempProvider = LocationProvider.getInstance(_criteria);
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
                            // Sleep so the PDE has time to be set up
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
                        log("Old Verizon session cleared.");
                    }
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
         * Initializes criteria according to the mode selected by the user. The
         * following algorithm is used: If costAllowed = FALSE mode is Stand
         * Alone Otherwise, if costAllowed=TRUE, -if horizontalAccuracy = 0,
         * mode is Data Optimal -if horizontalAccuracy > 0, -if multiplied fixes
         * requested, -if Telus, mode is MS-based -otherwise, -if powerUsage =
         * HIGH, mode is Speed Optimal; -if powerUsage != HIGH, mode is MS-based
         * -else if single fix requested, -if powerUsage = HIGH, mode is
         * Accuracy Optimal -if powerUsage != HIGH, mode is PDE Calculate -if
         * powerUsage = LOW mode is Cellsite
         */
        private void setupCriteria() {
            _criteria = new Criteria();
            _criteria.setPreferredResponseTime(Integer
                    .parseInt(_preferredResponseTimeField.getText()));

            switch (_modeField.getSelectedIndex()) {
            case 0: // Stand Alone(s)
                _isMultipleFixes = false;
                _isSmartMode = false;
                _criteria.setCostAllowed(false);
                validateHorizontalAccuracy(false);
                _criteria.setVerticalAccuracy(200);
                _criteria.setHorizontalAccuracy(Integer
                        .parseInt(_horizontalAccuracyField.getText()));
                log("Criteria set for Stand Alone");
                break;
            case 1: // Stand Alone(m)
                _isMultipleFixes = true;
                _isSmartMode = false;
                _criteria.setCostAllowed(false);
                validateHorizontalAccuracy(false);
                _criteria.setHorizontalAccuracy(Integer
                        .parseInt(_horizontalAccuracyField.getText()));
                _criteria.setVerticalAccuracy(200);
                log("Criteria set for Stand Alone");
                break;
            case 2: // Data Optimal(m)
                _isMultipleFixes = true;
                _isSmartMode = false;
                _criteria.setCostAllowed(true);
                validateHorizontalAccuracy(false);
                _criteria.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
                _criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
                log("Criteria set for Data Optimal");
                break;
            case 3: // Speed Optimal(m)
                _isMultipleFixes = true;
                _isSmartMode = false;
                _criteria.setCostAllowed(true);
                validateHorizontalAccuracy(false);
                _criteria.setHorizontalAccuracy(Integer
                        .parseInt(_horizontalAccuracyField.getText()));
                _criteria.setVerticalAccuracy(200);
                _criteria
                        .setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
                log("Criteria set for Speed Optimal");
                break;
            case 4: // MS-Based(m)
                _isMultipleFixes = true;
                _isSmartMode = false;
                _criteria.setCostAllowed(true);
                validateHorizontalAccuracy(false);
                _criteria.setHorizontalAccuracy(Integer
                        .parseInt(_horizontalAccuracyField.getText()));
                _criteria.setVerticalAccuracy(200);
                _criteria
                        .setPreferredPowerConsumption(Criteria.POWER_USAGE_MEDIUM);
                log("Criteria set for MS-Based");
                break;
            case 5: // Accuracy Optimal(s)
                _isMultipleFixes = false;
                _isSmartMode = false;
                _criteria.setCostAllowed(true);
                validateHorizontalAccuracy(false);
                _criteria.setHorizontalAccuracy(Integer
                        .parseInt(_horizontalAccuracyField.getText()));
                _criteria.setVerticalAccuracy(200);
                _criteria
                        .setPreferredPowerConsumption(Criteria.POWER_USAGE_HIGH);
                log("Criteria set for Accuracy Optimal");
                break;
            case 6: // PDE Calculate(s)
                _isMultipleFixes = false;
                _isSmartMode = false;
                _criteria.setCostAllowed(true);
                validateHorizontalAccuracy(false);
                _criteria.setHorizontalAccuracy(Integer
                        .parseInt(_horizontalAccuracyField.getText()));
                _criteria.setVerticalAccuracy(200);
                _criteria
                        .setPreferredPowerConsumption(Criteria.POWER_USAGE_MEDIUM);
                log("Criteria set for PDE Calculate");
                break;
            case 7: // Cellsite(s)
                _isMultipleFixes = false;
                _isSmartMode = false;
                _criteria.setCostAllowed(true);
                _criteria.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
                _criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
                _criteria
                        .setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
                log("Criteria set for Cellsite(s)");
                break;
            case 8: // Cellsite(m)
                _isMultipleFixes = true;
                _isSmartMode = false;
                _criteria.setCostAllowed(true);
                _criteria.setHorizontalAccuracy(Criteria.NO_REQUIREMENT);
                _criteria.setVerticalAccuracy(Criteria.NO_REQUIREMENT);
                _criteria
                        .setPreferredPowerConsumption(Criteria.POWER_USAGE_LOW);
                log("Criteria set for Cellsite(m)");
                break;
            case 9: // AFLT(s)
                _isMultipleFixes = false;
                _isSmartMode = false;
                _criteria.setCostAllowed(true);
                validateHorizontalAccuracy(false);
                _criteria.setHorizontalAccuracy(Integer
                        .parseInt(_horizontalAccuracyField.getText()));
                _criteria.setVerticalAccuracy(200);
                _criteria
                        .setPreferredPowerConsumption(Criteria.POWER_USAGE_MEDIUM);
                _criteria.setPreferredResponseTime(0);
                log("Criteria set for AFLT");
                break;
            case 10: // SmartMode(m)
                _isMultipleFixes = true;
                _isSmartMode = true;
                _criteria.setCostAllowed(true);
                validateHorizontalAccuracy(false);
                _criteria.setHorizontalAccuracy(Integer
                        .parseInt(_horizontalAccuracyField.getText()));
                _criteria.setVerticalAccuracy(200);
                _criteria
                        .setPreferredPowerConsumption(Criteria.POWER_USAGE_MEDIUM);
                log("Criteria set for Smart Mode");
                break;
            case 11: // default criteria(s)
                _isMultipleFixes = false;
                _isSmartMode = false;
                log("Criteria set for Default Criteria");
                break;
            case 12: // default criteria(m)
                _isMultipleFixes = true;
                _isSmartMode = false;
                log("Criteria set for Default Criteria");
                break;
            case 13: // null criteria(s)
                _isMultipleFixes = false;
                _isSmartMode = false;
                _criteria = null;
                log("Criteria set to null");
                break;
            case 14: // null criteria(m)
                _isMultipleFixes = true;
                _isSmartMode = false;
                _criteria = null;
                log("Criteria set to null");
                break;
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
                log("setupProvider()");

                try {
                    // Sleep to give _provider and _criteria enough time to
                    // instantiate
                    Thread.sleep(5000);
                } catch (final InterruptedException ie) {
                    log(ie.toString());
                }

                _provider = LocationProvider.getInstance(_criteria);
                log("LocationProvider initialized");

                if (_provider != null) {
                    if (_isMultipleFixes && _isSmartMode
                            && _fallBackCounter < FALL_BACK_COUNTER_THRESHOLD
                            || _isMultipleFixes && !_isSmartMode) {
                        // Multifix non-SmartMode or SmartMode going back to
                        // MS-Based
                        if (_isSmartMode) {
                            log("SmartMode in MS-Based mode - fallBackCounter: "
                                    + _fallBackCounter);
                        }
                        _provider.setLocationListener(new LocListener(),
                                _frequency, _timeout, _maxage);
                        log("LocationListener started");
                    } else {
                        // Single fix non-SmartMode or SmartMode Falling back to
                        // MS-Assisted
                        if (_isSmartMode) {
                            log("SmartMode in MS-Assisted mode - fallBackCounter: "
                                    + _fallBackCounter);
                        }

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
                _location = _provider.getLocation(100);
            } catch (final InterruptedException ie) {
                log("InterruptedException thrown by getLocation(): "
                        + ie.getMessage());
            } catch (final LocationException le) {
                log("LocationException thrown by getLocation(): "
                        + le.getMessage());
            }

            if (_location != null) {
                _uiApp.invokeLater(new Runnable() {
                    /**
                     * @see java.lang.Runnable#run()
                     */
                    public void run() {
                        _numberUpdatesField.setText(Integer
                                .toString(++_totalUpdates));
                    }
                });

                if (_location.isValid()) {
                    _lastValid = System.currentTimeMillis();

                    // Update UI to reflect new location
                    _uiApp.invokeLater(new Runnable() {
                        /**
                         * @see java.lang.Runnable#run()
                         */
                        public void run() {
                            _lastValidFixField
                                    .setText(dateFormatter(_lastValid));
                            _currentModeField
                                    .setText(getLocMethodString(_location
                                            .getLocationMethod()));

                            final StringBuffer buff = new StringBuffer();
                            buff.append(_location.getQualifiedCoordinates()
                                    .getLatitude());
                            buff.append(' ');
                            buff.append(_location.getQualifiedCoordinates()
                                    .getLongitude());
                            buff.append(' ');
                            buff.append(_location.getQualifiedCoordinates()
                                    .getAltitude());
                            buff.append(' ');
                            _currentLocationField.setText(buff.toString());

                            _currentSatelliteCountField
                                    .setText(getNumSatellites(_location));
                            _numberValidUpdatesField.setText(Integer
                                    .toString(++_validUpdates));
                            _numberAssistedUpdatesField.setText(Integer
                                    .toString(++_assistedUpdates));
                        }
                    });

                    final StringBuffer logText =
                            new StringBuffer("Valid single fix: ");
                    logText.append(_location.getQualifiedCoordinates()
                            .getLatitude());
                    logText.append(", ");
                    logText.append(_location.getQualifiedCoordinates()
                            .getLongitude());
                    logText.append(' ');
                    logText.append(_location.getQualifiedCoordinates()
                            .getAltitude());
                    log(logText.toString());
                    log("Method: "
                            + getLocMethodString(_location.getLocationMethod()));

                    // If Smart Mode, go back to MS-Based
                    if (_isSmartMode) {
                        log("Smart Mode got single MS-Assisted fix");
                        _fallBackCounter = 0;
                        resetProvider();
                        setupProvider();
                    }

                    if (_enableMapLocationField.getChecked()) {
                        // Launch Maps application with current location
                        displayLocationOnMap(_location);
                    }
                } else {
                    // Update UI to reflect invalid location
                    _uiApp.invokeLater(new Runnable() {
                        /**
                         * @see java.lang.Runnable#run()
                         */
                        public void run() {
                            _currentLocationField.setText("Invalid");
                            _currentSatelliteCountField.setText("-");
                            _numberInvalidUpdatesField.setText(Integer
                                    .toString(++_inValidUpdates));
                        }
                    });
                    log("Invalid single fix");

                    // Check if invalid fixes have exceeded allowed time
                    if (System.currentTimeMillis() - _lastValid >= Integer
                            .parseInt(_maxInvalidTimeField.getText()) * 1000
                            && System.currentTimeMillis() - _lastReset >= Integer
                                    .parseInt(_maxInvalidTimeField.getText()) * 1000
                            || System.currentTimeMillis() - _lastReset >= Integer
                                    .parseInt(_maxInvalidTimeField.getText()) * 1000) {
                        final StringBuffer logText =
                                new StringBuffer(
                                        "Resetting Location Provider because: \nInvalid fixes for ");
                        logText.append((System.currentTimeMillis() - _lastValid) / 1000);
                        logText.append(" seconds\nNo provider reset for ");
                        logText.append((System.currentTimeMillis() - _lastReset) / 1000);
                        logText.append(" seconds");
                        log(logText.toString());

                        // If Smart Mode, go back to MS-Based
                        if (_isSmartMode) {
                            log("Smart Mode failed to get single MS-Assisted fix");
                            _fallBackCounter = 0;
                            resetProvider();
                            setupProvider();
                        }

                        resetProvider();
                        setupProvider();
                    }
                }
            } else {
                log("Location is null");

                // If Smart Mode, go back to MS-Based
                if (_isSmartMode) {
                    log("Smart Mode FAILED! to get single MS-Assisted fix");
                    _fallBackCounter = 0;
                    resetProvider();
                    setupProvider();
                }
            }
        }

        /**
         * Invoke the Maps application to show a fix on a map
         * 
         * @param location
         *            The location object to display on map
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

                // Launch Maps application
                Invoke.invokeApplication(Invoke.APP_TYPE_MAPS,
                        new MapsArguments(MapsArguments.ARG_LOCATION_DOCUMENT,
                                document.toString()));
            } catch (final Exception e) {
                log("Unable to map Location. Please make sure that BlackBerry Maps is installed.");
            }
        }

        /**
         * Returns a String representation of the location process being used
         * 
         * @param method
         *            The the location method for which to retrieve a string
         * @return Location method string
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

            return buf.toString();
        }

        /**
         * Retrieve the satellite data for a given location
         * 
         * @param location
         *            The location to retrieve information for
         * @return A string describing the number of available satellites
         */
        private String getNumSatellites(final Location location) {
            String extra =
                    location.getExtraInfo("application/X-jsr179-location-nmea");

            // Retrieve the eighth section of the comma-delimited extra string
            for (int i = 0; i < 7; i++) {
                extra = extra.substring(extra.indexOf(',') + 1, extra.length());
            }

            return extra.substring(0, extra.indexOf(','));
        }

        /**
         * Resets the BlackBerryLocationProvider and removes reference
         */
        public void stop() {
            _isStopped = true;

            // Log the statistics for the location session
            log("Stopping Updates: "
                    + dateFormatter(System.currentTimeMillis()));
            log("Total Updates: " + _numberUpdatesField);
            log("Assisted Updates: " + _numberAssistedUpdatesField);
            log("Unassisted Updates: " + _numberUnassistedUpdatesField);
            log("Valid Updates: " + _numberValidUpdatesField);
            log("Invalid Updates: " + _numberInvalidUpdatesField);

            if (_provider != null) {
                _provider.setLocationListener(null, 0, 0, 0);
                _provider.reset();
                _provider = null;
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
         * LocationListener implementation
         */
        private class LocListener implements LocationListener {
            // Flag indicating an immediate reset is required due
            // to TEMPORARILY_UNAVAILABLE event.
            boolean _resetNow;

            /**
             * @see javax.microedition.location.LocationListener#locationUpdated(LocationProvider,
             *      Location)
             */
            public void locationUpdated(final LocationProvider provider,
                    final Location location) {
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
                    _lastValid = System.currentTimeMillis();
                    _uiApp.invokeLater(new Runnable() {
                        /**
                         * @see java.lang.Runnable#run()
                         */
                        public void run() {
                            // Update UI to reflect new location
                            _lastValidFixField
                                    .setText(dateFormatter(_lastValid));
                            _currentModeField
                                    .setText(getLocMethodString(location
                                            .getLocationMethod()));

                            final StringBuffer buff = new StringBuffer();
                            buff.append(location.getQualifiedCoordinates()
                                    .getLatitude());
                            buff.append(' ');
                            buff.append(location.getQualifiedCoordinates()
                                    .getLongitude());
                            buff.append(' ');
                            buff.append(location.getQualifiedCoordinates()
                                    .getAltitude());
                            buff.append(' ');
                            _currentLocationField.setText(buff.toString());

                            _currentSatelliteCountField
                                    .setText(getNumSatellites(location));
                            _numberValidUpdatesField.setText(Integer
                                    .toString(++_validUpdates));
                            _numberUnassistedUpdatesField.setText(Integer
                                    .toString(++_unassistedUpdates));
                        }
                    });

                    final StringBuffer buff =
                            new StringBuffer("Valid multiple fix: ");
                    buff.append(location.getQualifiedCoordinates()
                            .getLatitude());
                    buff.append(", ");
                    buff.append(location.getQualifiedCoordinates()
                            .getLongitude());
                    buff.append(' ');
                    buff.append(location.getQualifiedCoordinates()
                            .getAltitude());
                    log(buff.toString());

                    log("Method: "
                            + getLocMethodString(location.getLocationMethod()));

                    // If map location enabled, launch Maps application
                    if (_enableMapLocationField.getChecked()) {
                        displayLocationOnMap(location);
                    }
                } else {
                    if (_resetNow) {
                        resetProvider();
                        setupProvider();
                        return;
                    }

                    _uiApp.invokeLater(new Runnable() {
                        /**
                         * @see java.lang.Runnable#run()
                         */
                        public void run() {
                            _currentLocationField.setText("Invalid");
                            _currentSatelliteCountField.setText("-");
                            _numberInvalidUpdatesField.setText(Integer
                                    .toString(++_inValidUpdates));
                        }
                    });
                    log("Invalid multiple fix");

                    // If invalid fixes have exceeded allowed time, reset the
                    // location provider
                    if (System.currentTimeMillis() - _lastValid >= Integer
                            .parseInt(_maxInvalidTimeField.getText()) * 1000
                            && System.currentTimeMillis() - _lastReset >= Integer
                                    .parseInt(_maxInvalidTimeField.getText()) * 1000
                            || System.currentTimeMillis() - _lastReset >= Integer
                                    .parseInt(_maxInvalidTimeField.getText()) * 1000) {
                        final StringBuffer buff =
                                new StringBuffer(
                                        "Resetting Location Provider because: \nInvalid fixes for ");
                        buff.append((System.currentTimeMillis() - _lastValid)
                                / 1000 + " seconds \nNo provider reset for ");
                        buff.append((System.currentTimeMillis() - _lastReset)
                                / 1000 + " seconds");
                        log(buff.toString());

                        if (_isSmartMode) {
                            _fallBackCounter++;
                        }
                        resetProvider();
                        setupProvider();
                    }
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
                                                               // fix and went
                                                               // cold
                    log("State Change: Temp Unavailable");

                    // This is set to indicate that the provider has been reset
                    _lastValid = System.currentTimeMillis();
                    _uiApp.invokeLater(new Runnable() {
                        /**
                         * @see java.lang.Runnable#run()
                         */
                        public void run() {
                            _lastValidFixField
                                    .setText(dateFormatter(_lastValid));
                        }
                    });

                    log("Resetting Location Provider due to TEMPORARILY UNAVAILABLE state");

                    if (_isSmartMode) {
                        log("Smart Mode resetting...");
                        _fallBackCounter++;
                        _resetNow = true;
                    } else {
                        _resetNow = true;
                    }
                    break;
                }
            }
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
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
     * @see net.rim.device.api.ui.Screen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu, int)
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
                getClass().getResourceAsStream("/resource/help_core.txt");
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
}
