/*
 * UnifiedSearchDemoEntity.java
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

import javax.microedition.location.AddressInfo;
import javax.microedition.location.Landmark;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MapsArguments;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;
import net.rim.device.api.unifiedsearch.SearchField;
import net.rim.device.api.unifiedsearch.SearchFieldCriteria;
import net.rim.device.api.unifiedsearch.SearchFieldCriteriaList;
import net.rim.device.api.unifiedsearch.action.UiAction;
import net.rim.device.api.unifiedsearch.entity.SearchableEntity;
import net.rim.device.api.unifiedsearch.searchables.Searchable;
import net.rim.device.api.unifiedsearch.searchables.SearchableContentTypeConstants;
import net.rim.device.api.unifiedsearch.searchables.adapters.EntityBasedSearchableProvider;
import net.rim.device.api.unifiedsearch.searchables.adapters.SearchableDataObject;

/**
 * Implementation of a SearchableEntity
 */
public class UnifiedSearchDemoEntity extends SearchableDataObject {
    private final UnifiedSearchDemoDataObject _dataObject;
    private final EntityBasedSearchableProvider _searchableProvider;
    private final SearchFieldCriteriaList _searchFieldCriteriaList;
    private Image _icon;
    private final long _type;
    private UiAction _action;

    /**
     * Creates a new UnifiedSearchDemoEntity
     * 
     * @param dataObject
     *            A reference to the UnifiedSearchDemoDataObject this entity is
     *            associated with
     * @param searchableProvider
     *            A reference to the UnifiedSearchDemoSearchable this entity is
     *            associated with
     * @throws NullPointerException
     *             if arguments are null
     */
    public UnifiedSearchDemoEntity(
            final UnifiedSearchDemoDataObject dataObject,
            final EntityBasedSearchableProvider searchableProvider) {
        if (dataObject == null) {
            throw new NullPointerException(
                    "UnifiedSearchDemoDataObject is null");
        }
        if (searchableProvider == null) {
            throw new NullPointerException(
                    "UnifiedSearchDemoSearchable is null");
        }

        _dataObject = dataObject;
        _searchableProvider = searchableProvider;
        _searchFieldCriteriaList = new SearchFieldCriteriaList();
        _type = dataObject.getType();

        Bitmap img = null;

        if (_type == SearchableContentTypeConstants.CONTENT_TYPE_LOCATION) {
            img = Bitmap.getBitmapResource("location.png");
            _action = new LocationAction();
        }

        else if (_type == SearchableContentTypeConstants.CONTENT_TYPE_CONTACTS) {
            img = Bitmap.getBitmapResource("contact.png");
            _action = new ContactAction();
        }

        else if (_type == SearchableContentTypeConstants.CONTENT_TYPE_BROWSER) {
            img = Bitmap.getBitmapResource("url.png");
            _action = new UrlAction();
        }

        if (img != null) {
            _icon = ImageFactory.createImage(img);
        }

        else {
            _icon = searchableProvider.getIcon();
        }

        // Get search fields
        final SearchField[] fields =
                _searchableProvider.getSupportedSearchFields();

        for (int i = 0; i < fields.length; i++) {

            // Specify a delimiter to use on the phrase/keyword to break
            // it up into multiple phrases/keywords. Knowing what the
            // phrase/keyword is will help in determining a good delimiter.
            final String delimiter = " ";

            String[] searchPhrase = null;

            if (fields[i].getName().equals(
                    UnifiedSearchDemoPublisher.SEARCH_FIELD_NAME)) {
                searchPhrase = new String[] { _dataObject.getName() };
            } else if (fields[i].getName().equals(
                    UnifiedSearchDemoPublisher.SEARCH_FIELD_DATA)) {
                searchPhrase = new String[] { _dataObject.getData() };
            }

            // Add criteria to list
            final SearchFieldCriteria searchFieldCriteria =
                    new SearchFieldCriteria(fields[i], searchPhrase, true,
                            delimiter);
            _searchFieldCriteriaList.addCriteria(searchFieldCriteria);
        }
    }

    /**
     * @see SearchableEntity#getData()
     */
    public Object getData() {
        // Return the data object represented by this entity
        return _dataObject;
    }

    /**
     * @see SearchableEntity#getSearchCriteria()
     */
    public SearchFieldCriteriaList getSearchCriteria() {
        return _searchFieldCriteriaList;
    }

    /**
     * @see SearchableEntity#getSearchable()
     */
    public Searchable getSearchable() {
        // Return the UnifiedSearchDemoSearchable this entity is associated with
        return _searchableProvider;
    }

    /**
     * @see SearchableEntity#getSummary()
     */
    public String getSummary() {
        // Return the summary that will be shown when this entity appears in
        // search results
        return _dataObject.getData();
    }

    /**
     * @see SearchableEntity#getTitle()
     */
    public String getTitle() {
        // Return the title that will be shown when this entity appears in
        // search results
        return _dataObject.getName();
    }

    /**
     * @see SearchableEntity#getIcon()
     */
    public Image getIcon() {
        // Return the icon that will be shown when this entity appears in search
        // results
        return _icon;
    }

    /**
     * @see SearchableEntity#getUiActions(Object, UiAction[])
     */
    public UiAction getUiActions(final Object contextObject,
            final UiAction[] uiActions) {
        return _action;
    }

    /**
     * The UiAction for a contact
     */
    private class ContactAction extends UiAction {
        /**
         * @see Object#toString()
         */
        public String toString() {
            return "Email";
        }

        /**
         * @see UiAction#runAction()
         */
        protected void runAction() {
            // Invoke the Messages application and add the email address in the
            // data object as a recipient
            Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES,
                    new MessageArguments(MessageArguments.ARG_NEW, _dataObject
                            .getData(), "", ""));
        }
    }

    /**
     * The UiAction for a URL
     */
    private class UrlAction extends UiAction {
        /**
         * @see Object#toString()
         */
        public String toString() {
            return "Go";
        }

        /**
         * @see UiAction#runAction()
         */
        protected void runAction() {
            final BrowserSession browser = Browser.getDefaultSession();

            if (browser != null) {
                // Start a browser session with the URL in the data object
                browser.displayPage(_dataObject.getData());
            }
        }
    }

    /**
     * The UiAction for a location
     */
    private class LocationAction extends UiAction {
        /**
         * @see Object#toString()
         */
        public String toString() {
            return "Map";
        }

        /**
         * @see UiAction#runAction()
         */
        protected void runAction() {
            final AddressInfo address = new AddressInfo();

            // All Sample locations are in Waterloo
            address.setField(AddressInfo.COUNTY, "Canada");
            address.setField(AddressInfo.STATE, "Ontario");
            address.setField(AddressInfo.CITY, "Waterloo");
            address.setField(AddressInfo.STREET, _dataObject.getData());

            final Landmark[] landmark =
                    { new Landmark(_dataObject.getName(), null, null, address) };

            // Invoke the Maps application with the address in the data object
            Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, new MapsArguments(
                    landmark));
        }
    }
}
