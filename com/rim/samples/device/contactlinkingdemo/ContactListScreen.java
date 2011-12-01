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
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.table.AbstractTableModel;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;
import net.rim.device.api.util.StringUtilities;

/**
 * A screen displaying a list of contacts
 */
public final class ContactListScreen extends MainScreen {
    private static SampleContact[] _contacts;

    private static final int SELECT_CONTACT = 0;
    private static final int CREATE_NEW = 1;

    private MenuItem _viewEmailMenuItem;
    private MenuItem _viewPhoneMenuItem;
    private MenuItem _linkToBbContact;
    private MenuItem _altLinkToBbContact;

    private AbstractTableModel _model;
    private TableView _view;

    /**
     * Creates a new ContactListScreen object
     */
    public ContactListScreen() {
        super(Manager.NO_VERTICAL_SCROLL);

        // Initialize UI
        setTitle("Contact Linking Demo");

        _viewEmailMenuItem = new ViewEmailMenuItem();
        _viewPhoneMenuItem = new ViewPhoneMenuItem();
        _linkToBbContact = new LinkToContactMenuItem();
        _altLinkToBbContact = new AltLinkToContactMenuItem();

        // Initialize the contact list with all contact information
        initializeContacts();

        // Create an adapter for displaying contact data in table
        _model = new ContactTableModelAdapter();

        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        // Set the highlight style for the view
        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));

        // Create a data template that will format the model data as an array of
        // LabelFields
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final Field[] fields =
                        { new LabelField(((SampleContact) _model
                                .getRow(modelRowIndex)).toString(),
                                DrawStyle.ELLIPSIS | Field.NON_FOCUSABLE) };

                return fields;
            }
        };

        // Define the regions of the data template and column/row size
        dataTemplate.createRegion(new XYRect(0, 0, 1, 1));
        dataTemplate.setColumnProperties(0, new TemplateColumnProperties(
                Display.getWidth()));
        dataTemplate.setRowProperties(0, new TemplateRowProperties(24));

        _view.setDataTemplate(dataTemplate);
        dataTemplate.useFixedHeight(true);

        // Add the contact list to the screen
        add(_view);
    }

    /**
     * Returns the contact currently highlighted in table
     * 
     * @return The currently selected contact
     */
    private SampleContact getSelectedContact() {
        return (SampleContact) _model.getRow(_view.getRowNumberWithFocus());
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

        try {
            // Commit changes to the contact model
            contact.commit();

            // Link the linkable contact with the new BlackBerry contact
            contact =
                    LinkedContactUtilities
                            .linkContact(contact, linkableContact);
        } catch (final PIMException e) {
            Dialog.inform("BlackBerryContact.commit() threw exception: "
                    + e.toString());
        }

        return contact;
    }

    /**
     * @see MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        menu.add(_viewEmailMenuItem);
        menu.add(_viewPhoneMenuItem);
        final LinkableContact selected = getSelectedContact();
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
     * Adapter for displaying contact information in table format
     */
    private static class ContactTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _contacts.length;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns() {
            return 1;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        public Object doGetRow(final int rowIndex) {
            return _contacts[rowIndex];
        }
    };

    /**
     * MenuItem class to display a contact's email address
     */
    private class ViewEmailMenuItem extends MenuItem {
        public ViewEmailMenuItem() {
            super(new StringProvider("View Email"), 0x230020, 0);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    Dialog.inform(getSelectedContact().getString(
                            LinkableContact.EMAIL));
                }
            }));
        }
    }

    /**
     * MenuItem class to display a contact's phone number
     */
    private class ViewPhoneMenuItem extends MenuItem {
        public ViewPhoneMenuItem() {
            super(new StringProvider("View Phone"), 0x230020, 0);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    Dialog.inform(getSelectedContact().getString(
                            LinkableContact.MOBILE_PHONE));
                }
            }));
        }
    }

    /**
     * MenuItem to link a LinkableContact to a BlackBerryContact
     */
    private class LinkToContactMenuItem extends MenuItem {
        public LinkToContactMenuItem() {
            super(new StringProvider("Link To BlackBerry Contact"), 0x230030, 0);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    linkContact(getSelectedContact());
                }
            }));
        }
    }

    /**
     * MenuItem to link a LinkableContact to a BlackBerryContact using
     * ContactLinkingDemo.SECONDARY_APPLICATION_ID (to demonstrate how a
     * BlackBerryContact can be linked to by multiple applications).
     */
    private class AltLinkToContactMenuItem extends MenuItem {
        public AltLinkToContactMenuItem() {
            super(new StringProvider("Secondary Link To BlackBerry Contact"),
                    0x230040, 0);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    final LinkableContact linkableContact =
                            getSelectedContact();
                    final DefaultLinkableContact copy =
                            new DefaultLinkableContact(linkableContact
                                    .getContactID(),
                                    ContactLinkingDemo.SECONDARY_APPLICATION_ID);
                    copy.setString(Contact.NAME, linkableContact
                            .getString(Contact.NAME));
                    copy.setString(Contact.EMAIL, linkableContact
                            .getString(Contact.EMAIL));
                    copy.setString(Contact.ATTR_MOBILE, linkableContact
                            .getString(Contact.ATTR_MOBILE));
                    linkContact(copy);
                }
            }));
        }
    }

    /**
     * MenuItem to unlink a LinkableContact using
     * ContactLinkingDemo.SECONDARY_APPLICATION_ID (to demonstrate how a
     * BlackBerryContact can be linked to by multiple applications).
     */
    private class AltUnlinkContactMenuItem extends MenuItem {
        public AltUnlinkContactMenuItem() {
            super(new StringProvider("Secondary Unlink Contact"), 0x230050, 0);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    final LinkableContact linkableContact =
                            getSelectedContact();
                    final DefaultLinkableContact copy =
                            new DefaultLinkableContact(linkableContact
                                    .getContactID(),
                                    ContactLinkingDemo.SECONDARY_APPLICATION_ID);
                    unlinkContact(copy);
                }
            }));
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
            super(new StringProvider("Unlink Contact"), 0x230060, 0);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    unlinkContact(getSelectedContact());
                }
            }));
        }
    }
}
