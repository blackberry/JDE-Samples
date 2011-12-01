/*
 * PersistentStoreDemoScreen.java
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

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.table.AbstractTableModel;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

/**
 * This screen displays a list of Meetings
 */
public final class PersistentStoreDemoScreen extends MainScreen {
    private PersistentStoreDemo _uiApp;
    private Vector _meetings;
    private AbstractTableModel _model;
    private TableView _view;

    /**
     * Creates a new PersistentStoreDemoScreen object
     * 
     * @param meetings
     *            A vector of persistable Meeting objects
     */
    public PersistentStoreDemoScreen(final Vector meetings) {
        super(Manager.NO_VERTICAL_SCROLL);

        _uiApp = (PersistentStoreDemo) UiApplication.getUiApplication();
        _meetings = meetings;

        // Initialize UI components
        setTitle(new LabelField("Persistent Store Demo", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));

        // Create an adapter to display meetings list in a table
        _model = new MeetingTableModelAdapter();

        // Create the view and controller
        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final String text =
                        ((Meeting) _model.getRow(modelRowIndex))
                                .getField(Meeting.MEETING_NAME);
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

        // Menu item to create a new meeting
        final MenuItem newMeetingItem =
                new MenuItem(new StringProvider("New Meeting"), 0x230010, 0);
        newMeetingItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final Meeting meeting = new Meeting();
                _uiApp.pushScreen(new MeetingScreen(meeting, -1, true));
            }
        }));

        viewItem = new MenuItem(new StringProvider("View"), 0x230020, 1);
        viewItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                displayMeeting(false);
            }
        }));

        editItem = new MenuItem(new StringProvider("Edit"), 0x230030, 2);
        editItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                displayMeeting(true);
            }
        }));

        deleteItem = new MenuItem(new StringProvider("Delete"), 0x230040, 3);
        deleteItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Retrieve the highlighted Meeting object and remove it from
                // the
                // vector, then update the list field to reflect the change.
                final int i = _view.getRowNumberWithFocus();
                final String meetingName =
                        ((Meeting) _uiApp.getMeetings().elementAt(i))
                                .getField(Meeting.MEETING_NAME);
                final int result =
                        Dialog.ask(Dialog.DELETE, "Delete " + meetingName + "?");
                if (result == Dialog.YES) {
                    _model.removeRowAt(i);
                }
            }
        }));

        // Menu item to gain access to the controlled object
        final MenuItem retrieveItem =
                new MenuItem(new StringProvider("Access controlled object"),
                        0x230050, 0);
        retrieveItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                // Attempt to gain access to the controlled object. If
                // the module has been signed with the ACME private key,
                // the attempt will succeed.
                final PersistentObject controlledStore =
                        PersistentStore
                                .getPersistentObject(PersistentStoreDemo.PERSISTENT_STORE_DEMO_CONTROLLED_ID);
                if (controlledStore != null) {
                    try {
                        final Vector vector =
                                (Vector) controlledStore.getContents();
                        if (vector != null) {
                            Dialog.alert("Successfully accessed controlled object");
                        }
                    } catch (final SecurityException se) {
                        UiApplication.getUiApplication().invokeLater(
                                new Runnable() {
                                    public void run() {
                                        Dialog.alert("PersistentObject#getContents() threw "
                                                + se.toString());
                                    }
                                });
                    }
                }
            }
        }));

        addMenuItem(newMeetingItem);
        addMenuItem(retrieveItem);
    }

    /**
     * Returns a reference to the table model
     * 
     * @return The table model
     */
    public AbstractTableModel getModel() {
        return _model;
    }

    /**
     * Pushes a MeetingScreen to display the selected meeting
     * 
     * @param editable
     *            True if the meeting displayed should be editable, false if the
     *            meeting should be read only
     */
    void displayMeeting(final boolean editable) {
        if (_model.getNumberOfRows() != 0) {
            final int index = _view.getRowNumberWithFocus();
            _uiApp.pushScreen(new MeetingScreen((Meeting) _model.getRow(index),
                    index, editable));
        }
    }

    /**
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        if (_model.getNumberOfRows() > 0) {
            menu.add(viewItem);
            menu.add(editItem);
            menu.add(deleteItem);
        }

        super.makeMenu(menu, instance);
    }

    /**
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        // Intercept the ENTER key
        if (key == Characters.ENTER) {
            displayMeeting(false);
            return true;
        }

        // Intercept the ESC key - exit the app on its receipt
        if (key == Characters.ESCAPE) {
            _uiApp.persist();
            close();
            return true;
        }
        return super.keyChar(key, status, time);
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    protected boolean invokeAction(final int action) {
        switch (action) {
        case ACTION_INVOKE: // Trackpad click
            displayMeeting(false);
            return true;
        }
        return super.invokeAction(action);
    }

    // Inner classes------------------------------------------------------------

    /**
     * Adapter to display meeting data in table format
     */
    private class MeetingTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _meetings.size();
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
            return _meetings.elementAt(index);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doInsertRowAt(int,
         *      Object)
         */
        protected boolean doInsertRowAt(final int index, final Object object) {
            if (_meetings.size() == 0) {
                _meetings.addElement(object);
            } else {
                _meetings.insertElementAt(object, index);
            }
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object)
         */
        protected boolean doAddRow(final Object object) {
            _meetings.addElement(object);
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int)
         */
        protected boolean doRemoveRowAt(final int index) {
            _meetings.removeElementAt(index);
            return true;
        }
    };

    /**
     * Menu item to view selected meeting
     */
    private MenuItem viewItem;

    /**
     * Menu item to edit selected meeting
     */
    private MenuItem editItem;

    /**
     * Menu item to delete selected meeting
     */
    private MenuItem deleteItem;
}
