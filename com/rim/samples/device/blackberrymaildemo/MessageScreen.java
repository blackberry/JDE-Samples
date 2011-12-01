/*
 * MessageScreen.java
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

package com.rim.samples.device.blackberrymaildemo;

import java.util.Date;
import java.util.Vector;

import javax.microedition.pim.Contact;

import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.BodyPart;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.MimeBodyPart;
import net.rim.blackberry.api.mail.Multipart;
import net.rim.blackberry.api.mail.PDAPContactAttachmentPart;
import net.rim.blackberry.api.mail.ServiceConfiguration;
import net.rim.blackberry.api.mail.SupportedAttachmentPart;
import net.rim.blackberry.api.mail.TextBodyPart;
import net.rim.blackberry.api.mail.Transport;
import net.rim.blackberry.api.mail.UnsupportedAttachmentPart;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.IntHashtable;

/**
 * The MessageScreen class allows a user to view a selected message and edit the
 * message if the screen is marked editable. It manages the different TextFields
 * using a hashtable where the type of information that is held in a given
 * TextField is the key while the value is a Vector of TextFields associated
 * with that information type. This class supports displaying plain text, Mime,
 * supported and unsupported attachments and pdap contacts.
 */
public class MessageScreen extends MainScreen {
    // Constants
    public final static String NO_SUBJECT = "<No Subject>";
    public final static String UNKNOWN_NAME = "<?>";

    protected final static int SUBJECT = 0;
    protected final static int BODY = 1;
    protected final static int INFO = 2;
    protected final static int[] HEADER_KEYS = { Message.RecipientType.TO,
            Message.RecipientType.CC, Message.RecipientType.BCC };
    protected final static String[] HEADER_NAMES = { "To: ", "Cc: ", "Bcc: " };

    private final static int MAX_CHARS = 128;

    protected IntHashtable _fieldTable;
    protected Message _message;
    private final boolean _editable;

    /**
     * Creates a new MessageScreen object
     * 
     * @param message
     *            The message to display
     * @param editable
     *            True is the message is editable, otherwise false
     */
    public MessageScreen(final Message message, final boolean editable) {
        _fieldTable = new IntHashtable();
        _editable = editable;

        // Set the message and display its subject as the title if the
        // message exists.
        _message = message;
        if (_message != null) {
            setTitle(_message.getSubject());
        }

        displayMessage();
    }

    /**
     * Displays the message
     */
    void displayMessage() {
        displayMessageInformation();

        add(new SeparatorField());

        displayHeader();

        add(new SeparatorField());

        displayMessageBody();
    }

    /**
     * Displays information about the message's send and recieve properties
     */
    protected void displayMessageInformation() {
        // Add a field describing the source service
        final ServiceConfiguration sc =
                _message.getFolder().getStore().getServiceConfiguration();
        final EditField service =
                new EditField("Service: ", sc.getName(), MAX_CHARS,
                        Field.READONLY | Field.NON_FOCUSABLE);
        addTextFieldToTableAndScreen(service, INFO);

        // Add the folder field
        final EditField folder =
                new EditField("Folder: ", _message.getFolder().getName(),
                        MAX_CHARS, Field.READONLY | Field.NON_FOCUSABLE);
        addTextFieldToTableAndScreen(folder, INFO);

        // Add the status of the message
        final String statusString = getStatusString(_message);
        final EditField status =
                new EditField("Status: ", statusString, MAX_CHARS,
                        Field.READONLY | Field.NON_FOCUSABLE);
        addTextFieldToTableAndScreen(status, INFO);
    }

    /**
     * Displays information about the destination and source of the message as
     * well as its subject.
     */
    protected void displayHeader() {
        // Assign the appropriate EditField style property
        final long editableStyle = _editable ? Field.EDITABLE : Field.READONLY;

        // Display the headers (To:, Cc:, Bcc:)
        for (int key = 0; key < HEADER_KEYS.length; key++) {
            try {
                final Address[] addresses =
                        _message.getRecipients(HEADER_KEYS[key]);
                for (int index = 0; index < addresses.length; index++) {
                    // Retrieve the name
                    String name = addresses[index].getName();
                    if (name == null || name.length() == 0) {
                        name = addresses[index].getAddr();
                    }

                    // Create the edit field, associate the address to the field
                    // and add it to the screen and collection of fields.
                    final EditField headerField =
                            new EditField(HEADER_NAMES[key], name,
                                    TextField.DEFAULT_MAXCHARS, editableStyle);
                    headerField.setCookie(addresses[index]);

                    addTextFieldToTableAndScreen(headerField, HEADER_KEYS[key]);
                }
            } catch (final MessagingException e) {
                BlackBerryMailDemo
                        .errorDialog("Error: could not retrieve message header.");
                close();
            }
        }

        // Display the 'Sent' date if it is available
        final Date sent = _message.getSentDate();
        if (sent != null) {
            final EditField sentDate =
                    new EditField("Sent: ", Util.getDateAsString(sent),
                            TextField.DEFAULT_MAXCHARS, Field.READONLY
                                    | Field.NON_FOCUSABLE);

            // Change the label to "Saved: " if the message hasn't been sent yet
            if (_message.getStatus() == Message.Status.TX_COMPOSING) {
                sentDate.setLabel("Saved: ");
            }

            add(sentDate);
        }

        // Display the 'Received' date if the message was an inbound message
        final Date recieved = _message.getSentDate();
        if (_message.isInbound() && recieved != null) {
            final EditField sentDate =
                    new EditField("Recieved: ", Util.getDateAsString(recieved),
                            TextField.DEFAULT_MAXCHARS, Field.READONLY
                                    | Field.NON_FOCUSABLE);
            add(sentDate);
        }

        // If the message was received, retrieve and display who sent the
        // message
        if (_message.isInbound()) {
            try {
                final Address from = _message.getFrom();
                String name = from.getName();
                if (name == null || name.length() == 0) {
                    name = from.getAddr();
                }

                final EditField fromField =
                        new EditField("From: ", name,
                                TextField.DEFAULT_MAXCHARS, editableStyle);
                fromField.setCookie(from);
                add(fromField);
            } catch (final MessagingException e) {
                BlackBerryMailDemo
                        .errorDialog("Error: could not retrieve message sender.");
                close();
            }
        }

        // Display the subject field
        String subject = _message.getSubject();
        if (subject == null) {
            subject = NO_SUBJECT;
        }

        final EditField subjectField =
                new EditField("Subject: ", subject, TextField.DEFAULT_MAXCHARS,
                        editableStyle);
        addTextFieldToTableAndScreen(subjectField, SUBJECT);
    }

    /**
     * Displays the message body
     */
    protected void displayMessageBody() {
        // Retrieve the parent of the message body
        final Object obj = _message.getContent();
        Multipart parent = null;
        if (obj instanceof MimeBodyPart || obj instanceof TextBodyPart) {
            final BodyPart bp = (BodyPart) obj;
            parent = bp.getParent();
        } else {
            parent = (Multipart) obj;
        }

        // Display the message body
        final String mpType = parent.getContentType();
        if (mpType
                .equals(BodyPart.ContentType.TYPE_MULTIPART_ALTERNATIVE_STRING)
                || mpType
                        .equals(BodyPart.ContentType.TYPE_MULTIPART_MIXED_STRING)) {
            displayMultipart(parent);
        }

        // Ensure there is at least one body field if nothing was displayed
        final Vector bodyVector = (Vector) _fieldTable.get(BODY);
        if (bodyVector == null || bodyVector.size() == 0) {
            if (_editable) {
                addTextFieldToTableAndScreen(new EditField("", ""), BODY);
            } else {
                addTextFieldToTableAndScreen(new RichTextField(""), BODY);
            }
        }
    }

    /**
     * Processes a multi-part message by displaying its body parts. Text body
     * parts are displayed before attachments and if a multi body part is
     * encountered, then it is processed through recursion by calling this
     * method on it.
     * 
     * @param multipart
     *            The multi-part to display
     * @param editable
     *            True if this multi-part is editable
     */
    protected void displayMultipart(final Multipart multipart) {
        // This vector stores fields which are to be displayed only after all
        // of the body fields are displayed. (Attachments and Contacts).
        final Vector delayedFields = new Vector();

        // Process each part of the multi-part, taking the appropriate action
        // depending on the part's type. This loop should: display text and
        // html body parts, recursively display multi-parts and store
        // attachments and contacts to display later.
        for (int index = 0; index < multipart.getCount(); index++) {
            final BodyPart bodyPart = multipart.getBodyPart(index);

            // If this body part is text then display all of it
            if (bodyPart instanceof TextBodyPart) {
                final TextBodyPart textBodyPart = (TextBodyPart) bodyPart;

                // If there are missing parts of the text, try to retrieve the
                // rest of it.
                if (textBodyPart.hasMore()) {
                    try {
                        Transport.more(textBodyPart, true);
                    } catch (final Exception e) {
                        BlackBerryMailDemo
                                .errorDialog("Transport.more(BodyPart, boolean) threw "
                                        + e.toString());
                    }
                }
                final String plainText = (String) textBodyPart.getContent();

                // Display the plain text, using an EditField if the message is
                // editable or a RichTextField if it is not editable. Note: this
                // does not add any empty fields.
                if (plainText.length() != 0) {
                    if (_editable) {
                        addTextFieldToTableAndScreen(new EditField("",
                                plainText), BODY);
                    } else {
                        addTextFieldToTableAndScreen(new RichTextField(
                                plainText), BODY);
                    }
                }
            } else if (bodyPart instanceof MimeBodyPart) {
                final MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;

                // If the content is text then display it
                final String contentType = mimeBodyPart.getContentType();
                if (contentType
                        .startsWith(BodyPart.ContentType.TYPE_TEXT_HTML_STRING)) {
                    final Object obj = mimeBodyPart.getContent();
                    if (obj != null) {
                        final String htmlText = new String((byte[]) obj);
                        addTextFieldToTableAndScreen(
                                new RichTextField(htmlText), BODY);
                    }
                } else if (contentType
                        .equals(BodyPart.ContentType.TYPE_MULTIPART_ALTERNATIVE_STRING)) {
                    // If the body part is a multi-part and it has the the
                    // content type of TYPE_MULTIPART_ALTERNATIVE_STRING, then
                    // recursively display the multi-part.
                    final Object obj = mimeBodyPart.getContent();
                    if (obj instanceof Multipart) {
                        final Multipart childMultipart = (Multipart) obj;
                        final String childMultipartType =
                                childMultipart.getContentType();
                        if (childMultipartType
                                .equals(BodyPart.ContentType.TYPE_MULTIPART_ALTERNATIVE_STRING)) {
                            displayMultipart(childMultipart);
                        }
                    }
                }
            } else if (bodyPart instanceof SupportedAttachmentPart
                    || bodyPart instanceof UnsupportedAttachmentPart) {
                // Extract the content type and name from the attachments
                final String contentType = bodyPart.getContentType();
                String name;
                if (bodyPart instanceof UnsupportedAttachmentPart) {
                    final UnsupportedAttachmentPart uap =
                            (UnsupportedAttachmentPart) bodyPart;
                    name = uap.getName();
                } else // The bodyPart is a SupportedAttachmentPart
                {
                    final SupportedAttachmentPart sap =
                            (SupportedAttachmentPart) bodyPart;
                    name = sap.getName();
                }

                // Format the content type and name to display and store
                // the field.
                final StringBuffer sb =
                        new StringBuffer(contentType.length() + name.length()
                                + 2);
                sb.append(contentType);
                sb.append('[');
                sb.append(name);
                sb.append(']');

                delayedFields.addElement(new RichTextField(sb.toString()));
            } else if (bodyPart instanceof PDAPContactAttachmentPart) {
                final Contact contact = (Contact) bodyPart.getContent();

                // Build the contact name
                final StringBuffer sb = new StringBuffer("Contact: ");
                if (contact.countValues(Contact.NAME) > 0) {
                    final String[] name =
                            contact.getStringArray(Contact.NAME, 0);

                    if (name[Contact.NAME_PREFIX] != null) {
                        sb.append(name[Contact.NAME_PREFIX]);
                        sb.append(' ');
                    }

                    if (name[Contact.NAME_GIVEN] != null) {
                        sb.append(name[Contact.NAME_GIVEN]);
                        sb.append(' ');
                    }

                    if (name[Contact.NAME_FAMILY] != null) {
                        sb.append(name[Contact.NAME_FAMILY]);
                    }

                    // Trim the last space of the name if it exists
                    final int lastChar = sb.length() - 1;
                    if (sb.charAt(lastChar) == ' ') {
                        sb.deleteCharAt(lastChar);
                    }
                } else {
                    sb.append(UNKNOWN_NAME);
                }

                // Create the contact attachment field and store it
                final RichTextField contactAttachment =
                        new RichTextField(sb.toString());
                contactAttachment.setCookie(contact);
                delayedFields.addElement(contactAttachment);
            }
        }

        // Now that the body parts have been displayed, display the queued
        // fields while separating them by inserting a separator field.
        for (int index = 0; index < delayedFields.size(); index++) {
            add(new SeparatorField());
            addTextFieldToTableAndScreen((TextField) delayedFields
                    .elementAt(index), BODY);
        }
    }

    /**
     * Compiles the status of a message into a readable string.
     * 
     * @param message
     *            The message whose status is to be compiled into a string
     * @return The string displaying the status of the message
     */
    public static String getStatusString(final Message message) {
        final StringBuffer statusStrBuffer = new StringBuffer();

        // Add any errors to the status string if it applies
        final int status = message.getStatus();
        if (status == Message.Status.RX_ERROR) {
            statusStrBuffer.append("RX ERROR, ");
        }

        if (status == Message.Status.TX_GENERAL_FAILURE) {
            statusStrBuffer.append("RX ERROR, ");
        }

        if (status == Message.Status.TX_ERROR) {
            statusStrBuffer.append("TX ERROR, ");
        }

        // Use the flags to add any message statuses
        final int flags = message.getFlags();
        if (0 != (flags & Message.Flag.OPENED)) {
            statusStrBuffer.append("Opened, ");
        }

        if (0 != (flags & Message.Flag.SAVED)) {
            statusStrBuffer.append("Saved, ");
        }

        if (0 != (flags & Message.Flag.FILED)) {
            statusStrBuffer.append("Filed, ");
        }

        // Check if the message has a high or low priority
        final byte messagePriority = message.getPriority();
        if (messagePriority == Message.Priority.HIGH) {
            statusStrBuffer.append("High Priority, ");
        } else if (messagePriority == Message.Priority.LOW) {
            statusStrBuffer.append("Low Priority, ");
        }

        // If there are any characters in the status string then delete the last
        // two characters if there are any characters to delete. Should be
        // either ", " or "  ".
        statusStrBuffer.delete(statusStrBuffer.length() - 2, statusStrBuffer
                .length());

        return statusStrBuffer.toString();
    }

    /**
     * Add a new field to the hashtable of TextFields and to the screen
     * 
     * @param field
     *            The field to add
     * @param type
     *            The type of field to add
     */
    protected void addTextFieldToTableAndScreen(final TextField field,
            final int type) {
        Vector fieldsByType = (Vector) _fieldTable.get(type);

        // If the vector of fields associated with the type is not made yet,
        // initialize one and put it into the fields collection.
        if (fieldsByType == null) {
            fieldsByType = new Vector(1);
            _fieldTable.put(type, fieldsByType);
        }

        fieldsByType.addElement(field);
        add(field);
    }

    /**
     * @see net.rim.device.api.ui.Screen#onClose()
     */
    public boolean onClose() {
        // If the message status is "received", mark it "read"
        if (_message != null
                && _message.getStatus() == Message.Status.RX_RECEIVED) {
            _message.setStatus(Message.Status.TX_READ, Message.Status.TX_ERROR);
            _message.setFlag(Message.Flag.OPENED, true);
        }

        return super.onClose();
    }
}
