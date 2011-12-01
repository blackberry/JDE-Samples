/**
 * MMSDemo.java
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

package com.rim.samples.device.mmsdemo;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessagePart;
import javax.wireless.messaging.MultipartMessage;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.table.TableModel;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This sample demonstrates the sending and receiving of MMS messages using the
 * javax.wireless.messaging APIs. Note that MMS is not supported by the
 * BlackBerry Smartphone simulator.
 */
public final class MMSDemo extends UiApplication {
    // The application ID that the MMS connection is opened with
    private static final String APP_ID = "com.rim.samples.device.mmsdemo";

    private static final int CLIENT_CHOICE = 0;
    private static final int SERVER_CHOICE = 1;
    private static final String[] CHOICES =
            { "Send MMS", "Receive MMS", "Exit" };

    static final int PICTURE = 0;
    static final int AUDIO = 1;

    // Parallel arrays containg file information
    private static final String[] MIME_TYPES = { "image/png", "audio/mp3" };
    private static final String[] FOLDER_NAMES = { "picture", "audio" };
    private static final String[] EXTENSIONS = { ".png", ".mp3" };

    private Connection _sendConn;
    private Connection _recConn;
    private MMSDemoSendScreen _mmsDemoSendScreen;
    private MMSDemoReceiveScreen _mmsDemoReceiveScreen;
    private final MainScreen _blankScreen;

    // Vector to hold MessagePart objects
    private final TableModel _messageParts;

    /**
     * Creates a new MMSDemo object
     */
    public MMSDemo() {
        _messageParts = new TableModel();
        _blankScreen = new MainScreen();
        _blankScreen.setTitle("MMS Demo");
        pushScreen(_blankScreen);
        chooseModeDialog();
    }

    /**
     * Creates a MessagePart with content of given type to be attached to
     * outgoing MultipartMessage
     * 
     * @param type
     *            The type of content to attach
     */
    void attach(final int type) {
        boolean exists = false;

        // Check that content has not already been added. For this sample there
        // is no point in attaching the same file twice. Note that every
        // MessagePart contained within a MultipartMessage must have a unique
        // content id.
        for (int i = 0; i < getMessageParts().getNumberOfRows(); i++) {
            final MessagePart messagePart =
                    (MessagePart) getMessageParts().getRow(i);
            final String contentLocation = messagePart.getContentLocation();
            if (contentLocation.equals(FOLDER_NAMES[type] + '0'
                    + EXTENSIONS[type])) {
                exists = true;
            }
        }
        if (!exists) {
            // Obtain content data from project resource
            final String filename = FOLDER_NAMES[type] + "0" + EXTENSIONS[type];
            final StringBuffer path = new StringBuffer("/media/");
            path.append(FOLDER_NAMES[type]);
            path.append("/");
            path.append(filename);
            final InputStream inputStream =
                    getClass().getResourceAsStream(path.toString());
            try {
                final byte[] contentData =
                        IOUtilities.streamToBytes(inputStream);

                // Create a MessagePart object with the contentData and add it
                // to the message parts vector
                final MessagePart messagePart =
                        new MessagePart(contentData, MIME_TYPES[type],
                                FOLDER_NAMES[type], filename, null);
                addMessagePart(messagePart);
            } catch (final IOException ioe) {
                errorDialog(ioe.toString());
            }
        }
    }

    /**
     * Allows user to choose between client or server mode
     */
    private void chooseModeDialog() {
        invokeLater(new Runnable() {
            public void run() {
                final int choice =
                        Dialog.ask("Select Mode", CHOICES, CLIENT_CHOICE);

                popScreen(_blankScreen);

                if (choice == CLIENT_CHOICE) {
                    _mmsDemoSendScreen = new MMSDemoSendScreen(MMSDemo.this);
                    pushScreen(_mmsDemoSendScreen);
                } else if (choice == SERVER_CHOICE) {
                    receiveMMS();
                    _mmsDemoReceiveScreen = new MMSDemoReceiveScreen();
                    pushScreen(_mmsDemoReceiveScreen);
                } else {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * Retrieves a vector of MessagePart objects
     * 
     * @return A vector that contains the message parts
     */
    TableModel getMessageParts() {
        return _messageParts;
    }

    /**
     * Adds a new MessagePart object
     * 
     * @param m
     *            The MessagePart object to be added
     */
    void addMessagePart(final MessagePart messagePart) {
        _messageParts.addRow(messagePart);
    }

    /**
     * Sends an MMS message
     */
    void sendMMS(final EditField addressField, final EditField subjectField,
            final EditField messageField) {
        final String address = addressField.getText();
        final String subject = subjectField.getText();
        final String message = messageField.getText();

        // Check for blank fields
        if (address.length() == 0) {
            errorDialog("Destination field cannot be blank");
            addressField.setFocus();
        } else if (subject.length() == 0) {
            errorDialog("Subject field cannot be blank");
            subjectField.setFocus();
        } else if (message.length() == 0) {
            errorDialog("Message field cannot be blank");
            messageField.setFocus();
        } else {
            MultipartMessage multipartMessage = null;
            try {
                // Open an MMS connection for sending
                _sendConn = Connector.open("mms://" + address + ":" + APP_ID);

                // Create the multipart message and set the subject
                multipartMessage =
                        (MultipartMessage) ((MessageConnection) _sendConn)
                                .newMessage(MessageConnection.MULTIPART_MESSAGE);
                multipartMessage.setSubject(subject);

                // Add the message text to the multipart message
                multipartMessage.addMessagePart(new MessagePart(message
                        .getBytes(), "text/plain", "text", null, null));

                // Add any attachments
                for (int i = 0; i < _messageParts.getNumberOfRows(); i++) {
                    multipartMessage.addMessagePart((MessagePart) _messageParts
                            .getRow(i));
                }
            } catch (final IOException ioe) {
                errorDialog(ioe.toString() + " when creating message.");
            }
            try {
                ((MessageConnection) _sendConn).send(multipartMessage);

                invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert("Message sent");
                    }
                });
            } catch (final IOException ioe) {
                errorDialog(ioe.toString() + " when sending message.");
            }
        }
    }

    /**
     * Listens for incoming MMS messages
     */
    private void receiveMMS() {
        final Thread t = new Thread() {
            public void run() {
                try {
                    // Open an MMS connection for receiving
                    _recConn = Connector.open("mms://:" + APP_ID);

                    for (;;) {
                        // Wait for an MMS
                        final MultipartMessage multipartMessage =
                                (MultipartMessage) ((MessageConnection) _recConn)
                                        .receive();

                        // Process the MMS in an other thread so this thread can
                        // continue to listen for messages without interruption.
                        new ProcessMMSThread(multipartMessage).start();
                    }
                } catch (final IOException ioe) {
                    MMSDemo.errorDialog("MessageConnection.receive() threw "
                            + ioe.toString());
                }
            }
        };
        t.start();
    }

    /**
     * A class to process an MMS message and update the screen
     */
    class ProcessMMSThread extends Thread {
        private final MultipartMessage _multipartMessage;
        private String _msgText;
        private Bitmap _bitmap;

        /**
         * Create a new ProcessMMSThread object
         * 
         * @param multipartMessage
         *            Message to display on the screen
         */
        ProcessMMSThread(final MultipartMessage multipartMessage) {
            _multipartMessage = multipartMessage;
        }

        public void run() {
            // Obtain the message part containing the message body
            final MessagePart textMsgPart =
                    _multipartMessage.getMessagePart("text");

            if (textMsgPart != null) {
                final byte[] bytes = textMsgPart.getContent();
                _msgText = new String(bytes);
            }

            final MessagePart pictureMsgPart =
                    _multipartMessage.getMessagePart("picture");
            if (pictureMsgPart != null) {
                final byte[] pictureData = pictureMsgPart.getContent();
                _bitmap = Bitmap.createBitmapFromBytes(pictureData, 0, -1, 1);
            }

            invokeLater(new Runnable() {
                public void run() {
                    // Update the screen on the event thread
                    _mmsDemoReceiveScreen.setStatus("Message received");
                    _mmsDemoReceiveScreen.setSubject(_multipartMessage
                            .getSubject());
                    _mmsDemoReceiveScreen.setMessage(_msgText);
                    _mmsDemoReceiveScreen.updateBitmapField(_bitmap);
                }
            });
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        try {
            if (_sendConn != null) {
                _sendConn.close();
            }
        } catch (final Exception e) {
        }
        try {
            if (_recConn != null) {
                _recConn.close();
            }
        } catch (final Exception e) {
        }
    }

    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final MMSDemo mmsDemo = new MMSDemo();
        mmsDemo.enterEventDispatcher();
    }

    /**
     * Presents a dialog to the user with a given message
     * 
     * @param message
     *            The text to display
     */
    public static void errorDialog(final String message) {
        UiApplication.getUiApplication().invokeLater(new Runnable() {
            public void run() {
                Dialog.alert(message);
            }
        });
    }
}
