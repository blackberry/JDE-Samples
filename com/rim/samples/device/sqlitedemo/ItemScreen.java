/*
 * ItemScreen.java
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

package com.rim.samples.device.sqlitedemo;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * A MainScreen class used to display and edit directory item details
 */
public final class ItemScreen extends MainScreen {
    private final DirectoryItem _item;
    private final EditField _nameField;
    private final EditField _locationField;
    private final EditField _phoneField;
    private final SQLManager _sqlManager;
    private final boolean _newItem;

    /**
     * Constructs a new ItemScreen object
     * 
     * @param item
     *            The DirectoryItem object to be displayed/edited
     * @param sqlManager
     *            A sqlManager instance used to perform database operations
     * @param newItem
     *            True if this screen is displaying a new, blank directory item.
     *            False if this screen is displaying an existing directory item.
     */
    public ItemScreen(final DirectoryItem item, final SQLManager sqlManager,
            final boolean newItem) {
        _item = item;
        _sqlManager = sqlManager;
        _newItem = newItem;

        // Initialize UI components
        setTitle("Item Screen");
        _nameField = new EditField("Name: ", _item.getName());
        _locationField = new EditField("Address: ", _item.getLocation());
        _phoneField =
                new EditField("Phone: ", _item.getPhone(), 20,
                        BasicEditField.FILTER_PHONE);
        add(_nameField);
        add(_locationField);
        add(_phoneField);
    }

    /**
     * @see MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        menu.add(new MenuItem("Save", 0x00010000, 0) {
            public void run() {
                onSave();
                close();
            }
        });
    }

    /**
     * Updates the DirectoryItem object being displayed with any changes made by
     * the user and saves the changes to the database.
     * 
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        // Item name is mandatory as it will be used as a node description by
        // the TreeField
        if (!_nameField.getText().equals("")) {
            String name = _item.getName();
            String location = _item.getLocation();
            String phone = _item.getPhone();

            boolean edited = false;

            // Check whether fields have been edited
            if (_nameField.isDirty()) {
                name = _nameField.getText();
                _item.setName(name);
                edited = true;
            }
            if (_locationField.isDirty()) {
                location = _locationField.getText();
                _item.setLocation(location);
                edited = true;
            }
            if (_phoneField.isDirty()) {
                phone = _phoneField.getText();
                _item.setPhone(phone);
                edited = true;
            }

            if (_newItem) {
                // Add a new item to the database
                final int id =
                        _sqlManager.addItem(name, location, phone, _item
                                .getCategoryId());
                if (id > -1) {
                    _item.setId(id);
                }
            } else {
                if (edited) {
                    // Update the existing database record
                    _sqlManager
                            .updateItem(_item.getId(), name, location, phone);
                }
            }

            return true;
        } else {
            Dialog.alert("Directory entry must have a name");
            _nameField.setFocus();
            return false;
        }
    }
}
