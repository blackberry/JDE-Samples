/*
 * ServiceRoutingDemo.java
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

package com.rim.samples.device.serviceroutingdemo;

import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.servicebook.ServiceRouting;
import net.rim.device.api.servicebook.ServiceRoutingListener;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.TreeField;
import net.rim.device.api.ui.component.TreeFieldCallback;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This application showcases how third party applications can take advantage of
 * the service routing API to leverage the serial bypass mechanism that was
 * implemented with 4.0 BES and 4.0 handhelds.
 */
public class ServiceRoutingDemo extends UiApplication implements
        TreeFieldCallback, ServiceRoutingListener {
    private final MainScreen _screen; // Screen shown to the user.
    private final RichTextField _statusField; // Field containing connected or
                                              // disconnected string.
    private final TreeField _treeField; // Tree of service records.
    private String _desktopUID; // UID representing the Desktop service record.
    private final UiApplication _app; // Application instance.

    /**
     * Entry point for application.
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final ServiceRoutingDemo demo = new ServiceRoutingDemo();
        demo.enterEventDispatcher();
    }

    /**
     * Default constructor.
     */
    public ServiceRoutingDemo() {
        // Save the current application instance for later use.
        _app = UiApplication.getUiApplication();

        // Create the MainScreen and add the appropriate UI fields.
        _screen = new MainScreen();
        _screen.setTitle("Service Routing Demo");

        final ServiceRouting sr = ServiceRouting.getInstance();
        sr.addListener(this);
        _statusField =
                new RichTextField(
                        sr.isSerialBypassActive() ? "Serial Bypass: Connected"
                                : "Serial Bypass: Disconnected");
        _screen.add(_statusField);

        _treeField = new TreeField(this, Field.FOCUSABLE);
        _treeField.setEmptyString("* No Service Records *", 0);
        _treeField.setDefaultExpanded(false);
        _screen.add(_treeField);

        pushScreen(_screen);

        // Fill out the tree based on the current service records.
        showServices();
    }

    /**
     * This method will enumerate through all of the services on the handheld
     * and indicate whether that service is enabled for serial bypass. This will
     * also be updated when serial bypass is connected or disconnected for the
     * handheld using the listener.
     */
    private void showServices() {
        // Remove all of the items from the listField.
        _treeField.deleteAll();

        // Get a copy of the ServiceRouting class.
        final ServiceRouting serviceRouting = ServiceRouting.getInstance();

        // Add in our new items by trolling through the ServiceBook API.
        final ServiceBook sb = ServiceBook.getSB();
        final ServiceRecord[] records = sb.getRecords();

        if (records == null) {
            return;
        }

        final int rootNode = _treeField.addChildNode(0, "Service Records");

        final int numRecords = records.length;

        for (int i = 0; i < numRecords; i++) {
            final String name = records[i].getName();
            String description = records[i].getDescription();

            if (description == null || description.length() == 0) {
                description = "No description available";
            }

            final String uid = records[i].getUid();
            final boolean serialBypass =
                    serviceRouting.isSerialBypassActive(uid);

            final int newParentNode = _treeField.addChildNode(rootNode, name);
            _treeField.addChildNode(newParentNode, description);
            _treeField.addChildNode(newParentNode, uid);
            _treeField.addChildNode(newParentNode,
                    serialBypass ? "Serial Bypass: Connected"
                            : "Serial Bypass: Disconnected");

            // Perform the check for the Desktop SerialRecord.
            if (name != null && name.equals("Desktop")) {
                _desktopUID = uid;
            }
        }

        _treeField.setCurrentNode(rootNode); // Set the root as the starting
                                             // point.
    }

    // //////////////////////////////////////////////////////////
    // / ServiceRoutingListener Interface Implementation ///
    // //////////////////////////////////////////////////////////

    /**
     * When a BlackBerry data service routing changes, this method is invoked.
     * <p>
     * 
     * @param service
     *            The relevant service, a UID of a {@link ServiceRecord}.
     * @param serviceState
     *            True if the associated service has undergone a routing change.
     *            You can query for the particular routing method via
     *            {@link ServiceRouting#isSerialBypassActive}.
     */
    public void serviceRoutingStateChanged(final String service,
            final boolean serviceState) {
        // Passing the UpdateRunnable to invokeLater() ensures
        // that it gets executed on this application's event thread.
        final UpdateRunnable runnable =
                new UpdateRunnable(service, serviceState);
        _app.invokeLater(runnable);
    }

    // ///////////////////////////////////////////////////////////
    // / TreeFieldCallback Interface Implementation ///
    // ///////////////////////////////////////////////////////////

    /**
     * Invoked when a particular tree item requires painting.
     * 
     * <p>
     * The graphics context passed to this method represents the entire tree,
     * not just the row for repainting. Accordingly, the y parameter indicates
     * how far down in the field's extent the repaint should occur.
     * 
     * @param treeField
     *            Tree field that requires repainting.
     * @param graphics
     *            Graphics context for the list.
     * @param node
     *            Node to draw.
     * @param y
     *            Distance from the top of the tree field for painting.
     * @param width
     *            Width of the area remaining to draw the item (accounting for
     *            the indent).
     * @param indent
     *            Number of pixels that should be reserved due to the nesting
     *            depth of the current item.
     */
    public void drawTreeItem(final TreeField treeField,
            final Graphics graphics, final int node, final int y,
            final int width, final int indent) {
        if (treeField != _treeField) {
            return;
        }

        final Object cookie = _treeField.getCookie(node);

        if (cookie instanceof String) {
            final String text = (String) cookie;
            graphics.drawText(text, indent, y, DrawStyle.ELLIPSIS, width);
        }
    }

    // ////////////////////////////////////////////////////////////////
    // / UpdateRunnable Implementation ///
    // ////////////////////////////////////////////////////////////////

    /**
     * This class is responsible for displaying the result of a service change.
     */
    private class UpdateRunnable implements Runnable {
        private final String _service;
        private final boolean _serviceState;

        /**
         * Associates the service changed and its new state with this object.
         * 
         * @param service
         *            The service that was changed
         * @param serviceState
         *            The new service state
         */
        private UpdateRunnable(final String service, final boolean serviceState) {
            _service = service;
            _serviceState = serviceState;
        }

        /**
         * This displays whether or not serial bypass is still active and
         * whether the desktop serial bypass is still available if the desktop
         * service had changed state.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            _statusField.setText(ServiceRouting.getInstance()
                    .isSerialBypassActive() ? "Serial Bypass: Connected"
                    : "Serial Bypass: Disconnected");
            showServices();

            // Show an indication to the user if the desktop service is
            // available over Serial bypass.
            if (_service.equals(_desktopUID)) {
                if (_serviceState) {
                    Dialog.inform("Desktop Serial Bypass available");
                } else {
                    Dialog.inform("Desktop Serial Bypass unavailable");
                }
            }
        }
    }
}
