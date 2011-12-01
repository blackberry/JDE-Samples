/*
 * PointOfInterestSearchable.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.unifiedsearchdemo;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.image.Image;
import net.rim.device.api.ui.image.ImageFactory;
import net.rim.device.api.unifiedsearch.SearchField;
import net.rim.device.api.unifiedsearch.action.UiAction;
import net.rim.device.api.unifiedsearch.entity.ExposureLevel;
import net.rim.device.api.unifiedsearch.entity.SearchableEntity;
import net.rim.device.api.unifiedsearch.query.NotificationListener;
import net.rim.device.api.unifiedsearch.searchables.EntityBasedSearchable;
import net.rim.device.api.unifiedsearch.searchables.Searchable;
import net.rim.device.api.unifiedsearch.searchables.SearchableContentTypeConstants;
import net.rim.device.api.util.Comparator;

/**
 * Implementation of EntityBasedSearchable. Allows application to provide
 * searchable content to the Unified Search Framework.
 */
public class PointOfInterestSearchable implements EntityBasedSearchable {
    public static final String SEARCH_FIELD_POI = "POI";
    public static final String SEARCH_FIELD_DATA = "Data";
    private long _registrationId;
    private Image _icon;
    private final SearchField[] _searchFields;

    /**
     * Creates a new PointOfInterestSearchable object
     */
    public PointOfInterestSearchable() {
        _searchFields =
                new SearchField[] { new SearchField(SEARCH_FIELD_POI),
                        new SearchField(SEARCH_FIELD_DATA) };

        final Bitmap img = Bitmap.getBitmapResource("default.png");

        if (img != null) {
            _icon = ImageFactory.createImage(img);
        } else {
            _icon = null;
        }
    }

    /**
     * @see EntityBasedSearchable#getSearchableEntities()
     */
    public SearchableEntity[] getSearchableEntities() {
        // We will manually publish the content with AppContentManager
        return new SearchableEntity[0];
    }

    /**
     * @see Searchable#defineSupportedSearchFields()
     */
    public SearchField[] defineSupportedSearchFields() {
        // Return an array of SearchField objects supported by this searchable
        return _searchFields;
    }

    /**
     * @see Searchable#getComparator()
     */
    public Comparator getComparator() {
        // Not implemented. Searchable applications can optionally provide a
        // Comparator for use by the Unified Search Framework to sort search
        // results.
        return null;
    }

    /**
     * @see Searchable#getIcon()
     */
    public Image getIcon() {
        // Return the application's icon that will be shown in search results
        return _icon;
    }

    /**
     * @see Searchable#getName()
     */
    public String getName() {
        // Return the searchable's name that will be shown in search results
        return "Unified Search Demo Publisher";
    }

    /**
     * @see Searchable#getPriority()
     */
    public int getPriority() {
        return PRIORITY_NORMAL;
    }

    /**
     * @see Searchable#getPrivacyLevel()()
     */
    public int getPrivacyLevel() {
        return ExposureLevel.LEVEL_PUBLIC;
    }

    /**
     * @see Searchable#getRegistrationID()
     */
    public long getRegistrationID() {
        return _registrationId;
    }

    /**
     * @see Searchable#getType()
     */
    public long getType() {
        return SearchableContentTypeConstants.CONTENT_TYPE_DEFAULT_ALL;
    }

    /**
     * @see Searchable#load(NotificationListener, int)
     */
    public void load(final NotificationListener observer, final int loadType) {
        // Not implemented
    }

    /**
     * @see Searchable#pause()
     */
    public void pause() {
        // Not implemented
    }

    /**
     * @see Searchable#resume()
     */
    public void resume() {
        // Not implemented
    }

    /**
     * @see Searchable#setRegistrationID(long)
     */
    public void setRegistrationID(final long registrationId) {
        _registrationId = registrationId;
    }

    /**
     * @see Searchable#getUiAction(SearchableEntity[])
     */
    public UiAction[] getUiAction(final SearchableEntity[] entities) {
        // Not implemented. Entities associated with this particular Searchable
        // do not provide UI actions.
        return null;
    }

    /**
     * @see Searchable#getUiActions(SearchableEntity[], Object, UiAction[])
     */
    public UiAction getUiActions(final SearchableEntity[] entities,
            final Object contextObject, final UiAction[] uiActions) {
        // Not implemented. Entities associated with this particular Searchable
        // do not provide UI actions.
        return null;
    }
}
