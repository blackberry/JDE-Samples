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

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.RichTextField;
import net.rim.device.api.ui.component.table.AbstractTableModel;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.PopupScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

/**
 * This screen allows the user to update the meeting information stored in an
 * associated Meeting object.
 */
public final class MeetingScreen extends MainScreen {
    private EditField _nameField;
    private EditField _descField;
    private EditField _dateField;
    private EditField _timeField;
    private EditField _notesField;
    private PopupScreen _popUp;
    private EditField _addAttendeeField;
    private PersistentStoreDemo _uiApp;
    private MeetingScreen _screen;
    private Meeting _meeting;
    private int _index;
    private AbstractTableModel _model;
    private TableView _view;

    /**
     * Creates a new MeetingScreen object
     * 
     * @param meeting
     *            The Meeting object associated with this screen
     * @param index
     *            The position of the meeting in the list. A value of -1
     *            represents a new meeting.
     * @param editable
     *            Indicates whether the screens fields are editable
     */
    public MeetingScreen(final Meeting meeting, final int index,
            final boolean editable) {
        super(Manager.NO_VERTICAL_SCROLL);

        _meeting = meeting;
        _index = index;

        // We need references to our application and this screen
        _uiApp = (PersistentStoreDemo) UiApplication.getUiApplication();
        _screen = this;

        // Initialize UI components
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

        // Menu item to save the displayed meeting
        final MenuItem saveItem =
                new MenuItem(new StringProvider("Save"), 0x230020, 11);
        saveItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                if (onSave()) {
                    close();
                }
            }
        }));

        // Menu item to add an attendee to meeting
        final MenuItem addAttendeeItem =
                new MenuItem(new StringProvider("Add Attendee"), 0x230010, 10);
        addAttendeeItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
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
        }));

        // Customize screen based on our editable state
        if (editable) {
            setTitle("Edit Screen");
            addMenuItem(saveItem);
            addMenuItem(addAttendeeItem);
        } else {
            setTitle(new LabelField("View Screen"));
            _nameField.setEditable(false);
            _descField.setEditable(false);
            _dateField.setEditable(false);
            _timeField.setEditable(false);
            _notesField.setEditable(false);
        }

        _model = new AttendeeTableModelAdapter();

        // Create view and controller
        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final String text = (String) _model.getRow(modelRowIndex);
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

        add(new RichTextField("Attendees:", Field.NON_FOCUSABLE));
        add(_view);

    }

    /**
     * Saves the current field contents in the associated Meeting object
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

    /**
     * Adapter to display meeting data in table format
     */
    private class AttendeeTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _meeting.getAttendees().size();
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns() {
            return 1;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        protected Object doGetRow(final int index) {
            return _meeting.getAttendees().elementAt(index);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object)
         */
        protected boolean doAddRow(final Object row) {
            _meeting.addAttendee((String) row);
            return true;
        }
    };

    /**
     * Represents Add button in Add Attendee pop up. Adds attendee name to the
     * new Meeting object.
     */
    private final class AddButton extends ButtonField {
        /**
         * Creates a new AddButton object
         */
        public AddButton() {
            super("Add", ButtonField.CONSUME_CLICK);
        }

        /**
         * @see net.rim.device.api.ui.Field#fieldChangeNotify(int)
         */
        protected void fieldChangeNotify(final int context) {
            if ((context & FieldChangeListener.PROGRAMMATIC) == 0) {
                // Add attendee name and refresh list
                _model.addRow(_addAttendeeField.getText());

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
    private final class CancelButton extends ButtonField {
        /**
         * Creates a new CancelButton object
         */
        public CancelButton() {
            super("Cancel", ButtonField.CONSUME_CLICK);
        }

        /**
         * @see net.rim.device.api.ui.Field#fieldChangeNotify(int)
         */
        protected void fieldChangeNotify(final int context) {
            if ((context & FieldChangeListener.PROGRAMMATIC) == 0) {
                _popUp.close();
            }
        }
    }

}
