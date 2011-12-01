/*
 * PointOfInterestPublisher.java
 *
 * AUTO_COPY_RIGHT_SUB_TAG
 */

package com.rim.samples.device.unifiedsearchdemo;

import java.util.Vector;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Status;
import net.rim.device.api.unifiedsearch.content.AppContentListener;
import net.rim.device.api.unifiedsearch.content.AppContentManager;
import net.rim.device.api.unifiedsearch.registry.RegistrationToken;
import net.rim.device.api.unifiedsearch.registry.SearchRegistry;

/**
 * This class registers a PointOfInterstSearchable with the Unified Search
 * Framework and inserts POIs in the cache into the Unified Search Framework.
 */
public class PointOfInterestPublisher implements AppContentListener {
    private final PointOfInterestSearchable _pointOfInterestSearchable;
    private final RegistrationToken _regToken;
    private UiApplication _uiApplication;

    /**
     * Creates a new PointOfInterestPublisher object
     * 
     * @throws PublisherException
     *             if registration to the Unified Search Framework fails
     */
    public PointOfInterestPublisher() throws PublisherException {
        // Register PointOfInterestSearchable with the Unified Search Framework
        _pointOfInterestSearchable = new PointOfInterestSearchable();
        _regToken =
                SearchRegistry.getInstance().register(
                        _pointOfInterestSearchable);

        if (!_regToken.isValid()) {
            throw new PublisherException(
                    "Application has failed to register with the Unified Search Framework");
        }
    }

    /**
     * Inserts PointOfInterest objects into the Unified Search Framework
     * 
     * @param poiVector
     *            A collection of PointOfInterest objects to insert into the
     *            Unified Search Framework
     */
    public void insertPOIs(final Vector poiVector) {
        PointOfInterestEntity[] poiEntities;
        synchronized (poiVector) {
            // Build a PointOfInterestEntity array out of the given
            // PointOfInterest objects
            final int size = poiVector.size();
            poiEntities = new PointOfInterestEntity[size];

            for (int i = size - 1; i >= 0; --i) {
                final PointOfInterest poi =
                        (PointOfInterest) poiVector.elementAt(i);
                poiEntities[i] =
                        new PointOfInterestEntity(poi,
                                _pointOfInterestSearchable);
            }
        }

        // Insert the POI entities into the Unified Search Framework
        AppContentManager.getInstance().insertContent(poiEntities, this,
                _regToken);

        // Caching the application reference so that it can be used to update
        // the UI in the listener callback.
        _uiApplication = UiApplication.getUiApplication();
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
                buffer.append(" points of interest");
                Status.show(buffer.toString(), 1250);
            }
        });
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
