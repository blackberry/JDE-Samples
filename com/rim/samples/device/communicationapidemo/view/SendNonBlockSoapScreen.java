/*
 * SendNonBlockSoapScreen.java
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

import java.util.Vector;

import net.rim.device.api.io.messaging.Message;
import net.rim.device.api.io.messaging.MessageProcessorException;
import net.rim.device.api.io.parser.soap.SOAPBody;
import net.rim.device.api.io.parser.soap.SOAPElement;
import net.rim.device.api.io.parser.soap.SOAPEnvelope;
import net.rim.device.api.io.parser.soap.SOAPHeader;
import net.rim.device.api.io.parser.soap.SOAPMessageProcessor;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

import com.rim.samples.device.communicationapidemo.CommunicationController;
import com.rim.samples.device.communicationapidemo.ResponseCallback;
import com.rim.samples.device.communicationapidemo.ui.FullWidthButton;

/**
 * SOAP processing functionality screen
 */
public final class SendNonBlockSoapScreen extends MainScreen {
    private final EditField _uriSenderField;
    private final RichTextField _formattedMsgContentField;
    private final RichTextField _msgContentField;
    private final ResponseCallback _callback;
    private boolean _pending;

    /**
     * Creates a new SendNonBlockSoapScreen object
     * 
     * @param controller
     */
    public SendNonBlockSoapScreen(final CommunicationController controller) {
        setTitle("SOAP");

        _callback = new SendNonBlockXmlScreenCallback(this);

        _uriSenderField =
                new EditField("URI:", CommunicationController.ECHO_SERVER_URI
                        + "SOAP", 140, 0);

        final FullWidthButton postButton = new FullWidthButton("Get Data");
        postButton.setChangeListener(new FieldChangeListener() {
            /**
             * @see FieldChangeListener#fieldChanged(Field, int)
             */
            public void fieldChanged(final Field field, final int context) {
                if (!_pending) {
                    _pending = true;
                    clearSoapVew("* No SOAP Message *");
                    controller.sendNonBlocking(_uriSenderField.getText(), true,
                            _callback);
                } else {
                    Dialog.alert("Previous request pending state...");
                }
            }
        });

        _formattedMsgContentField = new RichTextField("* No SOAP Message *");
        _msgContentField = new RichTextField("");

        // Add UI components to screen
        add(_uriSenderField);
        add(postButton);
        add(_formattedMsgContentField);
        add(new SeparatorField());
        add(_msgContentField);
    }

    private void updateSoapView(final Message message) {
        if (process(message)) {
            displaySoap(message);
        } else {
            clearSoapVew("* No SOAP Message *");
        }
    }

    /**
     * Displays parts of the SOAP Message
     */
    private void displaySoap(final Message message) {
        final Object soap = message.getObjectPayload();

        final SOAPEnvelope env = (SOAPEnvelope) soap;
        final SOAPHeader header = env.getHeader();
        final SOAPBody body = env.getBody();

        String str = "====== Envelope:";
        str += "\nName: " + env.getName();
        str += "\nNamespace: " + env.getNamespace();
        str += "\n====== Header:";
        if (header != null) {
            str += "\nName: " + header.getName();
            str += "\nNamespace: " + header.getNamespace();
            str += "\nNumber of children: " + header.getChildren().size();
        } else {
            str += "\n null";
        }

        str += "\n====== Body:";
        if (body != null) {
            str += "\nName: " + body.getName();
            str += "\nNamespace: " + body.getNamespace();

            final Vector children = body.getChildren();

            str += "\nNumber of children: " + children.size();
            for (int i = 0; i < children.size(); i++) {
                final SOAPElement element = (SOAPElement) children.elementAt(i);
                if (element != null) {
                    str += "\n  [child " + i + "]";
                    str += "\n   Name: " + element.getName();
                    str += "\n   Type: " + element.getType();
                    str += "\n   Namespace: " + element.getNamespace();
                }
            }
        } else {
            str += "\n null";
        }

        _formattedMsgContentField.setText(str);
        _msgContentField.setText(" ******* SOAP Request *******\n"
                + env.toSoapRequest());
    }

    /**
     * Processes message using SOAP Message Processor
     */
    private boolean process(final Message msg) {
        boolean isProcessed = true;

        final SOAPMessageProcessor processor = new SOAPMessageProcessor();
        try {
            processor.process(msg);
        } catch (final MessageProcessorException mpe) {
            System.out.println(mpe.toString());
            isProcessed = false;
        }

        return isProcessed;
    }

    private void clearSoapVew(final String statusString) {
        _formattedMsgContentField.setText(statusString);
        _msgContentField.setText("");
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    private static final class SendNonBlockXmlScreenCallback extends
            ResponseCallback {

        private final SendNonBlockSoapScreen _screen;

        private SendNonBlockXmlScreenCallback(
                final SendNonBlockSoapScreen screen) {
            _screen = screen;
        }

        public void onResponse(final Message message) {
            if (message != null) {
                _screen.updateSoapView(message);
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
