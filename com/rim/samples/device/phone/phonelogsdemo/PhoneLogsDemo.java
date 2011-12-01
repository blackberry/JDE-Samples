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
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.DateField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The main class for the PhoneLogs API demo app.
 */
public final class PhoneLogsDemo extends UiApplication {
    // Members
    // -------------------------------------------------------------------------------------
    private final PhoneLogs _phoneLogs;
    private final PhoneCallListField _normalCallList;
    private final PhoneCallListField _missedCallList;

    private PhoneLogsDemo() {
        _phoneLogs = PhoneLogs.getInstance();
        _normalCallList = new PhoneCallListField(PhoneLogs.FOLDER_NORMAL_CALLS);
        _missedCallList = new PhoneCallListField(PhoneLogs.FOLDER_MISSED_CALLS);

        /* parent. */pushScreen(new PhoneLogsDemoScreen());
    }

    /**
     * Entry point for the program. Just creates a new instance of the demo app
     * and begins listening for events.
     * 
     * @param args
     *            Command-line arguments (not used).
     */
    public static void main(final String[] args) {
        new PhoneLogsDemo().enterEventDispatcher();
    }

    // Private inner classes
    // -----------------------------------------------------------------------

    /**
     * This class represents a list field for displaying the call logs of a
     * specific folder on the device (either "normal calls" or "missed calls").
     */
    private final class PhoneCallListField extends ListField {
        // Members ----------------------------------------------
        private final long _folder;
        private final MenuItem _addPhoneCallLog;
        private final MenuItem _addConferencePhoneCallLog;
        private final MenuItem _deleteAllItem;

        /**
         * This constructor stores the call log folder that this list field
         * represents, and creates menu item to be included in the context menu.
         * 
         * @param folder
         *            The call log folder that this list field represents.
         */
        public PhoneCallListField(final long folder) {
            super(PhoneLogsDemo.this._phoneLogs.numberOfCalls(folder));

            _folder = folder;

            // Create menu item for adding a new phone call log.
            _addPhoneCallLog = new MenuItem("Add Phone Call", 200000, 110) {
                public void run() {
                    PhoneLogsDemo.this.pushScreen(new AddPhoneCallLogScreen());
                }
            };

            // Create menu item for adding a new conference phone call log.
            _addConferencePhoneCallLog =
                    new MenuItem("Add Conference Call", 200000, 110) {
                        public void run() {
                            PhoneLogsDemo.this
                                    .pushScreen(new AddConferencePhoneCallLogScreen());
                        }
                    };

            // Create menu item for deleting all call logs in a folder.
            _deleteAllItem = new MenuItem("Delete All", 300000, 130) {
                public void run() {
                    if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                        while (PhoneLogsDemo.this._phoneLogs
                                .numberOfCalls(_folder) > 0) {
                            PhoneLogsDemo.this._phoneLogs
                                    .deleteCall(0, _folder);
                        }

                        /* parent. */setSize(0);
                    }
                }
            };
        }

        /**
         * Retrieves the string to be displayed when this list field is empty.
         * 
         * @return The string to be displayed.
         */
        public String getEmptyString() {
            return "* No Phone Calls *";
        }

        /**
         * Retrieves the call log folder that this list field represents.
         * 
         * @return The call folder.
         */
        public long getFolder() {
            return _folder;
        }

        /**
         * Creates the context menu for this list field, which includes items
         * for adding a phone call and a conference call. If any calls exist in
         * this list field, items are also included that allow viewing, editing,
         * and deleting.
         * 
         * @return This list field's context menu.
         */
        public ContextMenu getContextMenu() {
            final ContextMenu contextMenu = super.getContextMenu();
            contextMenu.addItem(_addPhoneCallLog);
            contextMenu.addItem(_addConferencePhoneCallLog);

            if (PhoneLogsDemo.this._phoneLogs.numberOfCalls(_folder) > 0) {
                final int index = getSelectedIndex();
                final CallLog callLog =
                        (CallLog) /* parent. */getCallback().get(this, index);

                final MenuItem viewItem = new MenuItem("View", 300000, 100) {
                    public void run() {
                        PhoneLogsDemo.this.pushScreen(new ViewCallLogScreen(
                                callLog, index, _folder));
                    }
                };
                contextMenu.addItem(viewItem);

                final MenuItem editItem = new MenuItem("Edit", 300000, 110) {
                    public void run() {
                        PhoneLogsDemo.this.pushScreen(new EditCallLogScreen(
                                callLog, index, _folder));
                    }
                };
                contextMenu.addItem(editItem);

                final MenuItem deleteItem =
                        new MenuItem("Delete", 300000, 120) {
                            public void run() {
                                if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                                    delete(index);
                                }
                            }
                        };

                contextMenu.addItem(deleteItem);
                contextMenu.addItem(_deleteAllItem);
            }

            return contextMenu;
        }

        // Adds a phone call to this list field.
        public void add(final CallLog callLog) {
            PhoneLogsDemo.this._phoneLogs.addCall(callLog);
            /* parent. */setSize(PhoneLogsDemo.this._phoneLogs
                    .numberOfCalls(_folder));
        }

        // Deletes a phone call from this list field.
        public void delete(final int index) {
            PhoneLogsDemo.this._phoneLogs.deleteCall(index, _folder);
            /* parent. */setSize(PhoneLogsDemo.this._phoneLogs
                    .numberOfCalls(_folder));
        }
    }

    /**
     * This class represents the main screen for the PhoneLogs API demo app. It
     * displays two lists of phone calls: a "normal call" list and a
     * "missed call" list (the is a folder for each in the PhoneLogs API).
     */
    private final class PhoneLogsDemoScreen extends MainScreen implements
            ListFieldCallback {
        /**
         * This constructor displays the two call log lists.
         */
        public PhoneLogsDemoScreen() {
            super();

            /* parent. */setTitle(new LabelField("PhoneLogs API Demo",
                    DrawStyle.ELLIPSIS | Field.USE_ALL_WIDTH));

            /* parent. */add(new RichTextField("Normal Calls",
                    RichTextField.TEXT_ALIGN_HCENTER | Field.NON_FOCUSABLE));

            PhoneLogsDemo.this._normalCallList.setCallback(this);
            /* parent. */add(PhoneLogsDemo.this._normalCallList);

            /* parent. */add(new RichTextField("Missed Calls",
                    RichTextField.TEXT_ALIGN_HCENTER | Field.NON_FOCUSABLE));

            PhoneLogsDemo.this._missedCallList.setCallback(this);
            /* parent. */add(PhoneLogsDemo.this._missedCallList);

            /* parent. */addKeyListener(new PhoneLogsDemoKeyListener(this));
        }

        // ListFieldCallback methods
        // -------------------------------------------------------------------

        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
         */
        public void drawListRow(final ListField listField,
                final Graphics graphics, final int index, final int y,
                final int width) {
            final PhoneCallListField pcListField =
                    (PhoneCallListField) listField;
            final CallLog callLog =
                    PhoneLogsDemo.this._phoneLogs.callAt(index, pcListField
                            .getFolder());

            if (callLog instanceof PhoneCallLog) {
                final PhoneCallLog phoneCallLog = (PhoneCallLog) callLog;
                graphics.drawText(phoneCallLog.getParticipant().getNumber(), 0,
                        y, 0, width);
            } else {
                graphics.drawText("Conference Call", 0, y, 0, width);
            }
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
            final PhoneCallListField pcListField =
                    (PhoneCallListField) listField;
            return PhoneLogsDemo.this._phoneLogs.callAt(index, pcListField
                    .getFolder());
        }

        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
         *      , String , int)
         */
        public int indexOfList(final ListField listField, final String prefix,
                final int start) {
            return -1; // Not implemented.
        }

        // Private inner classes
        // -----------------------------------------------------------------------

        /**
         * This class is a menu item allowing a user to add a new call log.
         */
        private final class AddPhoneCall extends MenuItem {
            public AddPhoneCall() {
                super("Add Phone Call", 200000, 110);
            }

            /**
             * Creates a screen for adding a phone call.
             */
            public void run() {
                PhoneLogsDemo.this.pushScreen(new AddPhoneCallLogScreen());
            }
        }
    }

    /**
     * Screen for creating a new phone call log and saving it to the phone logs.
     */
    private final class AddPhoneCallLogScreen extends MainScreen {
        // Members
        // ---------------------------------------------------------------------------------
        PhoneCallLogController _controller;

        public AddPhoneCallLogScreen() {
            super();

            /* parent. */setTitle(new LabelField("Add Phone Call Log",
                    DrawStyle.ELLIPSIS | Field.USE_ALL_WIDTH));

            _controller = new PhoneCallLogController();
            final Vector fields = _controller.getFields(CallLogController.ADD);
            final int numFields = fields.size();

            for (int i = 0; i < numFields; ++i) {
                /* parent. */add((Field) fields.elementAt(i));
            }

            final MenuItem saveItem = new MenuItem("Save", 100000, 100) {
                public void run() {
                    if (savePhoneCallLog()) {
                        PhoneLogsDemo.this
                                .popScreen(AddPhoneCallLogScreen.this);
                    }
                }
            };
            /* parent. */addMenuItem(saveItem);

            /* parent. */addKeyListener(new PhoneLogsDemoKeyListener(this));
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
                    PhoneLogsDemo.this._missedCallList.add(phoneCallLog);
                } else {
                    PhoneLogsDemo.this._normalCallList.add(phoneCallLog);
                }

                return true;
            }

            return false;
        }
    }

    /**
     * Screen for creating a new conference phone call log and saving it to the
     * phone logs.
     */
    private final class AddConferencePhoneCallLogScreen extends MainScreen {
        // Members --------------------------------------------------
        private final ConferencePhoneCallLogController _controller;

        // Constants ------------------------------------------------
        private static final int FIRST_PARTICIPANT_INDEX = 4; // Screen index of
                                                              // the first
                                                              // participant
                                                              // field.

        public AddConferencePhoneCallLogScreen() {
            super();

            /* parent. */setTitle(new LabelField(
                    "Add Conference Phone Call Log", DrawStyle.ELLIPSIS
                            | Field.USE_ALL_WIDTH));
            _controller = new ConferencePhoneCallLogController();
            final Vector fields = _controller.getFields(CallLogController.ADD);
            final int numFields = fields.size();

            for (int i = 0; i < numFields; ++i) {
                /* parent. */add((Field) fields.elementAt(i));
            }

            final MenuItem saveItem = new MenuItem("Save", 100000, 100) {
                public void run() {
                    if (saveConferencePhoneCallLog()) {
                        PhoneLogsDemo.this
                                .popScreen(AddConferencePhoneCallLogScreen.this);
                    }
                }
            };
            /* parent. */addMenuItem(saveItem);

            /* parent. */addKeyListener(new PhoneLogsDemoKeyListener(this));
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
         * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
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
                final Field field = /* parent. */getLeafFieldWithFocus();
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
                PhoneLogsDemo.this._normalCallList.add(conferencePhoneCallLog);

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
            public AddParticipant() {
                super("Add Participant", 150000, 110);
            }

            public void run() {
                _controller.addParticipant();
                final Field field =
                        (Field) _controller.getFields(CallLogController.EDIT)
                                .lastElement();
                AddConferencePhoneCallLogScreen.this.add(field);
                AddConferencePhoneCallLogScreen.this.doPaint();
            }
        }

        /**
         * This class is a menu item for deleting a participant from a
         * conference call.
         */
        private final class DeleteParticipant extends MenuItem {
            private final int _index;
            private final Field _field;

            public DeleteParticipant(final int index, final Field field) {
                super("Delete Participant", 150000, 110);
                _index = index;
                _field = field;
            }

            public void run() {
                _controller.removeParticipantAt(_index);
                AddConferencePhoneCallLogScreen.this.delete(_field);
                AddConferencePhoneCallLogScreen.this.doPaint();
            }
        }
    }

    /**
     * A screen for viewing a call log.
     */
    private final class ViewCallLogScreen extends MainScreen {
        // Members
        // ---------------------------------------------------------------------------------
        private CallLogController _controller;
        private final int _index;
        private final long _folder;

        public ViewCallLogScreen(final CallLog callLog, final int index,
                final long folder) {
            super();

            _index = index;
            _folder = folder;

            /* parent. */setTitle(new LabelField("View Call Log",
                    DrawStyle.ELLIPSIS | Field.USE_ALL_WIDTH));

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
                /* parent. */add((Field) fields.elementAt(i));
            }

            final MenuItem editItem = new MenuItem("Edit", 300000, 110) {
                public void run() {
                    PhoneLogsDemo.this.popScreen(ViewCallLogScreen.this);
                    PhoneLogsDemo.this.pushScreen(new EditCallLogScreen(
                            _controller.getCallLog(), _index, _folder));
                }
            };

            /* parent. */addMenuItem(editItem);

            /* parent. */addKeyListener(new PhoneLogsDemoKeyListener(this));
        }
    }

    /**
     * A screen for editing a call log.
     */
    private final class EditCallLogScreen extends MainScreen {
        // Members --------------------------------------------------
        private CallLogController _controller;
        private final int _index;
        private final long _folder;

        // Constants ------------------------------------------------
        private static final int FIRST_PARTICIPANT_INDEX = 4;

        /**
         * This constructor makes a copy of the callLog parameter, which can be
         * edited and used to replace the old callLog. It then creates a
         * controller based on the copied call log, and place all its fields on
         * the screen.
         * 
         * @param callLog
         *            The call log to be copied and edited.
         * @param index
         *            The index of the call log to be edited.
         * @param folder
         *            The folder to which the call log belongs.
         */
        public EditCallLogScreen(final CallLog callLog, final int index,
                final long folder) {
            super();

            _index = index;
            _folder = folder;

            /* parent. */setTitle(new LabelField("Edit Call Log",
                    DrawStyle.ELLIPSIS | Field.USE_ALL_WIDTH));

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
                /* parent. */add((Field) fields.elementAt(i));
            }

            final MenuItem saveItem = new MenuItem("Save", 100000, 100) {
                public void run() {
                    if (saveEdits()) {
                        PhoneLogsDemo.this.popScreen(EditCallLogScreen.this);
                    }
                }
            };

            /* parent. */addMenuItem(saveItem);

            /* parent. */addKeyListener(new PhoneLogsDemoKeyListener(this));
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
         * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            super.makeMenu(menu, instance);

            if (_controller instanceof ConferencePhoneCallLogController) {
                final ConferencePhoneCallLog conferencePhoneCallLog =
                        (ConferencePhoneCallLog) _controller.getCallLog();
                menu.add(new AddParticipant());

                if (conferencePhoneCallLog.numberOfParticipants() > 2) {
                    final Field field = /* parent. */getLeafFieldWithFocus();
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
                PhoneLogsDemo.this._phoneLogs.swapCall(
                        _controller.getCallLog(), _index, _folder);

                return true;
            }

            return false;
        }

        /**
         * Makes a copy of a phone call log.
         * 
         * @param callLog
         *            The phone call log to be copied.
         * @return A copy of the phone call log.
         */
        private PhoneCallLog copyPhoneCallLog(final PhoneCallLog callLog) {
            return new PhoneCallLog(new Date(callLog.getDate().getTime()),
                    callLog.getType(), callLog.getDuration(), callLog
                            .getStatus(), new PhoneCallLogID(callLog
                            .getParticipant().getNumber()), callLog.getNotes());
        }

        /**
         * Makes a copy of a conference phone call log.
         * 
         * @param callLog
         *            The conference phone call log to be copied.
         * @return A copy of the conference phone call log.
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
         * This class is a menu item allowing a user to add a participant to
         * this screen.
         */
        private final class AddParticipant extends MenuItem {
            public AddParticipant() {
                super("Add Participant", 150000, 100);
            }

            public void run() {
                final ConferencePhoneCallLogController controller =
                        (ConferencePhoneCallLogController) _controller;
                controller.addParticipant();
                final Field field =
                        (Field) controller.getFields(CallLogController.EDIT)
                                .lastElement();
                EditCallLogScreen.this.add(field);
                EditCallLogScreen.this.doPaint();
            }
        }

        /**
         * This class is a menu item allowing a user to delete a participant
         * from this screen.
         */
        private final class DeleteParticipant extends MenuItem {
            private final int _index;
            private final Field _field;

            public DeleteParticipant(final int index, final Field field) {
                super("Delete Participant", 150000, 100);

                _index = index;
                _field = field;
            }

            public void run() {
                final ConferencePhoneCallLogController controller =
                        (ConferencePhoneCallLogController) _controller;
                controller.removeParticipantAt(_index);
                EditCallLogScreen.this.delete(_field);
                EditCallLogScreen.this.doPaint();
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
         * CallLogController constructor. Creates the fields used to
         * display/edit a call log's information.
         * 
         * @param callLog
         *            The call log whose information is to be displayed/edited.
         */
        public CallLogController(final CallLog callLog) {
            // Don't want this array to be a static class variable because the
            // locale may change.
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
         * information.
         * 
         * @return The updated call log.
         */
        public CallLog getCallLog() {
            updateLog();

            return _callLog;
        }

        /**
         * Returns a vector of fields for display and/or edit, depending on
         * type.
         * 
         * @param type
         *            The usage type.
         * @return A vector of this controller's fields.
         * @throws IllegalArgumentException
         *             if type is an invalid usage type.
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
         * controller's fields.
         */
        protected void updateLog() {
            _callLog.setNotes(_notes.getText());
            _callLog.getDate().setTime(_date.getDate());
            _callLog.setDuration(Integer.parseInt(_duration.getText()));
            _callLog.setStatus(_status.getSelectedIndex());
        }

        /**
         * Sets the fields in this controller to be either editable or
         * non-editable.
         * 
         * @param editable
         *            Whether or not this controller's fields should be
         *            editable.
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
         * Returns a vector of fields for display and/or edit, depending on
         * type.
         * 
         * @param type
         *            The usage type.
         * @return A vector of this controller's fields.
         * @throws IllegalArgumentException
         *             if type is an invalid usage type.
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
         * controller's fields.
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
         * Sets the fields in this controller to be editable or non-editable.
         * 
         * @param editable
         *            Whether or not this controller's fields should be
         *            editable.
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
         * Adds a new participant field to this controller.
         */
        public void addParticipant() {
            _participants.addElement(new BasicEditField("Participant: ", "",
                    Integer.MAX_VALUE, BasicEditField.FILTER_PHONE));
            final ConferencePhoneCallLog conferencePhoneCallLog =
                    (ConferencePhoneCallLog) _callLog;
            conferencePhoneCallLog.addParticipant(new PhoneCallLogID(""));
        }

        /**
         * Removes a participant field from this controller.
         * 
         * @param index
         *            The index of the participant to be removed.
         */
        public void removeParticipantAt(final int index) {
            _participants.removeElementAt(index);
            final ConferencePhoneCallLog conferencePhoneCallLog =
                    (ConferencePhoneCallLog) _callLog;
            conferencePhoneCallLog.removeParticipantAt(index);
        }
    }

    /**
     * This class controls the information specific to a phone call log.
     */
    private final class PhoneCallLogController extends CallLogController {
        // Members
        // ---------------------------------------------------------------------------------
        private final ObjectChoiceField _type;
        private final BasicEditField _participant;
        private int _oldType;

        /**
         * This constructor creates a new instance of a phone call log, and sets
         * the old call type to -1, meaning there is no "old type". The old type
         * is used for validation when the phone call log is edited.
         */
        public PhoneCallLogController() {
            this(new PhoneCallLog(new Date(), PhoneCallLog.TYPE_RECEIVED_CALL,
                    0, CallLog.STATUS_NORMAL, new PhoneCallLogID(""), ""));
            _oldType = -1;
        }

        public PhoneCallLogController(final PhoneCallLog phoneCallLog) {
            super(phoneCallLog);

            // Don't want this array to be a class variable because the locale
            // may change.
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
         * Returns a vector of fields for display and/or edit, depending on
         * type.
         * 
         * @param type
         *            The usage type.
         * @return A vector of this controller's fields.
         * @throws IllegalArgumentException
         *             if type is an invalid usage type.
         */
        public Vector getFields(final int type) {
            final Vector fields = super.getFields(type);
            fields.addElement(_type);
            fields.addElement(_participant);

            return fields;
        }

        /**
         * Updates the call log with the current information in this
         * controller's fields.
         */
        protected void updateLog() {
            super.updateLog();

            final PhoneCallLog phoneCallLog = (PhoneCallLog) _callLog;
            phoneCallLog.setType(_type.getSelectedIndex());
            phoneCallLog.setParticipant(new PhoneCallLogID(_participant
                    .getText()));
        }

        /**
         * Sets the fields in this controller to be editable or non-editable.
         * 
         * @param editable
         *            Whether or not this controller's fields should be
         *            editable.
         */
        protected void setEditable(final boolean editable) {
            super.setEditable(editable);

            _type.setEditable(editable);
            _participant.setEditable(editable);
        }
    }

    /**
     * This class implements a key listener so a menu is displayed when the user
     * presses ENTER.
     */
    private static final class PhoneLogsDemoKeyListener implements KeyListener {
        private final Screen _screen;

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
