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

package com.rim.samples.device.syncdemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
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
import net.rim.device.api.util.StringProvider;

/**
 * This screen allows the user to view and edit a contact's information.
 */
public final class ContactScreen extends MainScreen {
    // Members
    // ------------------------------------------------------------------
    private final EditField _first, _last, _email;
    private final SaveMenuItem _saveMenuItem;
    private ContactData _contact;

    // Inner classes
    // ------------------------------------------------------------
    /**
     * Saves the current contact's information.
     */
    private class SaveMenuItem extends MenuItem {
        /**
         * Default constructor
         */
        private SaveMenuItem() {
            super(new StringProvider("Save"), 0x230010, 5);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * Saves and closes this screen.
                 * 
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    // If successful, return to contact list.
                    if (onSave()) {
                        final UiApplication uiapp =
                                UiApplication.getUiApplication();
                        uiapp.popScreen(uiapp.getActiveScreen());
                    }
                }
            }));
        }
    }

    /**
     * Default constructor
     */
    public ContactScreen() {
        _saveMenuItem = new SaveMenuItem();

        setTitle(new LabelField("Contact", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));

        _first = new EditField("First: ", "");
        add(_first);

        _last = new EditField("Last: ", "");
        add(_last);

        _email =
                new EditField("Email: ", "", TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_EMAIL);
        add(_email);

        addMenuItem(_saveMenuItem);
    }

    /**
     * Constructor to display an existing contact and allow the user to edit the
     * information.
     * 
     * @param contact
     *            The contact data to view and edit
     * @param editable
     *            True if the contact is editable, otherwise false
     */
    public ContactScreen(final ContactData contact, final boolean editable) {
        this();

        _contact = contact;
        _first.setText(_contact.getFirst());
        _first.setEditable(editable);
        _last.setText(_contact.getLast());
        _last.setEditable(editable);
        _email.setText(_contact.getEmail());
        _email.setEditable(editable);
    }

    /**
     * Gets the contact information displayed by this screen.
     * 
     * @return The contact information displayed by this screen
     */
    public ContactData getContact() {
        return _contact;
    }

    /**
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        final String firstName = _first.getText().trim();
        final String lastName = _last.getText().trim();
        final String email = _email.getText().trim();

        // Check that a name and an email has been entered.
        if (firstName.length() == 0 && lastName.length() == 0
                || email.length() == 0) {
            Dialog.inform("First or Last Name and Email required");
            return false;
        } else {
            _contact = new ContactData();

            _contact.setFirst(firstName);
            _contact.setLast(lastName);
            _contact.setEmail(email);

            return true;
        }
    }

}
