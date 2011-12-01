/*
 * PointOfInterestEntity.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
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

/**
 * Implementation of a point of interest searchable entity. This basic
 * implementation does not provide any UI actions that might be associated with
 * this entity.
 */
public class PointOfInterestEntity implements SearchableEntity {
    private final PointOfInterest _poi;
    private final PointOfInterestSearchable _searchable;
    private final SearchFieldCriteriaList _searchFieldCriteriaList;
    private Image _icon;
    private final long _type;
    private UiAction _action;

    /**
     * Creates a new PointOfInterestEntity
     * 
     * @param poi
     *            A reference to the PointOfInterest this entity is associated
     *            with
     * @param searchable
     *            A reference to the PointOfInterestSearchable this entity is
     *            associated with
     * @throws NullPointerException
     *             if arguments are null
     */
    public PointOfInterestEntity(final PointOfInterest poi,
            final PointOfInterestSearchable searchable) {
        if (poi == null) {
            throw new NullPointerException("PointOfInterest is null");
        }
        if (searchable == null) {
            throw new NullPointerException("PointOfInterestSearchable is null");
        }

        _poi = poi;
        _searchable = searchable;
        _searchFieldCriteriaList = new SearchFieldCriteriaList();
        _type = poi.getType();

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
            _icon = searchable.getIcon();
        }

        // Get POI search fields
        final SearchField[] fields = _searchable.defineSupportedSearchFields();

        for (int i = 0; i < fields.length; i++) {
            // Set each field's search criteria to its value
            if (fields[i].getName().equals(
                    PointOfInterestSearchable.SEARCH_FIELD_POI)) {
                _searchFieldCriteriaList.addCriteria(new SearchFieldCriteria(
                        fields[i], new String[] { _poi.getName() }));
            } else if (fields[i].getName().equals(
                    PointOfInterestSearchable.SEARCH_FIELD_DATA)) {
                _searchFieldCriteriaList.addCriteria(new SearchFieldCriteria(
                        fields[i], new String[] { _poi.getData() }));
            }
        }
    }

    /**
     * @see SearchableEntity#getData()
     */
    public Object getData() {
        // Return the PointOfInterest represented by this entity
        return _poi;
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
        // Return the PointOfInterestSearchable this entity is associated with
        return _searchable;
    }

    /**
     * @see SearchableEntity#getSummary()
     */
    public String getSummary() {
        // Return the summary that will be shown when this entity appears in
        // search results
        return _poi.getData();
    }

    /**
     * @see SearchableEntity#getTitle()
     */
    public String getTitle() {
        // Return the title that will be shown when this entity appears in
        // search results
        return _poi.getName();
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
     * @see SearchableEntity#getTimeStamp()
     */
    public long getTimeStamp() {
        // Not implemented
        return 0;
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
            // point of interest as a recipient
            Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES,
                    new MessageArguments(MessageArguments.ARG_NEW, _poi
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
                // Start a browser session with the URL in the point of interest
                browser.displayPage(_poi.getData());
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
            address.setField(AddressInfo.STREET, _poi.getData());

            final Landmark[] landmark =
                    { new Landmark(_poi.getName(), null, null, address) };

            // Invoke the Maps application with the address in the point of
            // interest
            Invoke.invokeApplication(Invoke.APP_TYPE_MAPS, new MapsArguments(
                    landmark));
        }
    }
}
