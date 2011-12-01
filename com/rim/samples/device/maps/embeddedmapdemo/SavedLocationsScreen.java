/**
 * SavedLocationsScreen.java
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

package com.rim.samples.device.embeddedmapdemo;

import java.util.Vector;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
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
 * A screen to display the saved locations by name. It allows the user to select
 * a saved location for quick navigation on the map.
 */
public final class SavedLocationsScreen extends MainScreen {
    private Vector _mapLocations;
    private EmbeddedMapDemo.EmbeddedMapDemoScreen _mainScreen;
    private LocationTableModelAdapter _model;
    private TableView _view;

    /**
     * Constructs a new SavedLocationsScreen object
     * 
     * @param mapLocations
     *            The list of saved locations
     * @param mainScreen
     *            The screen containing the map to display the selected location
     *            on
     */
    public SavedLocationsScreen(final Vector mapLocations,
            final EmbeddedMapDemo.EmbeddedMapDemoScreen mainScreen) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Select a location to display");

        _mapLocations = mapLocations;
        _mainScreen = mainScreen;

        _model = new LocationTableModelAdapter();

        _view = new TableView(_model);
        final TableController controller = new TableController(_model, _view);
        controller.setFocusPolicy(TableController.ROW_FOCUS);
        _view.setController(controller);

        // Set command on touch screen/trackpad action
        controller.setCommand(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                displayAction();
            }

        }, null, null);

        // Set the highlight style for the view
        _view.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.LIGHTBLUE,
                        Color.LIGHTBLUE, Color.BLUE, Color.BLUE));

        // Create a data template that will format the model data as an array of
        // LabelFields
        final DataTemplate dataTemplate = new DataTemplate(_view, 1, 1) {
            public Field[] getDataFields(final int modelRowIndex) {
                final MapLocation mapLocation =
                        (MapLocation) _mapLocations.elementAt(modelRowIndex);
                final Field[] fields =
                        { new LabelField(mapLocation.toString(),
                                Field.NON_FOCUSABLE | DrawStyle.ELLIPSIS) };
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

        // Add the file to the screen
        add(_view);

        _displayItem = new MenuItem(new StringProvider("Display"), 0x230010, 0);
        _displayItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                displayAction();
            }
        }));

        _deleteItem = new MenuItem(new StringProvider("Delete"), 0x230020, 1);
        _deleteItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final MapLocation mapLocation =
                        (MapLocation) _model.getRow(_view
                                .getRowNumberWithFocus());
                final int result =
                        Dialog.ask(Dialog.DELETE, "Delete "
                                + mapLocation.toString() + "?");
                if (result == Dialog.YES) {
                    _model.removeRowAt(_view.getRowNumberWithFocus());
                    _mainScreen.clearEditFields();
                }
            }
        }));

        _deleteAllItem =
                new MenuItem(new StringProvider("Delete All"), 0x230030, 10);
        _deleteAllItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                final int result = Dialog.ask(Dialog.DELETE, "Are you sure?");
                if (result == Dialog.YES) {
                    _model.removeAllRows();
                    _mainScreen.clearEditFields();
                    close();
                }
            }
        }));
    }

    /**
     * Displays the currently selected location on the map. Will close this
     * screen.
     */
    private void displayAction() {
        _mainScreen.displayLocation((MapLocation) _model.getRow(_view
                .getRowNumberWithFocus()));
        close();
    }

    /**
     * @see Screen#keyChar(char, int, int)
     */
    public boolean keyChar(final char c, final int status, final int time) {
        switch (c) {
        case Characters.ENTER:
            displayAction();
            return true;
        }

        return super.keyChar(c, status, time);
    }

    /**
     * Displays the selected location
     */
    private MenuItem _displayItem;

    /**
     * Deletes the selected saved location
     */
    private MenuItem _deleteItem;

    /**
     * Deletes all the saved locations
     */
    private MenuItem _deleteAllItem;

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        if (_model.getNumberOfRows() > 0) {
            menu.add(_displayItem);
            menu.add(_deleteItem);

            if (_model.getNumberOfRows() > 1) {
                menu.add(_deleteAllItem);
            }
        }
    }

    /**
     * Adapter to display locations in table format
     */
    private class LocationTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _mapLocations.size();
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns() {
            return 1;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doRemoveRowAt(int)
         */
        protected boolean doRemoveRowAt(final int index) {
            _mapLocations.removeElementAt(index);
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        protected Object doGetRow(final int index) {
            return _mapLocations.elementAt(index);
        }

        /**
         * Remove all rows in the model
         */
        public void removeAllRows() {
            while (getNumberOfRows() > 0) {
                removeRowAt(0);
            }
        }
    }
}
