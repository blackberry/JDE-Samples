/**
 * EventScreen.java
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

package com.rim.samples.device.blackberry.pim;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.microedition.pim.Contact;
import javax.microedition.pim.Event;
import javax.microedition.pim.EventList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;
import javax.microedition.pim.RepeatRule;

import net.rim.blackberry.api.mail.Address;
import net.rim.blackberry.api.mail.AddressException;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Message;
import net.rim.blackberry.api.mail.MessagingException;
import net.rim.blackberry.api.mail.Multipart;
import net.rim.blackberry.api.mail.NoSuchServiceException;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.SupportedAttachmentPart;
import net.rim.blackberry.api.mail.TextBodyPart;
import net.rim.blackberry.api.mail.Transport;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;

public final class EventScreen extends MainScreen {
    // Members
    // ------------------------------------------------------------------
    private final EditField _subject, _location, _desc;
    private final ObjectChoiceField _recur;
    private final InviteContactMenuItem _inviteContactMenuItem;
    private final SaveMenuItem _saveMenuItem;
    private final Vector _invitees;
    private ContactListScreen _contactListScreen;
    private final DateField _startTime, _endTime;
    private Event _event;

    // Inner classes
    // ------------------------------------------------------------
    private class InviteContactMenuItem extends MenuItem {
        /**
         * This class is responsible for adding an invite field to the create
         * screen.
         */
        public InviteContactMenuItem() {
            super("Invite Contact", 100, 10);
        }

        public void run() {
            _contactListScreen = new ContactListScreen();
            UiApplication.getUiApplication()
                    .pushModalScreen(_contactListScreen);

            Contact contact;
            // Get selected contact from contact list.
            if ((contact = _contactListScreen.getSelectedContact()) != null) {
                final String[] name = contact.getStringArray(Contact.NAME, 0);
                boolean found = false;
                String nameseg;
                final StringBuffer sb = new StringBuffer();

                if ((nameseg = name[Contact.NAME_PREFIX]) != null) {
                    sb.append(nameseg);
                    found = true;
                }

                if ((nameseg = name[Contact.NAME_GIVEN]) != null) {
                    if (found) {
                        sb.append(' ');
                    }

                    sb.append(nameseg);
                    found = true;
                }

                if ((nameseg = name[Contact.NAME_FAMILY]) != null) {
                    if (found) {
                        sb.append(' ');
                    }
                    sb.append(nameseg);
                }

                final EditField newField =
                        new EditField("Invite: ", sb.toString(),
                                TextField.DEFAULT_MAXCHARS, Field.READONLY);

                if (_invitees.isEmpty()) // Add a separator to screen.
                {
                    insert(new SeparatorField(), getFieldCount());
                }

                // Store contact for email retrieval.
                _invitees.addElement(contact);
                insert(newField, getFieldCount());
            }
        }
    }

    private class SaveMenuItem extends MenuItem {
        public SaveMenuItem() {
            super("Save Event", 110, 5);
        }

        public void run() {
            // If successful display message and close screen.
            if (onSave()) {
                Dialog.alert("Event was saved successfully");
                onClose();
            }
        }
    }

    /**
     * Constructor for our event screen.
     */
    public EventScreen() {
        _saveMenuItem = new SaveMenuItem();

        _invitees = new Vector();
        _inviteContactMenuItem = new InviteContactMenuItem();

        setTitle("PIM Demo");

        _subject = new EditField("Subject: ", "");
        add(_subject);

        _location = new EditField("Location: ", "");
        add(_location);

        final long currentTime = System.currentTimeMillis();
        _startTime =
                new DateField("Start: ", currentTime + 3600000,
                        DateField.DATE_TIME);
        _endTime =
                new DateField("End: ", currentTime + 7200000,
                        DateField.DATE_TIME);

        add(new SeparatorField());

        add(_startTime);
        add(_endTime);

        add(new SeparatorField());

        _desc = new EditField("Description: ", "");
        add(_desc);

        add(new SeparatorField());
        final String choices[] =
                { "None", "Daily", "Weekly", "Monthly", "Yearly" };
        _recur = new ObjectChoiceField("Recurrence: ", choices, 0);
        add(_recur);

        addMenuItem(_inviteContactMenuItem);
        addMenuItem(MenuItem.separator(_inviteContactMenuItem.getOrdinal() + 1));
        addMenuItem(_saveMenuItem);
        addMenuItem(MenuItem.separator(_saveMenuItem.getOrdinal() + 1));
    }

    /**
     * Send email to invited contacts.
     */
    private void sendInvitations() {
        Address[] to;

        // Find an outbox folder and use it to construct a new message.
        Folder outbox = null;
        try {
            outbox =
                    Session.waitForDefaultSession().getStore().findFolder(
                            "Outbox")[0];
        } catch (final NoSuchServiceException e) {
            System.err.println(e);
        }

        final Message msg = new Message(outbox);

        // Add the date, subject.
        final Date currentTime = Calendar.getInstance().getTime();
        msg.setSentDate(currentTime);
        msg.setSubject(_subject.getText());

        // Add invitees as message recipients.
        to = new Address[_invitees.size()];
        try {
            Contact c;

            for (int i = 0; i < _invitees.size(); ++i) {
                c = (Contact) _invitees.elementAt(i);
                final String[] name = c.getStringArray(Contact.NAME, 0);
                boolean found = false;
                String nameseg;
                final StringBuffer sb = new StringBuffer();

                if ((nameseg = name[Contact.NAME_PREFIX]) != null) {
                    sb.append(nameseg);
                    found = true;
                }

                if ((nameseg = name[Contact.NAME_GIVEN]) != null) {
                    if (found) {
                        sb.append(' ');
                    }

                    sb.append(nameseg);
                    found = true;
                }

                if ((nameseg = name[Contact.NAME_FAMILY]) != null) {
                    if (found) {
                        sb.append(' ');
                    }

                    sb.append(nameseg);
                }

                to[i] =
                        new Address(c.getString(Contact.EMAIL, 0), sb
                                .toString());
            }

            try {
                msg.addRecipients(Message.RecipientType.TO, to);
            } catch (final MessagingException me) {
                System.err.println(me);
            }
        } catch (final AddressException e) {
            System.err.println(e);
        }

        // Create a new multipart object to hold the calendar attachment.
        final Multipart multipart = new Multipart("mixed");

        // Create a new calendar attachment with meeting request as body.
        final ByteArrayOutputStream bouts = new ByteArrayOutputStream();
        try {
            final String[] formats =
                    PIM.getInstance().supportedSerialFormats(PIM.EVENT_LIST);

            for (int i = 0;; ++i) {
                if (formats[i].indexOf("2.0") != -1) {
                    PIM.getInstance().toSerialFormat(_event, bouts, "",
                            formats[i]); // Use the 2.0 format.
                    break;
                }
            }
        } catch (final PIMException e) {
            System.err.println(e);
        } catch (final UnsupportedEncodingException e) {
            System.err.println(e);
        } catch (final ArrayIndexOutOfBoundsException e) {
            System.err.println(e); // We couldn't find the proper format for
                                   // encoding!
            Dialog.alert("Serial format conversion failure!");

            return;
        }

        final SupportedAttachmentPart bodypart =
                new SupportedAttachmentPart(multipart,
                        "text/calendar; component=vevent", "event.ics", bouts
                                .toByteArray());

        // Add attachment to multipart.
        multipart.addBodyPart(new TextBodyPart(multipart, bouts.toString()));
        multipart.addBodyPart(bodypart);

        try {
            // Set multipart as message content.
            msg.setContent(multipart);

            // Send the message using transport .
            Transport.send(msg);
        } catch (final MessagingException e) {
            System.err.println(e);
        }
    }

    /**
     * Save our event.
     * 
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        try {
            final EventList eventList =
                    (EventList) PIM.getInstance().openPIMList(PIM.EVENT_LIST,
                            PIM.WRITE_ONLY);
            _event = eventList.createEvent();

            final String subject = _subject.getText().trim();
            final String location = _location.getText().trim();
            final long startTime = _startTime.getDate();
            final long endTime = _endTime.getDate();
            final String description = _desc.getText().trim();

            if (subject.length() == 0 || location.length() == 0) {
                Dialog.inform("Subject and location required");
                return false;
            }

            if (endTime <= startTime || startTime < System.currentTimeMillis()) {
                Dialog.inform("Invalid Start/End times");
                return false;
            }

            _event.addString(Event.SUMMARY, PIMItem.ATTR_NONE, subject);
            _event.addString(Event.LOCATION, PIMItem.ATTR_NONE, location);
            _event.addDate(Event.START, PIMItem.ATTR_NONE, startTime);
            _event.addDate(Event.END, PIMItem.ATTR_NONE, endTime);
            _event.addString(Event.NOTE, PIMItem.ATTR_NONE, description);

            final RepeatRule rule = new RepeatRule();

            // Set RepeatRule field and value based on user selection. See
            // javadocs for
            // additional fields and values.
            switch (_recur.getSelectedIndex()) {
            case 1:
                rule.setInt(RepeatRule.FREQUENCY, RepeatRule.DAILY);
                break;

            case 2:
                rule.setInt(RepeatRule.FREQUENCY, RepeatRule.WEEKLY);
                break;

            case 3:
                rule.setInt(RepeatRule.FREQUENCY, RepeatRule.MONTHLY);
                break;

            case 4:
                rule.setInt(RepeatRule.FREQUENCY, RepeatRule.YEARLY);
                break;
            }

            // Set recurrence frequency based on user selection.
            _event.setRepeat(rule);

            // Persist data to event list.
            _event.commit();

            sendInvitations();

            setDirty(false); // No need to save anymore.

            return true;

        } catch (final PIMException e) {
            System.err.println(e);
        }

        return false;
    }
}
