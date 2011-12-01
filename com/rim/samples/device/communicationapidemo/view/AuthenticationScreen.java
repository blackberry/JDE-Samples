/*
 * AuthenticationScreen.java
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

public final class AuthenticationScreen extends MainScreen {
    private final EditField _uriField;
    private final EditField _userIdField;
    private final EditField _passwordField;
    private final ResponseCallback _callback;
    private boolean _pending;

    /**
     * Creates a new AuthenticationScreen object
     */
    public AuthenticationScreen(final CommunicationController controller) {
        setTitle("Basic Authentication");

        _callback = new AuthentificationScreenCallback(this);

        // Initialize UI components
        final FullWidthButton backButton = new FullWidthButton("Back");
        _uriField =
                new EditField("Enter URL:",
                        "http://twitter.com/statuses/user_timeline.json", 140,
                        0);
        _userIdField = new EditField("Username:", "debobb", 140, 0);
        _passwordField = new EditField("Password:", "debo2010", 140, 0);

        backButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                ((UiApplication) AuthenticationScreen.this.getApplication())
                        .popScreen(AuthenticationScreen.this);
            }
        });

        final FullWidthButton postButton = new FullWidthButton("Get Data");
        postButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (!_pending) {
                    _pending = true;
                    controller.authenticate(_uriField.getText(), _userIdField
                            .getText(), _passwordField.getText(), _callback);
                } else {
                    Dialog.alert("Previous request pending state...");
                }

            }
        });

        final LabelField instructions =
                new LabelField(
                        "This screen allows you to set credentials on your request. To test authentication, "
                                + "enter a URL that needs basic authentication and add the username and password parameters to the request.",
                        Field.NON_FOCUSABLE);

        // Add components to screen
        add(backButton);
        add(_uriField);
        add(_userIdField);
        add(_passwordField);
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

    private static final class AuthentificationScreenCallback extends
            ResponseCallback {

        private final AuthenticationScreen _screen;

        private AuthentificationScreenCallback(final AuthenticationScreen screen) {
            _screen = screen;
        }

        public void onResponse(final Message message) {
            if (message != null) {

                String alertString =
                        " :RECEIVED[id: " + message.getMessageId() + "]:";

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
