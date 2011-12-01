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

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

final class SavedLocationsScreen extends MainScreen {
    private final Vector _mapLocations;
    private final EmbeddedMapDemo.EmbeddedMapDemoScreen _mainScreen;
    private final LocationsListField _mapLocationsList;

    SavedLocationsScreen(final Vector mapLocations,
            final EmbeddedMapDemo.EmbeddedMapDemoScreen mainScreen) {
        setTitle("Select a location to display");

        _mapLocations = mapLocations;
        _mainScreen = mainScreen;
        _mapLocationsList = new LocationsListField();

        add(_mapLocationsList);

        _mapLocationsList.reloadList();
    }

    private final MenuItem _displayItem = new MenuItem("Display", 110, 10) {
        public void run() {
            _mainScreen.displayLocation(_mapLocationsList.getSelectedIndex());
            close();
        }
    };

    private final MenuItem _deleteItem = new MenuItem("Delete", 110, 10) {
        public void run() {
            final MapLocation mapLocation =
                    (MapLocation) _mapLocations.elementAt(_mapLocationsList
                            .getSelectedIndex());
            final int result =
                    Dialog.ask(Dialog.DELETE, "Delete "
                            + mapLocation.toString() + "?");
            if (result == Dialog.YES) {
                _mapLocations.removeElementAt(_mapLocationsList
                        .getSelectedIndex());
                _mapLocationsList.reloadList();
                _mainScreen.clearEditFields();
            }
        }
    };

    private final MenuItem _deleteAllItem =
            new MenuItem("Delete All", 110, 10) {
                public void run() {
                    final int result =
                            Dialog.ask(Dialog.DELETE, "Are you sure?");
                    if (result == Dialog.YES) {
                        _mapLocations.removeAllElements();
                        _mainScreen.clearEditFields();
                        close();
                    }
                }
            };

    /**
     * @see net.rim.device.api.ui.Screen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        super.makeMenu(menu, instance);

        if (!_mapLocationsList.isEmpty()) {
            menu.add(_displayItem);
            menu.add(_deleteItem);

            if (_mapLocationsList.getSize() > 1) {
                menu.add(_deleteAllItem);
            }
        }
    }

    /************************************************************************************
     **************************** LocationsListField Class ****************************
     ************************************************************************************/
    private final class LocationsListField extends ListField implements
            ListFieldCallback {
        private LocationsListField() {
            setCallback(this);
        }

        private void reloadList() {
            setSize(_mapLocations.size());
        }

        // ListFieldCallback
        // methods---------------------------------------------------------------
        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField
         *      , Graphics , int , int , int)
         */
        public void drawListRow(final ListField list, final Graphics graphics,
                final int index, final int y, final int w) {
            final MapLocation mapLocation =
                    (MapLocation) _mapLocations.elementAt(index);
            final String text = mapLocation.toString();
            graphics.drawText(text, 0, y, DrawStyle.ELLIPSIS, w);
        }

        public Object get(final ListField list, final int index) {
            return ""; // Not implemented.
        }

        public int
                indexOfList(final ListField list, final String p, final int s) {
            return -1; // Not implemented.
        }

        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
         */
        public int getPreferredWidth(final ListField list) {
            // return Graphics.getScreenWidth();
            return Display.getWidth();
        }

        /**
         * Capture Enter key press to display the currently selected location.
         * 
         * @see net.rim.device.api.ui.Screen#keyChar(char, int, int)
         */
        public boolean
                keyChar(final char key, final int status, final int time) {
            switch (key) {
            case Characters.ENTER:
                requestDisplay();
            }

            return false;
        }

        /**
         * @see net.rim.device.api.ui.Screen#navigationClick(int, int)
         */
        protected boolean navigationClick(final int status, final int time) {
            return requestDisplay();
        }

        private boolean requestDisplay() {
            if (!isEmpty()) {
                _mainScreen.displayLocation(_mapLocationsList
                        .getSelectedIndex());
                close();
                return true;
            }

            return false;
        }
    }
}
