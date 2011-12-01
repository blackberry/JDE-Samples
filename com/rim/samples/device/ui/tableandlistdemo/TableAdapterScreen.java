/*
 * TableAdapterScreen.java
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

package com.rim.samples.device.ui.tableandlistdemo;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModelAdapter;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;

/**
 * A screen demonstrating the use of the Table and List API to display data held
 * in a non-table data structure in table format.
 */
public final class TableAdapterScreen extends MainScreen {
    private DeviceTableModelAdapter _tableModel;
    private Vector _devices;

    private static final int NUM_ROWS = 1;
    private static final int ROW_HEIGHT = 50;
    private static final int NUM_COLUMNS = 3;

    /**
     * Creates a new TableAdapterScreen object
     * 
     * @param deviceData
     *            Data read from file to be displayed in table
     */
    public TableAdapterScreen(final DemoStringTokenizer deviceData) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Table Adapter Screen");

        add(new LabelField("BlackBerry Devices", Field.FIELD_HCENTER));
        add(new SeparatorField());

        _devices = new Vector();

        _tableModel = new DeviceTableModelAdapter();

        // Add data to adapter
        while (deviceData.hasMoreTokens()) {
            final String modelNumber = deviceData.nextToken().trim();
            final String modelName = deviceData.nextToken().trim();
            deviceData.nextToken(); // Consume unwanted input
            final Bitmap bitmap =
                    Bitmap.getBitmapResource(modelNumber + ".png");
            deviceData.nextToken();
            deviceData.nextToken();

            final Object[] row = { modelName, modelNumber, bitmap };

            _tableModel.addRow(row);
        }

        // Set up table view and controller
        final TableView tableView = new TableView(_tableModel);
        tableView.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.WHITE, Color.WHITE,
                        Color.BLUEVIOLET, Color.BLUEVIOLET));
        final TableController tableController =
                new TableController(_tableModel, tableView);
        tableController.setFocusPolicy(TableController.ROW_FOCUS);
        tableView.setController(tableController);

        // Specify a simple data template for displaying 3 columns
        final DataTemplate dataTemplate =
                new DataTemplate(tableView, NUM_ROWS, NUM_COLUMNS) {
                    /**
                     * @see DataTemplate#getDataFields(int)
                     */
                    public Field[] getDataFields(final int modelRowIndex) {
                        final Object[] data =
                                (Object[]) _tableModel.getRow(modelRowIndex);
                        final Field[] fields =
                                { new BitmapField((Bitmap) data[0]),
                                        new LabelField(data[1]),
                                        new LabelField(data[2]) };
                        return fields;
                    }
                };

        dataTemplate.useFixedHeight(true);

        // Define regions and row height
        dataTemplate.setRowProperties(0, new TemplateRowProperties(ROW_HEIGHT));
        for (int i = 0; i < NUM_COLUMNS; i++) {
            dataTemplate.createRegion(new XYRect(i, 0, 1, 1));
            dataTemplate.setColumnProperties(i, new TemplateColumnProperties(
                    Display.getWidth() / NUM_COLUMNS));
        }

        // Apply the template to the view
        tableView.setDataTemplate(dataTemplate);

        add(tableView);
    }

    /**
     * A class encapsulating name, model number and <code>Bitmap</code> image
     * for a BlackBerry Device.
     */
    private final static class BlackBerryDevice {
        private final String _name;
        private final String _model;
        private final Bitmap _image;

        /**
         * Creates a new BlackBerryDevice object
         * 
         * @param name
         *            The name of the device
         * @param model
         *            The model number of the device
         * @param image
         *            An image of the device
         */
        BlackBerryDevice(final String name, final String model,
                final Bitmap image) {
            _name = name;
            _model = model;
            _image = image;
        }

        /**
         * Retrieves the device name
         * 
         * @return The name of the device
         */
        public String getName() {
            return _name;
        }

        /**
         * Retrieves the device model
         * 
         * @return The model of the device
         */
        public String getModel() {
            return _model;
        }

        /**
         * Retrieves the device image
         * 
         * @return The image for the device
         */
        public Bitmap getImage() {
            return _image;
        }
    }

    /**
     * Adapter for displaying BlackBerryDevice objects in a table format
     */
    private class DeviceTableModelAdapter extends TableModelAdapter {
        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfRows()
         */
        public int getNumberOfRows() {
            return _devices.size();
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#getNumberOfColumns()
         */
        public int getNumberOfColumns() {
            return NUM_COLUMNS;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doAddRow(Object)
         */
        protected boolean doAddRow(final Object row) {
            final Object[] arrayRow = (Object[]) row;
            _devices.addElement(new BlackBerryDevice((String) arrayRow[0],
                    (String) arrayRow[1], (Bitmap) arrayRow[2]));
            return true;
        }

        /**
         * @see net.rim.device.api.ui.component.table.TableModelAdapter#doGetRow(int)
         */
        protected Object doGetRow(final int index) {
            final BlackBerryDevice device =
                    (BlackBerryDevice) _devices.elementAt(index);
            final Object[] row =
                    { device.getImage(), device.getModel(), device.getName() };
            return row;
        }
    }
}
