/*
 * OTAContactScreen.java
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

package com.rim.samples.device.otasyncdemo;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This screen displays information about a contact, allowing the user to edit
 * the contact's information. It also allows the user to create new contacts.
 */
public final class OTAContactScreen extends MainScreen {
    // Members
    // ------------------------------------------------------------------
    private final EditField _first, _last, _email, _phone;
    private int _uid;
    private final SaveMenuItem _saveMenuItem;
    private OTAContactData _contact;

    // Inner Classes
    // ------------------------------------------------------------
    private class SaveMenuItem extends MenuItem {
        /**
         * Default constructor.
         */
        private SaveMenuItem() {
            super("Save", 100000, 5);
        }

        /**
         * Saves the contact's information.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            // If successful, return to contact list.
            if (onSave()) {
                final UiApplication uiapp = UiApplication.getUiApplication();
                uiapp.popScreen(uiapp.getActiveScreen());
            }
        }
    }

    /**
     * Default constructor.
     */
    public OTAContactScreen() {
        _saveMenuItem = new SaveMenuItem();

        setTitle(new LabelField("Edit Contact", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));

        _first = new EditField("First: ", "");
        add(_first);

        _last = new EditField("Last: ", "");
        add(_last);

        _email =
                new EditField("Email: ", "", TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_EMAIL);
        add(_email);

        _phone =
                new EditField("Phone:", "", TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_PHONE);
        add(_phone);

        addMenuItem(_saveMenuItem);
    }

    /**
     * Constructs a screen to view and edit a contact's information.
     * 
     * @param contact
     *            The contact to view/edit
     */
    public OTAContactScreen(final OTAContactData contact) {
        this();

        _uid = contact.getUID();
        _first.setText(contact.getFirst());
        _last.setText(contact.getLast());
        _email.setText(contact.getEmail());
        _phone.setText(contact.getPhone());
    }

    /**
     * Retrieve the current contact being displayed.
     * 
     * @return The contact being displayed on this screen
     */
    OTAContactData getContact() {
        return _contact;
    }

    /**
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        final String firstName = _first.getText().trim();
        final String lastName = _last.getText().trim();
        final String email = _email.getText().trim();
        final String phone = _phone.getText().trim();

        // Check that first or last name and email has been entered.
        if (firstName.length() == 0 && lastName.length() == 0
                || email.length() == 0) {
            Dialog.inform("First or Last Name and Email required");
            return false;
        } else {
            _contact = new OTAContactData();

            _contact.setGUID(_uid);
            _contact.setFirst(firstName);
            _contact.setLast(lastName);
            _contact.setEmail(email);
            _contact.setPhone(phone);

            return true;
        }
    }
}
