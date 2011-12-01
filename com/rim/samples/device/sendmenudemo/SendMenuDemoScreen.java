/*
 * SendMenuDemoScreen.java
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

package com.rim.samples.device.sendmenudemo;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;

import net.rim.blackberry.api.sendmenu.SendCommand;
import net.rim.blackberry.api.sendmenu.SendCommandContextKeys;
import net.rim.blackberry.api.sendmenu.SendCommandMenu;
import net.rim.blackberry.api.sendmenu.SendCommandRepository;
import net.rim.device.api.system.Clipboard;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * A MainScreen subclass representing the UI and related logic for the
 * SendMenuDemo application.
 */
public class SendMenuDemoScreen extends MainScreen {
    private static final String ROOT = "file:///SDCard/";
    private final Vector _elements = new Vector();

    private FileExplorerTableModelAdapter _model;
    private TableView _view;

    private EditField _editField;

    /**
     * Constructs a new SendMenuDemoScreen object
     */
    public SendMenuDemoScreen() {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Send Menu Demo");

        // Add an EditField which will be used to demonstrate the Send Menu API
        // with a textual context.
        _editField =
                new EditField("",
                        "Select text in this field and invoke the send menu");
        add(_editField);

        add(new SeparatorField());

        // Add label for the TableView which will be used to demonstrate the
        // Send Menu API in the context of a group of files.
        final LabelField rootDirectorylabelField =
                new LabelField("Contents of " + ROOT, Field.FIELD_HCENTER);
        add(rootDirectorylabelField);

        _model = new FileExplorerTableModelAdapter();

        // Initialize the TableView
        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        // Set the highlight style for the view
        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));

        // Create a data template that will format the model data as
        // an array of LabelFields.
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final String text = (String) _model.getRow(modelRowIndex);
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

        // Add the view the screen
        add(_view);

        enumerateDirectory(ROOT);
    }

    /**
     * @see MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        final Field fieldWithFocus = this.getFieldWithFocus();

        if (fieldWithFocus == _editField) {
            // Get selected text from the EditField
            final Clipboard clipboard = Clipboard.getClipboard();
            _editField.selectionCopy(clipboard);
            final Object obj = clipboard.get();

            if (obj instanceof String) {
                final String selectedText = (String) obj;
                addSendMenuForText(menu, selectedText);
            }
        } else if (fieldWithFocus == _view) {
            final String file =
                    (String) _model.getRow(_view.getRowNumberWithFocus());
            if (file != null) {
                addSendMenuForFile(menu, file);
            }
        }
    }

    /**
     * Adds a sub menu containing send commands to the specified menu instance
     * 
     * @param menu
     *            The menu which will have a sub menu added
     * @param text
     *            The textual context that will be used to query the
     *            SendCommandRepository for appropriate commands
     */
    private static void addSendMenuForText(final Menu menu, final String text) {
        final JSONObject context = new JSONObject();
        try {
            context.put(SendCommandContextKeys.TEXT, text);
            context.put(SendCommandContextKeys.SUBJECT, "Selected text");

        } catch (final JSONException e) {
            SendMenuDemo.errorDialog("JSONObject.put() threw " + e.toString());
        }

        // Query the SendCommandRepository for appropriate commands
        final SendCommandRepository repository =
                SendCommandRepository.getInstance();
        final SendCommand[] sendCommands =
                repository.get(SendCommand.TYPE_TEXT, context, true);

        if (sendCommands != null) {
            // Add submenu to the menu
            menu.add(new SendCommandMenu(sendCommands, 0, 0));
        }
    }

    /**
     * Adds a sub menu containing send commands to the specified menu instance
     * 
     * @param menu
     *            The menu which will have a sub menu added
     * @param file
     *            The file context that will be used to query the
     *            SendCommandRepository for appropriate commands
     */
    private static void addSendMenuForFile(final Menu menu, final String file) {
        final JSONObject context = new JSONObject();
        try {
            context.put(SendCommandContextKeys.PATH, file);
        } catch (final JSONException e) {
            SendMenuDemo.errorDialog("JSONObject.put() threw " + e.toString());
        }

        // Query the SendCommandRepository for appropriate commands
        final SendCommandRepository repository =
                SendCommandRepository.getInstance();
        final SendCommand[] sendCommands =
                repository.get(SendCommand.TYPE_PATH, context, true);

        if (sendCommands != null) {
            // Add submenu to the menu
            menu.add(new SendCommandMenu(sendCommands, 0, 0));
        }
    }

    /**
     * Reads the given path and enumerates through it, adding FileHolder objects
     * to the TableView for each file in the specified path.
     * 
     * @param root
     *            The file path to be enumerated
     */
    private void enumerateDirectory(final String root) {
        if (root != null) {
            FileConnection fc = null;
            Enumeration rootEnum = null;

            // Open the file system and get the list of directories/files
            try {
                fc = (FileConnection) Connector.open(root);
                rootEnum = fc.list();
            } catch (final IOException e) {
                SendMenuDemo.errorDialog(e.toString());
                return;
            } finally {
                if (fc != null) {
                    // Everything is read, make sure to close the connection
                    try {
                        fc.close();
                        fc = null;
                    } catch (final IOException e) {
                    }
                }
            }

            if (rootEnum != null) {
                // Read through the list of directories/files
                while (rootEnum.hasMoreElements()) {
                    final String file = root + (String) rootEnum.nextElement();

                    try {
                        fc = (FileConnection) Connector.open(file);
                        if (!fc.isDirectory()) {
                            _model.addRow(file);
                        }
                    } catch (final IOException e) {
                        System.out.println("Connector.open(" + file
                                + ") threw " + e.toString());
                    }
                }

                if (fc != null) {
                    // Everything is read, close the connection.
                    try {
                        fc.close();
                        fc = null;
                    } catch (final Exception ioex) {
                    }
                }
            }
        }
    }

    /**
     * TableModelAdapter subclass for displaying file entries in table format
     */
    private class FileExplorerTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _elements.size();
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
        protected boolean doAddRow(final Object row) {
            _elements.addElement(row);
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int)
         */
        protected boolean doRemoveRowAt(final int index) {
            _elements.removeElementAt(index);
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        protected Object doGetRow(final int index) {
            return _elements.elementAt(index);
        }
    }
}
