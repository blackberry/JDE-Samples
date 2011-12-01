/**
 * ContactListScreen.java
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

package com.rim.samples.device.contactlinkingdemo;

import java.util.Vector;

import javax.microedition.pim.Contact;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import net.rim.blackberry.api.invoke.AddressBookArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.blackberry.api.pdap.BlackBerryContactList;
import net.rim.blackberry.api.pdap.contactlinking.DefaultLinkableContact;
import net.rim.blackberry.api.pdap.contactlinking.LinkableContact;
import net.rim.blackberry.api.pdap.contactlinking.LinkedContactUtilities;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringUtilities;

/**
 * The main screen class for the Contact Linking Demo application
 */
public final class ContactListScreen extends MainScreen {
    private static SampleContact[] _contacts;

    private static final int SELECT_CONTACT = 0;
    private static final int CREATE_NEW = 1;

    private final MenuItem _viewEmailMenuItem;
    private final MenuItem _viewPhoneMenuItem;
    private final MenuItem _linkToBbContact;
    private final MenuItem _altLinkToBbContact;

    private final SampleContactListField _listField;

    /**
     * Creates a new ContactListScreen object
     */
    public ContactListScreen() {
        // Initialize UI
        setTitle("Contact Linking Demo");

        _viewEmailMenuItem = new ViewEmailMenuItem();
        _viewPhoneMenuItem = new ViewPhoneMenuItem();
        _linkToBbContact = new LinkToContactMenuItem();
        _altLinkToBbContact = new AltLinkToContactMenuItem();

        // Initialize the contact list with all contact's information
        initializeContacts();
        _listField = new SampleContactListField(_contacts);

        // Add the contact list to the screen
        add(_listField);
    }

    /**
     * Initializes the contacts
     */
    private static void initializeContacts() {
        int i = 0;

        // Instantiate a new SampleContact array
        _contacts = new SampleContact[7];

        // Instantiate a new SampleContact object
        _contacts[i] = new SampleContact(Integer.toString(i));
        _contacts[i].setString(LinkableContact.NAME, "Clyde Warren");
        _contacts[i].setString(LinkableContact.MOBILE_PHONE, "555-1234");
        _contacts[i].setString(LinkableContact.EMAIL, "cwarren@rim.com");

        i++;
        _contacts[i] = new SampleContact(Integer.toString(i));
        _contacts[i].setString(LinkableContact.NAME, "Scott Wyatt");
        _contacts[i].setString(LinkableContact.MOBILE_PHONE, "555-7348");
        _contacts[i].setString(LinkableContact.EMAIL, "swyatt@rim.com");

        i++;
        _contacts[i] = new SampleContact(Integer.toString(i));
        _contacts[i].setString(LinkableContact.NAME, "Kevin Wilhelm");
        _contacts[i].setString(LinkableContact.MOBILE_PHONE, "555-1123");
        _contacts[i].setString(LinkableContact.EMAIL, "kwilhelm@rim.com");

        i++;
        _contacts[i] = new SampleContact(Integer.toString(i));
        _contacts[i].setString(LinkableContact.NAME, "Karen Whittle");
        _contacts[i].setString(LinkableContact.MOBILE_PHONE, "555-3456");
        _contacts[i].setString(LinkableContact.EMAIL, "kwhittle@rim.com");

        i++;
        _contacts[i] = new SampleContact(Integer.toString(i));
        _contacts[i].setString(LinkableContact.NAME, "Tanya Wahl");
        _contacts[i].setString(LinkableContact.MOBILE_PHONE, "555-7785");
        _contacts[i].setString(LinkableContact.EMAIL, "twahl@rim.com");

        i++;
        _contacts[i] = new SampleContact(Integer.toString(i));
        _contacts[i].setString(LinkableContact.NAME, "Trevor Van Daele");
        _contacts[i].setString(LinkableContact.MOBILE_PHONE, "555-7676");
        _contacts[i].setString(LinkableContact.EMAIL, "tvandaele@rim.com");

        i++;
        _contacts[i] = new SampleContact(Integer.toString(i));
        _contacts[i].setString(LinkableContact.NAME, "Heather Tiegs");
        _contacts[i].setString(LinkableContact.MOBILE_PHONE, "555-5586");
        _contacts[i].setString(LinkableContact.EMAIL, "htiegs@rim.com");
    }

    /**
     * Gets the contact for a given login ID
     * 
     * @param id
     *            The login id of the contact we're interested in
     * @return The contact with the given login ID
     */
    public static SampleContact getUserForID(final String id) {
        if (_contacts == null) {
            initializeContacts();
        }
        final int numUsers = _contacts.length;
        for (int i = 0; i < numUsers; i++) {
            if (_contacts[i].getContactID().equals(id)) {
                return _contacts[i];
            }
        }
        return null;
    }

    /**
     * Unlinks a contact. This typically happens when the contact is selected to
     * be linked to another SampleContact.
     * 
     * @param linkableContact
     *            The contact to be unlinked
     */
    private void unlinkContact(final LinkableContact linkableContact) {
        final BlackBerryContact contact =
                LinkedContactUtilities.getLinkedContact(linkableContact);
        if (contact != null) {
            LinkedContactUtilities.unlinkContact(contact, linkableContact
                    .getApplicationID());
        }
    }

    /**
     * Links a LinkableContact object to a BlackBerryContact
     * 
     * @param linkableContact
     *            The LinkableContact object to be linked
     */
    private static void linkContact(final LinkableContact linkableContact) {
        // Check if there is a linking candidate
        BlackBerryContact bbContact =
                LinkedContactUtilities.getContactLinkCandidate(linkableContact);

        // No linking candidate returned
        if (bbContact == null) {
            final String[] choices = { "Select Contact", "Create New" };
            final int answer =
                    Dialog.ask(
                            linkableContact.getString(LinkableContact.NAME)
                                    + " is currently not "
                                    + "associated with a contact. Please select an option.",
                            choices, CREATE_NEW);

            // Manually select contact from address book
            if (answer == SELECT_CONTACT) {
                BlackBerryContactList contacts = null;
                try {
                    contacts =
                            (BlackBerryContactList) PIM.getInstance()
                                    .openPIMList(PIM.CONTACT_LIST,
                                            PIM.READ_WRITE);
                } catch (final PIMException e) {
                    ContactLinkingDemo
                            .errorDialog("Couldn't open contacts list.  PIM.openPIMList() threw "
                                    + e.toString());
                    return;
                }

                final Object choice = contacts.choose();
                if (choice instanceof BlackBerryContact) {
                    bbContact = (BlackBerryContact) choice;
                }

                if (bbContact != null) {
                    // Check if contact from address book has been linked to
                    // another LinkableContact.
                    if (LinkedContactUtilities.isContactLinked(bbContact,
                            linkableContact.getApplicationID())) {
                        final int selection =
                                Dialog.ask(
                                        Dialog.D_YES_NO,
                                        "This contact is already linked. Are you sure you want "
                                                + "to link to a different contact? ",
                                        Dialog.NO);

                        if (selection == Dialog.YES) {
                            // Undo the previous linking
                            bbContact =
                                    LinkedContactUtilities.unlinkContact(
                                            bbContact, linkableContact
                                                    .getApplicationID());
                        } else {
                            bbContact = null;
                        }
                    }

                    if (bbContact != null) {
                        // Link the BlackBerryContact to the LinkableContact
                        bbContact =
                                LinkedContactUtilities.linkContact(bbContact,
                                        linkableContact);
                    }
                }
            } else if (answer == CREATE_NEW) {
                // Create a new contact from phone for the current linking
                bbContact = createContactFor(linkableContact);
            }
        } else // Found link candidate
        {
            // Get display name
            final String displayName = getDisplayName(bbContact);

            // Create prompt message
            final StringBuffer msg =
                    new StringBuffer(
                            "Linking candidate found. Link to contact ");
            if (displayName != null) {
                msg.append(displayName);
            }
            msg.append('?');

            // Prompt to link contact
            final int answer =
                    Dialog.ask(Dialog.D_YES_NO, msg.toString(), Dialog.NO);

            if (answer == Dialog.YES) {
                bbContact =
                        LinkedContactUtilities.linkContact(bbContact,
                                linkableContact);
            } else {
                bbContact = null;
            }

        }
        if (bbContact != null) {
            Dialog.inform("Contact has been linked successfully");
        }
    }

    /**
     * Create a new address card for a contact and link that contact to the
     * application.
     * 
     * @param userData
     *            The LinkableContact object to be created in the contacts list
     * @return A reference to a new BlackBerryContact, or null if the operation
     *         was not successful
     */
    private static BlackBerryContact createContactFor(
            final LinkableContact linkableContact) {
        // Obtain list of contacts
        BlackBerryContactList contacts;
        try {
            contacts =
                    (BlackBerryContactList) PIM.getInstance().openPIMList(
                            PIM.CONTACT_LIST, PIM.READ_WRITE);
        } catch (final PIMException e) {
            ContactLinkingDemo
                    .errorDialog("Could not open contacts list.  PIM#openPIMList() threw "
                            + e.toString());
            return null;
        }

        // Create a new BlackBerryContact
        BlackBerryContact contact =
                (BlackBerryContact) contacts.createContact();

        // Set the mobile phone number for the new BlackBerryContact
        final String mobileNumber =
                linkableContact.getString(LinkableContact.MOBILE_PHONE);
        if (mobileNumber != null) {
            contact.addString(Contact.TEL, Contact.ATTR_MOBILE, mobileNumber);
        }

        // Set the email address for the new BlackBerryContact
        final String emailAddress =
                linkableContact.getString(LinkableContact.EMAIL);
        if (emailAddress != null) {
            contact.addString(Contact.EMAIL, Contact.ATTR_PREFERRED,
                    linkableContact.getString(LinkableContact.EMAIL));
        }

        // Set the name for the new BlackBerryContact
        final String[] names =
                StringUtilities.stringToWords(linkableContact
                        .getString(LinkableContact.NAME));
        final String[] nameArray =
                new String[contacts.stringArraySize(Contact.NAME)];
        nameArray[Contact.NAME_FAMILY] = names[1];
        nameArray[Contact.NAME_GIVEN] = names[0];
        contact.addStringArray(Contact.NAME, PIMItem.ATTR_NONE, nameArray);

        // Invoke the Adress Book application
        final AddressBookArguments abArg =
                new AddressBookArguments(AddressBookArguments.ARG_NEW, contact);
        Invoke.invokeApplication(Invoke.APP_TYPE_ADDRESSBOOK, abArg);

        // Link the linkable contact with the new BlackBerry contact
        contact = LinkedContactUtilities.linkContact(contact, linkableContact);

        return contact;
    }

    /**
     * @see MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        menu.add(_viewEmailMenuItem);
        menu.add(_viewPhoneMenuItem);
        final LinkableContact selected =
                _contacts[_listField.getSelectedIndex()];
        if (LinkedContactUtilities.getLinkedContact(selected) != null) {
            menu.add(new UnlinkContactMenuItem());
        } else {
            menu.add(_linkToBbContact);
        }

        final DefaultLinkableContact copy =
                new DefaultLinkableContact(selected.getContactID(),
                        ContactLinkingDemo.SECONDARY_APPLICATION_ID);
        if (LinkedContactUtilities.getLinkedContact(copy) != null) {
            menu.add(new AltUnlinkContactMenuItem());
        } else {
            menu.add(_altLinkToBbContact);
        }
        super.makeMenu(menu, instance);
    }

    /**
     * MenuItem class to display a contact's email address
     */
    private class ViewEmailMenuItem extends MenuItem {
        public ViewEmailMenuItem() {
            super("View Email", 0x00000000, 0);
        }

        public void run() {
            Dialog.inform(_contacts[_listField.getSelectedIndex()]
                    .getString(LinkableContact.EMAIL));
        }
    }

    /**
     * MenuItem class to display a contact's phone number
     */
    private class ViewPhoneMenuItem extends MenuItem {
        public ViewPhoneMenuItem() {
            super("View Phone", 0x00000001, 0);
        }

        public void run() {
            Dialog.inform(_contacts[_listField.getSelectedIndex()]
                    .getString(LinkableContact.MOBILE_PHONE));
        }
    }

    /**
     * MenuItem to link a LinkableContact to a BlackBerryContact
     */
    private class LinkToContactMenuItem extends MenuItem {
        public LinkToContactMenuItem() {
            super("Link To BlackBerry Contact", 0x00010000, 0);
        }

        public void run() {
            linkContact(_contacts[_listField.getSelectedIndex()]);
        }
    }

    /**
     * MenuItem to link a LinkableContact to a BlackBerryContact using
     * ContactLinkingDemo.SECONDARY_APPLICATION_ID (to demonstrate how a
     * BlackBerryContact can be linked to by multiple applications).
     */
    private class AltLinkToContactMenuItem extends MenuItem {
        public AltLinkToContactMenuItem() {
            super("Secondary Link To BlackBerry Contact", 0x00010001, 0);
        }

        public void run() {
            final LinkableContact linkableContact =
                    _contacts[_listField.getSelectedIndex()];
            final DefaultLinkableContact copy =
                    new DefaultLinkableContact(linkableContact.getContactID(),
                            ContactLinkingDemo.SECONDARY_APPLICATION_ID);
            copy.setString(Contact.NAME, linkableContact
                    .getString(Contact.NAME));
            copy.setString(Contact.EMAIL, linkableContact
                    .getString(Contact.EMAIL));
            copy.setString(Contact.ATTR_MOBILE, linkableContact
                    .getString(Contact.ATTR_MOBILE));
            linkContact(copy);
        }
    }

    /**
     * MenuItem to unlink a LinkableContact using
     * ContactLinkingDemo.SECONDARY_APPLICATION_ID (to demonstrate how a
     * BlackBerryContact can be linked to by multiple applications).
     */
    private class AltUnlinkContactMenuItem extends MenuItem {
        public AltUnlinkContactMenuItem() {
            super("Secondary Unlink Contact", 0x00010003, 0);
        }

        public void run() {
            final LinkableContact linkableContact =
                    _contacts[_listField.getSelectedIndex()];
            final DefaultLinkableContact copy =
                    new DefaultLinkableContact(linkableContact.getContactID(),
                            ContactLinkingDemo.SECONDARY_APPLICATION_ID);
            unlinkContact(copy);
        }
    }

    /**
     * Returns the name to be displayed for a given Contact
     * 
     * @param contact
     *            The Contact for which to extract the display name
     * @return The name to be displayed
     */
    public static String getDisplayName(final Contact contact) {
        if (contact == null) {
            return null;
        }

        String displayName = null;

        // See if there is a meaningful name set for the contact
        if (contact.countValues(Contact.NAME) > 0) {
            final String[] name = contact.getStringArray(Contact.NAME, 0);
            final String firstName = name[Contact.NAME_GIVEN];
            final String lastName = name[Contact.NAME_FAMILY];
            if (firstName != null && lastName != null) {
                displayName = firstName + " " + lastName;
            } else if (firstName != null) {
                displayName = firstName;
            } else if (lastName != null) {
                displayName = lastName;
            }

            if (displayName != null) {
                final String namePrefix = name[Contact.NAME_PREFIX];
                if (namePrefix != null) {
                    displayName = namePrefix + " " + displayName;
                }
                return displayName;
            }
        }

        // If no meaningful name is set, use the company name
        if (contact.countValues(Contact.ORG) > 0) {
            final String companyName = contact.getString(Contact.ORG, 0);
            if (companyName != null) {
                return companyName;
            }
        }
        return displayName;
    }

    /**
     * MenuItem to unlink a LinkableContact
     */
    private class UnlinkContactMenuItem extends MenuItem {
        public UnlinkContactMenuItem() {
            super("Unlink Contact", 0x00010002, 0);
        }

        public void run() {
            unlinkContact(_contacts[_listField.getSelectedIndex()]);
        }
    }
}

/**
 * A ListField class to display SampleContact objects
 */
class SampleContactListField extends ListField implements ListFieldCallback {
    private final Vector _list = new Vector();

    /**
     * Creates a new SampleContactListField object
     * 
     * @param contacts
     *            An array of SampleContact objects to display
     */
    public SampleContactListField(final SampleContact[] contacts) {
        super();
        displayData(contacts);
        setCallback(this);
        setEmptyString("", DrawStyle.HCENTER);
    }

    /**
     * Displays the data for this ListField
     * 
     * @param list
     *            The array of contacts to be displayed
     */
    private void displayData(SampleContact[] list) {
        if (list == null) {
            list = new SampleContact[0];
        }

        _list.setSize(list.length);

        for (int lv = list.length - 1; lv >= 0; --lv) {
            _list.setElementAt(list[lv], lv);
        }

        setSize(list.length, 0);

        fieldChangeNotify(FieldChangeListener.PROGRAMMATIC);
    }

    // ListFieldCallback implementation ----------------------------------------
    /**
     * @see ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        return Display.getWidth();
    }

    /**
     * @see ListFieldCallback#indexOfList(ListField, String, int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return -1;
    }

    /**
     * @see ListFieldCallback#get(ListField, int)
     */
    public Object get(final ListField listField, final int index) {
        return _list.elementAt(index);
    }

    /**
     * @see ListFieldCallback#drawListRow(ListField, Graphics, int, int, int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        // 4 pixel padding from left and right
        graphics.drawText(_list.elementAt(index).toString(), 0, y,
                DrawStyle.HDEFAULT, width);
    }
}
