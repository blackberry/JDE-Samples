/*
 * TableComponent.java
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

package com.rim.samples.device.accessibilitydemo.customcomponentsdemo;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.accessibility.AccessibleContext;
import net.rim.device.api.ui.accessibility.AccessibleRole;
import net.rim.device.api.ui.accessibility.AccessibleState;
import net.rim.device.api.ui.accessibility.AccessibleTable;
import net.rim.device.api.ui.accessibility.AccessibleText;
import net.rim.device.api.ui.accessibility.AccessibleValue;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * Sample implementation of a tabular accessible UI component. The table cells
 * consist of instances of the AccessibleLabel inner class arranged in columns
 * and rows. This particular table is equivalent to an HTML type table in that
 * the cells can not be selected individually.
 */
public final class TableComponent extends HorizontalFieldManager implements
        AccessibleContext, AccessibleTable {
    private final int _columnCount;
    private final int _rowCount;
    private final String[] _columnNames;
    private final String[][] _cells;

    /**
     * Constructs a new TableComponent
     * 
     * @param columnCount
     *            The number of columns in the table
     * @param rowCount
     *            The number of rows in the table
     */
    public TableComponent(final int columnCount, final int rowCount) {
        super(Field.USE_ALL_WIDTH);

        _columnCount = columnCount;
        _rowCount = rowCount;

        // Initialize the table
        _columnNames = new String[_columnCount];
        _cells = new String[_rowCount][_columnCount];
        final VerticalFieldManager[] columns =
                new VerticalFieldManager[columnCount];
        for (int i = 0; i < _columnCount; i++) {
            columns[i] = new VerticalFieldManager();
            for (int j = 0; j < _rowCount; j++) {
                final HorizontalFieldManager hfm = new HorizontalFieldManager();

                final String text = "TC" + "(" + i + ", " + j + ")";
                _cells[j][i] = text;
                hfm.add(new LabelField(text));
                hfm.add(new LabelField("   |   "));
                columns[i].add(hfm);
            }

            add(columns[i]);
        }
    }

    /**
     * @see Manager#getAccesssibleContext()
     */
    public AccessibleContext getAccessibleContext() {
        return this;
    }

    // ***************** AccessibleTable implementation ************************

    /**
     * @see AccessibleTable#getAccessibleAt(int, int)
     */
    public AccessibleContext getAccessibleAt(final int r, final int c) {
        // Check whether the cell is selected
        final boolean cellSelected = isAccessibleSelected(r, c);

        // Wrap the cell value into a label
        return new AccessibleLabel(_cells[r][c], cellSelected);
    }

    /**
     * @see AccessibleTable#getAccessibleColumnCount()
     */
    public int getAccessibleColumnCount() {
        // Number of columns in the table
        return _columnCount;
    }

    /**
     * @see AccessibleTable#getAccessibleRowCount()
     */
    public int getAccessibleRowCount() {
        // Return number of rows in the table
        return _rowCount;
    }

    /**
     * @see AccessibleTable#getAccessibleColumnHeader()
     */
    public AccessibleContext[] getAccessibleColumnHeader() {
        // Return an array of column names as collection of labels
        final AccessibleContext[] result =
                new AccessibleContext[_columnNames.length];

        for (int i = 0; i < result.length; i++) {
            result[i] = new AccessibleLabel(_columnNames[i], false);
        }

        return result;
    }

    /**
     * @see AccessibleTable#getAccessibleRowHeader()
     */
    public AccessibleContext[] getAccessibleRowHeader() {
        // There are no labels for table rows, only columns have labels
        return null;
    }

    /**
     * @see AccessibleTable#getSelectedAccessibleColumns()
     */
    public int[] getSelectedAccessibleColumns() {
        return new int[] { -1 };
    }

    /**
     * @see AccessibleTable#getSelectedAccessibleRows()
     */
    public int[] getSelectedAccessibleRows() {
        return new int[] { -1 };
    }

    /**
     * @see AccessibleTable#isAccessibleSelected(int, int)
     */
    public boolean isAccessibleSelected(final int r, final int c) {
        // Table cells are not selectable
        return false;
    }

    /**
     * Implementation of an accessible label, used as table cell or column
     * header label
     */
    private class AccessibleLabel implements AccessibleContext {
        private final String _label;
        private final boolean _selected; // True if the label is selected -
                                         // applicable for table cells only

        // Constructor
        private AccessibleLabel(final String label, final boolean selected) {
            _label = label;
            _selected = selected;
        }

        // ***************** AccessibleContext implementation
        // ********************

        /**
         * @see AccessibleContext#getAccessibleName()
         */
        public String getAccessibleName() {
            // Return label text.
            return _label;
        }

        /**
         * @see AccessibleContext#getAccessibleChildAt(int)
         */
        public AccessibleContext getAccessibleChildAt(final int index) {
            // No children in the label.
            return null;
        }

        /**
         * @see AccessibleContext#getAccessibleChildCount()
         */
        public int getAccessibleChildCount() {
            // No children in the label
            return 0;
        }

        /**
         * @see AccessibleContext#getAccessibleParent()
         */
        public AccessibleContext getAccessibleParent() {
            // Return table component as parent
            return TableComponent.this;
        }

        /**
         * @see AccessibleContext#getAccessibleRole()
         */
        public int getAccessibleRole() {
            // This accessible element should be treated as a label
            return AccessibleRole.LABEL;
        }

        /**
         * @see AccessibleContext#getAccessibleSelectionAt(int)
         */
        public AccessibleContext getAccessibleSelectionAt(final int index) {
            // No children in the label
            return null;
        }

        /**
         * @see AccessibleContext#getAccessibleSelectionCount()
         */
        public int getAccessibleSelectionCount() {
            // No children in the label
            return 0;
        }

        /**
         * @see AccessibleContext#getAccessibleStateSet()
         */
        public int getAccessibleStateSet() {
            // User can select the label
            if (_selected) {
                return AccessibleState.SELECTABLE | AccessibleState.SELECTED;
            } else {
                return AccessibleState.SELECTABLE;
            }
        }

        /**
         * @see AccessibleContext#getAccessibleTable()
         */
        public AccessibleTable getAccessibleTable() {
            // Label doesn't have another table inside
            return null;
        }

        /**
         * @see AccessibleContext#getAccessibleText()
         */
        public AccessibleText getAccessibleText() {
            // No text
            return null;
        }

        /**
         * @see AccessibleContext#getAccessibleValue()
         */
        public AccessibleValue getAccessibleValue() {
            // No numerical value
            return null;
        }

        /**
         * @see AccessibleContext#isAccessibleChildSelected(int)
         */
        public boolean isAccessibleChildSelected(final int index) {
            // No children in the label
            return false;
        }

        /**
         * @see AccessibleContext#isAccessibleStateSet(int)
         */
        public boolean isAccessibleStateSet(final int state) {
            return (getAccessibleStateSet() & state) != 0;
        }
    }

    // ******* AccessibleContext implementation for the TableComponent *******

    /**
     * @see AccessibleContext#getAccessibleTable()
     */
    public AccessibleTable getAccessibleTable() {
        // Return accessible table element
        return this;
    }

    /**
     * @see AccessibleContext#getAccessibleChildAt(int)
     */
    public AccessibleContext getAccessibleChildAt(final int index) {
        // Table component doesn't have any children,
        // AccessibleTable interface should be used for tabular data.
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleChildCount()
     */
    public int getAccessibleChildCount() {
        // Table component exposes its child cells through the
        // AccessibleTable interface.
        return 0;
    }

    /**
     * @see AccessibleContext#getAccessibleName()
     */
    public String getAccessibleName() {
        // Will be provided to screen reader
        return " My Table ";
    }

    /**
     * @see AccessibleContext#getAccessibleParent()
     */
    public AccessibleContext getAccessibleParent() {
        // Return manager where table component was added
        final Manager manager = getManager();
        return manager != null ? manager.getAccessibleContext() : null;
    }

    /**
     * @see AccessibleContext#getAccessibleRole()
     */
    public int getAccessibleRole() {
        // This is a table component, screen reader will get its info
        // through getAccessibleTable() method.
        return AccessibleRole.TABLE;
    }

    /**
     * @see AccessibleContext#getAccessibleSelectionAt(int)
     */
    public AccessibleContext getAccessibleSelectionAt(final int index) {
        // Table component doesn't have any children,
        // AccessibleTable interface should be used for tabular selection data.
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleSelectionCount()
     */
    public int getAccessibleSelectionCount() {
        // Table component doesn't have any children,
        // AccessibleTable interface should be used for tabular selection data.
        return 0;
    }

    /**
     * @see AccessibleContext#getAccessibleStateSet()
     */
    public int getAccessibleStateSet() {
        // Table can be focused
        final boolean focused = isFocus();
        if (focused) {
            return AccessibleState.FOCUSABLE | AccessibleState.FOCUSED;
        } else {
            return AccessibleState.FOCUSABLE;
        }
    }

    /**
     * @see AccessibleContext#isAccessibleStateSet()
     */
    public boolean isAccessibleStateSet(final int state) {
        return (state & getAccessibleStateSet()) != 0;
    }

    /**
     * @see AccessibleContext#getAccessibleText()
     */
    public AccessibleText getAccessibleText() {
        // Table has no text
        return null;
    }

    /**
     * @see AccessibleContext#getAccessibleValue()
     */
    public AccessibleValue getAccessibleValue() {
        // Table has no numerical value
        return null;
    }

    /**
     * @see AccessibleContext#isAccessibleChildSelected(int)
     */
    public boolean isAccessibleChildSelected(final int index) {
        // Table component doesn't have any children,
        // AccessibleTable interface should be used for tabular selection data.
        return false;
    }
}
