/**
 * HomeScreenDemoSecondaryScreen.java
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

package com.rim.samples.device.homescreendemo;

import java.util.Vector;

import net.rim.blackberry.api.homescreen.HomeScreen;
import net.rim.blackberry.api.homescreen.Shortcut;
import net.rim.blackberry.api.homescreen.ShortcutProvider;
import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.component.table.SimpleList;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.picker.HomeScreenLocationPicker;
import net.rim.device.api.util.StringProvider;

/**
 * This screen class displays a list of available pictures in a given directory
 * and allows a user to set a picture as the home screen background. This class
 * also allows a user to create a shortcut on the home screen that displays this
 * picture selection screen directly.
 */
public final class HomeScreenDemoSecondaryScreen extends MainScreen {
    private final String _pictureDirectoryURL;
    private final Vector _pictureFileNames;
    private final SimpleList _picturesList;
    private final ButtonField _shortcutButton;
    private final HomeScreenLocationPicker _homeScreenLocationPicker;
    private final MenuItem _setHomeScreenImage;
    private final MenuItem _addHomeScreenShortCut;

    private static final String SHORTCUT_ID = "shortcut";

    private static final int APP_DESCRIPTOR_INDEX = 1;

    /**
     * Create a new HomeScreenDemoSecondaryScreen object
     * 
     * @param pictureDirectoryURL
     *            The URL of the directory from which the pictures are read
     * @param pictureFileNames
     *            A vector listing the file names of pictures in the directory
     */
    public HomeScreenDemoSecondaryScreen(final String pictureDirectoryURL,
            final Vector pictureFileNames) {
        _pictureDirectoryURL = pictureDirectoryURL;
        _pictureFileNames = pictureFileNames;

        setTitle("Secondary Screen");

        add(new LabelField("Select an image:", Field.FIELD_HCENTER));

        final DemoVerticalFieldManager vfm =
                new DemoVerticalFieldManager(Manager.NO_VERTICAL_SCROLL);

        // Create a SimpleList to display the available image names
        _picturesList = new SimpleList(vfm);

        for (int i = 0; i < _pictureFileNames.size(); i++) {
            _picturesList.add((String) _pictureFileNames.elementAt(i));
        }

        _picturesList.setCommand(new Command(new CommandHandler() {
            /**
             * @see CommandHandler#execute(ReadOnlyCommandMetadata, Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                setImage();
            }
        }));

        add(vfm);

        final SeparatorField separator = new SeparatorField();
        separator.setPadding(50, 0, 0, 0);
        add(separator);
        add(new LabelField("Add shortcut to the home screen",
                Field.FIELD_HCENTER));
        add(new SeparatorField());

        _homeScreenLocationPicker = HomeScreenLocationPicker.create();
        add(_homeScreenLocationPicker);

        // Create a button to add a shortcut to this screen on the home screen
        _shortcutButton =
                new ButtonField("Add shortcut", Field.FIELD_HCENTER
                        | ButtonField.CONSUME_CLICK);
        _shortcutButton.setChangeListener(new FieldChangeListener() {
            public void fieldChanged(final Field field, final int context) {
                addShortcut();
            }
        });

        add(_shortcutButton);

        _setHomeScreenImage =
                new MenuItem(new StringProvider("Set as Home Screen Image"),
                        0x230010, 0);
        _setHomeScreenImage.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                setImage();
            }
        }));

        _addHomeScreenShortCut =
                new MenuItem(new StringProvider("Add Shortcut"), 0x230020, 1);
        _addHomeScreenShortCut.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) {
                addShortcut();
            }
        }));
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#makeMenu(Menu,int)
     */
    protected void makeMenu(final Menu menu, final int instance) {
        if (_shortcutButton.isFocus() || _homeScreenLocationPicker.isFocus()) {
            menu.add(_addHomeScreenShortCut);
        } else {
            menu.add(_setHomeScreenImage);
        }

        super.makeMenu(menu, instance);
    }

    /**
     * Prompts the user to set the home screen image to the currently selected
     * image.
     */
    private void setImage() {
        final String name =
                (String) _pictureFileNames.elementAt(_picturesList
                        .getFocusRow());

        if (Dialog.ask(Dialog.D_YES_NO, "Set home screen image to " + name
                + "?") == Dialog.YES) {
            final String uri = _pictureDirectoryURL + name;
            HomeScreen.setBackgroundImage(uri);
        }
    }

    /**
     * Adds a shortcut on the device home screen
     */
    private void addShortcut() {
        // Create new shortcut and add at the location specified by the
        // LocationPicker
        if (HomeScreen.doesShortcutExist(SHORTCUT_ID)) {
            if (Dialog.ask(Dialog.D_YES_NO, "Shortcut exists. Overwrite?") == Dialog.YES) {
                HomeScreen.removeShortcut(SHORTCUT_ID);
            } else {
                return;
            }
        }
        final Shortcut newShortcut =
                ShortcutProvider.createShortcut("Home Screen Demo Shortcut",
                        SHORTCUT_ID, APP_DESCRIPTOR_INDEX);
        HomeScreen.addShortcut(newShortcut, _homeScreenLocationPicker
                .getLocation());

        // Store the directory URL in the persistent store for this sample
        final PersistentObject store =
                PersistentStore
                        .getPersistentObject(HomeScreenDemo.HOMESCREEN_DEMO_ID);
        synchronized (store) {
            store.setContents(_pictureDirectoryURL);
            PersistentObject.commit(store);
        }

        Dialog.inform("Shortcut added successfully.");
    }

    /**
     * @see MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * A VerticalFieldManager which performs an action on an Enter key press
     */
    private final class DemoVerticalFieldManager extends VerticalFieldManager {
        /**
         * Creates a new DemoVerticalFieldManager object
         * 
         * @param style
         *            Style bit for this manager
         */
        DemoVerticalFieldManager(final long style) {
            super(style);
        }

        /**
         * @see net.rim.device.api.ui.Manager#keyChar(char, int, int)
         */
        protected boolean
                keyChar(final char c, final int status, final int time) {
            if (c == Characters.ENTER) {
                setImage();
                return true;
            }

            return super.keyChar(c, status, time);
        }
    }
}
