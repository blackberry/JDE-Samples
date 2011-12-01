/**
 * CommunicationController.java
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

package com.rim.samples.device.communicationapidemo.local;

import net.rim.device.api.io.URI;
import net.rim.device.api.io.messaging.Context;
import net.rim.device.api.io.messaging.DestinationFactory;
import net.rim.device.api.io.messaging.FireAndForgetDestination;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

import com.rim.samples.device.communicationapidemo.util.Utils;

/**
 * Contains methods which demonstrate Communication API use cases
 */
public final class CommunicationController {
    // URI of the simple echo server which responds to http get request sent
    // from
    // the application by sending back requested resource (xml, text, json,
    // etc).
    public static String ECHO_SERVER_URI;

    public static int TIMEOUT; // Response timeout
    private static UiApplication _app;

    private final Context _context;

    /**
     * Creates a new CommunicationController object
     */
    public CommunicationController() {
        _context = new Context("MyContext");
        _app = UiApplication.getUiApplication();
        try {
            // Read settings from config.xml
            ECHO_SERVER_URI = Utils.getEchoServerUri();
            TIMEOUT = Utils.getTimeout();
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Sends message to the provided URI using FireAndForget destination
     * 
     * @param uriSenderStr
     *            Sender destination URI
     */
    public void sendFireForget(final String uriSenderStr) {
        FireAndForgetDestination fireForgetDest = null;

        try {
            fireForgetDest =
                    (FireAndForgetDestination) DestinationFactory
                            .getSenderDestination(_context.getName(), URI
                                    .create(uriSenderStr));

            if (fireForgetDest == null) {
                fireForgetDest =
                        DestinationFactory.createFireAndForgetDestination(
                                _context, URI.create(uriSenderStr));
            }

            final int msgId = fireForgetDest.sendNoResponse();

            alertDialog("Message [id:" + msgId + "] has been sent!");
        } catch (final Exception e) {
            alertDialog(e.toString());
        }
    }

    /**
     * Displays a pop-up dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void alertDialog(final String message) {
        _app.invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }
}
