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

public final class ContactScreen extends MainScreen {
    // Members
    // ------------------------------------------------------------------
    private final EditField _first, _last, _email;
    private final SaveMenuItem _saveMenuItem;
    private ContactData _contact;

    // Inner classes
    // ------------------------------------------------------------
    private class SaveMenuItem extends MenuItem {
        private SaveMenuItem() {
            super("Save", 100000, 5);
        }

        public void run() {
            // If successful, return to contact list.
            if (onSave()) {
                final UiApplication uiapp = UiApplication.getUiApplication();
                uiapp.popScreen(uiapp.getActiveScreen());
            }
        }
    }

    // Constructor
    public ContactScreen() {
        _saveMenuItem = new SaveMenuItem();

        setTitle(new LabelField("Add Contact", DrawStyle.ELLIPSIS
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

    public ContactData getContact() {
        return _contact;
    }

    /**
     * 
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        final String firstName = _first.getText().trim();
        final String lastName = _last.getText().trim();
        final String email = _email.getText().trim();

        // Check that first or last name and email has been entered.
        if (firstName.length() == 0 && lastName.length() == 0
                || email.length() == 0) {
            Dialog.inform("First or Last Name and Email required");
            return false;
        } else {
            _contact = new ContactData();

            _contact.setFirst(firstName);
            _contact.setLast(lastName);

            return true;
        }
    }

}
