/*
 * MeetingScreen.java
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

package com.rim.samples.device.persistentstoredemo;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * This screen allows the user to update the meeting information stored in an
 * associated Meeting object.
 */
final class MeetingScreen extends MainScreen implements ListFieldCallback {
    private final EditField _nameField;
    private final EditField _descField;
    private final EditField _dateField;
    private final EditField _timeField;
    private final EditField _notesField;
    private PopupScreen _popUp;
    private EditField _addAttendeeField;
    private final PersistentStoreDemo _uiApp;
    private final ListField _attendeesList;
    private final MeetingScreen _screen;
    private final Meeting _meeting;
    private final int _index;

    // Constructor
    /**
     * @param meeting
     *            The Meeting object associated with this screen.
     * @param index
     *            The position of the meeting in the list. A value of -1
     *            represents a new meeting.
     * @param editable
     *            Indicates whether the screens fields are editable.
     */
    MeetingScreen(final Meeting meeting, final int index, final boolean editable) {
        _meeting = meeting;
        _index = index;

        // We need references to our application and this screen.
        _uiApp = (PersistentStoreDemo) UiApplication.getUiApplication();
        _screen = this;

        // Initialize UI components.
        _nameField =
                new EditField("Meeting Name: ", _meeting
                        .getField(Meeting.MEETING_NAME));
        _descField =
                new EditField("Description: ", _meeting.getField(Meeting.DESC));
        _dateField = new EditField("Date: ", _meeting.getField(Meeting.DATE));
        _timeField = new EditField("Time: ", _meeting.getField(Meeting.TIME));
        _notesField =
                new EditField("Notes: ", _meeting.getField(Meeting.NOTES));
        add(_nameField);
        add(_descField);
        add(_dateField);
        add(_timeField);
        add(_notesField);

        // Customize screen based on our editable state.
        if (editable) {
            setTitle(new LabelField("Edit Screen", DrawStyle.ELLIPSIS
                    | Field.USE_ALL_WIDTH));
            addMenuItem(saveItem);
            addMenuItem(addAttendeeItem);
        } else {
            setTitle(new LabelField("View Screen", DrawStyle.ELLIPSIS
                    | Field.USE_ALL_WIDTH));
            _nameField.setEditable(false);
            _descField.setEditable(false);
            _dateField.setEditable(false);
            _timeField.setEditable(false);
            _notesField.setEditable(false);
        }

        // Initialize the attendees list field.
        _attendeesList = new ListField();
        add(new RichTextField("Attendees:", Field.NON_FOCUSABLE));
        add(_attendeesList);

        // Set callback and update list of attendees.
        _attendeesList.setCallback(this);
        updateList();
    }

    /**
     * Method to refresh our attendees list field.
     */
    private void updateList() {
        _attendeesList.setSize(_meeting.getAttendees().size());
    }

    /**
     * Saves the current field contents in the associated Meeting object.
     * 
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        if (!_nameField.getText().equals("")) {
            _meeting.setField(Meeting.MEETING_NAME, _nameField.getText());
            _meeting.setField(Meeting.DESC, _descField.getText());
            _meeting.setField(Meeting.DATE, _dateField.getText());
            _meeting.setField(Meeting.TIME, _timeField.getText());
            _meeting.setField(Meeting.NOTES, _notesField.getText());
            _uiApp.saveMeeting(_meeting, _index);
            return super.onSave();
        } else {
            Dialog.alert("Meeting name required");
            return false;
        }
    }

    // Inner classes------------------------------------------------------------

    private final MenuItem addAttendeeItem = new MenuItem("Add Attendee",
            11000, 10) {
        /**
         * Display popup screen which allows user to enter the name of an
         * attendee.
         */
        public void run() {
            final VerticalFieldManager vfm = new VerticalFieldManager();
            _popUp = new PopupScreen(vfm);
            _addAttendeeField = new EditField("Enter Name: ", "");
            _popUp.add(_addAttendeeField);
            final HorizontalFieldManager hfm =
                    new HorizontalFieldManager(Field.FIELD_HCENTER);
            hfm.add(new AddButton());
            hfm.add(new CancelButton());
            _popUp.add(hfm);
            _uiApp.pushScreen(_popUp);
        }
    };

    private final MenuItem saveItem = new MenuItem("Save", 11000, 11) {
        /**
         * Saves the meeting and closes this screen.
         */
        public void run() {
            if (onSave()) {
                close();
            }
        }
    };

    /**
     * Represents Add button in Add Attendee pop up. Adds attendee name to the
     * new Meeting object.
     */
    class AddButton extends ButtonField {
        // Constructor
        public AddButton() {
            super("Add", ButtonField.CONSUME_CLICK);
        }

        /**
         * Overrides super.
         * 
         * @see net.rim.device.api.ui.Field#fieldChangeNotify(int)
         */
        protected void fieldChangeNotify(final int context) {
            if ((context & FieldChangeListener.PROGRAMMATIC) == 0) {
                // Add attendee name and refresh list.
                _meeting.addAttendee(_addAttendeeField.getText());
                _screen.updateList();

                // If no other fields have been edited, we need to set the
                // screen's state to dirty so that a save dialog will be
                // displayed when the screen is closed.
                if (!_screen.isDirty()) {
                    _screen.setDirty(true);
                }
                _popUp.close();
            }
        }
    }

    /**
     * Represents Cancel button in Add Attendee pop up. Closes the pop up
     * screen.
     */
    class CancelButton extends ButtonField {
        // Constructor
        public CancelButton() {
            super("Cancel", ButtonField.CONSUME_CLICK);
        }

        /**
         * Overrides super.
         * 
         * @see net.rim.device.api.ui.Field#fieldChangeNotify(int)
         */
        protected void fieldChangeNotify(final int context) {
            if ((context & FieldChangeListener.PROGRAMMATIC) == 0) {
                _popUp.close();
            }
        }
    }

    // ListFieldCallback methods
    // ----------------------------------------------------------------------

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField,Graphics,int,int,int)
     */
    public void drawListRow(final ListField list, final Graphics graphics,
            final int index, final int y, final int w) {
        final Vector attendees = _meeting.getAttendees();
        final String text = (String) attendees.elementAt(index);
        graphics.drawText(text, 0, y, 0, w);
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField ,
     *      int)
     */
    public Object get(final ListField list, final int index) {
        return null; // Not implemented
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#indexOfList(ListField
     *      , String , int)
     */
    public int indexOfList(final ListField list, final String p, final int s) {
        return 0; // Not implemented
    }

    /**
     * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
     */
    public int getPreferredWidth(final ListField list) {
        return Display.getWidth();
    }
}
