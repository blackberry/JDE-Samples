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

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.component.table.SimpleList;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * This screen is displayed when there are multiple matches for a user location
 * search. The user can cancel or choose one of the options to display a
 * location on the map.
 */
public final class LookupMatchesScreen extends MainScreen {
    private final Landmark[] _landmarks;
    private final SimpleList _list;
    private final EmbeddedMapDemo.EmbeddedMapDemoScreen _mainScreen;

    /**
     * Create a new LookupMatchesScreen object
     * 
     * @param landmarks
     *            An array of landmarks representing all matches
     * @param mainScreen
     *            A pointer to the screen where the map is
     */
    public LookupMatchesScreen(final Landmark[] landmarks,
            final EmbeddedMapDemo.EmbeddedMapDemoScreen mainScreen) {
        super(Manager.NO_VERTICAL_SCROLL);

        setTitle("Multiple matches!  Please choose one.");
        _mainScreen = mainScreen;
        _landmarks = landmarks;

        _list = new SimpleList(this);
        for (int i = 0; i < _landmarks.length; i++) {
            _list.add(_landmarks[i].getName());
        }

        // Set command on touch screen/trackpad action
        _list.setCommand(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                displayAction();
            }

        }, null, null);

        // Menu item to display the currently selected location
        final MenuItem displayItem =
                new MenuItem(new StringProvider("Display"), 0x230010, 10);
        displayItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                displayAction();
            }
        }));

        // Closes this screen without changing the map or any fields
        final MenuItem cancel =
                new MenuItem(new StringProvider("Cancel Lookup"), 0x230020, 11);
        cancel.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                close();
            }
        }));

        addMenuItem(displayItem);
        addMenuItem(cancel);
    }

    /**
     * Displays the currently selected location on the map. Will close this
     * screen.
     */
    private void displayAction() {
        _mainScreen.addAndDisplayLocation(_landmarks[_list.getFocusRow()]);
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
}
