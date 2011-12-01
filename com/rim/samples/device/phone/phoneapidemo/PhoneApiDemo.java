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
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.ControlledAccessException;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableModelChangeEvent;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringProvider;

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
    private static PhoneNumberTableModelAdapter _model;

    // Make sure a database exists for this application
    static {
        _persist = PersistentStore.getPersistentObject(0x15835f89fc421f8cL); // com.rim.samples.device.phone.phoneapidemo

        synchronized (_persist) {
            _phoneNumberList = (Vector) _persist.getContents();

            _model = new PhoneNumberTableModelAdapter();

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
     * Commits the phone number records to the persistent store
     */
    private static void savePhoneNumberRecords() {
        synchronized (_persist) {
            _persist.commit();
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
     * Adapter class for displaying phone number information in table format
     */
    private static class PhoneNumberTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _phoneNumberList.size();
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns() {
            return 1;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int
         *      )
         */
        protected Object doGetRow(final int index) {
            return _phoneNumberList.elementAt(index);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int
         *      )
         */
        protected boolean doRemoveRowAt(final int index) {
            return _phoneNumberList.removeElement(doGetRow(index));
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object
         *      )
         */
        protected boolean doAddRow(final Object object) {
            _phoneNumberList.addElement(object);
            return true;
        }

        /**
         * Force the table to refresh the listed elements
         */
        public void refresh() {
            notifyListeners(new TableModelChangeEvent(
                    TableModelChangeEvent.COLUMN_UPDATED, this, -1, 0));
        }
    };

    /**
     * The main screen for the Phone API application. It displays a list of
     * phone numbers that have been contacted, and allows the user to view the
     * record for each one.
     */
    private final class PhoneApiDemoMainScreen extends MainScreen {
        // Members --------------------------------------------------
        private MenuItem _deleteAllItem;
        private TableView _view;

        /**
         * PhoneApiDemoMainScreen constructor. Creates the fields and menu items
         * used on this screen.
         */
        private PhoneApiDemoMainScreen() {
            super(Manager.NO_VERTICAL_SCROLL);

            setTitle("Phone API Demo");

            _view = new TableView(_model);

            final TableController controller =
                    new TableController(_model, _view);
            _view.setController(controller);

            _view.setDataTemplateFocus(BackgroundFactory
                    .createLinearGradientBackground(Color.LIGHTBLUE,
                            Color.LIGHTBLUE, Color.BLUE, Color.BLUE));
            final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
                public Field[] getDataFields(final int modelRowIndex) {
                    final PhoneNumberRecord record =
                            (PhoneNumberRecord) _model.getRow(modelRowIndex);
                    final String text =
                            (String) record
                                    .getField(PhoneNumberRecord.PHONE_NUMBER);

                    final Field[] fields =
                            { new LabelField(text, Field.NON_FOCUSABLE) };

                    return fields;
                }
            };
            dataTemplate.createRegion(new XYRect(0, 0, 1, 1));
            dataTemplate.setColumnProperties(0, new TemplateColumnProperties(
                    Display.getWidth()));
            dataTemplate.setRowProperties(0, new TemplateRowProperties(32));
            _view.setDataTemplate(dataTemplate);
            dataTemplate.useFixedHeight(true);

            add(_view);

            // Menu item that deletes all the phone number records.
            _deleteAllItem =
                    new MenuItem(new StringProvider("Delete All"), 0x230010, 0);
            _deleteAllItem.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                        while (_model.getNumberOfRows() > 1) {
                            _model.removeRowAt(0, false);
                        }
                        if (_model.getNumberOfRows() == 1) {
                            _model.removeRowAt(0);
                        }
                        PhoneApiDemo.savePhoneNumberRecords();
                    }
                }
            }));

            addKeyListener(new PhoneApiDemoKeyListener(this));
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#onExposed()
         */
        protected void onExposed() {
            _model.refresh();
        }

        /**
         * Creates the menu for this screen
         * 
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            super.makeMenu(menu, instance);

            // If there are any items in the list, add menu items to view and
            // delete them.
            if (_model.getNumberOfRows() > 0) {
                final int index = _view.getRowNumberWithFocus();
                final PhoneNumberRecord record =
                        (PhoneNumberRecord) _model.getRow(index);
                menu.add(new View(record));
                menu.addSeparator();
                menu.add(new Delete(record));
                menu.add(_deleteAllItem);
                menu.addSeparator();
            }
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
             * Constructs a menu item to view a record when invoked
             * 
             * @param record
             *            The record to view
             */
            private View(final PhoneNumberRecord record) {
                super(new StringProvider("View"), 0x230020, 100);
                _record = record;
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        final MainScreen screen = new MainScreen();

                        screen.setTitle(new LabelField("View Phone Record"));

                        final PhoneNumberRecordDisplayer displayer =
                                new PhoneNumberRecordDisplayer(_record);
                        final Vector fields = displayer.getFields();
                        final int numFields = fields.size();

                        for (int i = 0; i < numFields; ++i) {
                            screen.add((Field) fields.elementAt(i));
                        }

                        screen.addKeyListener(new PhoneApiDemoKeyListener(
                                screen));

                        PhoneApiDemo.this.pushScreen(screen);
                    }
                }));
            }
        }

        /**
         * This class is a menu item allowing a user to delete a phone number
         * record
         */
        private final class Delete extends MenuItem {

            /**
             * Constructs a menu item to delete a phone record when invoked
             * 
             * @param record
             *            The phone record to delete
             */
            private Delete(final PhoneNumberRecord record) {
                super(new StringProvider("Delete"), 0x230030, 110);
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * Deletes the phone record
                     * 
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                            _model.removeRowAt(_view.getRowNumberWithFocus());
                            PhoneApiDemo.savePhoneNumberRecords();
                        }
                    }
                }));
            }
        }
    }

    /**
     * Persistable phone number record
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
         * Constructs a PhoneNumberRecord from a phone number
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
         * Retrieves one of this record's fields
         * 
         * @param index
         *            The index of the field to retrieve
         * @return The field
         */
        private Object getField(final int index) {
            return _fields.elementAt(index);
        }

        /**
         * Sets one of this record's fields
         * 
         * @param index
         *            The index of the field to set
         * @param o
         *            The object that the field is set to
         */
        private void setField(final int index, final Object o) {
            _fields.setElementAt(o, index);
        }

        /**
         * Determines if this record and the parameter refer to the same phone
         * number record.
         * 
         * @param o
         *            The object to check for equality
         * @return True if this record and o refer to the same phone number
         *         record; false otherwise
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
         * Determines whether this record is currently recording "talk time"
         * 
         * @return True if this record is currently recording "talk time"; false
         *         otherwise
         */
        private boolean isActive() {
            return _startTime != 0;
        }

        /**
         * Causes this record to start recording "talk time"
         */
        public void start() {
            _startTime = new Date().getTime();
        }

        /**
         * Causes this record to temporarily stop recording "talk time"
         */
        private void putOnHold() {
            long talkTime = ((Long) _fields.elementAt(TALK_TIME)).longValue();
            talkTime += new Date().getTime() - _startTime;
            _fields.setElementAt(new Long(talkTime), TALK_TIME);
            _startTime = 0;
        }

        /**
         * Causes this record to resume recording "talk time"
         */
        private void resume() {
            start();
        }

        /**
         * Causes this record to stop recording "talk time"
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

        // Helper object for searching the list of records
        private final PhoneNumberRecord _searchRecord = new PhoneNumberRecord(
                "");

        // Maps call IDs to their phone numbers
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
         *            The ID of the call that connected
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
                _model.addRow(record);
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
         *            The ID of the call that disconnected
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
         *            The ID of the call that was resumed
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
         * record exists
         * 
         * @param callId
         *            The ID of the phone number record to retrieve
         * @return The phone number record, or null if no record matches callId
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
         *            The phone number of the phone number record to retrieve
         * @return The phone number record, or null if no record matches
         *         phoneNumber
         */
        private PhoneNumberRecord getPhoneNumberRecordByPhoneNumber(
                final String phoneNumber) {
            _searchRecord.setField(PhoneNumberRecord.PHONE_NUMBER, phoneNumber);
            final int index = _phoneNumberList.indexOf(_searchRecord);

            if (index != -1) {
                return (PhoneNumberRecord) _model.getRow(index);
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
         * Creates a new PhoneApiDemoKeyListener object
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
