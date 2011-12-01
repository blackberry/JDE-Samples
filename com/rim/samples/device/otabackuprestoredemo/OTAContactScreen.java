/**
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

package com.rim.samples.device.otabackuprestoredemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.TextField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * This screen allows the user to view and edit contact information
 */
public final class OTAContactScreen extends MainScreen {
    private final EditField _first, _last, _email;
    private int _uid = -1;
    private final SaveMenuItem _saveMenuItem;
    private BackMenuItem _backMenuItem;
    private OTAContactData _contact;

    /**
     * A MenuItem class to saves the current contact
     */
    private class SaveMenuItem extends MenuItem {
        /**
         * Creates a new SaveMenuItem object
         */
        private SaveMenuItem() {
            super(new StringProvider("Save"), 0x230010, 5);
            this.setCommand(new Command(new CommandHandler() {

                /**
                 * Saves the contact and closes this screen
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
     * Closes this screen and goes back one screen
     */
    private static class BackMenuItem extends MenuItem {
        /**
         * Creates a new BackMenuItem object
         */
        private BackMenuItem() {
            super(new StringProvider("Back"), 0x230020, 5);
            this.setCommand(new Command(new CommandHandler() {

                /**
                 * Closes this screen
                 * 
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    final UiApplication uiapp =
                            UiApplication.getUiApplication();
                    uiapp.popScreen(uiapp.getActiveScreen());
                }
            }));
        }
    }

    /**
     * Creates a new OTAContactScreen object
     */
    public OTAContactScreen() {
        _saveMenuItem = new SaveMenuItem();

        setTitle("Contact");

        _first = new EditField("First: ", "");
        add(_first);

        _last = new EditField("Last: ", "");
        add(_last);

        _email =
                new EditField("Email: ", "", TextField.DEFAULT_MAXCHARS,
                        BasicEditField.FILTER_EMAIL);
        add(_email);
    }

    /**
     * Creates a new OTAContactScreen object, specifying an existing contact to
     * view/edit
     * 
     * @param contact
     *            The contact to display
     * @param editable
     *            True if the contact information is editable, otherwise false
     */
    public OTAContactScreen(final OTAContactData contact, final boolean editable) {
        this();

        _backMenuItem = new BackMenuItem();

        _contact = contact;
        _first.setText(_contact.getFirst());
        _first.setEditable(editable);
        _last.setText(_contact.getLast());
        _last.setEditable(editable);
        _email.setText(_contact.getEmail());
        _email.setEditable(editable);
        _uid = contact.getUID();
    }

    /**
     * Retrieves the contact being displayed on this screen
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

        // Check that first or last name and email has been entered
        if (firstName.length() == 0 && lastName.length() == 0
                || email.length() == 0) {
            Dialog.inform("Please enter a first or last name and an email address.");

            return false;
        } else {
            if (_uid == -1) {
                // uid == -1 -> This is a new contact.
                _contact = new OTAContactData();
            }

            _contact.setFirst(firstName);
            _contact.setLast(lastName);
            _contact.setEmail(email);

            return true;
        }
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        // If we are viewing a contact, we aren't able to edit it. In that case
        // we
        // just want a menu item enabling us to go back to the contact list.
        if (_contact == null || _uid != -1) {
            menu.add(_saveMenuItem);
        } else {
            menu.add(_backMenuItem);
        }

        super.makeMenu(menu, instance);
    }
}
