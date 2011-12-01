/*
 * TableScreen.java
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

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.XYEdges;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.DataTemplate;
import net.rim.device.api.ui.component.table.RegionStyles;
import net.rim.device.api.ui.component.table.TableController;
import net.rim.device.api.ui.component.table.TableModel;
import net.rim.device.api.ui.component.table.TableView;
import net.rim.device.api.ui.component.table.TemplateColumnProperties;
import net.rim.device.api.ui.component.table.TemplateRowProperties;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.ui.decor.Border;
import net.rim.device.api.ui.decor.BorderFactory;

/**
 * This sample demonstrates the use of the Table and List API to create a table
 * displaying a list of BlackBerry Smartphone devices. Formatting is specified
 * using a DataTemplate. Styles are stored in a RegionStyles object and can be
 * adjusted programmatically.
 */
public final class TableScreen extends MainScreen {
    private final RegionStyles _style;
    private final TableModel _tableModel;
    private final TableView _tableView;
    private final TableController _controller;

    private static final int NUM_ROWS = 4;
    private static final int NUM_COLUMNS = 2;
    private static final int IMAGE_WIDTH = 50;

    /**
     * Creates a new TableScreen object
     * 
     * @param deviceData
     *            Data read from file to be displayed in table
     */
    public TableScreen(final DemoStringTokenizer deviceData) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Table Screen");

        final StyleChangeDialog dialog = new StyleChangeDialog();
        dialog.doModal();
        _style = dialog.getRegionStyle();

        _tableModel = new TableModel();

        // Set up view and controller
        _tableView = new TableView(_tableModel);
        _tableView.setDataTemplateFocus(BackgroundFactory
                .createLinearGradientBackground(Color.WHITE, Color.WHITE,
                        Color.BLUEVIOLET, Color.BLUEVIOLET));
        _controller = new TableController(_tableModel, _tableView);
        _tableView.setController(_controller);

        setStyle();

        add(new LabelField("BlackBerry Devices", Field.FIELD_HCENTER));

        add(new SeparatorField());

        add(_tableView);

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

            // Add data to the TableModel
            _tableModel.addRow(new Object[] { bitmap, displayName.toString(),
                    os, year, interfaces });
        }
    }

    /**
     * A dialog popup used to change the table style
     */
    private static class StyleChangeDialog extends Dialog {
        private static final String[] _borderStyles = { "Dashed", "Dotted",
                "Filled", "Solid", "Transparent" };
        private static ObjectChoiceField _borderStyle;

        private static final String[] _horizontalAlignments = { "Center",
                "Left", "Right" };
        private static final String[] _verticalAlignments = { "Middle",
                "Bottom", "Top" };
        private static ObjectChoiceField _regionHorizontalAlign;
        private static ObjectChoiceField _regionVerticalAlign;

        /**
         * Creates a new StyleChangeDialog object
         */
        public StyleChangeDialog() {
            super(Dialog.D_OK_CANCEL, "Choose Style", Dialog.OK, null,
                    Dialog.GLOBAL_STATUS);

            _borderStyle =
                    new ObjectChoiceField("Border Style: ", _borderStyles, 0);
            _regionHorizontalAlign =
                    new ObjectChoiceField("Horizontal Alignment: ",
                            _horizontalAlignments, 0);
            _regionVerticalAlign =
                    new ObjectChoiceField("Vertical Alignment: ",
                            _verticalAlignments, 0);

            add(new SeparatorField());
            add(_borderStyle);
            add(_regionHorizontalAlign);
            add(_regionVerticalAlign);
        }

        /**
         * Returns a new region style with the settings defined in this dialog
         * 
         * @return The defined region style
         */
        public RegionStyles getRegionStyle() {
            int border = 0;
            int horizontal = 0;
            int vertical = 0;

            // Determine the selected border style
            switch (_borderStyle.getSelectedIndex()) {
            case 0:
                border = Border.STYLE_DASHED;
                break;
            case 1:
                border = Border.STYLE_DOTTED;
                break;
            case 2:
                border = Border.STYLE_FILLED;
                break;
            case 3:
                border = Border.STYLE_SOLID;
                break;
            case 4:
                border = Border.STYLE_TRANSPARENT;
                break;
            }

            // Determine the selected horizontal alignment
            switch (_regionHorizontalAlign.getSelectedIndex()) {
            case 0:
                horizontal = RegionStyles.ALIGN_CENTER;
                break;
            case 1:
                horizontal = RegionStyles.ALIGN_LEFT;
                break;
            case 2:
                horizontal = RegionStyles.ALIGN_RIGHT;
                break;
            }

            // Determine the selected vertical alignment
            switch (_regionVerticalAlign.getSelectedIndex()) {
            case 0:
                vertical = RegionStyles.ALIGN_MIDDLE;
                break;
            case 1:
                vertical = RegionStyles.ALIGN_BOTTOM;
                break;
            case 2:
                vertical = RegionStyles.ALIGN_TOP;
                break;

            }

            return new RegionStyles(BorderFactory.createSimpleBorder(
                    new XYEdges(1, 1, 1, 1), border), null, null, null,
                    horizontal, vertical);
        }
    }

    /**
     * Creates and displays a new TableView with a user-defined style
     */
    public void setStyle() {
        // Specify a data template for each item describing a block with four
        // rows and
        // two columns. Create a region so that the image will be displayed
        // across
        // four rows.
        final DataTemplate dataTemplate =
                new DataTemplate(_tableView, NUM_ROWS, NUM_COLUMNS) {
                    /**
                     * @see DataTemplate#getDataFields(int)
                     */
                    public Field[] getDataFields(final int modelRowIndex) {
                        final Object[] data =
                                (Object[]) _tableModel.getRow(modelRowIndex);
                        final Field[] fields = new Field[data.length];
                        for (int i = 0; i < data.length; i++) {
                            if (data[i] instanceof Bitmap) {
                                fields[i] = new BitmapField((Bitmap) data[i]);
                            } else if (data[i] instanceof String) {
                                fields[i] =
                                        new LabelField(data[i], Field.FOCUSABLE);
                            } else {
                                fields[i] = (Field) data[i];
                            }
                        }

                        return fields;
                    }
                };

        // Set the style and apply it to the data template via the
        // setRowProperties() method
        dataTemplate.createRegion(new XYRect(0, 0, 1, 4), _style);

        dataTemplate.setRowProperties(0, new TemplateRowProperties(Font
                .getDefault().getHeight()
                + (_style.getBorder() == null ? 0 : _style.getBorder().getTop()
                        + _style.getBorder().getBottom())
                + (_style.getMargin() == null ? 0 : _style.getMargin().top
                        + _style.getMargin().bottom)));

        for (int i = 0; i < NUM_ROWS; i++) {
            dataTemplate.createRegion(new XYRect(1, i, 1, 1), _style);
            dataTemplate.setRowProperties(i, new TemplateRowProperties(Font
                    .getDefault().getHeight()
                    + (_style.getBorder() == null ? 0 : _style.getBorder()
                            .getTop()
                            + _style.getBorder().getBottom())
                    + (_style.getMargin() == null ? 0 : _style.getMargin().top
                            + _style.getMargin().bottom)));
        }

        // Calculate and programmatically set the width of the image section of
        // the table
        final int width =
                IMAGE_WIDTH
                        + (_style.getBorder() == null ? 0 : _style.getBorder()
                                .getTop()
                                + _style.getBorder().getBottom())
                        + (_style.getMargin() == null ? 0
                                : _style.getMargin().top
                                        + _style.getMargin().bottom);
        dataTemplate
                .setColumnProperties(0, new TemplateColumnProperties(width));

        // Set the width of the text portion of the table
        dataTemplate.setColumnProperties(1, new TemplateColumnProperties(
                Display.getWidth() - width));

        // Apply the template to the view
        _tableView.setDataTemplate(dataTemplate);
        dataTemplate.useFixedHeight(true);
    }
}
