/**
 * OTABackupRestoreDemo.java
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

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This application demonstrates how to use the
 * OTABackUpRestoreContactCollection class to back up contacts over the air onto
 * a BES. See the "readme.txt" file in this project for setup details.
 */
public class OTABackupRestoreDemo extends UiApplication implements
        ListFieldCallback {
    private static ListField _listField;
    private static AddContactAction _addContactAction;

    private static OTABackupRestoreContactCollection _contacts;

    /**
     * Adds a contact to the contact list
     */
    private class AddContactAction extends MenuItem {
        /**
         * Creates a new AddContactAction object
         */
        private AddContactAction() {
            super("Add", 100000, 10);
        }

        /**
         * Adds a contact to the contact list
         */
        public void run() {
            // Retrieve the contact's information from the user
            final OTAContactScreen screen = new OTAContactScreen();
            UiApplication.getUiApplication().pushModalScreen(screen);

            final OTAContactData contact = screen.getContact();

            // Add the contact
            if (contact != null) {
                // Create a unique id for the contact - required for ota sync.
                contact.setUID(UIDGenerator.getUID());

                // Add the contact to the collection.
                _contacts.addSyncObject(contact);
            }

            reloadContactList();
        }
    }

    /**
     * Views a contact from the contact list
     */
    private static class ViewContactAction extends MenuItem {
        private final int _index;

        /**
         * Constructs a menu item to view a specific contact from the contact
         * list
         * 
         * @param index
         *            The index of the contact from the contact list to view
         */
        private ViewContactAction(final int index) {
            super("View", 100000, 5);
            _index = index;
        }

        /**
         * Displays the contact information.
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            final OTAContactScreen screen =
                    new OTAContactScreen(_contacts.contactAt(_index), false);
            UiApplication.getUiApplication().pushScreen(screen);
        }
    }

    /**
     * A class to edits a contact
     */
    private static class EditContactAction extends MenuItem {
        private final int _index;

        /**
         * Constructs a menu item to edit a specific contact from the contact
         * list
         * 
         * @param index
         *            The index of the contact in the contact list to edit
         */
        private EditContactAction(final int index) {
            super("Edit", 100000, 6);
            _index = index;
        }

        /**
         * Edits the contact
         * 
         * @see java.lang.Runnable#run()
         */
        public void run() {
            final OTAContactData oldContact = _contacts.contactAt(_index);
            final OTAContactScreen screen =
                    new OTAContactScreen(oldContact, true);
            UiApplication.getUiApplication().pushModalScreen(screen);

            // Get the newly updated contact
            final OTAContactData newContact = screen.getContact();

            // Update the contact in the collection.
            _contacts.updateSyncObject(oldContact, newContact);
        }
    }

    /**
     * This is the main screen which displays the contact list and creates the
     * menu to let the user manipulate the contacts.
     */
    private static class OTABackupRestoreDemoScreen extends MainScreen {
        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            menu.add(_addContactAction);

            menu.addSeparator();

            final int index = _listField.getSelectedIndex();

            if (index >= 0) {
                menu.add(new ViewContactAction(index));
                menu.add(new EditContactAction(index));
            }

            menu.addSeparator();

            super.makeMenu(menu, instance);
        }
    }

    /**
     * Creates a new OTABackupRestoreDemo object
     */
    public OTABackupRestoreDemo() {
        // Create a new screen for the application
        final OTABackupRestoreDemoScreen screen =
                new OTABackupRestoreDemoScreen();

        _addContactAction = new AddContactAction();

        screen.setTitle("OTA Backup/Restore Contacts");

        _listField = new ListField();
        _listField.setCallback(this);
        screen.add(_listField);

        // Push the screen onto the UI stack for rendering
        pushScreen(screen);

        reloadContactList();
    }

    /**
     * Refreshes the contact list on screen
     */
    private void reloadContactList() {
        _listField.setSize(_contacts.size());
    }

    // ListFieldCallback methods
    // ------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,
     *      Graphics, int, int, int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        if (listField == _listField && index < _contacts.size()) {
            final OTAContactData contact = _contacts.contactAt(index);
            final String personal =
                    contact.getFirst() + " " + contact.getLast();
            graphics.drawText(personal, 0, y, 0, width);
        }
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField,
     *      int)
     */
    public Object get(final ListField listField, final int index) {
        if (listField == _listField) {
            // If index is out of bounds an exception will be thrown, but
            // that's the behaviour we want in that case.
            return _contacts.contactAt(index);
        }

        return null;
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        // Use all the width of the current LCD
        return Display.getWidth();
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
     *      , String, int)
     */
    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return -1; // Not implemented.
    }

    /**
     * Entry point for the application.
     * 
     * @param args
     *            Command line arguments
     */
    public static void main(final String[] args) {
        boolean startup = false;

        for (int i = 0; i < args.length; ++i) {
            if (args[i].startsWith("init")) {
                startup = true;
            }
        }

        // Get the collection enabled for ota backup/restore
        _contacts = OTABackupRestoreContactCollection.getInstance();

        if (startup) {
            // Enable app for synchronization
            SyncManager.getInstance().enableSynchronization(_contacts);
        } else {
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            final OTABackupRestoreDemo app = new OTABackupRestoreDemo();
            app.enterEventDispatcher();
        }
    }
}
