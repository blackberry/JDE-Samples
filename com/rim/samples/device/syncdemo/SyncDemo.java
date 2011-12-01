/**
 * SyncDemo.java
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

import java.io.EOFException;
import java.util.Vector;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.ConverterUtilities;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.DataBuffer;

/**
 * This application stores contact information in a PersistantObject which can
 * be synchronized with BlackBerry Desktop Manager using Backup and Restore.
 * 
 * Note: This class always retrieves the most recent contact list by loading the
 * contacts from the persistant store so any instance of the SyncDemo class may
 * be used for synchronization.
 */
public final class SyncDemo extends UiApplication implements SyncConverter,
        SyncCollection, ListFieldCallback {
    // Members
    // ------------------------------------------------------------------
    private final ListField _listField;
    private final AddContactAction _addContactAction;
    private final ViewContactAction _viewContactAction;

    // Statics
    // ------------------------------------------------------------------
    private static final int FIELDTAG_FIRST_NAME = 1;
    private static final int FIELDTAG_LAST_NAME = 2;
    private static final int FIELDTAG_EMAIL_ADDRESS = 3;
    private static final long KEY = -2115940372; // Hash of
                                                 // com.rim.samples.device.syncdemo

    private static PersistentObject _persist;
    private static Vector _contacts;

    /**
     * Entry point for application.
     * 
     * @param args
     *            Command line arguments.
     */
    public static void main(final String[] args) {

        _persist = PersistentStore.getPersistentObject(KEY);
        _contacts = (Vector) _persist.getContents();

        if (args != null && args.length > 0 && args[0].equals("init")) {
            // Initialize persistent store on startup.
            if (_contacts == null) {
                _contacts = new Vector();
                _persist.setContents(_contacts);
                _persist.commit();
            }

            // Enable app for synchronization
            SyncManager.getInstance().enableSynchronization(new SyncDemo());
        } else {
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            final SyncDemo app = new SyncDemo();
            app.enterEventDispatcher();
        }
    }

    // Inner classes -----------------------------------------------------------
    /**
     * Adds a contact to the persistent store.
     */
    private class AddContactAction extends MenuItem {
        /**
         * Default constructor
         */
        private AddContactAction() {
            super("Add", 100000, 10);
        }

        public void run() {
            final ContactScreen screen = new ContactScreen();
            UiApplication.getUiApplication().pushModalScreen(screen);

            final ContactData contact = screen.getContact();

            if (contact != null) {
                _contacts.addElement(contact);
                _persist.setContents(_contacts);
                _persist.commit();
            }

            reloadContactList();
        }
    }

    /**
     * Views the selected contact's information.
     */
    private class ViewContactAction extends MenuItem {
        /**
         * Default constructor
         */
        public ViewContactAction() {
            super("View", 100001, 10);
        }

        public void run() {
            final ContactScreen screen =
                    new ContactScreen((ContactData) _contacts
                            .elementAt(_listField.getSelectedIndex()), false);
            UiApplication.getUiApplication().pushScreen(screen);
        }
    }

    /**
     * Edits a contact.
     */
    private class EditContactAction extends MenuItem {
        private final int _index;

        /**
         * Constructs a menu item to edit a specific contact from the contact
         * list.
         * 
         * @param index
         *            The index of the contact in the contact list to edit
         */
        private EditContactAction(final int index) {
            super("Edit", 100000, 6);
            _index = index;
        }

        public void run() {
            final ContactData oldContactData =
                    (ContactData) _contacts.elementAt(_index);
            final ContactScreen screen =
                    new ContactScreen(oldContactData, true);
            UiApplication.getUiApplication().pushModalScreen(screen);

            final ContactData newContactData = screen.getContact();

            if (newContactData != null) {
                if (_contacts.contains(oldContactData)) {
                    _contacts.setElementAt(newContactData, _contacts
                            .indexOf(oldContactData));
                    PersistentObject.commit(_contacts);
                }
            }

            reloadContactList();
        }
    }

    /**
     * Deletes a contact.
     */
    private class DeleteContactAction extends MenuItem {
        private final int _deleteIndex;

        /**
         * Constructs a menu item to delete a specific contact when invoked.
         * 
         * @param deleteIndex
         *            The index of the contact to delete.
         */
        private DeleteContactAction(final int deleteIndex) {
            super("Delete", 100000, 7);
            _deleteIndex = deleteIndex;
        }

        public void run() {
            final ContactData contactData =
                    (ContactData) _contacts.elementAt(_deleteIndex);

            final int result =
                    Dialog.ask(Dialog.DELETE, "Delete "
                            + contactData.getFirst() + " "
                            + contactData.getLast() + "?");
            if (result == Dialog.YES) {
                _contacts.removeElementAt(_deleteIndex);
                reloadContactList();
            }
        }
    }

    /**
     * This screen acts as the main screen which allows the user to add and view
     * synchronized contacts.
     */
    private final class SyncDemoScreen extends MainScreen {
        /**
         * Default constructor
         */
        private SyncDemoScreen() {
            setTitle(new LabelField("Contacts", DrawStyle.ELLIPSIS
                    | Field.USE_ALL_WIDTH));
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            menu.add(_addContactAction);
            menu.addSeparator();

            if (_contacts.size() > 0) {

                final EditContactAction _editContactAction =
                        new EditContactAction(_listField.getSelectedIndex());
                menu.add(_editContactAction);

                final DeleteContactAction _deleteContactAction =
                        new DeleteContactAction(_listField.getSelectedIndex());
                menu.add(_deleteContactAction);

                menu.add(_viewContactAction);
            }

            menu.addSeparator();

            super.makeMenu(menu, instance);
        }
    }

    /**
     * Default constructor
     */
    public SyncDemo() {

        _listField = new ListField();
        _listField.setCallback(this);

        _addContactAction = new AddContactAction();
        _viewContactAction = new ViewContactAction();

        // Create a new screen for the application.
        final SyncDemoScreen screen = new SyncDemoScreen();

        screen.add(_listField);

        // Push the screen onto the UI stack for rendering.
        pushScreen(screen);

        reloadContactList();
    }

    /**
     * Refreshes contact list on screen.
     * 
     * @return True
     */
    private void reloadContactList() {
        _listField.setSize(_contacts.size());
    }

    // SyncConverter methods
    // ----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncConverter#convert(SyncObject,DataBuffer,int)
     */
    public boolean convert(final SyncObject object, final DataBuffer buffer,
            final int version) {
        if (version == getSyncVersion()) {
            if (object instanceof ContactData) {
                final String first = ((ContactData) object).getFirst();
                final String last = ((ContactData) object).getLast();
                final String email = ((ContactData) object).getEmail();

                // Write the contact information to the DataBuffer.
                ConverterUtilities.writeString(buffer, FIELDTAG_FIRST_NAME,
                        first);
                ConverterUtilities
                        .writeString(buffer, FIELDTAG_LAST_NAME, last);
                ConverterUtilities.writeString(buffer, FIELDTAG_EMAIL_ADDRESS,
                        email);

                return true;
            }
        }

        return false;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncConverter#convert(DataBuffer,int,int)
     */
    public SyncObject convert(final DataBuffer data, final int version,
            final int UID) {
        final ContactData contact = new ContactData(UID);

        try {
            // Extract the contact information from the DataBuffer.
            while (data.available() > 0) {
                if (ConverterUtilities.isType(data, FIELDTAG_FIRST_NAME)) {
                    contact.setFirst(new String(ConverterUtilities
                            .readByteArray(data)).trim());
                }

                if (ConverterUtilities.isType(data, FIELDTAG_LAST_NAME)) {
                    contact.setLast(new String(ConverterUtilities
                            .readByteArray(data)).trim());
                }

                if (ConverterUtilities.isType(data, FIELDTAG_EMAIL_ADDRESS)) {
                    contact.setEmail(new String(ConverterUtilities
                            .readByteArray(data)).trim());
                }
            }

            return contact;
        } catch (final EOFException e) {
            System.err.println(e.toString());
        }

        return null;
    }

    // SyncCollection methods
    // ----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#addSyncObject(SyncObject)
     */
    public boolean addSyncObject(final SyncObject object) {
        _contacts.addElement(object);

        return true;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#updateSyncObject(SyncObject,SyncObject)
     */
    public boolean updateSyncObject(final SyncObject oldObject,
            final SyncObject newObject) {
        return false; // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#removeSyncObject(SyncObject)
     */
    public boolean removeSyncObject(final SyncObject object) {
        return false; // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#removeAllSyncObjects()
     */
    public boolean removeAllSyncObjects() {
        return false; // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjects()
     */
    public SyncObject[] getSyncObjects() {
        final SyncObject[] contactArray = new SyncObject[_contacts.size()];

        for (int i = _contacts.size() - 1; i >= 0; --i) {
            contactArray[i] = (SyncObject) _contacts.elementAt(i);
        }

        return contactArray;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObject(int)
     */
    public SyncObject getSyncObject(final int uid) {
        for (int i = _contacts.size() - 1; i >= 0; --i) {
            final SyncObject so = (SyncObject) _contacts.elementAt(i);

            if (so.getUID() == uid) {
                return so;
            }
        }

        return null;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#isSyncObjectDirty(SyncObject)
     */
    public boolean isSyncObjectDirty(final SyncObject object) {
        return false; // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#setSyncObjectDirty(SyncObject)
     */
    public void setSyncObjectDirty(final SyncObject object) {
        // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#clearSyncObjectDirty(SyncObject)
     */
    public void clearSyncObjectDirty(final SyncObject object) {
        // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjectCount()
     */
    public int getSyncObjectCount() {
        _persist = PersistentStore.getPersistentObject(KEY);
        _contacts = (Vector) _persist.getContents();

        return _contacts.size();
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncVersion()
     */
    public int getSyncVersion() {
        return 1;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncName()
     */
    public String getSyncName() {
        return "Contacts";
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncName(Locale)
     */
    public String getSyncName(final Locale locale) {
        return null;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncConverter()
     */
    public SyncConverter getSyncConverter() {
        return this;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#beginTransaction()
     */
    public void beginTransaction() {
        _persist = PersistentStore.getPersistentObject(KEY);
        _contacts = (Vector) _persist.getContents();
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#endTransaction()
     */
    public void endTransaction() {
        _persist.setContents(_contacts);
        _persist.commit();
    }

    // ListFieldCallback methods
    // ------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        if (listField == _listField && index < _contacts.size()) {
            final ContactData contact =
                    (ContactData) _contacts.elementAt(index);
            final String personal =
                    contact.getFirst() + " " + contact.getLast();
            graphics.drawText(personal, 0, y, 0, width);
        }
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField ,
     *      int)
     */
    public Object get(final ListField listField, final int index) {
        if (listField == _listField) {
            // If index is out of bounds an exception will be thrown, but that's
            // the
            // behaviour we want in that case.
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

    public int indexOfList(final ListField listField, final String prefix,
            final int start) {
        return -1; // Not implemented.
    }
}
