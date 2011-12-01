/**
 * ContactScreen.java
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

import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import net.rim.blackberry.api.pdap.BlackBerryContact;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;

public final class ContactScreen extends MainScreen {
    private final EditField _first, _last, _email, _home, _home2;
    private final SaveMenuItem _saveMenuItem;

    private class SaveMenuItem extends MenuItem {
        private SaveMenuItem() {
            super("Save Contact", 100000, 5);
        }

        public void run() {
            // If successful, return to contact list.
            if (onSave()) {
                final UiApplication uiapp = UiApplication.getUiApplication();
                uiapp.popScreen(uiapp.getActiveScreen());
            }
        }
    }

    /**
     * Creates a new ContactScreen object
     */
    public ContactScreen() {
        _saveMenuItem = new SaveMenuItem();

        setTitle("Create Contact");

        _first = new EditField("First: ", "");
        add(_first);

        _last = new EditField("Last: ", "");
        add(_last);

        _email =
                new EditField("Email: ", "", TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_EMAIL);
        add(_email);

        _home =
                new EditField("Home: ", "", TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_PHONE);
        add(_home);

        _home2 =
                new EditField("Home2: ", "", TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_PHONE);
        add(_home2);

        addMenuItem(_saveMenuItem);
    }

    /**
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        final String firstName = _first.getText().trim();
        final String lastName = _last.getText().trim();
        final String email = _email.getText().trim();
        final String home = _home.getText().trim();
        final String home2 = _home2.getText().trim();

        // Check that first or last name and email has been entered.
        if (firstName.length() == 0 && lastName.length() == 0
                || email.length() == 0) {
            Dialog.inform("First or Last Name and Email required");

            return false;
        } else {
            // Save the contact.
            try {
                final ContactList contactList =
                        (ContactList) PIM.getInstance().openPIMList(
                                PIM.CONTACT_LIST, PIM.WRITE_ONLY);
                final Contact contact = contactList.createContact();
                final String[] name =
                        new String[contactList.stringArraySize(Contact.NAME)];

                /*
                 * This section adds values to selective PIM items. Additional
                 * items can be added for Contact or BlackBerryContact fields.
                 * Please refer to javadocs.
                 */
                if (firstName.length() != 0) {
                    name[Contact.NAME_GIVEN] = firstName;
                }

                if (lastName.length() != 0) {
                    name[Contact.NAME_FAMILY] = lastName;
                }

                contact.addStringArray(Contact.NAME, PIMItem.ATTR_NONE, name);

                if (home.length() != 0) {
                    contact.addString(Contact.TEL, Contact.ATTR_HOME, home);
                }

                if (home2.length() != 0) {
                    contact.addString(Contact.TEL,
                            BlackBerryContact.ATTR_HOME2, home2);
                }

                if (email.length() != 0) {
                    // Perform email validation here.
                    contact.addString(Contact.EMAIL, Contact.ATTR_HOME, email);
                }

                // Persist data to contact list.
                contact.commit();

                // Instruct caller to return to contact list.
                return true;
            } catch (final PIMException e) {
                PIMDemo.errorDialog(e.toString());

                return false;
            }
        }
    }
}
