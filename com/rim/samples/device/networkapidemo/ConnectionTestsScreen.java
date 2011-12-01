/**
 * ConnectionTestsScreen.java
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

package com.rim.samples.device.networkapidemo;

import net.rim.device.api.io.transport.ConnectionAttemptListener;
import net.rim.device.api.io.transport.ConnectionDescriptor;
import net.rim.device.api.io.transport.ConnectionFactory;
import net.rim.device.api.io.transport.TransportDescriptor;
import net.rim.device.api.io.transport.TransportInfo;
import net.rim.device.api.io.transport.options.BisBOptions;
import net.rim.device.api.io.transport.options.TcpCellularOptions;
import net.rim.device.api.io.transport.options.WapOptions;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.util.IntVector;

/**
 * A screen showing all options allowed to open a connection using the Network
 * API. Clicking the "Connect" button with no options specified triggers the
 * Network API to obtain a connection through the first available transport. If
 * connection is not possible through one transport, the next transport is tried
 * and so on until success or failure occurs. Clicking the "Show Options" button
 * shows all available connection options.
 */
public final class ConnectionTestsScreen extends MainScreen implements
        FieldChangeListener, ConnectionAttemptListener {
    // Edit box to enter URL
    private final BasicEditField _urlEditField;

    // Manager containing option fields
    private final VerticalFieldManager _optionFieldsManager;

    // "Show Options" button that shows or hides connection options when clicked
    private final ButtonField _optionsBtn;

    // Connection options
    private final LabelField _labelConnectionOpt;

    // Connection mode: "READ" or "WRITE" or "READ/WRITE"
    private final ObjectChoiceField _connectionMode;

    // Enables connection timeout option when checked
    private final CheckboxField _timeoutSupported;

    // Specifies connection timeout in milliseconds
    private final BasicEditField _connectionTimeout;

    // Enables "tls/ssl" end to end connection options
    private final CheckboxField _endToEndRequired;
    private final CheckboxField _endToEndDesired;

    // Retries options
    private final LabelField _labelRetriesOpt;

    // Specifies how long ConnectionFactory should try to establish a connection
    private final BasicEditField _timeLimit;

    // Specifies how many times ConnectionFactory should try to establish a
    // connection
    private final BasicEditField _attemptsLimit;

    // Specifies retry factor @see ConnectionFactory.setRetryFactor()
    private final BasicEditField _retryFactor;

    // Transport options
    // Enables transport selection and order when checked
    private final CheckboxField _transportSelection;

    // Transport selection types: "none", "TCP Cellular", "Wap", "Wap2", "Mds",
    // "Bis B", "TCP Wifi"
    private final ObjectChoiceField _order1;
    private final ObjectChoiceField _order2;
    private final ObjectChoiceField _order3;
    private final ObjectChoiceField _order4;
    private final ObjectChoiceField _order5;
    private final ObjectChoiceField _order6;

    private final LabelField _labelDisallowedTrasnports;

    // Enables or disables specific transport types
    private final CheckboxField _disallowDirectTCP;
    private final CheckboxField _disallowWap;
    private final CheckboxField _disallowWap2;
    private final CheckboxField _disallowMds;
    private final CheckboxField _disallowBisB;
    private final CheckboxField _disallowWifi;

    // TCP Cellular options
    private final LabelField _labelTcpCellular;

    // Edit boxes to enter APN settings if different from Options>Advanced
    // Options>TCP/IP
    private final EditField _tcpApn;
    private final EditField _tcpApnUser;
    private final EditField _tcpApnPassword;

    // Wap options
    private final LabelField _labelWap;

    // Edit boxes to enter WAP settings if different from Options>Advanced
    // Options>Service Book
    private final EditField _wapGatewayApn;
    private final EditField _wapGatewayIp;
    private final EditField _wapGatewayPort;
    private final EditField _wapSourceIp;
    private final EditField _wapSourcePort;
    private final EditField _wapUser;
    private final EditField _wapPassword;
    private final CheckboxField _wapEnableWTLS;

    // BisB options
    private final LabelField _labelBisB;
    private final EditField _bisBConnectionType;

    private boolean _optionsHidden;

    private final UiApplication _uiApp;

    /**
     * Creates a new ConnectionTestsScreen object
     */
    public ConnectionTestsScreen() {
        setTitle("Connection Tests");

        // Display URL label and edit box on top of the screen
        _urlEditField =
                new BasicEditField("URL: ", "http://www.blackberry.com", 128,
                        BasicEditField.FILTER_URL);
        add(_urlEditField);

        // Display "Connect" button that triggers connection to be established
        // with the specified URL.
        final ButtonField connectBtn =
                new ButtonField("Connect", Field.FIELD_HCENTER
                        | ButtonField.CONSUME_CLICK);
        connectBtn.setChangeListener(this);

        // Display "Show Options" button that displays connection options when
        // clicked
        _optionsBtn =
                new ButtonField("Show Options", Field.FIELD_HCENTER
                        | ButtonField.CONSUME_CLICK);
        _optionsBtn.setChangeListener(this);
        _optionsHidden = true;

        add(new SeparatorField());

        // Region to layout "Connect" and "Show Options" buttons side by side
        final HorizontalFieldManager hfmBtns =
                new HorizontalFieldManager(Field.FIELD_HCENTER);
        hfmBtns.add(connectBtn);
        hfmBtns.add(_optionsBtn);
        add(hfmBtns);

        add(new SeparatorField());

        _labelConnectionOpt = new LabelField("Connection Settings [optional]");

        // Connection mode
        final String[] connectionModes = { "READ", "WRITE", "READ/WRITE" };
        _connectionMode =
                new ObjectChoiceField("  Connection Mode: ", connectionModes, 2);

        // Connection timeout
        _timeoutSupported = new CheckboxField("  Support timeouts", false);
        _connectionTimeout =
                new BasicEditField("Connection timeout: ", "", 6,
                        BasicEditField.FILTER_INTEGER);

        // Connection security settings for tls/ssl
        _endToEndRequired =
                new CheckboxField("  tls/ssl end to end required", false);
        _endToEndDesired =
                new CheckboxField("  tls/ssl end to end desired", false);

        // Connection retry options
        _labelRetriesOpt = new LabelField("Retry options [optional]");
        _timeLimit =
                new BasicEditField("  Time Limit for Connections: ", "0", 6,
                        BasicEditField.FILTER_INTEGER);
        _attemptsLimit =
                new BasicEditField("  Attempts Limit: ", "0", 4,
                        BasicEditField.FILTER_INTEGER);
        _retryFactor =
                new BasicEditField("  Retry Factor: ", "0", 6,
                        BasicEditField.FILTER_INTEGER);

        // Transport selection
        final String[] transportNames =
                { "none", "TCP Cellular", "Wap", "Wap2", "Mds", "Bis B",
                        "TCP Wifi" };

        // Preferred transport types option
        _transportSelection =
                new CheckboxField("Preferred Transport Types [Optional]", false);
        _transportSelection.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                if (_transportSelection.getChecked()) {
                    // Transport selection check box is checked. Enable drop
                    // downs for choosing the preferred transport.
                    _order1.setEditable(true);
                    _order2.setEditable(true);
                    _order3.setEditable(true);
                    _order4.setEditable(true);
                    _order5.setEditable(true);
                    _order6.setEditable(true);
                } else {
                    // Transport selection check box is not checked. Disable
                    // drop
                    // downs for choosing the preferred transport and set them
                    // to
                    // "none".
                    _order1.setSelectedIndex(0);
                    _order1.setEditable(false);
                    _order2.setSelectedIndex(0);
                    _order2.setEditable(false);
                    _order3.setSelectedIndex(0);
                    _order3.setEditable(false);
                    _order4.setSelectedIndex(0);
                    _order4.setEditable(false);
                    _order5.setSelectedIndex(0);
                    _order5.setEditable(false);
                    _order6.setSelectedIndex(0);
                    _order6.setEditable(false);
                }
            }
        });

        // Initialize preferred transport types option.
        // By default disable the options.
        _order1 = new ObjectChoiceField("First:", transportNames);
        _order1.setEditable(false);
        _order2 = new ObjectChoiceField("Second:", transportNames);
        _order2.setEditable(false);
        _order3 = new ObjectChoiceField("Third:", transportNames);
        _order3.setEditable(false);
        _order4 = new ObjectChoiceField("Forth:", transportNames);
        _order4.setEditable(false);
        _order5 = new ObjectChoiceField("Fifth:", transportNames);
        _order5.setEditable(false);
        _order6 = new ObjectChoiceField("Sixth:", transportNames);
        _order6.setEditable(false);

        // Initialize disallowed transport types check boxes
        _labelDisallowedTrasnports =
                new LabelField("Disallowed Transport Types [optional]:");
        _disallowDirectTCP = new CheckboxField("TCP Cellular", false);
        _disallowWap = new CheckboxField("Wap", false);
        _disallowWap2 = new CheckboxField("Wap2", false);
        _disallowMds = new CheckboxField("Mds", false);
        _disallowBisB = new CheckboxField("Bis B", false);
        _disallowWifi = new CheckboxField("TCP Wifi", false);

        // Initialize TCP Cellular transport options
        _labelTcpCellular = new LabelField("TCP Cellular Options [optional]:");
        _tcpApn = new EditField("  APN: ", "");
        _tcpApnUser = new EditField("  Username: ", "");
        _tcpApnPassword = new EditField("  Password: ", "");

        // Initialize WAP transport options
        _labelWap = new LabelField("WAP Options [optional]:");
        _wapGatewayApn = new EditField("  Gateway APN: ", "");
        _wapGatewayIp = new EditField("  Gateway IP: ", "");
        _wapGatewayPort = new EditField("  Gateway Port: ", "");
        _wapSourceIp = new EditField("  Source IP: ", "");
        _wapSourcePort = new EditField("  Source Port: ", "");
        _wapUser = new EditField("  Username: ", "");
        _wapPassword = new EditField("  Password: ", "");

        _wapEnableWTLS = new CheckboxField("  Enable WTLS", false);

        // Initialize BisB transport options
        _labelBisB = new LabelField("BisB Options [mandatory for BisB]");
        _bisBConnectionType = new EditField("  Connection Type: ", "");

        // Add options to a VerticalFieldManager
        _optionFieldsManager = new VerticalFieldManager();
        _optionFieldsManager.add(_labelConnectionOpt);
        _optionFieldsManager.add(_connectionMode);
        _optionFieldsManager.add(_timeoutSupported);
        _optionFieldsManager.add(_endToEndRequired);
        _optionFieldsManager.add(_endToEndDesired);
        _optionFieldsManager.add(_connectionTimeout);

        _optionFieldsManager.add(new SeparatorField());

        _optionFieldsManager.add(_labelRetriesOpt);
        _optionFieldsManager.add(_timeLimit);
        _optionFieldsManager.add(_attemptsLimit);
        _optionFieldsManager.add(_retryFactor);

        _optionFieldsManager.add(new SeparatorField());

        _optionFieldsManager.add(_transportSelection);
        _optionFieldsManager.add(_order1);
        _optionFieldsManager.add(_order2);
        _optionFieldsManager.add(_order3);
        _optionFieldsManager.add(_order4);
        _optionFieldsManager.add(_order5);
        _optionFieldsManager.add(_order6);

        _optionFieldsManager.add(new SeparatorField());

        _optionFieldsManager.add(_labelDisallowedTrasnports);
        _optionFieldsManager.add(_disallowDirectTCP);
        _optionFieldsManager.add(_disallowWap);
        _optionFieldsManager.add(_disallowWap2);
        _optionFieldsManager.add(_disallowMds);
        _optionFieldsManager.add(_disallowBisB);
        _optionFieldsManager.add(_disallowWifi);

        _optionFieldsManager.add(new SeparatorField());

        _optionFieldsManager.add(_labelTcpCellular);
        _optionFieldsManager.add(_tcpApn);
        _optionFieldsManager.add(_tcpApnUser);
        _optionFieldsManager.add(_tcpApnPassword);

        _optionFieldsManager.add(new SeparatorField());

        _optionFieldsManager.add(_labelWap);
        _optionFieldsManager.add(_wapGatewayApn);
        _optionFieldsManager.add(_wapGatewayIp);
        _optionFieldsManager.add(_wapGatewayPort);
        _optionFieldsManager.add(_wapUser);
        _optionFieldsManager.add(_wapPassword);
        _optionFieldsManager.add(_wapSourceIp);
        _optionFieldsManager.add(_wapSourcePort);
        _optionFieldsManager.add(_wapEnableWTLS);

        _optionFieldsManager.add(new SeparatorField());

        _optionFieldsManager.add(_labelBisB);
        _optionFieldsManager.add(_bisBConnectionType);

        _uiApp = UiApplication.getUiApplication();
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Prevents the save dialog from being displayed
        return true;
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _optionsBtn) {
            // Shows or hides connection options
            toggleOptions();
            return;
        }

        // Try to get connection
        final String url = _urlEditField.getText().trim();

        if ("".equals(url)) {
            // URL is empty, display a warning
            displayMessage("Please provide a URL");
            return;
        }

        // Create ConnectionFactory
        final ConnectionFactory factory = new ConnectionFactory();

        // Register as listener
        factory.setConnectionAttemptListener(this);

        // Set connection mode
        switch (_connectionMode.getSelectedIndex()) {
        case 0:
            factory.setConnectionMode(ConnectionFactory.ACCESS_READ);
            break;
        case 1:
            factory.setConnectionMode(ConnectionFactory.ACCESS_WRITE);
            break;
        case 2:
            factory.setConnectionMode(ConnectionFactory.ACCESS_READ_WRITE);
            break;
        }

        // Set connection timeout
        factory.setTimeoutSupported(_timeoutSupported.getChecked());

        factory.setEndToEndDesired(_endToEndDesired.getChecked());
        factory.setEndToEndRequired(_endToEndRequired.getChecked());

        try {
            final String connectionTimeoutText = _connectionTimeout.getText();
            if (connectionTimeoutText != null
                    && connectionTimeoutText.trim().length() > 0) {
                // Parse connection timeout text into long
                final long connectionTimeout =
                        Long.parseLong(connectionTimeoutText);
                if (connectionTimeout >= 0) {
                    // Set connection timeout value
                    factory.setConnectionTimeout(connectionTimeout);
                }
            }
        } catch (final NumberFormatException nfe) {
            displayMessage("Long.parseLong(String) threw : " + nfe.toString());
        }

        // Set connection security settings for tls/ssl
        factory.setEndToEndDesired(_endToEndDesired.getChecked());
        factory.setEndToEndRequired(_endToEndRequired.getChecked());

        // Set time limit
        final long timeLimit = Long.parseLong(_timeLimit.getText());
        if (timeLimit > 0) {
            factory.setTimeLimit(timeLimit);
        }

        // Set attempts limit
        final int attemptsLimit = Integer.parseInt(_attemptsLimit.getText());
        if (attemptsLimit > 0) {
            factory.setAttemptsLimit(attemptsLimit);
        }

        // Set retry factor
        final long retryFactor = Long.parseLong(_retryFactor.getText());
        if (retryFactor > 0) {
            factory.setRetryFactor(retryFactor);
        }

        // Set TCP Cellular options
        if (_tcpApn.getText().trim().length() != 0
                || _tcpApnUser.getText().trim().length() != 0
                || _tcpApnPassword.getText().trim().length() != 0) {

            final TcpCellularOptions tcpOptions = new TcpCellularOptions();
            tcpOptions.setApn(_tcpApn.getText().trim());
            tcpOptions.setTunnelAuthUsername(_tcpApnUser.getText().trim());
            tcpOptions.setTunnelAuthPassword(_tcpApnPassword.getText().trim());

            factory.setTransportTypeOptions(
                    TransportInfo.TRANSPORT_TCP_CELLULAR, tcpOptions);
        }

        // Set WAP Options
        if (!_wapGatewayApn.getText().trim().equals("")
                || !_wapGatewayIp.getText().trim().equals("")
                || !_wapGatewayPort.getText().trim().equals("")
                || !_wapSourceIp.getText().trim().equals("")
                || !_wapSourcePort.getText().trim().equals("")
                || !_wapUser.getText().trim().equals("")
                || !_wapPassword.getText().trim().equals("")
                || _wapEnableWTLS.getChecked()) {

            final WapOptions wapOptions = new WapOptions();
            wapOptions.setWapGatewayApn(_wapGatewayApn.getText().trim());
            wapOptions.setWapGatewayIp(_wapGatewayIp.getText().trim());
            wapOptions.setWapGatewayPort(_wapGatewayPort.getText().trim());
            wapOptions.setWapSourceIp(_wapSourceIp.getText().trim());
            wapOptions.setWapSourcePort(_wapSourcePort.getText().trim());
            wapOptions.setTunnelAuthUsername(_wapUser.getText().trim());
            wapOptions.setTunnelAuthPassword(_wapPassword.getText().trim());
            wapOptions.setWapEnableWTLS(_wapEnableWTLS.getChecked());

            factory.setTransportTypeOptions(TransportInfo.TRANSPORT_WAP,
                    wapOptions);
        }

        // Set BisB options
        factory.setTransportTypeOptions(TransportInfo.TRANSPORT_BIS_B,
                new BisBOptions(_bisBConnectionType.getText().trim()));

        // Get preferred transports option
        int[] preferredTransports = null;

        if (_transportSelection.getChecked()) {
            if (_order1.getSelectedIndex() == 0
                    && _order2.getSelectedIndex() == 0
                    && _order3.getSelectedIndex() == 0
                    && _order4.getSelectedIndex() == 0
                    && _order5.getSelectedIndex() == 0
                    && _order6.getSelectedIndex() == 0) {
                // Display a warning if none of the preferred transport is
                // specified when "Preferred Transports" check box is checked
                displayMessage("Please select at least one transport or uncheck \"Preferred Transports\"");
                return;
            }

            // Get rid of the "none" elements
            final IntVector transportIdsVector = new IntVector();
            if (_order1.getSelectedIndex() != 0) {
                transportIdsVector.addElement(_order1.getSelectedIndex());
            }
            if (_order2.getSelectedIndex() != 0) {
                transportIdsVector.addElement(_order2.getSelectedIndex());
            }
            if (_order3.getSelectedIndex() != 0) {
                transportIdsVector.addElement(_order3.getSelectedIndex());
            }
            if (_order4.getSelectedIndex() != 0) {
                transportIdsVector.addElement(_order4.getSelectedIndex());
            }
            if (_order5.getSelectedIndex() != 0) {
                transportIdsVector.addElement(_order5.getSelectedIndex());
            }
            if (_order6.getSelectedIndex() != 0) {
                transportIdsVector.addElement(_order6.getSelectedIndex());
            }

            transportIdsVector.trimToSize();
            preferredTransports = transportIdsVector.getArray();
        }

        // Process disallowed transports
        final IntVector disallowedTransports = new IntVector();

        if (_disallowDirectTCP.getChecked()) {
            disallowedTransports
                    .addElement(TransportInfo.TRANSPORT_TCP_CELLULAR);
        }
        if (_disallowWap.getChecked()) {
            disallowedTransports.addElement(TransportInfo.TRANSPORT_WAP);
        }
        if (_disallowWap2.getChecked()) {
            disallowedTransports.addElement(TransportInfo.TRANSPORT_WAP2);
        }
        if (_disallowMds.getChecked()) {
            disallowedTransports.addElement(TransportInfo.TRANSPORT_MDS);
        }
        if (_disallowBisB.getChecked()) {
            disallowedTransports.addElement(TransportInfo.TRANSPORT_BIS_B);
        }
        if (_disallowWifi.getChecked()) {
            disallowedTransports.addElement(TransportInfo.TRANSPORT_TCP_WIFI);
        }

        // Trim excess
        disallowedTransports.trimToSize();

        // Configure the factory if needed
        if (disallowedTransports.size() > 0) {
            factory.setDisallowedTransportTypes(disallowedTransports.getArray());
        }

        // Get the connection
        Status.show("Attempting Connection...", 500);
        final Thread t =
                new Thread(new ConnectionDescriptorRetriever(url, factory,
                        preferredTransports));
        t.start();
    }

    /**
     * @see ConnectionAttemptListener#attemptFailed(TransportDescriptor, int,
     *      String, Exception)
     */
    public void
            attemptFailed(final TransportDescriptor transport,
                    final int attemptNumber, final String url,
                    final Exception exception) {
        final StringBuffer sb = new StringBuffer();

        // Prepare a connection failure message
        sb.append("Failed attempting: ").append(
                TransportInfo
                        .getTransportTypeName(transport.getTransportType()));
        if (transport.getUid() != null) {
            sb.append(": [").append(transport.getUid()).append(']');
        }
        sb.append("\n Attempt Number: ").append(attemptNumber);
        sb.append("\n exception: ");

        if (exception == null) {
            sb.append("none");
        } else if (exception.getMessage() != null) {
            sb.append(exception.getMessage());
        } else {
            sb.append(exception.getClass().getName());
        }

        showStatus(sb.toString(), 2000);
    }

    /**
     * @see ConnectionAttemptListener#attemptSucceeded(int,
     *      ConnectionDescriptor)
     */
    public void attemptSucceeded(final int attemptNumber,
            final ConnectionDescriptor connection) {
        final TransportDescriptor transport =
                connection.getTransportDescriptor();

        final StringBuffer sb = new StringBuffer();

        // Prepare a connection success message
        sb.append("Sucess attempting: ").append(
                TransportInfo
                        .getTransportTypeName(transport.getTransportType()));
        if (transport.getUid() != null) {
            sb.append(": [").append(transport.getUid()).append(']');
        }
        sb.append("\n Attempt Number: ").append(attemptNumber);

        showStatus(sb.toString(), 2000);
    }

    /**
     * @see ConnectionAttemptListener#attempting(TransportDescriptor, int,
     *      String)
     */
    public boolean attempting(final TransportDescriptor transport,
            final int attemptNumber, final String url) {
        final StringBuffer sb = new StringBuffer();

        // Prepare a connection attempting message
        sb.append("Attempting: ").append(
                TransportInfo
                        .getTransportTypeName(transport.getTransportType()));
        if (transport.getUid() != null) {
            sb.append(": [").append(transport.getUid()).append(']');
        }
        sb.append("\n Attempt Number: ").append(attemptNumber);

        showStatus(sb.toString(), 2000);

        return true;
    }

    /**
     * @see ConnectionAttemptListener#attemptAborted(String, Exception)
     */
    public void attemptAborted(final String url, final Exception exception) {
        showStatus("Attempt Aborted: "
                + (exception != null ? exception.getMessage()
                        : "Reason Unkwown"), 2000);
    }

    /**
     * Displays ConnectionDescriptor details or "Connection Failed" message
     * 
     * @param conDescriptor
     *            The <code>ConnectionDescriptor</code>
     */
    private void
            conDescriptorRetrieved(final ConnectionDescriptor conDescriptor) {
        _uiApp.invokeLater(new Runnable() {
            public void run() {
                if (conDescriptor != null) {
                    // Connection can be established. Display the result
                    // of a connection
                    displayConnectionDescriptor(conDescriptor);
                } else {
                    // Connection can not be established. Display failure
                    // message.
                    displayMessage("Connection Failed");
                }
            }
        });
    }

    /**
     * A class to retrieve a ConnectionDescriptor
     */
    private final class ConnectionDescriptorRetriever implements Runnable {
        // URL provided by the user
        final String _url;

        // Factory that creates connections
        final ConnectionFactory _factory;

        // Preferred transports to use when opening a connection
        final int[] _preferredTransports;

        /**
         * Creates a new ConnectionDescriptorRetriever object
         * 
         * @param url
         *            The URL for this ConnectionDescriptorRetriever
         * @param factory
         *            A ConnectionFactory used to retrieve a connection
         * @param preferredTransports
         *            An array of preferred transport types
         * @throws IllegalArgumentException
         *             if ConnectionFactory argument is null
         */
        ConnectionDescriptorRetriever(final String url,
                final ConnectionFactory factory, final int[] preferredTransports) {
            if (factory == null) {
                throw new IllegalArgumentException();
            }

            _url = url;
            _factory = factory;
            _preferredTransports = preferredTransports;
        }

        /**
         * @see Runnable#run()
         */
        public void run() {
            ConnectionDescriptor conDescriptor = null;

            if (_preferredTransports != null) {
                // Set factory to use preferred transports
                if (_preferredTransports.length > 1) {
                    _factory.setPreferredTransportTypes(_preferredTransports);
                    conDescriptor = _factory.getConnection(_url);
                } else {
                    conDescriptor =
                            _factory.getConnection(_url,
                                    _preferredTransports[0], null);
                }
            } else {
                // Use default factory setting to try all transports until a
                // successful connection is established .
                conDescriptor = _factory.getConnection(_url);
            }

            // Consume the connection
            conDescriptorRetrieved(conDescriptor);
        }
    }

    /**
     * Displays or hides connection options
     */
    private void toggleOptions() {
        if (_optionsHidden) {
            // Update the button to "Hide Options"
            _optionsBtn.setLabel("Hide Options");

            // Show connection options
            add(_optionFieldsManager);
            _optionsHidden = false;

        } else {
            // Update the button to "Show Options"
            _optionsBtn.setLabel("Show Options");

            // Hide connection options
            delete(_optionFieldsManager);
            _optionsHidden = true;
        }
    }

    /**
     * Displays a message in a dialog
     * 
     * @param msg
     *            The message to be displayed
     */
    private void displayMessage(final String msg) {
        _uiApp.invokeLater(new Runnable() {
            /**
             * @see Runnable#run()
             */
            public void run() {
                Dialog.alert(msg);
            }
        });
    }

    /**
     * Shows status dialog for a specified time in milliseconds
     * 
     * @param msg
     *            The message to be displayed
     * @param time
     *            The time in milliseconds the dialog is displayed
     */
    private void showStatus(final String msg, final int time) {
        _uiApp.invokeAndWait(new Runnable() {
            /**
             * @see Runnable#run()
             */
            public void run() {
                Status.show(msg, time);
            }
        });
    }

    /**
     * Displays a connection detail screen
     * 
     * @param con
     *            The ConnectionDescriptor that describes the connection
     */
    private void displayConnectionDescriptor(final ConnectionDescriptor con) {
        _uiApp.pushScreen(new ConnectionDetailsScreen(con, _urlEditField
                .getText()));
    }
}
