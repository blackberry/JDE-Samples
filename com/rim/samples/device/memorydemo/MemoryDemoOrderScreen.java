/*
 * MemoryDemoOrderScreen.java
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

package com.rim.samples.device.memorydemo;

import java.util.Vector;

import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Screen used for displaying and/or editing an order record.
 */
/* package */final class MemoryDemoOrderScreen extends MainScreen {
    // Members
    // -------------------------------------------------------------------------------------
    private boolean _editable;
    private final OrderRecordController _controller;
    private OrderRecord _updatedOrderRecord;

    private final MenuItem _editItem = new MenuItem("Edit", 0, 100) {
        public void run() {
            /* outer. */makeEditScreen();
        }
    };

    private final MenuItem _saveItem = new MenuItem("Save", 0, 200) {
        public void run() {
            if ( /* outer. */onSave()) {
                UiApplication.getUiApplication().popScreen(
                        MemoryDemoOrderScreen.this);
            }
        }
    };

    /**
     * This constructor makes a view or edit screen for an order record.
     * 
     * @param orderRecord
     *            The order record to view and/or edit.
     * @param editable
     *            Whether or not this screen is editable.
     */
    MemoryDemoOrderScreen(final OrderRecord orderRecord, final boolean editable) {
        super();

        _editable = editable;
        String title;

        if (editable) {
            title = "Edit Order Record";
        } else {
            title = "View Order Record";
        }

        setTitle(new LabelField(title, DrawStyle.ELLIPSIS | Field.USE_ALL_WIDTH));

        _controller = new OrderRecordController(orderRecord, editable);
        final Vector fields = _controller.getFields();
        final int numFields = fields.size();

        for (int i = 0; i < numFields; ++i) {
            add((Field) fields.elementAt(i));
        }
    }

    /**
     * Retrieves an updated copy of this screen's order record.
     * 
     * @return The updated order record, or null if this screen is not an edit
     *         screen.
     */
    OrderRecord getUpdatedOrderRecord() {
        return _updatedOrderRecord;
    }

    /**
     * Intercepts the ENTER key and displays this screen's menu.
     * 
     * @see net.rim.device.api.ui.Screen#keyChar(char,int,int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        if (key == Characters.ENTER) {
            return onMenu(0);
        }

        return super.keyChar(key, status, time);
    }

    /**
     * Saves the order record, and makes it available for retrieval.
     * 
     * @see net.rim.device.api.ui.Screen#onSave()
     */
    protected boolean onSave() {
        _updatedOrderRecord = _controller.getUpdatedOrderRecord();

        return true;
    }

    /**
     * Makes this screen's menu. If the screen is editable, it adds a "Save"
     * option; otherwise, it add an "Edit" option.
     * 
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu, int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        if (_editable) {
            menu.add(_saveItem);
        } else {
            menu.add(_editItem);
        }
    }

    /**
     * Makes this screen editable.
     */
    private void makeEditScreen() {
        setTitle(new LabelField("Edit Order Record", DrawStyle.ELLIPSIS
                | Field.USE_ALL_WIDTH));
        _editable = true;
        _controller.makeEditable();
    }
}
