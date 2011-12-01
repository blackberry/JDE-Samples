/*
 * PhoneLogsDemo.java
 * 
 * A simple program that demonstrates the PhoneLogs API.  Directly edits the 
 * phone logs stored on the device.
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

package com.rim.samples.device.phone.phonelogsdemo;

import java.util.Date;
import java.util.Vector;

import net.rim.blackberry.api.phone.phonelogs.CallLog;
import net.rim.blackberry.api.phone.phonelogs.ConferencePhoneCallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLog;
import net.rim.blackberry.api.phone.phonelogs.PhoneCallLogID;
import net.rim.blackberry.api.phone.phonelogs.PhoneLogs;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableModelChangeEvent;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

/**
 * The main class for the PhoneLogs API demo app
 */
public final class PhoneLogsDemo extends UiApplication {
    // Members
    // -------------------------------------------------------------------------------------
    private final PhoneLogs _phoneLogs;
    private final PhoneCallTableModelAdapter _normalCallModel;
    private final PhoneCallTableModelAdapter _missedCallModel;

    // Create a new PhoneLogsDemo object
    public PhoneLogsDemo() {
        _phoneLogs = PhoneLogs.getInstance();
        _normalCallModel =
                new PhoneCallTableModelAdapter(PhoneLogs.FOLDER_NORMAL_CALLS);
        _missedCallModel =
                new PhoneCallTableModelAdapter(PhoneLogs.FOLDER_MISSED_CALLS);

        pushScreen(new PhoneLogsDemoScreen());
    }

    /**
     * Entry point for the program. Just creates a new instance of the demo app
     * and begins listening for events.
     * 
     * @param args
     *            Command-line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        new PhoneLogsDemo().enterEventDispatcher();
    }

    // Private inner classes
    // -----------------------------------------------------------------------

    /**
     * Adapter class for displaying phone logs in table format
     */
    private class PhoneCallTableModelAdapter extends TableModelAdapter {
        private final long _folder;

        /**
         * Create a new PhoneCallTableModelAdapter object
         * 
         * @param folder
         *            The folder of logs to display
         */
        PhoneCallTableModelAdapter(final long folder) {
            _folder = folder;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return PhoneLogsDemo.this._phoneLogs.numberOfCalls(_folder);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns() {
            return 1;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object)
         */
        protected boolean doAddRow(final Object data) {
            PhoneLogsDemo.this._phoneLogs.addCall((CallLog) data);
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        protected Object doGetRow(final int index) {
            return PhoneLogsDemo.this._phoneLogs.callAt(index, _folder);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int)
         */
        protected boolean doRemoveRowAt(final int index) {
            PhoneLogsDemo.this._phoneLogs.deleteCall(index, _folder);
            return true;
        }

        /**
         * Swaps one row in the model with a new row
         * 
         * @param index
         *            Index of the row to replace
         * @param row
         *            The row to insert
         */
        public void swapRow(final int index, final Object row) {
            PhoneLogsDemo.this._phoneLogs.swapCall((CallLog) row, index,
                    _folder);
            notifyListeners(new TableModelChangeEvent(
                    TableModelChangeEvent.ROW_UPDATED, this, index, -1));
        }

        /**
         * Delete all rows in the model
         */
        public void deleteAllRows() {
            while (PhoneLogsDemo.this._phoneLogs.numberOfCalls(_folder) > 0) {
                removeRowAt(0);
            }
        }
    }

    /**
     * This class represents a table for displaying the call logs of a specific
     * folder on the device (either "normal calls" or "missed calls").
     */
    private static final class PhoneCallTable {
        // Members ----------------------------------------------

        private PhoneCallTableModelAdapter _model;
        private TableView _view;

        /**
         * Creates a new PhoneCallTable object
         * 
         * @param model
         *            The model representing the phone logs this table will
         *            display
         */
        public PhoneCallTable(final PhoneCallTableModelAdapter model) {
            _model = model;

            _view = new TableView(_model);

            final TableController controller =
                    new TableController(_model, _view);
            controller.setFocusPolicy(TableController.ROW_FOCUS);
            _view.setController(controller);

            // Create a DataTemplate to format Phone Log data for table rows
            _view.setDataTemplateFocus(BackgroundFactory
                    .createLinearGradientBackground(Color.LIGHTBLUE,
                            Color.LIGHTBLUE, Color.BLUE, Color.BLUE));
            final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
                public Field[] getDataFields(final int modelRowIndex) {
                    final CallLog log = (CallLog) _model.getRow(modelRowIndex);
                    String text;
                    if (log instanceof PhoneCallLog) {
                        final PhoneCallLog phoneCallLog = (PhoneCallLog) log;
                        text = phoneCallLog.getParticipant().getNumber();
                    } else {
                        text = "Conference call";
                    }

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

        }

        /**
         * Retries the field representing the view component of the table
         * 
         * @return The table view
         */
        public Field getView() {
            return _view;
        }

        /**
         * Deletes the currently selected item in the table
         */
        public void deleteSelectedItem() {
            _model.removeRowAt(_view.getRowNumberWithFocus());
        }

        /**
         * Delete all items in the table
         */
        public void deleteAll() {
            _model.deleteAllRows();
        }

        /**
         * Returns the number of rows in the table
         * 
         * @return The number of rows in the table
         */
        public int getNumberOfRows() {
            return _model.getNumberOfRows();
        }

        /**
         * Returns the currently selected CallLog
         * 
         * @return The currently selected CallLog
         */
        public CallLog getSelectedItem() {
            return (CallLog) _model.getRow(_view.getRowNumberWithFocus());
        }

        /**
         * Returns the model for the data displayed in this table
         * 
         * @return The model
         */
        public PhoneCallTableModelAdapter getModel() {
            return _model;
        }

        /**
         * Returns the index of the currently selected item
         * 
         * @return The index of the currently selected item
         */
        public int getSelectedIndex() {
            return _view.getRowNumberWithFocus();
        }
    }

    /**
     * This class represents the main screen for the PhoneLogs API demo app. It
     * displays two lists of phone calls: a "normal call" list and a
     * "missed call" list (the is a folder for each in the PhoneLogs API).
     */
    private final class PhoneLogsDemoScreen extends MainScreen {
        private final PhoneCallTable _normalCallTable;
        private final PhoneCallTable _missedCallTable;

        private final MenuItem _addPhoneCallLog;
        private final MenuItem _addConferencePhoneCallLog;
        private final MenuItem _deleteAllItem;

        /**
         * Creares a new PhoneLogsDemoScreen object
         */
        public PhoneLogsDemoScreen() {
            super(Manager.NO_VERTICAL_SCROLL);

            setTitle("PhoneLogs API Demo");

            add(new RichTextField("Normal Calls",
                    RichTextField.TEXT_ALIGN_HCENTER | Field.NON_FOCUSABLE));

            _normalCallTable = new PhoneCallTable(_normalCallModel);

            add(_normalCallTable.getView());

            add(new RichTextField("Missed Calls",
                    RichTextField.TEXT_ALIGN_HCENTER | Field.NON_FOCUSABLE));

            _missedCallTable = new PhoneCallTable(_missedCallModel);

            add(_missedCallTable.getView());

            addKeyListener(new PhoneLogsDemoKeyListener(this));

            // Create menu item for adding a new phone call log
            _addPhoneCallLog =
                    new MenuItem(new StringProvider("Add Phone Call"),
                            0x230010, 0);
            _addPhoneCallLog.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    PhoneLogsDemo.this.pushScreen(new AddPhoneCallLogScreen());
                }
            }));

            // Create menu item for adding a new conference phone call log
            _addConferencePhoneCallLog =
                    new MenuItem(new StringProvider("Add Conference Call"),
                            0x230020, 1);
            _addConferencePhoneCallLog.setCommand(new Command(
                    new CommandHandler() {
                        /**
                         * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                         *      Object)
                         */
                        public void execute(
                                final ReadOnlyCommandMetadata metadata,
                                final Object context) {
                            PhoneLogsDemo.this
                                    .pushScreen(new AddConferencePhoneCallLogScreen());
                        }
                    }));

            // Create menu item for deleting all call logs in a folder
            _deleteAllItem =
                    new MenuItem(new StringProvider("Delete All"), 0x230030,
                            130);
            _deleteAllItem.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                        _normalCallTable.deleteAll();
                        _missedCallTable.deleteAll();
                    }
                }
            }));
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            menu.add(_addPhoneCallLog);
            menu.add(_addConferencePhoneCallLog);

            // Get the table that currently has focus, if any
            PhoneCallTable table = null;
            if (_normalCallTable.getView().isFocus()) {
                table = _normalCallTable;
            } else if (_missedCallTable.getView().isFocus()) {
                table = _missedCallTable;
            }

            if (table != null) {
                // Create a new reference to the currently seleccted table. Must
                // be final
                // so that it can be referenced from anonymous inner classes.
                final PhoneCallTable tableRef = table;
                if (tableRef.getNumberOfRows() > 0) {
                    final CallLog callLog = table.getSelectedItem();

                    // Create menu item to view the currently selected call
                    final MenuItem viewItem =
                            new MenuItem(new StringProvider("View"), 0x230010,
                                    100);
                    viewItem.setCommand(new Command(new CommandHandler() {
                        /**
                         * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                         *      Object)
                         */
                        public void execute(
                                final ReadOnlyCommandMetadata metadata,
                                final Object context) {
                            PhoneLogsDemo.this
                                    .pushScreen(new ViewCallLogScreen(callLog,
                                            tableRef.getSelectedIndex(),
                                            tableRef.getModel()));
                        }
                    }));
                    menu.add(viewItem);

                    // Create menu item to edit the currently selected call
                    final MenuItem editItem =
                            new MenuItem(new StringProvider("Edit"), 0x230020,
                                    110);
                    editItem.setCommand(new Command(new CommandHandler() {
                        /**
                         * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                         *      Object)
                         */
                        public void execute(
                                final ReadOnlyCommandMetadata metadata,
                                final Object context) {
                            PhoneLogsDemo.this
                                    .pushScreen(new EditCallLogScreen(callLog,
                                            tableRef.getSelectedIndex(),
                                            tableRef.getModel()));
                        }
                    }));
                    menu.add(editItem);

                    // Create menu item to delete the currently selected call
                    final MenuItem deleteItem =
                            new MenuItem(new StringProvider("Delete"),
                                    0x230030, 120);
                    deleteItem.setCommand(new Command(new CommandHandler() {
                        /**
                         * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                         *      Object)
                         */
                        public void execute(
                                final ReadOnlyCommandMetadata metadata,
                                final Object context) {
                            if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                                tableRef.deleteSelectedItem();
                            }
                        }
                    }));

                    menu.add(deleteItem);
                    menu.add(_deleteAllItem);
                }
            }
            super.makeMenu(menu, instance);
        }
    }

    /**
     * Screen for creating a new phone call log
     */
    private final class AddPhoneCallLogScreen extends MainScreen {
        // Members
        // ---------------------------------------------------------------------------------
        PhoneCallLogController _controller;

        // Creates a new AddPhoneCallLogScreen object
        public AddPhoneCallLogScreen() {
            super();

            setTitle(new LabelField("Add Phone Call Log"));

            _controller = new PhoneCallLogController();
            final Vector fields = _controller.getFields(CallLogController.ADD);
            final int numFields = fields.size();

            for (int i = 0; i < numFields; ++i) {
                add((Field) fields.elementAt(i));
            }

            // Create a menu item to save the new call
            final MenuItem saveItem =
                    new MenuItem(new StringProvider("Save"), 0x230010, 100);
            saveItem.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    if (savePhoneCallLog()) {
                        PhoneLogsDemo.this
                                .popScreen(AddPhoneCallLogScreen.this);
                    }
                }
            }));
            addMenuItem(saveItem);

            addKeyListener(new PhoneLogsDemoKeyListener(this));
        }

        /**
         * Saves the screen's information if the user changes anything and tries
         * to exit without saving.
         * 
         * @return True if the screen's information was saved; false otherwise
         * 
         * @see net.rim.device.api.ui.Screen#onSave()
         */
        protected boolean onSave() {
            return savePhoneCallLog();
        }

        /**
         * If the information in the controller is valid, this method saves the
         * phone call log to the proper folder.
         * 
         * @return True if the phone call log is saved; false otherwise.
         */
        private boolean savePhoneCallLog() {
            if (_controller.validate()) {
                final PhoneCallLog phoneCallLog =
                        (PhoneCallLog) _controller.getCallLog();

                if (phoneCallLog.getType() == PhoneCallLog.TYPE_MISSED_CALL_UNOPENED
                        || phoneCallLog.getType() == PhoneCallLog.TYPE_MISSED_CALL_OPENED) {
                    PhoneLogsDemo.this._missedCallModel.addRow(phoneCallLog);
                } else {
                    PhoneLogsDemo.this._normalCallModel.addRow(phoneCallLog);
                }

                return true;
            }

            return false;
        }
    }

    /**
     * Screen for creating a new conference phone call log and saving it to the
     * phone logs
     */
    private final class AddConferencePhoneCallLogScreen extends MainScreen {
        // Members --------------------------------------------------
        private final ConferencePhoneCallLogController _controller;

        // Constants ------------------------------------------------
        private static final int FIRST_PARTICIPANT_INDEX = 4; // Screen index of
                                                              // the first
                                                              // participant
                                                              // field.

        /**
         * Create a new AddConferencePhoneCallLogScreen object
         */
        public AddConferencePhoneCallLogScreen() {
            super();

            setTitle("Add Conference Phone Call Log");
            _controller = new ConferencePhoneCallLogController();
            final Vector fields = _controller.getFields(CallLogController.ADD);
            final int numFields = fields.size();

            for (int i = 0; i < numFields; ++i) {
                add((Field) fields.elementAt(i));
            }

            // Create a menu item to save the new call
            final MenuItem saveItem =
                    new MenuItem(new StringProvider("Save"), 0x230010, 100);
            saveItem.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    if (saveConferencePhoneCallLog()) {
                        PhoneLogsDemo.this
                                .popScreen(AddConferencePhoneCallLogScreen.this);
                    }
                }
            }));
            addMenuItem(saveItem);

            addKeyListener(new PhoneLogsDemoKeyListener(this));
        }

        /**
         * Saves the screen's information if the user changes anything and tries
         * to exit without saving.
         * 
         * @return True if the screen's information was saved; false otherwise.
         * 
         * @see net.rim.device.api.ui.Screen#onSave()
         */
        protected boolean onSave() {
            return saveConferencePhoneCallLog();
        }

        /**
         * Make a menu with options to add/remove participants from the
         * conference phone call log.
         * 
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            super.makeMenu(menu, instance);

            menu.add(new AddParticipant());
            final ConferencePhoneCallLog conferencePhoneCallLog =
                    (ConferencePhoneCallLog) _controller.getCallLog();

            if (conferencePhoneCallLog.numberOfParticipants() > 2) // Can't have
                                                                   // less than
                                                                   // 2
                                                                   // participants
                                                                   // in a
                                                                   // conference
                                                                   // call.
            {
                final Field field = getLeafFieldWithFocus();
                final int index = field.getIndex();

                if (index >= FIRST_PARTICIPANT_INDEX) {
                    menu.add(new DeleteParticipant(index
                            - FIRST_PARTICIPANT_INDEX, field));
                }
            }
        }

        /**
         * If the information in the screen's controller is valid, this method
         * saves the conference phone call log.
         * 
         * @return True if the conference phone call log is valid; false
         *         otherwise.
         */
        private boolean saveConferencePhoneCallLog() {
            if (_controller.validate()) {
                final ConferencePhoneCallLog conferencePhoneCallLog =
                        (ConferencePhoneCallLog) _controller.getCallLog();
                PhoneLogsDemo.this._normalCallModel
                        .addRow(conferencePhoneCallLog);

                return true;
            }

            return false;
        }

        // Private inner classes representing menu items for this screen
        // ----------------

        /**
         * This class is a menu item for adding a participant to a conference
         * phone call log.
         */
        private final class AddParticipant extends MenuItem {
            /**
             * Create a new AddParticipant object
             */
            public AddParticipant() {
                super(new StringProvider("Add Participant"), 0x230020, 110);
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        _controller.addParticipant();
                        final Field field =
                                (Field) _controller.getFields(
                                        CallLogController.EDIT).lastElement();
                        AddConferencePhoneCallLogScreen.this.add(field);
                        AddConferencePhoneCallLogScreen.this.doPaint();
                    }
                }));
            }
        }

        /**
         * This class is a menu item for deleting a participant from a
         * conference call
         */
        private final class DeleteParticipant extends MenuItem {
            private final int _index;
            private final Field _field;

            /**
             * Create a new DeleteParticipant object
             * 
             * @param index
             *            The index of the participant to delete
             * @param field
             *            The field in which the participant is displayed
             */
            public DeleteParticipant(final int index, final Field field) {
                super(new StringProvider("Delete Participant"), 0x230030, 110);
                _index = index;
                _field = field;
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        _controller.removeParticipantAt(_index);
                        AddConferencePhoneCallLogScreen.this.delete(_field);
                        AddConferencePhoneCallLogScreen.this.doPaint();
                    }
                }));
            }
        }
    }

    /**
     * A screen for viewing a call log
     */
    private final class ViewCallLogScreen extends MainScreen {
        // Members
        // ---------------------------------------------------------------------------------
        private CallLogController _controller;
        private final int _index;
        private final PhoneCallTableModelAdapter _model;

        /**
         * Create a new ViewCallLogScreen object
         * 
         * @param callLog
         *            The CallLog to view
         * @param index
         *            The index of the CallLog in the model
         * @param model
         *            The model through which the CallLog is manipulated
         */
        public ViewCallLogScreen(final CallLog callLog, final int index,
                final PhoneCallTableModelAdapter model) {
            super();

            _index = index;
            _model = model;

            setTitle(new LabelField("View Call Log"));

            if (callLog instanceof PhoneCallLog) {
                final PhoneCallLog phoneCallLog = (PhoneCallLog) callLog;
                _controller = new PhoneCallLogController(phoneCallLog);
            } else {
                final ConferencePhoneCallLog conferencePhoneCallLog =
                        (ConferencePhoneCallLog) callLog;
                _controller =
                        new ConferencePhoneCallLogController(
                                conferencePhoneCallLog);
            }

            final Vector fields = _controller.getFields(CallLogController.VIEW);
            final int numFields = fields.size();

            for (int i = 0; i < numFields; ++i) {
                add((Field) fields.elementAt(i));
            }

            // Menu item to edit the displayed CallLog
            final MenuItem editItem =
                    new MenuItem(new StringProvider("Edit"), 0x230010, 110);
            editItem.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    PhoneLogsDemo.this.popScreen(ViewCallLogScreen.this);
                    PhoneLogsDemo.this.pushScreen(new EditCallLogScreen(
                            _controller.getCallLog(), _index, _model));
                }
            }));

            addMenuItem(editItem);

            addKeyListener(new PhoneLogsDemoKeyListener(this));
        }
    }

    /**
     * A screen for editing a call log
     */
    private final class EditCallLogScreen extends MainScreen {
        // Members --------------------------------------------------
        private CallLogController _controller;
        private final int _index;
        private final PhoneCallTableModelAdapter _model;

        // Constants ------------------------------------------------
        private static final int FIRST_PARTICIPANT_INDEX = 4;

        /**
         * Creates a new EditCallLogScreen object
         * 
         * This constructor makes a copy of the callLog parameter, which can be
         * edited and used to replace the old callLog. It then creates a
         * controller based on the copied call log, and place all its fields on
         * the screen.
         * 
         * @param callLog
         *            The call log to be copied and edited
         * @param index
         *            The index of the call log to be edited
         * @param folder
         *            The folder to which the call log belongs
         */
        public EditCallLogScreen(final CallLog callLog, final int index,
                final PhoneCallTableModelAdapter model) {
            super();

            _index = index;
            _model = model;

            setTitle(new LabelField("Edit Call Log"));

            if (callLog instanceof PhoneCallLog) {
                final PhoneCallLog phoneCallLog = (PhoneCallLog) callLog;
                final PhoneCallLog copy = copyPhoneCallLog(phoneCallLog);
                _controller = new PhoneCallLogController(copy);
            } else {
                final ConferencePhoneCallLog conferencePhoneCallLog =
                        (ConferencePhoneCallLog) callLog;
                final ConferencePhoneCallLog copy =
                        copyConferencePhoneCallLog(conferencePhoneCallLog);
                _controller = new ConferencePhoneCallLogController(copy);
            }

            final Vector fields = _controller.getFields(CallLogController.EDIT);
            final int numFields = fields.size();

            for (int i = 0; i < numFields; ++i) {
                add((Field) fields.elementAt(i));
            }

            // Menu itme to save the current phone log
            final MenuItem saveItem =
                    new MenuItem(new StringProvider("Save"), 0x230010, 100);
            saveItem.setCommand(new Command(new CommandHandler() {
                /**
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    if (saveEdits()) {
                        PhoneLogsDemo.this.popScreen(EditCallLogScreen.this);
                    }
                }
            }));

            addMenuItem(saveItem);

            addKeyListener(new PhoneLogsDemoKeyListener(this));
        }

        /**
         * Saves the screen's information if the user changes anything and tries
         * to exit without saving.
         * 
         * @return True if the screen's information was saved; false otherwise.
         * 
         * @see net.rim.device.api.ui.Screen#onSave()
         */
        protected boolean onSave() {
            return saveEdits();
        }

        /**
         * Creates this screen's menu. On top of the normal menu items, it adds
         * menu items to add and delete participants if this screen is editing a
         * conference phone call log.
         * 
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            super.makeMenu(menu, instance);

            if (_controller instanceof ConferencePhoneCallLogController) {
                final ConferencePhoneCallLog conferencePhoneCallLog =
                        (ConferencePhoneCallLog) _controller.getCallLog();
                menu.add(new AddParticipant());

                if (conferencePhoneCallLog.numberOfParticipants() > 2) {
                    final Field field = getLeafFieldWithFocus();
                    final int index = field.getIndex();

                    if (index >= FIRST_PARTICIPANT_INDEX) {
                        menu.add(new DeleteParticipant(index
                                - FIRST_PARTICIPANT_INDEX, field));
                    }
                }
            }
        }

        /**
         * If the information in the screen's controller is valid, this method
         * saves the call log by swapping it with the call log it is replacing.
         * Returns true if the call log is saved; false otherwise.
         * 
         * @return True if the call log is saved; false otherwise.
         */
        private boolean saveEdits() {
            if (_controller.validate()) {
                _model.swapRow(_index, _controller.getCallLog());

                return true;
            }

            return false;
        }

        /**
         * Makes a copy of a phone call log
         * 
         * @param callLog
         *            The phone call log to be copied
         * @return A copy of the phone call log
         */
        private PhoneCallLog copyPhoneCallLog(final PhoneCallLog callLog) {
            return new PhoneCallLog(new Date(callLog.getDate().getTime()),
                    callLog.getType(), callLog.getDuration(), callLog
                            .getStatus(), new PhoneCallLogID(callLog
                            .getParticipant().getNumber()), callLog.getNotes());
        }

        /**
         * Makes a copy of a conference phone call log
         * 
         * @param callLog
         *            The conference phone call log to be copied
         * @return A copy of the conference phone call log
         */
        private ConferencePhoneCallLog copyConferencePhoneCallLog(
                final ConferencePhoneCallLog callLog) {
            final ConferencePhoneCallLog newLog =
                    new ConferencePhoneCallLog(new Date(callLog.getDate()
                            .getTime()), callLog.getDuration(), callLog
                            .getStatus(), new PhoneCallLogID(callLog
                            .getParticipantAt(0).getNumber()),
                            new PhoneCallLogID(callLog.getParticipantAt(1)
                                    .getNumber()), callLog.getNotes());

            final int numParticipants = callLog.numberOfParticipants();

            for (int i = 2; i < numParticipants; ++i) {
                newLog.addParticipant(new PhoneCallLogID(callLog
                        .getParticipantAt(i).getNumber()));
            }

            return newLog;
        }

        // Private inner classes representing this screen's menu items
        // -----------------------------

        /**
         * Menu item allowing a user to add a participant to this screen
         */
        private final class AddParticipant extends MenuItem {
            /**
             * Create a new AddParticipant object
             */
            public AddParticipant() {
                super(new StringProvider("Add Participant"), 0x230020, 100);
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        final ConferencePhoneCallLogController controller =
                                (ConferencePhoneCallLogController) _controller;
                        controller.addParticipant();
                        final Field field =
                                (Field) controller.getFields(
                                        CallLogController.EDIT).lastElement();
                        EditCallLogScreen.this.add(field);
                        EditCallLogScreen.this.doPaint();
                    }
                }));
            }
        }

        /**
         * Menu item allowing a user to delete a participant from this screen
         */
        private final class DeleteParticipant extends MenuItem {
            private final int _index;
            private final Field _field;

            /**
             * Create a new DeleteParticipant object
             * 
             * @param index
             *            The index of the participant to delete
             * @param field
             *            The field in which the participant is displayed
             */
            public DeleteParticipant(final int index, final Field field) {
                super(new StringProvider("Delete Participant"), 0x230030, 100);

                _index = index;
                _field = field;
                this.setCommand(new Command(new CommandHandler() {
                    /**
                     * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                     *      Object)
                     */
                    public void execute(final ReadOnlyCommandMetadata metadata,
                            final Object context) {
                        final ConferencePhoneCallLogController controller =
                                (ConferencePhoneCallLogController) _controller;
                        controller.removeParticipantAt(_index);
                        EditCallLogScreen.this.delete(_field);
                        EditCallLogScreen.this.doPaint();
                    }
                }));
            }
        }
    }

    /**
     * Abstract base class for controlling a call log. Allows a call log's
     * information to be displayed and/or edited on the screen using fields.
     * This class is extended by the PhoneCallLogController and
     * ConferencePhoneCallLogController classes, which provide specific
     * functionality for those two types of phone calls.
     */
    private static abstract class CallLogController {
        // Members
        // ---------------------------------------------------------------------------------
        protected CallLog _callLog;
        private final BasicEditField _notes;
        private final DateField _date;
        private final BasicEditField _duration;
        private final ObjectChoiceField _status;

        // Constants ------------------------------------------------
        public static final int ADD = 0;
        public static final int VIEW = 1;
        public static final int EDIT = 2;

        /**
         * Creates a new CallLogController object. Creates the fields used to
         * display/edit a call log's information.
         * 
         * @param callLog
         *            The call log whose information is to be displayed/edited
         */
        public CallLogController(final CallLog callLog) {
            // This array should not be a static class variable because the
            // locale may change
            final String[] choices =
                    { "Normal", "Busy", "Congestion error",
                            "Path unavailability error", "Number unobtainable",
                            "Authentication failure", "Emergency calls only",
                            "Call hold error", "Outgoing calls barred",
                            "General error", "Maintenance required",
                            "Service not available",
                            "Call failed due to fading",
                            "Call lost due to fading",
                            "Call failed, try again", "FDN mismatch",
                            "Connection denied" };

            _callLog = callLog;
            _notes = new BasicEditField("Notes: ", callLog.getNotes());
            _date =
                    new DateField("Date: ", callLog.getDate().getTime(),
                            DateField.DATE_TIME);
            _duration =
                    new BasicEditField("Duration (seconds): ", Integer
                            .toString(callLog.getDuration()),
                            Integer.MAX_VALUE, BasicEditField.FILTER_INTEGER);
            _status =
                    new ObjectChoiceField("Call Status: ", choices, callLog
                            .getStatus());
        }

        /**
         * Abstract method to be implemented by subclasses. Validates the
         * information contained in the fields.
         * 
         * @return True if information is valid; false otherwise.
         */
        public abstract boolean validate();

        /**
         * Returns a call log updated with the controller's most recent
         * information
         * 
         * @return The updated call log
         */
        public CallLog getCallLog() {
            updateLog();

            return _callLog;
        }

        /**
         * Returns a vector of fields for display and/or edit, depending on type
         * 
         * @param type
         *            The usage type
         * @return A vector of this controller's fields
         * @throws IllegalArgumentException
         *             if type is an invalid usage type
         */
        protected Vector getFields(final int type) {
            switch (type) {
            case ADD:

            case EDIT:
                setEditable(true);
                break;

            case VIEW:
                setEditable(false);
                break;

            default:
                throw new IllegalArgumentException();
            }

            final Vector fields = new Vector();
            fields.addElement(_notes);
            fields.addElement(_date);
            fields.addElement(_duration);
            fields.addElement(_status);

            return fields;
        }

        /**
         * Updates the call log with the current information in this
         * controller's fields
         */
        protected void updateLog() {
            _callLog.setNotes(_notes.getText());
            _callLog.getDate().setTime(_date.getDate());
            _callLog.setDuration(Integer.parseInt(_duration.getText()));
            _callLog.setStatus(_status.getSelectedIndex());
        }

        /**
         * Sets the fields in this controller to be either editable or
         * non-editable
         * 
         * @param editable
         *            Whether or not this controller's fields should be editable
         */
        protected void setEditable(final boolean editable) {
            _notes.setEditable(editable);
            _date.setEditable(editable);
            _duration.setEditable(editable);
            _status.setEditable(editable);
        }
    }

    /**
     * This class controls the information specific to a conference phone call
     * log.
     */
    private final class ConferencePhoneCallLogController extends
            CallLogController {
        // Members --------------------------------------------------
        private final Vector _participants; // Participants in the conference
                                            // call.

        /**
         * Creates a new ConferencePhoneCallLogController object
         */
        public ConferencePhoneCallLogController() {
            this(new ConferencePhoneCallLog(new Date(), 0,
                    CallLog.STATUS_NORMAL, new PhoneCallLogID(""),
                    new PhoneCallLogID(""), ""));
        }

        /**
         * This constructor fills the vector of participants with the
         * participant information contained in the conferencePhoneCallLog
         * parameter.
         * 
         * @param conferencePhoneCallLog
         *            The conference phone call log from which to get the
         *            information.
         */
        public ConferencePhoneCallLogController(
                final ConferencePhoneCallLog conferencePhoneCallLog) {
            super(conferencePhoneCallLog);

            final int numParticipants =
                    conferencePhoneCallLog.numberOfParticipants();
            _participants = new Vector(numParticipants);

            for (int i = 0; i < numParticipants; ++i) {
                _participants.addElement(new BasicEditField("Participant: ",
                        conferencePhoneCallLog.getParticipantAt(i).getNumber(),
                        Integer.MAX_VALUE, BasicEditField.FILTER_PHONE));
            }
        }

        /**
         * Provides validation for the information contained in this conference
         * phone call log's fields.
         * 
         * @return true if all information is valid; false otherwise.
         */
        public boolean validate() {
            final int numParticipants = _participants.size();

            for (int i = 0; i < numParticipants; ++i) {
                final BasicEditField participant =
                        (BasicEditField) _participants.elementAt(i);

                if (participant.getText().length() < 7) {
                    Dialog.alert("Please enter a valid phone number for participant "
                            + (i + 1) + ".");

                    return false;
                }
            }

            return true;
        }

        /**
         * Returns a vector of fields for display and/or edit, depending on type
         * 
         * @param type
         *            The usage type
         * @return A vector of this controller's fields
         * @throws IllegalArgumentException
         *             if type is an invalid usage type
         */
        public Vector getFields(final int type) {
            final Vector fields = super.getFields(type);
            final int numParticipants = _participants.size();

            for (int i = 0; i < numParticipants; ++i) {
                fields.addElement(_participants.elementAt(i));
            }

            return fields;
        }

        /**
         * Updates the call log with the current information in this
         * controller's fields
         */
        protected void updateLog() {
            super.updateLog();

            final ConferencePhoneCallLog conferencePhoneCallLog =
                    (ConferencePhoneCallLog) _callLog;
            final int numParticipants = _participants.size();

            for (int i = 0; i < numParticipants; ++i) {
                final BasicEditField participant =
                        (BasicEditField) _participants.elementAt(i);
                conferencePhoneCallLog.setParticipantAt(i, new PhoneCallLogID(
                        participant.getText()));
            }

            final int oldNumParticipants =
                    conferencePhoneCallLog.numberOfParticipants();

            for (int i = oldNumParticipants - 1; i >= numParticipants; --i) {
                conferencePhoneCallLog.removeParticipantAt(i);
            }
        }

        /**
         * Sets the fields in this controller to be editable or non-editable
         * 
         * @param editable
         *            Whether or not this controller's fields should be editable
         */
        protected void setEditable(final boolean editable) {
            super.setEditable(editable);

            final int numParticipants = _participants.size();

            for (int i = 0; i < numParticipants; ++i) {
                final BasicEditField participant =
                        (BasicEditField) _participants.elementAt(i);
                participant.setEditable(editable);
            }
        }

        /**
         * Adds a new participant field to this controller
         */
        public void addParticipant() {
            _participants.addElement(new BasicEditField("Participant: ", "",
                    Integer.MAX_VALUE, BasicEditField.FILTER_PHONE));
            final ConferencePhoneCallLog conferencePhoneCallLog =
                    (ConferencePhoneCallLog) _callLog;
            conferencePhoneCallLog.addParticipant(new PhoneCallLogID(""));
        }

        /**
         * Removes a participant field from this controller
         * 
         * @param index
         *            The index of the participant to be removed
         */
        public void removeParticipantAt(final int index) {
            _participants.removeElementAt(index);
            final ConferencePhoneCallLog conferencePhoneCallLog =
                    (ConferencePhoneCallLog) _callLog;
            conferencePhoneCallLog.removeParticipantAt(index);
        }
    }

    /**
     * This class controls the information specific to a phone call log
     */
    private final class PhoneCallLogController extends CallLogController {
        // Members
        // ---------------------------------------------------------------------------------
        private final ObjectChoiceField _type;
        private final BasicEditField _participant;
        private int _oldType;

        /**
         * Creates a new PhoneCallLogController object.
         * 
         * This constructor creates a new instance of a phone call log, and sets
         * the old call type to -1, meaning there is no "old type". The old type
         * is used for validation when the phone call log is edited.
         */
        public PhoneCallLogController() {
            this(new PhoneCallLog(new Date(), PhoneCallLog.TYPE_RECEIVED_CALL,
                    0, CallLog.STATUS_NORMAL, new PhoneCallLogID(""), ""));
            _oldType = -1;
        }

        /**
         * Creates a new PhoneCallLogController object
         * 
         * @param phoneCallLog
         *            The phoneCallLog to control
         */
        public PhoneCallLogController(final PhoneCallLog phoneCallLog) {
            super(phoneCallLog);

            // Should not be a class variable because the locale may change
            final String[] choices =
                    { "Received Call", "Placed Call", "Missed Call (Unopened)",
                            "Missed Call (Opened)" };

            _type =
                    new ObjectChoiceField("Call Type: ", choices, phoneCallLog
                            .getType());
            _participant =
                    new BasicEditField("Participant: ", phoneCallLog
                            .getParticipant().getNumber(), Integer.MAX_VALUE,
                            BasicEditField.FILTER_PHONE);
            _oldType = phoneCallLog.getType();
        }

        /**
         * Provides validation for the information contained in this phone call
         * log's fields. Note that the PhoneLogs swapCall() method prevents a
         * normal call from replacing a missed call and vice-versa. Even though
         * this problem could be worked around in a real application, it is left
         * as part of this demo application for illustrative purposes.
         * 
         * @return true if all information is valid; false otherwise.
         * 
         * @see EditCallLogScreen#saveEdits()
         */
        public boolean validate() {
            final int type = _type.getSelectedIndex();

            if (type != -1) {
                if ((type == PhoneCallLog.TYPE_RECEIVED_CALL || type == PhoneCallLog.TYPE_PLACED_CALL)
                        && (_oldType == PhoneCallLog.TYPE_MISSED_CALL_UNOPENED || _oldType == PhoneCallLog.TYPE_MISSED_CALL_OPENED)) {
                    Dialog.alert("Cannot replace a missed phone call with a normal phone call.");

                    return false;
                }
                if ((type == PhoneCallLog.TYPE_MISSED_CALL_UNOPENED || type == PhoneCallLog.TYPE_MISSED_CALL_OPENED)
                        && (_oldType == PhoneCallLog.TYPE_RECEIVED_CALL || _oldType == PhoneCallLog.TYPE_PLACED_CALL)) {
                    Dialog.alert("Cannot replace a normal phone call with a missed phone call.");

                    return false;
                }
            }
            if (_participant.getText().length() < 7) {
                Dialog.alert("Please enter a valid participant phone number.");

                return false;
            }

            return true;
        }

        /**
         * Returns a vector of fields for display and/or edit, depending on type
         * 
         * @param type
         *            The usage type
         * @return A vector of this controller's fields
         * @throws IllegalArgumentException
         *             if type is an invalid usage type
         */
        public Vector getFields(final int type) {
            final Vector fields = super.getFields(type);
            fields.addElement(_type);
            fields.addElement(_participant);

            return fields;
        }

        /**
         * Updates the call log with the current information in this
         * controller's fields
         */
        protected void updateLog() {
            super.updateLog();

            final PhoneCallLog phoneCallLog = (PhoneCallLog) _callLog;
            phoneCallLog.setType(_type.getSelectedIndex());
            phoneCallLog.setParticipant(new PhoneCallLogID(_participant
                    .getText()));
        }

        /**
         * Sets the fields in this controller to be editable or non-editable
         * 
         * @param editable
         *            Whether or not this controller's fields should be editable
         */
        protected void setEditable(final boolean editable) {
            super.setEditable(editable);

            _type.setEditable(editable);
            _participant.setEditable(editable);
        }
    }

    /**
     * This class implements a key listener so a menu is displayed when the user
     * presses ENTER
     */
    private static final class PhoneLogsDemoKeyListener implements KeyListener {
        private final Screen _screen;

        /**
         * Creates a new PhoneLogsDemoKeyListener object
         * 
         * @param screen
         *            The screen in which to display the menu on
         */
        public PhoneLogsDemoKeyListener(final Screen screen) {
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
