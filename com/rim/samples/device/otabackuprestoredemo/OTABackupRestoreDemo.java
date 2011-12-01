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

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.synchronization.SyncManager;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.XYRect;
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
 * This application demonstrates how to use the
 * OTABackUpRestoreContactCollection class to back up contacts over the air onto
 * a BES. See the "readme.txt" file in this project for setup details.
 */
public class OTABackupRestoreDemo extends UiApplication {
    private static boolean _startup;
    private static OTABackupRestoreContactCollection _collection;
    private TableView _view;
    private AbstractTableModel _model;

    private AddContactAction _addContactAction;

    /**
     * Adds a contact to the contact list
     */
    private class AddContactAction extends MenuItem {
        /**
         * Creates a new AddContactAction object
         */
        private AddContactAction() {
            super(new StringProvider("Add"), 0x230000, 10);
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * Adds a contact to the contact list
                 * 
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    // Retrieve the contact's information from the user
                    final OTAContactScreen screen = new OTAContactScreen();
                    UiApplication.getUiApplication().pushModalScreen(screen);

                    final OTAContactData contact = screen.getContact();

                    // Add the contact
                    if (contact != null) {
                        // Create a unique id for the contact - required for OTA
                        // sync.
                        contact.setUID(UIDGenerator.getUID());

                        // Add the contact to the collection.
                        _model.addRow(contact);
                    }
                }
            }));
        }
    }

    /**
     * Views a contact from the contact list
     */
    private class ViewContactAction extends MenuItem {
        private final int _index;

        /**
         * Constructs a menu item to view a specific contact from the contact
         * list
         * 
         * @param index
         *            The index of the contact from the contact list to view
         */
        private ViewContactAction(final int index) {
            super(new StringProvider("View"), 0x230020, 5);
            _index = index;
            this.setCommand(new Command(new CommandHandler() {

                /**
                 * Displays the contact information.
                 * 
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    final OTAContactScreen screen =
                            new OTAContactScreen((OTAContactData) _model
                                    .getRow(_index), false);
                    UiApplication.getUiApplication().pushScreen(screen);
                }
            }));
        }
    }

    /**
     * A class to edits a contact
     */
    private class EditContactAction extends MenuItem {
        private final int _index;

        /**
         * Constructs a menu item to edit a specific contact from the contact
         * list
         * 
         * @param index
         *            The index of the contact in the contact list to edit
         */
        private EditContactAction(final int index) {
            super(new StringProvider("Edit"), 0x230030, 6);
            _index = index;
            this.setCommand(new Command(new CommandHandler() {
                /**
                 * Edits the contact
                 * 
                 * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
                 *      Object)
                 */
                public void execute(final ReadOnlyCommandMetadata metadata,
                        final Object context) {
                    final OTAContactScreen screen =
                            new OTAContactScreen((OTAContactData) _model
                                    .getRow(_index), true);
                    UiApplication.getUiApplication().pushModalScreen(screen);

                    // Get the newly updated contact
                    final OTAContactData newContact = screen.getContact();

                    // Update the contact in the collection.
                    _model.removeRowAt(_index);
                    _model.insertRowAt(_index, newContact);
                }
            }));
        }
    }

    /**
     * This is the main screen which displays the contact list and creates the
     * menu to let the user manipulate the contacts.
     */
    private class OTABackupRestoreDemoScreen extends MainScreen {

        /**
         * Create a new OTABackupRestoreDemoScreen object
         */
        public OTABackupRestoreDemoScreen() {
            super(Manager.NO_VERTICAL_SCROLL);
        }

        /**
         * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
         */
        protected void makeMenu(final Menu menu, final int instance) {
            menu.add(_addContactAction);

            menu.addSeparator();

            final int index = _view.getRowNumberWithFocus();

            if (_model.getNumberOfRows() > 0 && index >= 0) {
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
        // Get the collection enabled for ota backup/restore
        _collection = OTABackupRestoreContactCollection.getInstance();

        // Create a new screen for the application
        final OTABackupRestoreDemoScreen screen =
                new OTABackupRestoreDemoScreen();

        _addContactAction = new AddContactAction();

        screen.setTitle("OTA Backup/Restore Contacts");

        // Create an adapter to display the contact collection in table format
        _model = new ContactTableModelAdapter();

        // Create view and controller
        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        // Set the highlight background for the row with focus
        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            /**
             * @see net.rim.device.api.ui.component.table.DataTemplate#getDataFields(int)
             */
            public Field[] getDataFields(final int modelRowIndex) {
                // Format the contact name for display
                final OTAContactData contact =
                        (OTAContactData) _model.getRow(modelRowIndex);
                final String personal =
                        contact.getFirst() + " " + contact.getLast();

                final Field[] fields =
                        { new LabelField(personal, Field.NON_FOCUSABLE) };

                return fields;
            }
        };

        // Create regions for formatting table
        dataTemplate.createRegion(new XYRect(0, 0, 1, 1));
        dataTemplate.setColumnProperties(0, new TemplateColumnProperties(
                Display.getWidth()));
        dataTemplate.setRowProperties(0, new TemplateRowProperties(32));
        _view.setDataTemplate(dataTemplate);
        dataTemplate.useFixedHeight(true);

        screen.add(_view);

        // Push the screen onto the UI stack for rendering
        pushScreen(screen);
    }

    /**
     * Adapter for displaying contact data in table format
     */
    private static class ContactTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _collection.size();
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
            return _collection.getSyncObjectAt(index);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object)
         */
        protected boolean doAddRow(final Object row) {
            return _collection.addSyncObject((SyncObject) row);
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int)
         */
        protected boolean doRemoveRowAt(final int index) {
            return _collection.removeSyncObject((SyncObject) getRow(index));
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doInsertRowAt(int,
         *      Object)
         */
        protected boolean doInsertRowAt(final int index, final Object newRow) {
            return _collection.insertSyncObjectAt(index, (SyncObject) newRow);
        }
    };

    /**
     * Entry point for the application.
     * 
     * @param args
     *            Command line arguments
     */
    public static void main(final String[] args) {
        _startup = false;

        for (int i = 0; i < args.length; ++i) {
            if (args[i].startsWith("init")) {
                _startup = true;
            }
        }

        // Get the collection enabled for ota backup/restore
        _collection = OTABackupRestoreContactCollection.getInstance();

        // Enable app for synchronization
        if (_startup) {
            SyncManager.getInstance().enableSynchronization(_collection);
        }

        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final OTABackupRestoreDemo app = new OTABackupRestoreDemo();
        app.enterEventDispatcher();
    }
}
