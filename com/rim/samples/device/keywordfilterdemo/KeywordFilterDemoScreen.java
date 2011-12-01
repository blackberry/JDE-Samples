/*
 * KeywordFilterDemoScreen.java
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

package com.rim.samples.device.keywordfilterdemo;

import net.rim.device.api.command.Command;
import net.rim.device.api.command.CommandHandler;
import net.rim.device.api.command.ReadOnlyCommandMetadata;
import net.rim.device.api.system.Characters;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.KeywordFilterField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.util.StringProvider;

/**
 * The main screen for the KeywordFilterDemo application
 */
public final class KeywordFilterDemoScreen extends MainScreen {
    private final KeywordFilterDemo _app;
    private final KeywordFilterField _keywordFilterField;

    /**
     * Creates a new KeywordFilterDemoScreen object
     * 
     * @param app
     *            The UiApplication creating an instance of this class.
     */
    public KeywordFilterDemoScreen(final KeywordFilterDemo app) {
        // A reference to the UiApplication instance for use in this class
        _app = app;

        // Obtain a reference to the UiApplication's KeywordFilterField
        _keywordFilterField = _app.getKeywordFilterField();

        // MenuItem to add a country to the list
        final MenuItem addElementItem =
                new MenuItem(new StringProvider("Add country"), 0x230010, 0);
        addElementItem.setCommand(new Command(new CommandHandler() {
            /**
             * @see net.rim.device.api.command.CommandHandler#execute(ReadOnlyCommandMetadata,
             *      Object)
             */
            public void execute(final ReadOnlyCommandMetadata metadata,
                    final Object context) { // Clear the search field
                _keywordFilterField.setKeyword("");

                // Create a Dialog instance which will allow a user to add a new
                // country to the keyword list.
                final String[] selections = { "Add", "Cancel" };
                final Dialog addDialog =
                        new Dialog("Add Country", selections, null, 0, null);
                final EditField inputField = new EditField("Country: ", "");
                addDialog.add(inputField);

                // Display the dialog and add a new element to the list
                // of countries.
                if (addDialog.doModal() == 0) // User selected "Add"
                {
                    _app.addElementToList(new Country(inputField.getText(), "",
                            ""));
                }
            }
        }));

        // Add menu item to the screen's menu
        addMenuItem(addElementItem);
    }

    /**
     * @see net.rim.device.api.ui.Screen#keyChar(char, int, int)
     */
    protected boolean keyChar(final char key, final int status, final int time) {
        if (key == Characters.ENTER) {
            displayInfoScreen();
            return true;
        }

        return super.keyChar(key, status, time);
    }

    /**
     * @see net.rim.device.api.ui.Screen#invokeAction(int)
     */
    public boolean invokeAction(final int action) {
        if (action == ACTION_INVOKE) {
            displayInfoScreen();
            return true;
        }

        return super.invokeAction(action);
    }

    /**
     * Creates an InfoScreen instance and pushes it onto the stack for
     * rendering.
     */
    private void displayInfoScreen() {
        // Retrieve the selected Country and use it to invoke a new InfoScreen
        final Country country =
                (Country) _keywordFilterField.getSelectedElement();

        if (country != null) {
            final InfoScreen infoScreen = new InfoScreen(country);
            _app.pushScreen(infoScreen);
        }
    }

    /**
     * @see net.rim.device.api.ui.container.MainScreen#onSavePrompt()
     */
    public boolean onSavePrompt() {
        // Suppress the save dialog
        return true;
    }

    /**
     * A MainScreen class to display secondary information for a selected
     * country.
     */
    private final static class InfoScreen extends MainScreen {
        /**
         * Creates a new InfoScreen object
         * 
         * @param country
         *            The country to display secondary information about
         */
        InfoScreen(final Country country) {
            // Set up and display UI elements
            setTitle(country.toString());
            final BasicEditField popField =
                    new BasicEditField("Population: ", country.getPopulation(),
                            20, Field.NON_FOCUSABLE);
            final BasicEditField capField =
                    new BasicEditField("Capital: ", country.getCapitalCity(),
                            20, Field.NON_FOCUSABLE);
            add(popField);
            add(capField);
        }
    }
}
