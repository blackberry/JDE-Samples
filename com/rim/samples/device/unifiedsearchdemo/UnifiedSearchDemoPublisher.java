/*
 * UnifiedSearchDemoPublisher.java
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

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;
import net.rim.device.api.unifiedsearch.SearchField;
import net.rim.device.api.unifiedsearch.SearchFieldCriteria;
import net.rim.device.api.unifiedsearch.SearchFieldCriteriaList;
import net.rim.device.api.unifiedsearch.content.AppContentListener;
import net.rim.device.api.unifiedsearch.content.AppContentManager;
import net.rim.device.api.unifiedsearch.entity.ExposureLevel;
import net.rim.device.api.unifiedsearch.registry.RegistrationToken;
import net.rim.device.api.unifiedsearch.searchables.SearchableContentTypeConstants;
import net.rim.device.api.unifiedsearch.searchables.adapters.EntityBasedSearchableProvider;

/**
 * This class registers a Searchable with the Unified Search Framework and
 * inserts data objects in the cache into the Unified Search Framework.
 */
public class UnifiedSearchDemoPublisher implements AppContentListener {
    public static final String SEARCH_FIELD_NAME = "Name";
    public static final String SEARCH_FIELD_DATA = "Data";

    private final EntityBasedSearchableProvider _searchableProvider;
    private final RegistrationToken _regToken;
    private final UiApplication _uiApplication;
    private UnifiedSearchDemoEntity[] _entities;

    /**
     * Creates a new UnifiedSearchDemoPublisher object
     * 
     * @throws PublisherException
     *             if registration to the Unified Search Framework fails
     */
    public UnifiedSearchDemoPublisher() throws PublisherException {
        // Instantiate an EntityBasedSearchableProvider. This is the object
        // that allows data belonging to this application to become searchable.
        _searchableProvider = new EntityBasedSearchableProvider();
        _searchableProvider.setPrivacyLevel(ExposureLevel.LEVEL_PUBLIC);
        _searchableProvider
                .setType(SearchableContentTypeConstants.CONTENT_TYPE_BROWSER
                        | SearchableContentTypeConstants.CONTENT_TYPE_CONTACTS
                        | SearchableContentTypeConstants.CONTENT_TYPE_LOCATION);

        final SearchField[] searchFields =
                new SearchField[] { new SearchField(SEARCH_FIELD_NAME),
                        new SearchField(SEARCH_FIELD_DATA) };

        _searchableProvider.setSupportedSearchFields(searchFields);

        final Bitmap bitmap = Bitmap.getBitmapResource("default.png");

        if (bitmap != null) {
            final Image img = ImageFactory.createImage(bitmap);
            _searchableProvider.setIcon(img);
        }

        _searchableProvider.setName("Unified Search Demo");

        // Register EntityBasedSearchableProvider with the Unified Search
        // Framework
        _regToken = _searchableProvider.register();

        if (!_regToken.isValid()) {
            throw new PublisherException(
                    "Application has failed to register with the Unified Search Framework");
        }

        // Cache reference to the UiApplication for later use
        _uiApplication = UiApplication.getUiApplication();
    }

    /**
     * Inserts data objects into the Unified Search Framework
     * 
     * @param dataVector
     *            A collection of data objects to insert into the Unified Search
     *            Framework
     */
    public void insertData(final Vector dataVector) {
        UnifiedSearchDemoEntity[] dataEntities;

        synchronized (dataVector) {
            // Build a UnifiedSearchDemoEntity array out of the given data
            // objects
            final int size = dataVector.size();
            dataEntities = new UnifiedSearchDemoEntity[size];

            for (int i = size - 1; i >= 0; --i) {
                final UnifiedSearchDemoDataObject object =
                        (UnifiedSearchDemoDataObject) dataVector.elementAt(i);
                dataEntities[i] =
                        new UnifiedSearchDemoEntity(object, _searchableProvider);
            }
        }

        setEntities(dataEntities);

        _searchableProvider.addSearchableData(dataEntities, this);
    }

    /**
     * Stores the UnifiedSearchDemoEntity array that was added to the Unified
     * Search Framework.
     * 
     * @param entities
     *            The UnifiedSearchDemoEntity array that was added to the
     *            Unified Search Framework
     */
    private void setEntities(final UnifiedSearchDemoEntity[] entities) {
        _entities = entities;
    }

    /**
     * Retrieves the stored UnifiedSearchDemoEntity[] object that was added to
     * the Unified Search Framework.
     * 
     * @return The PointOfInterestEntitiy[] that was added to the Unified Search
     *         Framework
     */
    private UnifiedSearchDemoEntity[] getEntities() {
        return _entities;
    }

    /**
     * Looks through all the UnifiedSearchDemoEntity data and look for data that
     * has an e-mail in it. Add the keyword "email" to that entity's
     * SearchCriteria.
     * 
     * @param entities
     *            UnifiedSearchDemoEntity array to be added to the Unified
     *            Search Framework
     */
    private void
            addAdditionalCriteria(final UnifiedSearchDemoEntity[] entities) {
        final int size = entities.length;

        // Iterate through the UnifiedSearchDemoEntity array and look
        // for instances that contain an email address.
        for (int i = size - 1; i >= 0; --i) {
            final String data = entities[i].getSummary();

            // Check if the data is a well formed email address
            if (validateEmail(data)) {
                final EntityBasedSearchableProvider searchable =
                        (EntityBasedSearchableProvider) entities[i]
                                .getSearchable();
                final SearchField[] fields =
                        searchable.getSupportedSearchFields();

                // Go through all the SeachFields for the particular
                // UnifiedSearchDemoEntity
                for (int j = 0; j < fields.length; j++) {
                    // Create the criteria that will be added
                    final SearchFieldCriteria criteria =
                            new SearchFieldCriteria(fields[j],
                                    new String[] { "email" });
                    final SearchFieldCriteriaList criteriaList =
                            new SearchFieldCriteriaList();
                    criteriaList.addCriteria(criteria);

                    // Add the additional search criteria to the Unified Search
                    // Framework
                    try {
                        AppContentManager.getInstance().addSearchCriteria(
                                entities[i], criteriaList, _regToken);
                    } catch (final Exception e) {
                    }
                }
            }
        }
    }

    /**
     * Validates the well formedness of an email address string. This method
     * does not make sure that the email is active or a real email. It only
     * looks to see if it is reasonably well formed.
     * 
     * @param email
     *            The possible email address that is going to get validated
     * @return true if the email is well formed, false if the email is not well
     *         formed
     */
    private boolean validateEmail(final String email) {
        final int posAt = email.indexOf("@");
        final int posDot = email.lastIndexOf('.');

        // Either "@" or "." is not in the string
        if (posAt == -1 || posDot == -1) {
            // Not well formed
            return false;
        }

        // "@" is the first character || "@" is the last character || "." is the
        // last char
        if (email.startsWith("@") || email.endsWith("@") || email.endsWith(".")) {
            // Not well formed
            return false;
        }

        // "." before "@" || The first "@" is not the last "@"
        // --> more than one "@" || The char directly after the
        // "@" is a "." ie dave@.rim.com
        if (posDot < posAt || posAt != email.lastIndexOf('@')
                || email.charAt(posAt + 1) == '.') {
            // Not well formed
            return false;
        }

        // If the length of the email address is less than 6 it is too small.
        // Smallest email example :: "a@b.ca" = 6 char length.
        if (email.length() < 6) {
            // Not well formed
            return false;
        }

        // The email address is reasonably well formed
        return true;
    }

    // AppContentListener implementation
    // -----------------------------------------

    /**
     * @see AppContentListener#onInsertComplete(int)
     */
    public void onInsertComplete(final int insertCount) {
        _uiApplication.invokeLater(new Runnable() {
            public void run() {
                // Inform user of successful insert
                final StringBuffer buffer = new StringBuffer("Inserted ");
                buffer.append(insertCount);
                buffer.append(" data objects. Try the search word \"email\"!");
                Status.show(buffer.toString(), 2000);
            }
        });

        // Add additional criteria
        final UnifiedSearchDemoEntity[] entities = getEntities();
        addAdditionalCriteria(entities);
    }

    /**
     * @see AppContentListener#onUpdateComplete(int)
     */
    public void onUpdateComplete(final int updCount) {
        // Not implemented
    }

    /**
     * @see AppContentListener#onDeleteComplete(int)
     */
    public void onDeleteComplete(final int delCount) {
        // Not implemented
    }
}

/**
 * A subclass of Exception to indicate a Unified Search Framework registration
 * error.
 */
class PublisherException extends Exception {
    PublisherException(final String message) {
        super(message);
    }
}
