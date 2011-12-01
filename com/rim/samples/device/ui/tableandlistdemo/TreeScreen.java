/*
 * TreeScreen.java
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

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.RegionStyles;
import net.rim.device.api.ui.component.table.SortedTableModel;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModel;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;
import net.rim.device.api.util.StringComparator;

/**
 * A class demonstrating the use of the Table API to display nested content.
 * This screen displays a list of BlackBerry devices grouped by BlackBerry
 * Smartphone Device Software version.
 */
public final class TreeScreen extends MainScreen {
    private SortedTableModel _tableModel;

    private static final int ROW_HEIGHT = 25;

    /*
     * Creates a new TreeScreen object
     * 
     * @param deviceData Data read from file to be displayed in table
     */
    public TreeScreen(final DemoStringTokenizer deviceData) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Tree Screen");

        add(new LabelField("BlackBerry Devices", Field.FIELD_HCENTER));
        add(new SeparatorField());

        // Initialize the model if it has not yet been loaded
        _tableModel =
                new SortedTableModel(StringComparator.getInstance(true), 2);

        // Populate the list
        while (deviceData.hasMoreTokens()) {
            final String modelNumber = deviceData.nextToken().trim();

            final StringBuffer displayName = new StringBuffer(modelNumber);

            final String modelName = deviceData.nextToken().trim();
            if (!modelName.equals(modelNumber)) {
                displayName.append(" (");
                displayName.append(modelName);
                displayName.append(")");
            }

            final String os = deviceData.nextToken().trim();
            final String imageFileName = modelNumber + ".png";
            final Bitmap bitmap = Bitmap.getBitmapResource(imageFileName);
            final String year = deviceData.nextToken().trim();
            final String interfaces = deviceData.nextToken().trim();

            _tableModel.addRow(new Object[] { bitmap, displayName.toString(),
                    os, year, interfaces });
        }

        // Create and apply style
        final RegionStyles style =
                new RegionStyles(BorderFactory.createSimpleBorder(new XYEdges(
                        1, 1, 1, 1), Border.STYLE_SOLID), null, null, null,
                        RegionStyles.ALIGN_LEFT, RegionStyles.ALIGN_TOP);

        // Create the view and controller
        final TableView tableView = new TableView(_tableModel);
        final TableController tableController =
                new TableController(_tableModel, tableView);

        // Set the controller focus policy to highlight rows
        tableController.setFocusPolicy(TableController.ROW_FOCUS);

        // Set the behaviour of the controller when a table item is clicked
        tableController.setCommand(new Command(new CommandHandler() {
            /**
             * @see CommandHandler#execute(ReadOnlyCommandMetadata, Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                Dialog.alert("Command Executed");
            }
        }));

        tableView.setController(tableController);

        // Create a DataTemplate that suppresses the third column
        final DataTemplate dataTemplate = new DataTemplate(tableView, 2, 3) {
            /**
             * @see DataTemplate#getDataFields(int)
             */
            public Field[] getDataFields(final int modelRowIndex) {
                final Object[] data =
                        (Object[]) ((TableModel) getView().getModel())
                                .getRow(modelRowIndex);

                final Field[] fields = new Field[4];
                fields[0] = new BitmapField((Bitmap) data[0]);
                fields[1] =
                        new LabelField(data[1], Field.FOCUSABLE
                                | DrawStyle.HCENTER);
                fields[2] = new LabelField(data[3], Field.FOCUSABLE);
                fields[3] = new LabelField(data[4], Field.FOCUSABLE);

                return fields;
            }
        };

        // Set up regions
        dataTemplate.createRegion(new XYRect(0, 0, 1, 2), style);
        dataTemplate.createRegion(new XYRect(1, 0, 2, 1), style);
        dataTemplate.createRegion(new XYRect(1, 1, 1, 1), style);
        dataTemplate.createRegion(new XYRect(2, 1, 1, 1), style);

        // Specify the size of each column by percentage, and the height of a
        // row
        dataTemplate.setColumnProperties(0, new TemplateColumnProperties(15,
                TemplateColumnProperties.PERCENTAGE_WIDTH));
        dataTemplate.setColumnProperties(1, new TemplateColumnProperties(15,
                TemplateColumnProperties.PERCENTAGE_WIDTH));
        dataTemplate.setColumnProperties(2, new TemplateColumnProperties(70,
                TemplateColumnProperties.PERCENTAGE_WIDTH));
        dataTemplate.setRowProperties(0, new TemplateRowProperties(ROW_HEIGHT));
        dataTemplate.setRowProperties(1, new TemplateRowProperties(ROW_HEIGHT));

        // Apply the template to the view
        tableView.setDataTemplate(dataTemplate);
        dataTemplate.useFixedHeight(true);

        add(tableView);
    }
}
