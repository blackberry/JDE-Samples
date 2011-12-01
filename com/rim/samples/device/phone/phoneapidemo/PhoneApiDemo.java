/*
 * PhoneApiDemo.java
 * 
 * A simple program that demonstrates the Phone API.  Persistently stores the 
 * "talk time" for each phone number contacted while the application is running.
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

package com.rim.samples.device.phone.phoneapidemo;

import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.blackberry.api.phone.AbstractPhoneListener;
import net.rim.blackberry.api.phone.Phone;
import net.rim.blackberry.api.phone.PhoneCall;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.Persistable;

/**
 * The main class for the Phone API demo app.
 */
public final class PhoneApiDemo extends UiApplication {
    // Members
    // -------------------------------------------------------------------------------------
    private final PhoneApiDemoMainScreen _mainScreen;

    // Statics
    // -------------------------------------------------------------------------------------
    private static PersistentObject _persist;
    private static Vector _phoneNumberList;

    // Make sure a database exists for this application.
    static {
        _persist = PersistentStore.getPersistentObject(0x15835f89fc421f8cL); // com.rim.samples.device.phone.phoneapidemo

        synchronized (_persist) {
            _phoneNumberList = (Vector) _persist.getContents();

            if (_phoneNumberList == null) {
                _phoneNumberList = new Vector();
                _persist.setContents(_phoneNumberList);
                _persist.commit();
            }
        }
    }

    /**
     * PhoneApiDemo Constructor. Creates the main screen for the app and pushes
     * it onto the display stack.
     */
    public PhoneApiDemo() {
        _mainScreen = new PhoneApiDemoMainScreen();
        /* parent. */pushScreen(_mainScreen);
    }

    /**
     * Commits the phone number records to the persistent store.
     */
    private static void savePhoneNumberRecords() {
        synchronized (_persist) {
            _persist.commit();
        }
    }

    /**
     * Sets the size of the list field in the main screen. This can only be done
     * when this app is in the foreground because it can cause a repaint.
     */
    public void activate() {
        super.activate();

        if (_mainScreen != null) {
            _mainScreen.setPhoneNumberListFieldSize();
        }
    }

    /**
     * Entry point for the application. On autostartup, it adds a new phone
     * listener for timing phone calls. On ribbon startup, a gui is presented
     * that allows users to view phone number "talk time" records.
     * 
     * @param args
     *            Command-line arguments (used for differentiating between
     *            autostartup and ribbon startup).
     */
    public static void main(final String[] args) {
        if (args.length == 1 && args[0].equals("autostartup")) {
            // Create and register the object that will listen for Phone events.
            // Check for
            // ControlledAccessException as per page 69 of the BlackBerry
            // Application
            // Developer Guide, Volume 2 (Version 4.0).
            try {
                Phone.addPhoneListener(new ConcretePhoneListener());
            } catch (final ControlledAccessException e) {
                UiApplication.getUiApplication().invokeLater(new Runnable() {
                    public void run() {
                        Dialog.alert("Access to Phone API restricted by system administrator: "
                                + e.toString());
                    }
                });

                System.exit(1);
            }
        } else {
            // Create a new instance of the application and make the currently
            // running thread the application's event dispatch thread.
            new PhoneApiDemo().enterEventDispatcher();
        }
    }

    // Private inner classes
    // -----------------------------------------------------------------------

    /**
     * The main screen for the Phone API application. It displays a list of
     * phone numbers that have been contacted, and allows the user to view the
     * record for each one.
     */
    private final class PhoneApiDemoMainScreen extends MainScreen implements
            ListFieldCallback {
        // Members --------------------------------------------------
        private final ListField _phoneNumberListField;
        private final MenuItem _deleteAllItem;

        /**
         * PhoneApiDemoMainScreen constructor. Creates the fields and menu items
         * used on this screen.
         */
        private PhoneApiDemoMainScreen() {
            super();

            /* parent. */setTitle("Phone API Demo");

            _phoneNumberListField = new ListField(_phoneNumberList.size());
            _phoneNumberListField.setCallback(this);
            /* parent. */add(_phoneNumberListField);

            // Menu item that deletes all the phone number records.
            _deleteAllItem = new MenuItem("Delete All", 300000, 120) {
                public void run() {
                    if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                        _phoneNumberList.removeAllElements();
                        PhoneApiDemo.savePhoneNumberRecords();
                        setPhoneNumberListFieldSize();
                    }
                }
            };

            /* parent. */addKeyListener(new PhoneApiDemoKeyListener(this));
        }

        /**
         * Sets the size of the list field that displays the phone number
         * records, which may cause a repaint. Since this app competes with the
         * BlackBerry Phone app for access to the UI, this method can only be
         * called at certain times, such as when validate() is called in the
         * PhoneApiDemo class.
         */
        void setPhoneNumberListFieldSize() {
            _phoneNumberListField.setSize(_phoneNumberList.size());
        }

        /**
         * Creates the menu for this screen.
         * 
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            super.makeMenu(menu, instance);

            // If there are any items in the list, add menu items to view and
            // delete them.
            if (_phoneNumberListField.getSize() > 0) {
                final int index = _phoneNumberListField.getSelectedIndex();
                final PhoneNumberRecord record =
                        (PhoneNumberRecord) get(_phoneNumberListField, index);
                menu.add(new View(record));
                menu.addSeparator();
                menu.add(new Delete(record));
                menu.add(_deleteAllItem);
                menu.addSeparator();
            }
        }

        // ListFieldCallback methods
        // ---------------------------------------------------------------
        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
         */
        public void drawListRow(final ListField listField,
                final Graphics graphics, final int index, final int y,
                final int width) {
            final PhoneNumberRecord record =
                    (PhoneNumberRecord) _phoneNumberList.elementAt(index);
            graphics.drawText((String) record
                    .getField(PhoneNumberRecord.PHONE_NUMBER), 0, y, 0, width);
        }

        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
         */
        public int getPreferredWidth(final ListField listField) {
            return Display.getWidth();
        }

        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField
         *      , int)
         */
        public Object get(final ListField listField, final int index) {
            return _phoneNumberList.elementAt(index);
        }

        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
         *      , String , int)
         */
        public int indexOfList(final ListField listField, final String prefix,
                final int start) {
            return -1; // Not implemented.
        }

        // Private inner classes representing menu items and listeners used by
        // this screen ---------

        /**
         * This class is a menu item allowing a user to view a phone number
         * record.
         */
        private final class View extends MenuItem {
            // Members ----------------------------------------------
            private final PhoneNumberRecord _record;

            /**
             * Constructs a menu item to view a record when invoked.
             * 
             * @param record
             *            The record to view
             */
            private View(final PhoneNumberRecord record) {
                super("View", 200000, 100);
                _record = record;
            }

            /**
             * Shows the phone number record.
             * 
             * @see java.lang.Runnable#run()
             */
            public void run() {
                final MainScreen screen = new MainScreen();

                screen.setTitle(new LabelField("View Phone Record"));

                final PhoneNumberRecordDisplayer displayer =
                        new PhoneNumberRecordDisplayer(_record);
                final Vector fields = displayer.getFields();
                final int numFields = fields.size();

                for (int i = 0; i < numFields; ++i) {
                    screen.add((Field) fields.elementAt(i));
                }

                screen.addKeyListener(new PhoneApiDemoKeyListener(screen));

                PhoneApiDemo.this.pushScreen(screen);
            }
        }

        /**
         * This class is a menu item allowing a user to delete a phone number
         * record.
         */
        private final class Delete extends MenuItem {
            // Members ----------------------------------------------
            private final PhoneNumberRecord _record;

            /**
             * Constructs a menu item to delete a phone record when invoked.
             * 
             * @param record
             *            The phone record to delete
             */
            private Delete(final PhoneNumberRecord record) {
                super("Delete", 300000, 110);
                _record = record;
            }

            /**
             * Deletes the phone record.
             * 
             * @see java.lang.Runnable#run()
             */
            public void run() {
                if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                    _phoneNumberList.removeElement(_record);
                    PhoneApiDemo.savePhoneNumberRecords();
                    PhoneApiDemoMainScreen.this.setPhoneNumberListFieldSize();
                }
            }
        }
    }

    /**
     * Persistable phone number record.
     */
    private static final class PhoneNumberRecord implements Persistable {
        // Members
        // ---------------------------------------------------------------------------------
        private final Vector _fields;
        private long _startTime;

        // Constants
        // -------------------------------------------------------------------------------
        private static final int PHONE_NUMBER = 0;
        private static final int TALK_TIME = 1;

        /**
         * Constructs a PhoneNumberRecord from a phone number.
         * 
         * @param phoneNumber
         *            The phone number of the record to be created
         */
        private PhoneNumberRecord(final String phoneNumber) {
            _fields = new Vector();
            _fields.addElement(phoneNumber);
            _fields.addElement(new Long(0));
            _startTime = 0;
        }

        /**
         * Retrieves one of this record's fields.
         * 
         * @param index
         *            The index of the field to retrieve.
         * @return The field.
         */
        private Object getField(final int index) {
            return _fields.elementAt(index);
        }

        /**
         * Sets one of this record's fields.
         * 
         * @param index
         *            The index of the field to set.
         * @param o
         *            The object that the field is set to.
         */
        private void setField(final int index, final Object o) {
            _fields.setElementAt(o, index);
        }

        /**
         * Determines if this record and the parameter refer to the same phone
         * number record.
         * 
         * @param o
         *            The object to check for equality.
         * @return True if this record and o refer to the same phone number
         *         record; false otherwise.
         */
        public boolean equals(final Object o) {
            if (o instanceof PhoneNumberRecord) {
                final PhoneNumberRecord record = (PhoneNumberRecord) o;
                final String phoneNumber1 =
                        (String) record._fields.elementAt(PHONE_NUMBER);
                final String phoneNumber2 =
                        (String) _fields.elementAt(PHONE_NUMBER);

                return phoneNumber1.equals(phoneNumber2);
            }

            return false;
        }

        /**
         * Determines whether this record is currently recording "talk time".
         * 
         * @return True if this record is currently recording "talk time"; false
         *         otherwise.
         */
        private boolean isActive() {
            return _startTime != 0;
        }

        /**
         * Causes this record to start recording "talk time".
         */
        public void start() {
            _startTime = new Date().getTime();
        }

        /**
         * Causes this record to temporarily stop recording "talk time".
         */
        private void putOnHold() {
            long talkTime = ((Long) _fields.elementAt(TALK_TIME)).longValue();
            talkTime += new Date().getTime() - _startTime;
            _fields.setElementAt(new Long(talkTime), TALK_TIME);
            _startTime = 0;
        }

        /**
         * Causes this record to resume recording "talk time".
         */
        private void resume() {
            start();
        }

        /**
         * Causes this record to stop recording "talk time".
         */
        private void end() {
            putOnHold();
        }
    }

    /**
     * This class is used to display the information inside a phone number
     * record on the screen using fields.
     */
    private static final class PhoneNumberRecordDisplayer {
        // Members ----------------------------------------------
        private final BasicEditField _phoneNumber;
        private final BasicEditField _talkTime;

        /**
         * Constructs a collection of fields to display the phone number
         * record's information.
         * 
         * @param phoneNumberRecord
         *            The phone number record to display
         */
        private PhoneNumberRecordDisplayer(
                final PhoneNumberRecord phoneNumberRecord) {
            final String phoneNumber =
                    (String) phoneNumberRecord
                            .getField(PhoneNumberRecord.PHONE_NUMBER);
            long talkTime =
                    ((Long) phoneNumberRecord
                            .getField(PhoneNumberRecord.TALK_TIME)).longValue();

            _phoneNumber = new BasicEditField("Phone Number: ", phoneNumber);
            _phoneNumber.setEditable(false);

            // Convert milliseconds into hh:mm:ss format.
            final int hours = (int) (talkTime / (1000 * 60 * 60));
            talkTime %= 1000 * 60 * 60;
            final int minutes = (int) (talkTime / (1000 * 60));
            talkTime %= 1000 * 60;
            final int seconds = (int) (talkTime / 1000);
            final StringBuffer timeString = new StringBuffer();

            if (hours > 0) {
                if (hours < 10) {
                    timeString.append(0);
                }

                timeString.append(hours).append(':');
            }

            if (minutes < 10) {
                timeString.append(0);
            }

            timeString.append(minutes).append(':');

            if (seconds < 10) {
                timeString.append(0);
            }

            timeString.append(seconds);
            _talkTime =
                    new BasicEditField("Talk Time: ", timeString.toString());
            _talkTime.setEditable(false);
        }

        /**
         * Retrives a vector containing this displayer's fields.
         * 
         * @return This displayer's fields.
         */
        private Vector getFields() {
            final Vector fields = new Vector();
            fields.addElement(_phoneNumber);
            fields.addElement(_talkTime);

            return fields;
        }
    }

    /**
     * Phone listener object. Listens for the callConnected, callDisconnected,
     * callHeld, and callResumed events and calculates talk time for each unique
     * phone number.
     */
    private static final class ConcretePhoneListener extends
            AbstractPhoneListener {
        // Members ----------------------------------------------

        // Helper object for searching the list of records.
        private final PhoneNumberRecord _searchRecord = new PhoneNumberRecord(
                "");

        // Maps call IDs to their phone numbers.
        private final Hashtable _phoneNumberTable = new Hashtable();

        /**
         * Default constructor
         */
        ConcretePhoneListener() {
            // Not implemented
        }

        /**
         * Called when a phone call is connected. Finds the record with the
         * call's phone number (or creates a new one if one doesn't exist),
         * starts the "talk time" timer, and saves the record list.
         * 
         * @param callId
         *            The ID of the call that connected.
         */
        public void callConnected(final int callId) {
            final PhoneCall phoneCall = Phone.getCall(callId);
            final String phoneNumber = phoneCall.getDisplayPhoneNumber();
            _phoneNumberTable.put(new Integer(callId), phoneNumber);
            PhoneNumberRecord record =
                    getPhoneNumberRecordByPhoneNumber(phoneNumber);

            if (record == null) {
                // No record exists yet with this phone number, so create one
                // and put it in the list.
                record = new PhoneNumberRecord(phoneNumber);
                _phoneNumberList.addElement(record);
            }

            if (!record.isActive()) {
                record.start();
                PhoneApiDemo.savePhoneNumberRecords();
            }
        }

        /**
         * Called when a phone call is disconnected. Finds the record with the
         * call's phone number, stops the "talk time" timer, and saves the
         * phoneNumberRecord list.
         * 
         * @param callId
         *            The ID of the call that disconnected.
         */
        public void callDisconnected(final int callId) {
            final PhoneNumberRecord record = getPhoneNumberRecord(callId);

            // If an incoming phone call is ignored by the user rather than
            // answered, then
            // no record exists in _phoneNumberTable that matches callId. Thus,
            // record may
            // be null. If that's the case, do nothing; otherwise, proceed as
            // normal and
            // end the call.
            if (record != null) {
                _phoneNumberTable.remove(new Integer(callId));

                if (record.isActive()) {
                    record.end();
                    PhoneApiDemo.savePhoneNumberRecords();
                }
            }
        }

        /**
         * Called when a phone call is put on hold. Finds the record with the
         * call's phone number, stops the "talk time" timer, and saves the
         * record list.
         * 
         * @param callId
         *            The ID of the call that was put on hold.
         */
        public void callHeld(final int callId) {
            final PhoneNumberRecord record = getPhoneNumberRecord(callId);

            if (record.isActive()) {
                record.putOnHold();
                PhoneApiDemo.savePhoneNumberRecords();
            }
        }

        /**
         * Called when a phone call is resumed (taken off hold). Finds the
         * record with the call's phone number, starts the "talk time" timer,
         * and saves the record list.
         * 
         * @param callId
         *            The ID of the call that was resumed.
         */
        public void callResumed(final int callId) {
            final PhoneNumberRecord record = getPhoneNumberRecord(callId);

            if (!record.isActive()) {
                record.resume();
                PhoneApiDemo.savePhoneNumberRecords();
            }
        }

        /**
         * Retrieves a phone number record by call ID. Returns null if no such
         * record exists.
         * 
         * @param callId
         *            The ID of the phone number record to retrieve.
         * @return The phone number record, or null if no record matches callId.
         */
        private PhoneNumberRecord getPhoneNumberRecord(final int callId) {
            final String phoneNumber =
                    (String) _phoneNumberTable.get(new Integer(callId));
            return getPhoneNumberRecordByPhoneNumber(phoneNumber);
        }

        /**
         * Retrieves a phone number record by phone number. Returns null if no
         * such record exists.
         * 
         * @param phoneNumber
         *            The phone number of the phone number record to retrieve.
         * @return The phone number record, or null if no record matches
         *         phoneNumber.
         */
        private PhoneNumberRecord getPhoneNumberRecordByPhoneNumber(
                final String phoneNumber) {
            _searchRecord.setField(PhoneNumberRecord.PHONE_NUMBER, phoneNumber);
            final int index = _phoneNumberList.indexOf(_searchRecord);

            if (index != -1) {
                return (PhoneNumberRecord) _phoneNumberList.elementAt(index);
            }

            return null;
        }
    }

    /**
     * This class implements a key listener for a screen so a menu is displayed
     * when the user presses ENTER.
     */
    private static final class PhoneApiDemoKeyListener implements KeyListener {
        private final Screen _screen;

        /**
         * Constructor
         * 
         * @param screen
         *            The screen with to display the menu on
         */
        public PhoneApiDemoKeyListener(final Screen screen) {
            _screen = screen;
        }

        // KeyListener methods
        // ---------------------------------------------------------------------
        /**
         * @see net.rim.device.api.system.KeyListener#keyChar(char,int,int)
         */
        public boolean
                keyChar(final char key, final int status, final int time) {
            if (key == Characters.ENTER) {
                return _screen.onMenu(0);
            }

            return false;
        }

        /**
         * @see net.rim.device.api.system.KeyListener#keyDown(int,int)
         */
        public boolean keyDown(final int keycode, final int time) {
            return false;
        }

        /**
         * @see net.rim.device.api.system.KeyListener#keyUp(int,int)
         */
        public boolean keyUp(final int keycode, final int time) {
            return false;
        }

        /**
         * @see net.rim.device.api.system.KeyListener#keyRepeat(int,int)
         */
        public boolean keyRepeat(final int keycode, final int time) {
            return false;
        }

        /**
         * @see net.rim.device.api.system.KeyListener#keyStatus(int,int)
         */
        public boolean keyStatus(final int keycode, final int time) {
            return false;
        }
    }
}
