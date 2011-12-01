/**
 * LookupMatchesScreen.java
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

import javax.microedition.location.Landmark;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.container.MainScreen;

/**
 * This screen is displayed when there are multiple matches for a user location
 * search. The user can cancel or choose one of the options to display a
 * location on the map.
 */
class LookupMatchesScreen extends MainScreen {

    private final Landmark[] _landmarks;
    private final MatchesListField _listField;
    private final EmbeddedMapDemo.EmbeddedMapDemoScreen _mainScreen;

    /**
     * Constructor
     * 
     * @param landmarks
     *            - An array of landmarks representing all matches
     * @param mainScreen
     *            - A pointer to the screen where the map is
     */
    LookupMatchesScreen(final Landmark[] landmarks,
            final EmbeddedMapDemo.EmbeddedMapDemoScreen mainScreen) {
        setTitle("Multiple matches!  Please choose one.");
        _mainScreen = mainScreen;
        _landmarks = landmarks;
        _listField = new MatchesListField();

        add(_listField);

        _listField.reloadList();

        addMenuItem(_displayItem);
        addMenuItem(_cancel);
    }

    /**
     * Menu item to display the currently selected location. Will close this
     * screen.
     */
    private final MenuItem _displayItem = new MenuItem("Display", 110, 10) {
        public void run() {
            _mainScreen.addAndDisplayLocation(_landmarks[_listField
                    .getSelectedIndex()]);
            close();
        }
    };

    /**
     * Closes this screen without changing the map or any fields.
     */
    private final MenuItem _cancel = new MenuItem("Cancel Lookup", 110, 11) {
        public void run() {
            close();
        }
    };

    /**
     * A list field that displays the different location matches
     */
    private final class MatchesListField extends ListField implements
            ListFieldCallback {

        /**
         * Constructor
         */
        private MatchesListField() {
            setCallback(this);
        }

        /**
         * Set the number of items in the field and then select the first one
         */
        private void reloadList() {
            setSize(_landmarks.length);
        }

        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#drawListRow(ListField
         *      , Graphics , int , int , int)
         */
        public void drawListRow(final ListField list, final Graphics graphics,
                final int index, final int y, final int w) {
            graphics.drawText(_landmarks[index].getName(), 0, y,
                    DrawStyle.ELLIPSIS, w);
        }

        /**
         * Not implemented.
         * 
         * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField,
         *      int)
         */
        public Object get(final ListField list, final int index) {
            return "";
        }

        /**
         * Not implemented.
         * 
         * @see net.rim.device.api.ui.component.ListFieldCallback#get(ListField,
         *      int)
         */
        public int
                indexOfList(final ListField list, final String p, final int s) {
            return -1;
        }

        /**
         * @see net.rim.device.api.ui.component.ListFieldCallback#getPreferredWidth(ListField)
         */
        public int getPreferredWidth(final ListField list) {
            return Display.getWidth();
        }

        /**
         * Captures Enter key press to display the currently selected location.
         * 
         * @see net.rim.device.api.ui.Screen#keyChar(char, int, int)
         */
        public boolean
                keyChar(final char key, final int status, final int time) {
            switch (key) {
            case Characters.ENTER:
                _mainScreen.addAndDisplayLocation(_landmarks[_listField
                        .getSelectedIndex()]);
                close();
                return true;
            }

            return false;
        }

        /**
         * @see net.rim.device.api.ui.Screen#navigationClick(int, int)
         */
        protected boolean navigationClick(final int status, final int time) {
            _mainScreen.addAndDisplayLocation(_landmarks[_listField
                    .getSelectedIndex()]);
            close();
            return true;
        }
    }
}
