/*
 * SendBlockScreen.java
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
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.GaugeField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BorderFactory;

import com.rim.samples.device.communicationapidemo.CommunicationController;
import com.rim.samples.device.communicationapidemo.CommunicationControllerListener;
import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class SendBlockScreen extends MainScreen implements
        CommunicationControllerListener {
    private final EditField _uriSenderField;
    private final GaugeField _gfProgress;
    private boolean _pending;

    /**
     * Creates a new SendBlockScreen object
     */
    public SendBlockScreen(final CommunicationController controller) {
        setTitle("Blocking");

        // Initialize UI components
        final FullWidthButton backButton = new FullWidthButton("Back");
        backButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                ((UiApplication) SendBlockScreen.this.getApplication())
                        .popScreen(SendBlockScreen.this);
            }
        });

        final LabelField instructions =
                new LabelField(
                        "Enter a destination URI and send a request message to it. "
                                + "The response will displayed in a pop-up screen.",
                        Field.NON_FOCUSABLE);

        _uriSenderField =
                new EditField("Sender URI:",
                        CommunicationController.ECHO_SERVER_URI + "TEXT", 140,
                        0);

        _gfProgress =
                new GaugeField("Timeout:", 0, CommunicationController.TIMEOUT,
                        0, GaugeField.PERCENT);
        _gfProgress.setBorder(BorderFactory.createSimpleBorder(new XYEdges(1,
                1, 1, 1)));

        final FullWidthButton postButton =
                new FullWidthButton("Send message and receive response");
        postButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {

                if (!_pending) {
                    _pending = true;
                    _gfProgress.setValue(0);
                    add(_gfProgress);
                    controller.sendBlocking(_uriSenderField.getText(),
                            SendBlockScreen.this);
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

    /**
     * @see com.rim.samples.device.communicationapidemo.CommunicationControllerListener#onWaitTimerCounterChanged(int)
     */
    public void onWaitTimerCounterChanged(final int time) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                // Update progress bar
                _gfProgress.setValue(time);
            }
        });
    }

    /**
     * @see com.rim.samples.device.communicationapidemo.CommunicationControllerListener
     *      #onWaitTimerCompleted()
     */
    public void onWaitTimerCompleted() {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                // delete progress bar
                delete(_gfProgress);
                _pending = false;
            }
        });
    }
}
