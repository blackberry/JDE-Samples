/*
 * UnifiedSearchDemoPublisherScreen.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
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
    private LabelField _poiListTitle;
    private ObjectListField _poiList;
    private ButtonField _addPoiButton, _publishButton, _searchButton,
            _addFromFileButton;
    private final Vector _poiVector = new Vector();
    private final PointOfInterestPublisher _poiPublisher;

    /**
     * Creates a new UnifiedSearchDemoPublisherScreen object
     */
    public UnifiedSearchDemoPublisherScreen(
            final PointOfInterestPublisher poiPublisher) {
        super(NO_VERTICAL_SCROLL);

        _poiPublisher = poiPublisher;

        setTitle("Unified Search Demo");

        createUI();
    }

    /**
     * @see net.rim.device.api.ui.Screen#onSavePrompt()
     */
    protected boolean onSavePrompt() {
        if (_poiVector.size() == 0) {
            // Suppress the save dialog
            return true;
        }

        return super.onSavePrompt();
    }

    /**
     * @see Screen#save()
     */
    public void save() {
        _poiPublisher.insertPOIs(_poiVector);

        // Clear POIs from the collection and list
        _poiVector.removeAllElements();
        _poiList.set(null);
        setDirty(false);
    }

    /**
     * Adds some points of interest to the screen's collection
     */
    private void onAddPOIs() {
        // Add new POIs to collection
        _poiVector.addElement(new PointOfInterest("Rim One", "175 Columbia St",
                SearchableContentTypeConstants.CONTENT_TYPE_LOCATION));
        _poiVector.addElement(new PointOfInterest("Rim Two", "295 Phillip St",
                SearchableContentTypeConstants.CONTENT_TYPE_LOCATION));
        _poiVector.addElement(new PointOfInterest("Rim Three",
                "185 Columbia St",
                SearchableContentTypeConstants.CONTENT_TYPE_LOCATION));
        _poiVector.addElement(new PointOfInterest("John Graham", "aaa@bbb.com",
                SearchableContentTypeConstants.CONTENT_TYPE_CONTACTS));
        _poiVector.addElement(new PointOfInterest("Google",
                "http://www.google.com/",
                SearchableContentTypeConstants.CONTENT_TYPE_BROWSER));

        updatePOIs();
    }

    /**
     * Updates the screen's list of POIs
     */
    private void updatePOIs() {
        // Update the UI list to display the new POIs
        final Object[] elementArray = new Object[_poiVector.size()];
        _poiVector.copyInto(elementArray);
        _poiList.set(elementArray);
    }

    /**
     * Retrieves PointOfInterest objects represented in a file
     */
    private void onAddFromFile() {
        final InputStream is = getClass().getResourceAsStream("/POIs.txt");
        if (is == null) {
            UnifiedSearchDemo.errorDialog("Could not find file resource");
        } else {
            try {
                final Vector newPois =
                        UnifiedSearchDemoFileReader.getPoisFromStream(is);
                if (newPois != null) {
                    final int size = newPois.size();
                    for (int i = 0; i < size; ++i) {
                        _poiVector.addElement(newPois.elementAt(i));
                    }
                    updatePOIs();
                }
            } catch (final IOException ioe) {
                UnifiedSearchDemo
                        .errorDialog("Could not add points of interest from file: "
                                + ioe.getMessage());
            }
        }
    }

    /**
     * Creates the user interface fields for the screen
     */
    private void createUI() {
        // Add a label for the list
        _poiListTitle =
                new LabelField("Points of interest", Field.USE_ALL_WIDTH);
        _poiListTitle.setBackground(BackgroundFactory
                .createSolidTransparentBackground(Color.DARKBLUE, 128));
        add(_poiListTitle);

        // Create list field
        _poiList = new ObjectListField();

        // Initialize buttons
        _addPoiButton =
                new ButtonField("Add points of interest",
                        ButtonField.CONSUME_CLICK);
        _addFromFileButton =
                new ButtonField("Add from file", ButtonField.CONSUME_CLICK);
        _publishButton =
                new ButtonField("Publish points of interest",
                        ButtonField.CONSUME_CLICK);
        _searchButton = new ButtonField("Search", ButtonField.CONSUME_CLICK);
        _addPoiButton.setChangeListener(this);
        _publishButton.setChangeListener(this);
        _addFromFileButton.setChangeListener(this);
        _searchButton.setChangeListener(this);

        // Add fields to manager
        final VerticalFieldManager vfm =
                new VerticalFieldManager(VERTICAL_SCROLL);
        vfm.add(_poiList);
        vfm.add(new SeparatorField());
        vfm.add(_addPoiButton);
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
        if (field == _addPoiButton) {
            onAddPOIs();
        } else if (field == _searchButton) {
            UiApplication.getUiApplication().pushScreen(
                    new UnifiedSearchDemoSearchScreen());
        } else if (field == _addFromFileButton) {
            onAddFromFile();
        } else if (field == _publishButton) {
            if (_poiVector.size() == 0) {
                Dialog.alert("No points of interest to publish");
            } else {
                save();
            }
        }
    }
}
