/*
 * StreamDataScreen.java
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

package com.rim.samples.device.communicationapidemo.view;

import net.rim.device.api.io.messaging.ByteMessage;
import net.rim.device.api.io.messaging.Message;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.CommunicationController;
import com.rim.samples.device.communicationapidemo.ResponseCallback;
import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class StreamDataScreen extends MainScreen {
    private final EditField _uriSenderField;
    private final ResponseCallback _callback;
    private boolean _pending;

    /**
     * Creates a new StreamDataScreen object
     */
    public StreamDataScreen(final CommunicationController controller) {
        setTitle("Stream Data Upload");

        _callback = new StreamDataScreenCallback(this);

        // Initialize UI components
        final LabelField instructions = new LabelField("", Field.NON_FOCUSABLE);
        _uriSenderField =
                new EditField("Upload URI:",
                        CommunicationController.ECHO_SERVER_URI + "TEXT", 140,
                        0);

        final FullWidthButton backButton = new FullWidthButton("Back");
        backButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                ((UiApplication) StreamDataScreen.this.getApplication())
                        .popScreen(StreamDataScreen.this);
            }
        });

        final FullWidthButton postButton = new FullWidthButton("Upload Data");
        postButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (!_pending) {
                    _pending = true;
                    controller.uploadStream(_uriSenderField.getText(),
                            _callback);
                } else {
                    Dialog.alert("Previous request pending state...");
                }

            }
        });

        // Add components to screen
        add(backButton);
        add(_uriSenderField);
        add(postButton);
        add(new SeparatorField());
        add(instructions);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    private static final class StreamDataScreenCallback extends
            ResponseCallback {
        private final StreamDataScreen _screen;

        private StreamDataScreenCallback(final StreamDataScreen screen) {
            _screen = screen;
        }

        public void onResponse(final Message message) {
            if (message != null) {

                String alertString =
                        "RECEIVED[id: " + message.getMessageId() + "]:";

                final String stringPayload =
                        ((ByteMessage) message).getStringPayload();
                if (stringPayload != null) {
                    alertString += "\n" + stringPayload;
                }
                Dialog.alert(alertString);

            }
            _screen._pending = false;
        }

        public void onTimeout(final int timeout) {
            _screen._pending = false;
            final String timeoutMessage =
                    "Wait for response: timed out after " + timeout + " sec";
            Dialog.alert(timeoutMessage);
        }
    }
}
