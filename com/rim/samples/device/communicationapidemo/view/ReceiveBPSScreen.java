/*
 * ReceiveBPSScreen.java
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
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.CommunicationController;
import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class ReceiveBPSScreen extends MainScreen {
    private final EditField _contentField;
    private final EditField _uriField;
    private final EditField _appIdField;
    private final EditField _uriReceiverField;

    /**
     * Create a new ReceiveBPSScreen object
     */
    public ReceiveBPSScreen(final CommunicationController controller) {
        setTitle("BPS Push");

        // Initialize UI components
        _uriField =
                new EditField("BPS URI:", "http://pushapi.eval.blackberry.com",
                        140, 0);
        _uriReceiverField =
                new EditField("Listen URI:", "local://:11111/test2", 140, 0); // where
                                                                              // 11111
                                                                              // -
                                                                              // is
                                                                              // the
                                                                              // push
                                                                              // port
        _appIdField = new EditField("Application ID:", "", 140, 0);

        final FullWidthButton backButton = new FullWidthButton("Back");
        backButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                ((UiApplication) ReceiveBPSScreen.this.getApplication())
                        .popScreen(ReceiveBPSScreen.this);
            }
        });

        _contentField =
                new EditField("Content provider URI:",
                        "https://10.11.23.45:8443/sample-app/subscribe", 140, 0);

        final FullWidthButton postButton =
                new FullWidthButton("Subscribe for push");
        postButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.registerBPSPush(_appIdField.getText(), _uriField
                        .getText(), _contentField.getText(), _uriReceiverField
                        .getText());
            }
        });

        final LabelField instructions =
                new LabelField(
                        "This test allows you to register for pushes from a content provider. "
                                + "You must enter the URL of the BPS server as well as the content provider URL.",
                        Field.NON_FOCUSABLE);

        // Add components to screen
        add(backButton);
        add(_uriField);
        add(_appIdField);
        add(_contentField);
        add(_uriReceiverField);
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
}
