/*
 * ClientScreen.java
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

package com.rim.samples.device.bluetoothjsr82demo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

/**
 * Client Screen class for the BluetoothJSR82Demo application
 */
public final class ClientScreen extends MainScreen implements DiscoveryListener {
    private int _uuid;
    private RichTextField _statusField;
    private RemoteDevice _remoteDevice;
    private String _url;
    private BluetoothTableModelAdapter _model;
    private TableView _view;
    private DiscoveryAgent _discoveryAgent;
    private final Vector _remoteDevices = new Vector();

    /**
     * Creates a new ClientScreen object
     */
    public ClientScreen(final int uuid) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Bluetooth JSR82 Demo Client");

        _uuid = uuid;
        _statusField = new RichTextField(Field.NON_FOCUSABLE);
        add(_statusField);

        final MenuItem infoScreen =
                new MenuItem(new StringProvider("Info Screen"), 0x230010, 1);
        infoScreen.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                UiApplication.getUiApplication().pushScreen(new InfoScreen());
            }
        }));

        addMenuItem(infoScreen);

        try {
            Dialog.alert("Search for Devices");

            _model = new BluetoothTableModelAdapter();

            _view = new TableView(_model);
            final TableController controller =
                    new TableController(_model, _view);
            controller.setFocusPolicy(TableController.ROW_FOCUS);
            _view.setController(controller);

            // Set the highlight style for the view
            _view.setDataTemplateFocus(BackgroundFactory
                    .createLinearGradientBackground(Color.LIGHTBLUE,
                            Color.LIGHTBLUE, Color.BLUE, Color.BLUE));

            // Create a data template that will format the model data as an
            // array of Fields
            final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
                public Field[] getDataFields(final int modelRowIndex) {
                    final RemoteDevice _remoteDevice =
                            (RemoteDevice) _model.getRow(modelRowIndex);

                    final Field[] fields =
                            { new LabelField(_remoteDevice.toString(),
                                    Field.NON_FOCUSABLE) };
                    return fields;
                }
            };

            // Define the regions of the data template and column/row size
            dataTemplate.createRegion(new XYRect(0, 0, 1, 1));
            dataTemplate.setColumnProperties(0, new TemplateColumnProperties(
                    Display.getWidth()));
            dataTemplate.setRowProperties(0, new TemplateRowProperties(24));

            _view.setDataTemplate(dataTemplate);
            dataTemplate.useFixedHeight(true);

            // Add the bluetooth list to the screen
            add(_view);

            _remoteDevices.setSize(0);
            _discoveryAgent = LocalDevice.getLocalDevice().getDiscoveryAgent();
            _discoveryAgent
                    .startInquiry(DiscoveryAgent.GIAC, ClientScreen.this);

            updateStatus("Searching...");
        } catch (final BluetoothStateException bse) {
            BluetoothJSR82Demo.errorDialog(bse.toString());
        }
    }

    /**
     * Updates the text displayed by the status field
     * 
     * @param message
     *            the text to be displayed
     */
    public void updateStatus(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            /**
             * @see java.lang.Runnable#run()
             */
            public void run() {
                _statusField.setText(_statusField.getText() + "\n" + message);
            }
        });
    }

    /**
     * Adapter for displaying Bluetooth device information in table format
     */
    private class BluetoothTableModelAdapter extends TableModelAdapter {

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _remoteDevices.size();
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns() {
            return 1;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object)
         */
        public boolean doAddRow(final Object row) {
            _remoteDevices.addElement(row);
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        public Object doGetRow(final int index) {
            return _remoteDevices.elementAt(index);
        }
    }

    // DiscoveryListener
    // methods*************************************************

    /**
     * @see javax.bluetooth.DiscoveryListener#diviceDiscovered(RemoteDevice,
     *      DeviceClass)
     */
    public void deviceDiscovered(final RemoteDevice remoteDevice,
            final DeviceClass cod) {
        if (!_remoteDevices.contains(remoteDevice)) {
            _model.addRow(remoteDevice);
        }
    }

    /**
     * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
     */
    public void inquiryCompleted(final int discType) {
        delete(_statusField);
        add(_view);

        final MenuItem connectToDevice =
                new MenuItem(new StringProvider("Connect to Device"), 0x230020,
                        0);
        connectToDevice.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                ClientScreen.this.deleteAll();
                _statusField = new RichTextField();
                ClientScreen.this.add(_statusField);
                try {
                    final UUID[] uuidSet = { new UUID(_uuid) };
                    final int[] attrSet = { 0x0100 };
                    final RemoteDevice _remoteDevice =
                            (RemoteDevice) _model.getRow(_view
                                    .getRowNumberWithFocus());
                    _discoveryAgent.searchServices(attrSet, uuidSet,
                            _remoteDevice, ClientScreen.this);
                } catch (final BluetoothStateException bse) {
                    BluetoothJSR82Demo.errorDialog(bse.toString());
                }
            }
        }));
        addMenuItem(connectToDevice);
    }

    /**
     * @see javax.bluetooth.DiscoveryListener#servicesDiscovered(int,
     *      ServiceRecord)
     */
    public void servicesDiscovered(final int transID,
            final ServiceRecord[] servRecord) {
        _url = servRecord[0].getConnectionURL(0, false);
    }

    /**
     * @see javax.bluetooth.DiscoveryListener#serviceSearchCompleted(int, int)
     */
    public void serviceSearchCompleted(final int transID, final int respCode) {
        switch (respCode) {

        // If the search is completed and the server URL was found,
        // connect to the URL and handle the connection.
        case DiscoveryListener.SERVICE_SEARCH_COMPLETED:
            if (_url != null) {
                switch (_uuid) {
                case BluetoothJSR82Demo.SPP_UUID:
                    final SPPConnectThread sppThread = new SPPConnectThread();
                    sppThread.start();
                    break;

                case BluetoothJSR82Demo.L2CAP_UUID:
                    final L2CAPConnectThread l2capThread =
                            new L2CAPConnectThread();
                    l2capThread.start();
                    break;

                case BluetoothJSR82Demo.OPP_UUID:
                    final OPPConnectThread oppThread = new OPPConnectThread();
                    oppThread.start();
                    break;
                }
            }
            break;

        case DiscoveryListener.SERVICE_SEARCH_DEVICE_NOT_REACHABLE:
            Dialog.alert("Search service device not reachable");
            break;

        case DiscoveryListener.SERVICE_SEARCH_NO_RECORDS:
            Dialog.alert("Service search has found no records");
            break;

        case DiscoveryListener.SERVICE_SEARCH_ERROR:
            Dialog.alert("Service search error");
            break;

        case DiscoveryListener.SERVICE_SEARCH_TERMINATED:
            Dialog.alert("Service search terminated");
            break;
        }
    }

    // SPP CLIENT THREAD
    // ********************************************************************

    /**
     * A thread that connects to an SPP Server, sends a single lined message and
     * waits for a reponse.
     */
    class SPPConnectThread extends Thread {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                StreamConnection connection = null;
                DataOutputStream os = null;
                DataInputStream is = null;
                try {
                    // Send the server a request to open a connection
                    connection = (StreamConnection) Connector.open(_url);
                    updateStatus("[CLIENT] SPP session created");

                    // Send a message to the server
                    final String message = "\nJSR-82 CLIENT says hello!";
                    updateStatus("[CLIENT] Sending message....");
                    os = connection.openDataOutputStream();
                    os.write(message.getBytes());
                    os.flush();

                    // Read a message
                    is = connection.openDataInputStream();
                    final byte[] buffer = new byte[1024];
                    final int readBytes = is.read(buffer);
                    final String receivedMessage =
                            new String(buffer, 0, readBytes);
                    updateStatus("[CLIENT] Message received: "
                            + receivedMessage);
                } finally {
                    os.close();
                    is.close();
                    connection.close();
                    updateStatus("[CLIENT] SPP session closed");
                }
            } catch (final IOException ioe) {
                BluetoothJSR82Demo.errorDialog(ioe.toString());
            }
        }
    }

    // L2CAP CLIENT THREAD
    // ********************************************************************

    /**
     * A thread that connects to a selected L2CAP server, sends a single lined
     * message and waits for a response.
     */
    class L2CAPConnectThread extends Thread {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                L2CAPConnection connection = null;
                try {
                    // Send the server a request to open a connection
                    connection = (L2CAPConnection) Connector.open(_url);
                    updateStatus("[ClIENT] L2CAP session created");

                    // Send a message to the server
                    final String message =
                            "\n[CLIENT] JSR-82 CLIENT says hello!";
                    updateStatus("Sending message....");
                    connection.send(message.getBytes());

                    // Read a message
                    final byte[] buffer = new byte[1024];
                    final int readBytes = connection.receive(buffer);
                    final String receivedMessage =
                            new String(buffer, 0, readBytes);
                    updateStatus("[CLIENT] Message received: "
                            + receivedMessage);
                } finally {
                    connection.close();
                    updateStatus("[ClIENT] L2CAP session closed");
                }
            } catch (final IOException ioe) {
                BluetoothJSR82Demo.errorDialog(ioe.toString());
            }
        }
    }

    // OPP CLIENT THREAD
    // ********************************************************************

    /**
     * A thread that connects to a selected OPP Server and pushes a single file
     * with metadata.
     */
    class OPPConnectThread extends Thread {
        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                Connection connection = null;
                OutputStream outputStream = null;
                Operation putOperation = null;
                ClientSession cs = null;
                try {
                    // Send a request to the server to open a connection
                    connection = Connector.open(_url);
                    cs = (ClientSession) connection;
                    cs.connect(null);
                    updateStatus("[CLIENT] OPP session created");

                    // Send a file with meta data to the server
                    final byte filebytes[] = "[CLIENT] Hello..".getBytes();
                    final HeaderSet hs = cs.createHeaderSet();
                    hs.setHeader(HeaderSet.NAME, "test.txt");
                    hs.setHeader(HeaderSet.TYPE, "text/plain");
                    hs.setHeader(HeaderSet.LENGTH, new Long(filebytes.length));

                    putOperation = cs.put(hs);
                    updateStatus("[CLIENT] Pushing file: " + "test.txt");
                    updateStatus("[CLIENT] Total file size: "
                            + filebytes.length + " bytes");

                    outputStream = putOperation.openOutputStream();
                    outputStream.write(filebytes);
                    updateStatus("[CLIENT] File push complete");
                } finally {
                    outputStream.close();
                    putOperation.close();
                    cs.disconnect(null);
                    connection.close();
                    updateStatus("[CLIENT] Connection Closed");
                }
            } catch (final Exception e) {
                BluetoothJSR82Demo.errorDialog(e.toString());
            }
        }
    }
}
