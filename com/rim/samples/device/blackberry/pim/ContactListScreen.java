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

package com.rim.samples.device.blackberry.pim;

import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;

public final class ContactListScreen extends MainScreen implements
        ListFieldCallback {
    // Members
    // ------------------------------------------------------------------
    private final ListField _listField;
    private ContactList _contactList;
    private Contact _contact;
    private Vector _contacts;
    private final AddContactAction _addContactAction;
    private final SelectContactAction _selectContactAction;

    // Inner classes
    // ------------------------------------------------------------
    private class SelectContactAction extends MenuItem {
        /**
         * This class is responsible for selecting a contact from the list.
         */
        public SelectContactAction() {
            super("Select Contact", 100000, 10);
        }

        public void run() {
            final int index = _listField.getSelectedIndex();

            if (index != -1 && !_contacts.isEmpty()) {
                _contact =
                        (Contact) _contacts.elementAt(_listField
                                .getSelectedIndex());
            } else {
                _contact = null;
            }

            final UiApplication uiapp = UiApplication.getUiApplication();
            uiapp.popScreen(uiapp.getActiveScreen());
        }
    }

    private class AddContactAction extends MenuItem {
        private Screen _screen;

        /**
         * This class is responsible for adding an invite field to the create
         * screen.
         */
        public AddContactAction() {
            super("Add New Contact", 100000, 10);
        }

        public void setScreen(final Screen s) {
            _screen = s;
        }

        public void run() {
            if (_screen == null) {
                throw new IllegalStateException(
                        "PIMDemo: No screen set for AddContactAction!");
            }

            UiApplication.getUiApplication().pushModalScreen(
                    new ContactScreen());
            reloadContactList();
        }
    }

    // Constructors
    // -------------------------------------------------------------
    public ContactListScreen() {
        _addContactAction = new AddContactAction();
        _addContactAction.setScreen(this);
        _selectContactAction = new SelectContactAction();
        setTitle("Contact List");

        _listField = new ListField();
        _listField.setCallback(this);
        add(_listField);

        addMenuItem(_selectContactAction);
        addMenuItem(_addContactAction);

        reloadContactList();
    }

    public Contact getSelectedContact() {
        return _contact;
    }

    private boolean reloadContactList() {
        try {
            _contactList =
                    (ContactList) PIM.getInstance().openPIMList(
                            PIM.CONTACT_LIST, PIM.READ_ONLY);

            final Contact hasEmail = _contactList.createContact();
            hasEmail.addString(Contact.ORG, PIMItem.ATTR_NONE, "");
            hasEmail.addString(Contact.EMAIL, Contact.ATTR_HOME, "");

            final Enumeration contactsWithEmail = _contactList.items();
            _contacts = enumToVector(contactsWithEmail);
            _listField.setSize(_contacts.size());

            return true;
        } catch (final PIMException e) {
            return false;
        }
    }

    private Vector enumToVector(final Enumeration enumeration) {
        final Vector v = new Vector();

        if (enumeration == null) {
            return v;
        }

        while (enumeration.hasMoreElements()) {
            v.addElement(enumeration.nextElement());
        }

        return v;
    }

    // ListFieldCallback methods
    // ------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        if (listField == _listField && index < _contacts.size()) {
            final Contact item = (Contact) _contacts.elementAt(index);
            final String[] name = item.getStringArray(Contact.NAME, 0);
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

            graphics.drawText(sb.toString(), 0, y, 0, width);
        }
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField ,
     *      int)
     */
    public Object get(final ListField listField, final int index) {
        if (listField == _listField) {
            // If index is out of bounds an exception will be thrown, but that's
            // the behaviour
            // we want in that case.
            return _contacts.elementAt(index);
        }

        return null;
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        // Use all the width of the current LCD.
        return Display.getWidth();
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
     *      , String , int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return -1; // Not implemented.
    }
}
