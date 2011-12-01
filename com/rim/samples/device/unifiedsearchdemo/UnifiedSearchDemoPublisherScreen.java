/*
 * UnifiedSearchDemoPublisherScreen.java
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

package com.rim.samples.device.unifiedsearchdemo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.ObjectListField;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;
import net.rim.device.api.ui.decor.BackgroundFactory;
import net.rim.device.api.unifiedsearch.searchables.SearchableContentTypeConstants;

/**
 * The publisher screen for the Unified Search Demo Application
 */
public class UnifiedSearchDemoPublisherScreen extends MainScreen implements
        FieldChangeListener {
    private LabelField _listTitle;
    private ObjectListField _listField;
    private ButtonField _addButton;
    private ButtonField _publishButton;
    private ButtonField _searchButton;
    private ButtonField _addFromFileButton;
    private final Vector _dataObjects;
    private final UnifiedSearchDemoPublisher _publisher;

    /**
     * Creates a new UnifiedSearchDemoPublisherScreen object
     */
    public UnifiedSearchDemoPublisherScreen(
            final UnifiedSearchDemoPublisher publisher) {
        super(NO_VERTICAL_SCROLL);

        _publisher = publisher;

        _dataObjects = new Vector();

        createUI();
    }

    /**
     * @see net.rim.device.api.ui.Screen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        if (_dataObjects.size() == 0) {
            // Suppress the save dialog
            return true;
        }

        return super.onSavePrompt();
    }

    /**
     * @see Screen#save()
     */
    public void save() {
        _publisher.insertData(_dataObjects);

        // Clear data from the collection and list
        _dataObjects.removeAllElements();
        _listField.set(null);
        setDirty(false);
    }

    /**
     * Adds some data to the screen's collection
     */
    private void onAddData() {
        // Add new data to collection
        _dataObjects.addElement(new UnifiedSearchDemoDataObject("Rim One",
                "175 Columbia St",
                SearchableContentTypeConstants.CONTENT_TYPE_LOCATION));
        _dataObjects.addElement(new UnifiedSearchDemoDataObject("Rim Two",
                "295 Phillip St",
                SearchableContentTypeConstants.CONTENT_TYPE_LOCATION));
        _dataObjects.addElement(new UnifiedSearchDemoDataObject("Rim Three",
                "185 Columbia St",
                SearchableContentTypeConstants.CONTENT_TYPE_LOCATION));
        _dataObjects.addElement(new UnifiedSearchDemoDataObject("John Graham",
                "aaa@bbb.com",
                SearchableContentTypeConstants.CONTENT_TYPE_CONTACTS));
        _dataObjects.addElement(new UnifiedSearchDemoDataObject("BlackBerry",
                "http://mobile.blackberry.com",
                SearchableContentTypeConstants.CONTENT_TYPE_BROWSER));

        updateData();
    }

    /**
     * Updates the screen's list of data
     */
    private void updateData() {
        // Update the UI list to display the new data
        final Object[] elementArray = new Object[_dataObjects.size()];
        _dataObjects.copyInto(elementArray);
        _listField.set(elementArray);
    }

    /**
     * Retrieves data objects represented in a file
     */
    private void onAddFromFile() {
        final InputStream is = getClass().getResourceAsStream("/data.txt");
        if (is == null) {
            UnifiedSearchDemo.errorDialog("Could not find file resource");
        } else {
            try {
                final Vector objectsFromFile =
                        UnifiedSearchDemoFileReader.getDataFromStream(is);
                if (objectsFromFile != null) {
                    final int size = objectsFromFile.size();
                    for (int i = 0; i < size; ++i) {
                        _dataObjects.addElement(objectsFromFile.elementAt(i));
                    }
                    updateData();
                }
            } catch (final IOException ioe) {
                UnifiedSearchDemo.errorDialog("Could not add data from file: "
                        + ioe.getMessage());
            }
        }
    }

    /**
     * Creates the user interface for the screen
     */
    private void createUI() {
        setTitle("Unified Search Demo");

        // Add a label for the list
        _listTitle = new LabelField("Data: ", Field.USE_ALL_WIDTH);
        _listTitle.setBackground(BackgroundFactory
                .createSolidTransparentBackground(Color.DARKBLUE, 128));
        add(_listTitle);

        // Create list field
        _listField = new ObjectListField();

        // Initialize buttons
        _addButton = new ButtonField("Add Data", ButtonField.CONSUME_CLICK);
        _addFromFileButton =
                new ButtonField("Add From File", ButtonField.CONSUME_CLICK);
        _publishButton =
                new ButtonField("Publish Data", ButtonField.CONSUME_CLICK);
        _searchButton = new ButtonField("Search", ButtonField.CONSUME_CLICK);
        _addButton.setChangeListener(this);
        _publishButton.setChangeListener(this);
        _addFromFileButton.setChangeListener(this);
        _searchButton.setChangeListener(this);

        // Add fields to manager
        final VerticalFieldManager vfm =
                new VerticalFieldManager(VERTICAL_SCROLL);
        vfm.add(_listField);
        vfm.add(new SeparatorField());
        vfm.add(_addButton);
        vfm.add(new SeparatorField());
        vfm.add(_addFromFileButton);
        vfm.add(new SeparatorField());
        vfm.add(_publishButton);
        vfm.add(new SeparatorField());
        vfm.add(_searchButton);

        add(vfm);
    }

    /**
     * @see FieldChangeListener#fieldChanged(Field, int)
     */
    public void fieldChanged(final Field field, final int context) {
        if (field == _addButton) {
            onAddData();
        } else if (field == _searchButton) {
            UiApplication.getUiApplication().pushScreen(
                    new UnifiedSearchDemoSearchScreen());
        } else if (field == _addFromFileButton) {
            onAddFromFile();
        } else if (field == _publishButton) {
            if (_dataObjects.size() == 0) {
                Dialog.alert("No data to publish");
            } else {
                save();
            }
        }
    }
}
