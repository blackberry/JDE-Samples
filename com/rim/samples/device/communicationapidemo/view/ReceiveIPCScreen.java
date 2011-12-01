/*
 * ReceiveIPCScreen.java
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

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.CommunicationController;
import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class ReceiveIPCScreen extends MainScreen {
    private final EditField _uriReceiverField;
    private final CheckboxField _isBlocking;
    private final CheckboxField _autoStartEnabled;
    private static final String PATH_STRING = "/test2";

    /**
     * Creates a new ReceiveIPCScreen object
     */
    public ReceiveIPCScreen(final CommunicationController controller) {
        setTitle("IPC/Local Receiver");

        // Initialize UI components
        final LabelField instructions =
                new LabelField(
                        "Register receiver to listen for pushes on local:///<path>."
                                + " The push message should be sent from Send Messages section "
                                + "of this app or the Communication API Local Helper app . "
                                + "The sender destination URI for the push message "
                                + "should be: local://<AppName>/<path>."
                                + "Blocking receiver listens only for one message "
                                + "and exits, while non-blocking receives all messages sent to it.",
                        Field.NON_FOCUSABLE);

        _uriReceiverField =
                new EditField("Receiver URI:", "local://" + PATH_STRING, 140, 0);

        _isBlocking = new CheckboxField("Blocking", false);
        _isBlocking.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                // Avoid conflict blocking and non-blocking destinations with
                // the same name
                if (_isBlocking.getChecked()) {
                    _uriReceiverField.setText("local://" + PATH_STRING + "9");
                } else {
                    _uriReceiverField.setText("local://" + PATH_STRING);
                }

            }
        });

        _autoStartEnabled =
                new CheckboxField("Auto-start when message arrives", true);

        final FullWidthButton backButton = new FullWidthButton("Back");
        backButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                ((UiApplication) ReceiveIPCScreen.this.getApplication())
                        .popScreen(ReceiveIPCScreen.this);
            }
        });

        final FullWidthButton startButton =
                new FullWidthButton("Start Receiver");
        startButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (_isBlocking.getChecked()) {
                    // Blocking receiver
                    controller.startBlockingReceiverIPC(_uriReceiverField
                            .getText(), _autoStartEnabled.getChecked(),
                            CommunicationController.TIMEOUT);
                } else {
                    // Non blocking receiver
                    controller.startNonBlockingReceiverIPC(_uriReceiverField
                            .getText(), _autoStartEnabled.getChecked());
                }
            }
        });

        final FullWidthButton pauseButton =
                new FullWidthButton("Pause Receiver");
        pauseButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.pauseReceiver(_uriReceiverField.getText());
            }
        });

        final FullWidthButton resumeButton =
                new FullWidthButton("Resume Receiver");
        resumeButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.resumeReceiver(_uriReceiverField.getText());
            }
        });

        final FullWidthButton releaseButton =
                new FullWidthButton("Release Receiver");
        releaseButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.releaseReceiver(_uriReceiverField.getText());
            }
        });

        final FullWidthButton destroyButton =
                new FullWidthButton("Destroy Receiver");
        destroyButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.destroyReceiver(_uriReceiverField.getText());
            }
        });

        // Add components to screen
        add(backButton);
        add(_uriReceiverField);
        add(_isBlocking);
        add(_autoStartEnabled);
        add(startButton);
        add(pauseButton);
        add(resumeButton);
        add(releaseButton);
        add(destroyButton);
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
}
