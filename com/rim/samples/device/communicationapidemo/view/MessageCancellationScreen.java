/**
 * MessageCancellationScreen.java
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
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.CommunicationController;
import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

public final class MessageCancellationScreen extends MainScreen {
    private final EditField _uriSenderField;
    private final EditField _contentField1;
    private final EditField _contentField2;

    /**
     * Creates a new MessageCancellationScreen object
     */
    public MessageCancellationScreen(final CommunicationController controller) {
        setTitle("Cancel Messages");

        // Initialize UI components
        final FullWidthButton backButton = new FullWidthButton("Back");
        backButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                ((UiApplication) MessageCancellationScreen.this
                        .getApplication())
                        .popScreen(MessageCancellationScreen.this);
            }
        });

        // Set the destination to which messages will be to be sent:
        _uriSenderField =
                new EditField("Destination:",
                        CommunicationController.ECHO_SERVER_URI + "TEXT");

        final FullWidthButton destinationButton =
                new FullWidthButton("Set Destination");
        destinationButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.setNonBlockingSenderDestination(_uriSenderField
                        .getText());
            }
        });

        // First Message
        _contentField1 = new EditField("Message:", "Message One");
        final FullWidthButton postButton1 =
                new FullWidthButton("Send and Cancel (ALL)");
        postButton1.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.testMessageCancellable(_contentField1.getText(),
                        true); // cancellable = true

            }
        });

        // Second Message
        _contentField2 = new EditField("Message:", "Message Two");
        final FullWidthButton postButton2 =
                new FullWidthButton("Send and Cancel (NEVER)");
        postButton2.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.testMessageCancellable(_contentField2.getText(),
                        false); // Cancellable = false
            }
        });

        // Cancel all cancellable
        final FullWidthButton cancelButton =
                new FullWidthButton("Cancel All Cancellable");
        cancelButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                controller.testCancelAllCancellable();
            }
        });

        final RichTextField messages =
                new RichTextField(
                        "Cancel All Cancellable tries to send four different messages with different"
                                + " cancellation options set on them and cancels them all.",
                        Field.NON_FOCUSABLE);

        // Add components to screen
        add(backButton);
        add(_uriSenderField);
        add(destinationButton);
        add(_contentField1);
        add(postButton1);
        add(_contentField2);
        add(postButton2);
        add(cancelButton);
        add(new SeparatorField());
        add(messages);
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }
}
