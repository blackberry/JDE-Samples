/*
 * FileExplorerDemoScreen.java
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

package com.rim.samples.device.fileexplorerdemo;

import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import net.rim.blackberry.api.invoke.CameraArguments;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeypadListener;
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
import net.rim.device.api.ui.component.table.TableModel;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.util.StringProvider;

/**
 * Main screen displays list of all directories/files
 */
public final class FileExplorerDemoScreen extends MainScreen {
    private FileExplorerDemo _uiApp;
    private FileExplorerDemoJournalListener _fileListener;
    private String _parentRoot;

    private AbstractTableModel _model;
    private TableView _view;

    /**
     * Creates a new FileExplorerDemoScreen object
     */
    FileExplorerDemoScreen() {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("File Explorer Demo");

        _model = new TableModel();

        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        controller.setCommand(new Command(new CommandHandler() {
            /**
             * @see CommandHandler#execute(ReadOnlyCommandMetadata, Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                selectAction();
            }
        }));

        _view.setController(controller);

        // Set the highlight style for the view
        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));

        // Create a data template that will format the model data as an array of
        // LabelFields
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final FileExplorerDemoFileHolder fileholder =
                        (FileExplorerDemoFileHolder) _model
                                .getRow(modelRowIndex);

                String text;

                if (fileholder.isDirectory()) {
                    text = fileholder.getPath();
                } else {
                    text = fileholder.getFileName();
                }

                final Field[] fields =
                        { new LabelField(text, DrawStyle.ELLIPSIS
                                | Field.NON_FOCUSABLE) };

                return fields;
            }
        };

        // Define the regions of the data template and column/row size
        dataTemplate.createRegion(new XYRect(0, 0, 1, 1));
        dataTemplate.setColumnProperties(0, new TemplateColumnProperties(
                Display.getWidth()));
        dataTemplate.setRowProperties(0, new TemplateRowProperties(24));

        _view.setDataTemplate(dataTemplate);
        dataTemplate.useFixedHeight(true);

        // Add the contact list to the screen
        add(_view);

        // Populate the table
        readRoots(null);

        _uiApp = (FileExplorerDemo) UiApplication.getUiApplication();
        _fileListener = new FileExplorerDemoJournalListener(this);
        _uiApp.addFileSystemJournalListener(_fileListener);
    }

    /**
     * Overrides super. Removes listener before closing the screen
     * 
     * @see net.rim.device.api.ui.Screen#close()
     */
    public void close() {
        _uiApp.removeFileSystemJournalListener(_fileListener);
        super.close();
    }

    /**
     * Deletes the selected file or directory
     */
    private void deleteAction() {
        final int index = _view.getRowNumberWithFocus();
        final FileExplorerDemoFileHolder fileholder =
                (FileExplorerDemoFileHolder) _model.getRow(index);

        if (fileholder != null) {
            final String filename =
                    fileholder.getPath() + fileholder.getFileName();

            if (Dialog.ask(Dialog.D_DELETE) == Dialog.DELETE) {
                FileConnection fc = null;

                try {
                    fc = (FileConnection) Connector.open("file:///" + filename);
                    fc.delete();
                    _model.removeRowAt(index);
                } catch (final Exception ex) {
                    FileExplorerDemo
                            .errorDialog("Unable to delete file or directory: "
                                    + filename);
                } finally {
                    try {
                        if (fc != null) {
                            fc.close();
                            fc = null;
                        }
                    } catch (final Exception ioex) {
                        FileExplorerDemo.errorDialog("deleteAction() threw "
                                + ioex.toString());
                    }
                }
            }
        }
    }

    /**
     * Overrides default. Enter key will take action on directory/file. Escape
     * key will go up one directory or close application if at top level.
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     * 
     */
    public boolean keyChar(final char c, final int status, final int time) {
        if (c == Characters.ESCAPE && goBack()) {
            return true;
        }

        return super.keyChar(c, status, time);
    }

    /**
     * Creates the menu to be used in the application
     * 
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    public void makeMenu(final Menu menu, final int instance) {
        /*
         * Menu item for invoking the camera application. This provides a
         * convenient method of adding a file to the device file system in order
         * to demonstrate the FileSystemJournalListener.
         */
        final MenuItem cameraItem =
                new MenuItem(new StringProvider("Camera"), 0x230010, 0);
        cameraItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                Invoke.invokeApplication(Invoke.APP_TYPE_CAMERA,
                        new CameraArguments());
            }
        }));

        // Menu item for deleting the selected file
        final MenuItem deleteItem =
                new MenuItem(new StringProvider("Delete"), 0x230020, 1);
        deleteItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                deleteAction();
            }
        }));

        // Menu item for displaying information on the selected file
        final MenuItem selectItem =
                new MenuItem(new StringProvider("Select"), 0x230030, 2);
        selectItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                selectAction();
            }
        }));

        // Menu item for going back one directory in the directory hierarchy
        final MenuItem backItem =
                new MenuItem(new StringProvider("Go Back"), 0x230040, 3);
        backItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                goBack();
            }
        }));

        // Only display the menu if no actions are performed
        if (instance == Menu.INSTANCE_DEFAULT && _model.getNumberOfRows() > 0) {
            if (DeviceInfo.hasCamera()) {
                menu.add(cameraItem);
            }

            // Add delete item if selected item is not a directory and is not a
            // file in read only system partition.
            if (_view.getRowNumberWithFocus() < _model.getNumberOfRows()) {
                final FileExplorerDemoFileHolder fileholder =
                        (FileExplorerDemoFileHolder) _model.getRow(_view
                                .getRowNumberWithFocus());
                if (!fileholder.isDirectory()
                        && !fileholder.getPath().startsWith("system/")) {
                    menu.add(deleteItem);
                    menu.add(selectItem);
                }

                if (_parentRoot != null) {
                    menu.add(backItem);
                }
            }
        }

        super.makeMenu(menu, instance);
    }

    /**
     * Overrides default implementation. Performs the select action if the
     * trackwheel was clicked; otherwise, the default action occurs.
     * 
     * @see net.rim.device.api.ui.Screen#navigationClick(int,int)
     */
    public boolean navigationClick(final int status, final int time) {
        if ((status & KeypadListener.STATUS_TRACKWHEEL) != KeypadListener.STATUS_TRACKWHEEL) {
            return selectAction();
        }

        return super.navigationClick(status, time);
    }

    /**
     * Reads the path that was passed in and enumerates through it
     * 
     * @param root
     *            Path to be read.
     */
    private void readRoots(final String root) {
        _parentRoot = root;

        // Clear list contents
        while (_model.getNumberOfRows() > 0) {
            _model.removeRowAt(0);
        }

        FileConnection fc = null;
        Enumeration rootEnum = null;

        if (root != null) {
            // Open the file system and get the list of directories/files
            try {
                fc =
                        (FileConnection) Connector.open("file:///" + root,
                                Connector.READ);
                rootEnum = fc.list();
            } catch (final Exception ioex) {
                FileExplorerDemo.errorDialog(ioex.toString());
            } finally {

                if (fc != null) {
                    // Everything is read, make sure to close the connection
                    try {
                        fc.close();
                        fc = null;
                    } catch (final Exception ioex) {
                        FileExplorerDemo.errorDialog("readRoots() threw "
                                + ioex.toString());
                    }
                }
            }
        }

        // There was no root to read, so now we are reading the system roots
        if (rootEnum == null) {
            rootEnum = FileSystemRegistry.listRoots();
        }

        // Read through the list of directories/files
        while (rootEnum.hasMoreElements()) {
            String file = (String) rootEnum.nextElement();

            if (root != null) {
                file = root + file;
            }

            readSubroots(file);
        }
    }

    /**
     * Reads all the directories and files from the provided path
     * 
     * @param file
     *            Upper directory to be read.
     */
    private void readSubroots(final String file) {
        FileConnection fc = null;

        try {
            fc =
                    (FileConnection) Connector.open("file:///" + file,
                            Connector.READ);

            // Create a file holder from the FileConnection so that the
            // connection is not left open
            final FileExplorerDemoFileHolder fileholder =
                    new FileExplorerDemoFileHolder(file);
            fileholder.setDirectory(fc.isDirectory());
            _model.addRow(fileholder);
        } catch (final Exception ioex) {
            FileExplorerDemo.errorDialog("Connector.open() threw "
                    + ioex.toString());
        } finally {
            if (fc != null) {
                // Everything is read, make sure to close the connection
                try {
                    fc.close();
                    fc = null;
                } catch (final Exception ioex) {
                    FileExplorerDemo.errorDialog("readSubRoots() threw "
                            + ioex.toString());
                }
            }
        }
    }

    /**
     * Displays information on the selected file
     * 
     * @return True.
     */
    private boolean selectAction() {
        if (_model.getNumberOfRows() <= 0) {
            return false;
        }

        final FileExplorerDemoFileHolder fileholder =
                (FileExplorerDemoFileHolder) _model.getRow(_view
                        .getRowNumberWithFocus());

        if (fileholder != null) {
            // If it's a directory then show what's in the directory
            if (fileholder.isDirectory()) {
                readRoots(fileholder.getPath());
            } else {
                // It's a file so display information on it
                _uiApp.pushScreen(new FileExplorerDemoScreenFileInfoPopup(
                        fileholder));
            }
        }

        return true;
    }

    /**
     * Updates the list of files
     */
    public void updateList() {
        synchronized (_uiApp.getAppEventLock()) {
            readRoots(_parentRoot);
        }
        ;
    }

    /**
     * Goes back one directory in the directory hierarchy, if possible
     * 
     * @return True if we went back a directory; false otherwise
     */
    private boolean goBack() {
        if (_parentRoot != null) {
            String backParentRoot =
                    _parentRoot.substring(0, _parentRoot.lastIndexOf('/'));
            backParentRoot =
                    backParentRoot.substring(0,
                            backParentRoot.lastIndexOf('/') + 1);

            if (backParentRoot.length() > 0) {
                readRoots(backParentRoot);
            } else {
                readRoots(null);
            }

            return true;
        }

        return false;
    }
}
