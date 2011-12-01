/*
 * AutoCompleteFieldDemo.java
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

package com.rim.samples.device.ui.autocompletefielddemo;

import net.rim.device.api.collection.util.BasicFilteredList;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.AutoCompleteField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Sample application to demonstrate the AutoCompleteField and BasicFilteredList
 * classes
 */
public class AutoCompleteFieldDemo extends UiApplication {
    /**
     * Entry point for application
     * 
     * @param args
     *            Command line arguments (not used)
     */
    public static void main(final String[] args) {
        // Create a new instance of the application and make the currently
        // running thread the application's event dispatch thread.
        final AutoCompleteFieldDemo app = new AutoCompleteFieldDemo();
        app.enterEventDispatcher();
    }

    /**
     * Creates a new AutoCompleteFieldDemo object
     */
    public AutoCompleteFieldDemo() {
        pushScreen(new AutoCompleteFieldDemoScreen());
    }

    /**
     * MainScreen class for the AutoCompleteFieldDemo application
     */
    static final class AutoCompleteFieldDemoScreen extends MainScreen {
        private final BasicFilteredList _filteredListContacts;

        /**
         * Creates a new AutoCompleteFieldDemoScreen object
         */
        AutoCompleteFieldDemoScreen() {
            setTitle("Auto Complete Field Demo");

            add(new LabelField("Type in a field to search"));
            add(new SeparatorField());

            // Create the filtered lists
            _filteredListContacts = new BasicFilteredList();
            final BasicFilteredList filteredListMedia = new BasicFilteredList();
            final BasicFilteredList filteredListMonths =
                    new BasicFilteredList();

            // Add data source for contacts
            _filteredListContacts.addDataSource(0,
                    BasicFilteredList.DATA_SOURCE_CONTACTS,
                    BasicFilteredList.DATA_FIELD_CONTACTS_NAME_FULL
                            | BasicFilteredList.DATA_FIELD_CONTACTS_COMPANY
                            | BasicFilteredList.DATA_FIELD_CONTACTS_EMAIL,
                    BasicFilteredList.DATA_FIELD_CONTACTS_NAME_FULL,
                    BasicFilteredList.DATA_FIELD_CONTACTS_EMAIL, -1, null,
                    BasicFilteredList.COMPARISON_IGNORE_CASE);

            // Add data source for music
            filteredListMedia.addDataSource(0,
                    BasicFilteredList.DATA_SOURCE_MUSIC,
                    BasicFilteredList.DATA_FIELD_MUSIC_SONG,
                    BasicFilteredList.DATA_FIELD_MUSIC_ARTIST
                            | BasicFilteredList.DATA_FIELD_MUSIC_ALBUM
                            | BasicFilteredList.DATA_FIELD_MUSIC_SONG,
                    BasicFilteredList.DATA_FIELD_MUSIC_SONG, -1, null,
                    BasicFilteredList.COMPARISON_IGNORE_CASE);

            // Add data source for pictures
            filteredListMedia.addDataSource(0,
                    BasicFilteredList.DATA_SOURCE_PICTURES,
                    BasicFilteredList.DATA_FIELD_PICTURES_TITLE,
                    BasicFilteredList.DATA_FIELD_PICTURES_TITLE, -1, -1, null,
                    BasicFilteredList.COMPARISON_IGNORE_CASE);

            // Add data source for videos
            filteredListMedia.addDataSource(0,
                    BasicFilteredList.DATA_SOURCE_VIDEOS,
                    BasicFilteredList.DATA_FIELD_VIDEOS_TITLE,
                    BasicFilteredList.DATA_FIELD_VIDEOS_TITLE, -1, -1, null,
                    BasicFilteredList.COMPARISON_IGNORE_CASE);

            // Add data set for months
            final String[] months =
                    { "January", "February", "March", "April", "May", "June",
                            "July", "August", "September", "October",
                            "November", "December" };
            filteredListMonths.addDataSet(0, months, "Month",
                    BasicFilteredList.COMPARISON_IGNORE_CASE);

            // Create AutoCompleteFields
            final AutoCompleteField autoCompleteFieldContacts =
                    new AutoCompleteField(_filteredListContacts,
                            AutoCompleteField.LIST_STATIC
                                    | AutoCompleteField.LIST_DROPDOWN);
            final AutoCompleteField autoCompleteFieldMedia =
                    new AutoCompleteField(filteredListMedia,
                            AutoCompleteField.LIST_STATIC
                                    | AutoCompleteField.LIST_SHOW_DATA_SET_NAME
                                    | AutoCompleteField.LIST_DROPDOWN);
            final AutoCompleteField autoCompleteFieldMonths =
                    new AutoCompleteField(filteredListMonths,
                            AutoCompleteField.LIST_STATIC
                                    | AutoCompleteField.LIST_DROPDOWN);

            // Add the AutoCompleteFields to the screen
            add(new LabelField("Choose a contact"));
            add(autoCompleteFieldContacts);
            add(new LabelField("Choose media"));
            add(autoCompleteFieldMedia);
            add(new LabelField("Choose a month"));
            add(autoCompleteFieldMonths);
        }

        /**
         * @see MainScreen#onSavePrompt()
         */
        protected boolean onSavePrompt() {
            // Suppress the save dialog
            return true;
        }
    }
}
