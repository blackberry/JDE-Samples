/*
 * OTASyncDemo.java
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

import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.Display;
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

/**
 * Sample to demonstrate synchronization of contact data with a simulated BES
 * environment. This sample requires the BlackBerry Sync Server SDK and the
 * BlackBerry Email and MDS Services Simulators, available from
 * www.blackberry.com/developers. For more information on how to use this sample
 * see the Synchronization Server SDK Development Guide bundled with the
 * BlackBerry Sync Server SDK.
 */

class OTASyncDemo extends UiApplication implements ListFieldCallback {
    // Members
    // ------------------------------------------------------------------
    private final ListField _listField;
    private final AddContactAction _addContactAction;
    private final RefreshAction _refreshAction;

    // Statics
    // ------------------------------------------------------------------
    private static OTAContactCollection _otaContactCollection;

    // Inner Classes
    // ------------------------------------------------------------
    private class AddContactAction extends MenuItem {
        private AddContactAction() {
            super("Add", 100000, 5);
        }

        public void run() {
            final OTAContactScreen screen = new OTAContactScreen();
            UiApplication.getUiApplication().pushModalScreen(screen);

            final OTAContactData contact = screen.getContact();

            if (contact != null) {
                contact.setGUID(UIDGenerator.getUID());
                _otaContactCollection.addSyncObject(contact);
            }

            reloadContactList();
        }
    }

    private class EditContactAction extends MenuItem {
        private final int _contactIndex;

        private EditContactAction(final int contactIndex) {
            super("Edit", 100000, 6);
            _contactIndex = contactIndex;
        }

        public void run() {
            final OTAContactData oldContactData =
                    (OTAContactData) _otaContactCollection.getAt(_contactIndex);
            final OTAContactScreen screen =
                    new OTAContactScreen(oldContactData);
            UiApplication.getUiApplication().pushModalScreen(screen);

            final OTAContactData newContactData = screen.getContact();

            if (newContactData != null) {
                _otaContactCollection.updateSyncObject(oldContactData,
                        newContactData);
            }

            reloadContactList();
        }
    }

    private class DeleteContactAction extends MenuItem {
        private final int _deleteIndex;

        private DeleteContactAction(final int deleteIndex) {
            super("Delete", 100000, 7);
            _deleteIndex = deleteIndex;
        }

        public void run() {
            final OTAContactData contactData =
                    (OTAContactData) _otaContactCollection.getAt(_deleteIndex);

            final int result =
                    Dialog.ask(Dialog.DELETE, "Delete "
                            + contactData.getFirst() + " "
                            + contactData.getLast() + "?");
            if (result == Dialog.YES) {
                _otaContactCollection
                        .removeSyncObject((SyncObject) _otaContactCollection
                                .getAt(_deleteIndex));
                reloadContactList();
            }
        }
    }

    private class RefreshAction extends MenuItem {
        private RefreshAction() {
            super("Refresh", 100000, 8);
        }

        public void run() {
            reloadContactList();
        }
    }

    private class OTASyncDemoScreen extends MainScreen {

        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            menu.add(_addContactAction);
            menu.add(_refreshAction);

            if (_otaContactCollection.getSyncObjectCount() > 0) {
                final EditContactAction _editContactAction =
                        new EditContactAction(_listField.getSelectedIndex());
                menu.add(_editContactAction);

                final DeleteContactAction _deleteContactAction =
                        new DeleteContactAction(_listField.getSelectedIndex());
                menu.add(_deleteContactAction);
            }

            menu.addSeparator();

            super.makeMenu(menu, instance);
        }
    }

    // Constructor
    private OTASyncDemo() {
        // Create a new screen for the application.
        final OTASyncDemoScreen screen = new OTASyncDemoScreen();

        _addContactAction = new AddContactAction();
        _refreshAction = new RefreshAction();

        screen.setTitle(new LabelField("OTA Sync Demo Contacts",
                DrawStyle.ELLIPSIS | Field.USE_ALL_WIDTH));

        _listField = new ListField();
        _listField.setCallback(this);
        screen.add(_listField);

        // Push the screen onto the UI stack for rendering.
        pushScreen(screen);

        reloadContactList();
    }

    private boolean reloadContactList() {
        // Refreshes contact list on screen.
        _listField.setSize(_otaContactCollection.getSyncObjectCount());

        return true;
    }

    // ListFieldCallback methods
    // ------------------------------------------------
    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(final ListField listField, final Graphics graphics,
            final int index, final int y, final int width) {
        if (listField == _listField
                && index < _otaContactCollection.getSyncObjectCount()) {
            final OTAContactData contact =
                    (OTAContactData) _otaContactCollection.getAt(index);
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
            // If index is out of bounds an exception will be thrown, but
            // that's the behaviour we want in that case.
            return _otaContactCollection.getAt(index);
        }

        return null;
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField listField) {
        // use all the width of the current LCD
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

    public static void main(final String[] args) {
        boolean startup = false;

        for (int i = 0; i < args.length; ++i) {
            if (args[i].startsWith("init")) {
                startup = true;
            }
        }

        _otaContactCollection = OTAContactCollection.getInstance();

        if (startup) {
            // Enable app for synchronization.
            SyncManager.getInstance().enableSynchronization(
                    _otaContactCollection);
        } else {
            final OTASyncDemo app = new OTASyncDemo();
            app.enterEventDispatcher();
        }
    }
}
