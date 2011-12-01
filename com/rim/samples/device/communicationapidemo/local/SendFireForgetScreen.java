/*
 * SendFireForgetScreen.java
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

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;
import com.rim.samples.device.communicationapidemo.util.Utils;

public final class SendFireForgetScreen extends MainScreen {
    private final EditField _uriSenderField;
    private final EditField _pathField;
    private final CheckboxField _isLocal;
    private static final String PATH_STRING = "/test2";

    // Application name to which messages are sent to
    private static final String APP_NAME = "CommunicationAPIDemo";

    /**
     * Creates a new SendFireForgetScreen object
     */
    public SendFireForgetScreen(final CommunicationController controller) {
        setTitle("Send IPC Messages (Fire-and-Forget)");

        // Initialize UI components
        _isLocal = new CheckboxField("Local Address ", true);
        _isLocal.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (_isLocal.getChecked()) {
                    // Local
                    _pathField.setText(PATH_STRING);
                    updateSenderField();
                } else {
                    // Http
                    _pathField.setText("--NOT USED--");
                    _uriSenderField
                            .setText(CommunicationController.ECHO_SERVER_URI
                                    + "TEXT");
                }

            }
        });

        _pathField = new EditField("Path:", PATH_STRING, 140, 0);
        _pathField.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                updateSenderField();
            }
        });

        _uriSenderField = new EditField("Sender URI:", "", 140, 0);

        final FullWidthButton backButton = new FullWidthButton("Back");
        backButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                ((UiApplication) SendFireForgetScreen.this.getApplication())
                        .popScreen(SendFireForgetScreen.this);
            }
        });

        final FullWidthButton postButton = new FullWidthButton("Send message");
        postButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.sendFireForget(_uriSenderField.getText());
            }
        });

        final LabelField instructions =
                new LabelField(
                        "Enter a destination URL and send a fire-and-forget message to it. Responses are not processed.",
                        Field.NON_FOCUSABLE);

        updateSenderField();

        // Add components to the screen
        add(backButton);
        add(_isLocal);
        add(_pathField);
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

    private void updateSenderField() {
        final String path = _pathField.getText();
        _uriSenderField.setText(Utils.createLocalClientUri(APP_NAME, path));
    }
}
