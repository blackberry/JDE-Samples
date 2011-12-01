/*
 * OTABackupRestoreContactCollection.java
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

package com.rim.samples.device.otabackuprestoredemo;

import java.io.EOFException;
import java.util.Vector;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.SyncCollection;
import net.rim.device.api.synchronization.SyncCollectionSchema;
import net.rim.device.api.synchronization.SyncConverter;
import net.rim.device.api.synchronization.SyncObject;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RuntimeStore;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.util.CloneableVector;
import net.rim.device.api.util.DataBuffer;
import net.rim.device.api.util.ListenerUtilities;

/**
 * A collection enabled for OTA backup/restore. Basically a serially syncable
 * collection with few added interfaces.
 */
public class OTABackupRestoreContactCollection implements SyncConverter,
        SyncCollection, OTASyncCapable, CollectionEventSource {

    private PersistentObject _persist; // The persistable object for the
                                       // contacts
    private Vector _contacts; // The actual contacts
    private Vector _listeners; // Listeners to generate events when contacts are
                               // added
    private final SyncCollectionSchema _schema; // Lets us know about the data
                                                // we are backing up

    private static final long PERSISTENT_KEY = 0x266babf899b20b56L; // com.rim.samples.device.otabackuprestoredemo.OTABackupRestoreContactCollection._persist
    private static final long AR_KEY = 0xef780e08b3a7cf07L; // com.rim.samples.device.otabackuprestoredemo.OTABackupRestoreContactCollection

    private static final int FIELDTAG_FIRST_NAME = 1;
    private static final int FIELDTAG_LAST_NAME = 2;
    private static final int FIELDTAG_EMAIL_ADDRESS = 3;

    private static final int DEFAULT_RECORD_TYPE = 1; // The id for the default
                                                      // (and the only) record
                                                      // type

    // Key fields - lets the server know which fields uniquely define a record
    private static final int[] KEY_FIELD_IDS = new int[] { FIELDTAG_FIRST_NAME,
            FIELDTAG_LAST_NAME };

    // Creates a new OTABackupRestoreContactCollection object
    private OTABackupRestoreContactCollection() {
        _persist = PersistentStore.getPersistentObject(PERSISTENT_KEY);
        _contacts = (Vector) _persist.getContents();

        if (_contacts == null) {
            _contacts = new Vector();
            _persist.setContents(_contacts);
            _persist.commit();
        }

        _listeners = new CloneableVector();

        // Set up the schema for the collection
        _schema = new SyncCollectionSchema();
        _schema.setDefaultRecordType(DEFAULT_RECORD_TYPE);
        _schema.setKeyFieldIds(DEFAULT_RECORD_TYPE, KEY_FIELD_IDS);
    }

    /**
     * Get the singleton instance of the OTABackupRestoreContactCollection class
     * 
     * @return The singleton instance of the OTABackupRestoreContactCollection
     *         class
     */
    static OTABackupRestoreContactCollection getInstance() {
        final RuntimeStore rs = RuntimeStore.getRuntimeStore();

        synchronized (rs) {
            OTABackupRestoreContactCollection collection =
                    (OTABackupRestoreContactCollection) rs.get(AR_KEY);

            if (collection == null) {
                collection = new OTABackupRestoreContactCollection();
                rs.put(AR_KEY, collection);
            }

            return collection;
        }
    }

    // SyncConverter
    // methods-----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncConverter#convert(SyncObject,DataBuffer,int)
     */
    public boolean convert(final SyncObject object, final DataBuffer buffer,
            final int version) {
        if (version == getSyncVersion()) {
            if (object instanceof OTAContactData) {
                final String first = ((OTAContactData) object).getFirst();
                final String last = ((OTAContactData) object).getLast();
                final String email = ((OTAContactData) object).getEmail();

                // In compliance with desktop sync format
                buffer.writeShort(first.length() + 1);
                buffer.writeByte(FIELDTAG_FIRST_NAME);
                buffer.write(first.getBytes());
                buffer.writeByte(0);
                buffer.writeShort(last.length() + 1);
                buffer.writeByte(FIELDTAG_LAST_NAME);
                buffer.write(last.getBytes());
                buffer.writeByte(0);
                buffer.writeShort(email.length() + 1);
                buffer.writeByte(FIELDTAG_EMAIL_ADDRESS);
                buffer.write(email.getBytes());
                buffer.writeByte(0);

                return true;
            }
        }

        return false;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncConverter#convert(DataBuffer,int,int)
     */
    public SyncObject convert(final DataBuffer data, final int version,
            final int UID) {
        try {
            final OTAContactData contact = new OTAContactData(UID);

            while (data.available() > 0) {
                final int length = data.readShort();
                final byte[] bytes = new byte[length];

                switch (data.readByte()) {
                case FIELDTAG_FIRST_NAME:
                    data.readFully(bytes);

                    // Trim null-terminator.
                    contact.setFirst(new String(bytes).trim());
                    break;

                case FIELDTAG_LAST_NAME:
                    data.readFully(bytes);
                    contact.setLast(new String(bytes).trim());
                    break;

                case FIELDTAG_EMAIL_ADDRESS:
                    data.readFully(bytes);
                    contact.setEmail(new String(bytes).trim());
                    break;

                default:
                    data.readFully(bytes);
                    // other fields not supported
                    break;
                }
            }

            return contact;
        } catch (final EOFException e) {
            UiApplication.getUiApplication().invokeLater(new Runnable() {
                public void run() {
                    Dialog.alert(e.toString());
                }
            });
        }

        return null;
    }

    // SyncCollection
    // methods----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#addSyncObject(SyncObject)
     */
    public boolean addSyncObject(final SyncObject object) {
        // Add a contact to the persistent store
        _contacts.addElement(object);
        PersistentObject.commit(_contacts);

        // Use the CollectionListeners to let the server know the add was
        // successful
        for (int i = 0; i < _listeners.size(); i++) {
            final CollectionListener cl =
                    (CollectionListener) _listeners.elementAt(i);
            cl.elementAdded(this, object);
        }

        return true;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#updateSyncObject(SyncObject,
     *      SyncObject)
     */
    public boolean updateSyncObject(final SyncObject oldObject,
            final SyncObject newObject) {
        // Update a contact in the store
        if (_contacts.contains(oldObject)) {
            _contacts.setElementAt(newObject, _contacts.indexOf(oldObject));
            PersistentObject.commit(_contacts);
        }

        // Use the CollectionListeners to let the server know the update was
        // successful
        for (int i = 0; i < _listeners.size(); i++) {
            final CollectionListener cl =
                    (CollectionListener) _listeners.elementAt(i);
            cl.elementUpdated(this, oldObject, newObject);
        }

        return true;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#removeSyncObject(SyncObject)
     */
    public boolean removeSyncObject(final SyncObject object) {
        return false; // NA - This method would look much the same as
                      // addSyncObject
    }

    public boolean removeAllSyncObjects() {
        return false; // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjects()
     */
    public SyncObject[] getSyncObjects() {
        final SyncObject[] contactArray = new SyncObject[_contacts.size()];

        for (int i = _contacts.size() - 1; i >= 0; --i) {
            contactArray[i] = (SyncObject) _contacts.elementAt(i);
        }

        return contactArray;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjects()
     */
    public SyncObject getSyncObject(final int uid) {
        for (int i = _contacts.size() - 1; i >= 0; --i) {
            final SyncObject so = (SyncObject) _contacts.elementAt(i);

            if (so.getUID() == uid) {
                return so;
            }
        }

        return null;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#isSyncObjectDirty(SyncObject)
     */
    public boolean isSyncObjectDirty(final SyncObject object) {
        return false; // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#setSyncObjectDirty(SyncObject)
     */
    public void setSyncObjectDirty(final SyncObject object) {
        // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#clearSyncObjectDirty(SyncObject)
     */
    public void clearSyncObjectDirty(final SyncObject object) {
        // NA
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjectCount()
     */
    public int getSyncObjectCount() {
        _persist = PersistentStore.getPersistentObject(PERSISTENT_KEY);
        _contacts = (Vector) _persist.getContents();

        return _contacts.size();
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncVersion()
     */
    public int getSyncVersion() {
        return 1;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncName()
     */
    public String getSyncName() {
        return "OTABackupRestoreContacts";
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncName(Locale)
     */
    public String getSyncName(final Locale locale) {
        return null;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncConverter()
     */
    public SyncConverter getSyncConverter() {
        return this;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#beginTransaction()
     */
    public void beginTransaction() {
        _persist = PersistentStore.getPersistentObject(PERSISTENT_KEY);
        _contacts = (Vector) _persist.getContents();
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#endTransaction()
     */
    public void endTransaction() {
        _persist.setContents(_contacts);
        _persist.commit();
    }

    // OTASyncCapable methods
    // ---------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.OTASyncCapable#getSchema()
     */
    public SyncCollectionSchema getSchema() {
        // Returns our schema.
        return _schema;
    }

    // CollectionEventSource methods
    // --------------------------------------------
    /**
     * @see net.rim.device.api.collection.CollectionEventSource#addCollectionListener(Object)
     */
    public void addCollectionListener(final Object listener) {
        _listeners = ListenerUtilities.fastAddListener(_listeners, listener);
    }

    /**
     * @see net.rim.device.api.collection.CollectionEventSource#removeCollectionListener(Object)
     */
    public void removeCollectionListener(final Object listener) {
        _listeners = ListenerUtilities.removeListener(_listeners, listener);
    }

    /**
     * Gets the size of the contact list.
     * 
     * @return The size of the contact list
     */
    int size() {
        return _contacts.size();
    }

    /**
     * Retrieves a contact from the list.
     * 
     * @param index
     *            The index of the contact to retrieve
     * @return The contact at the specified index
     */
    OTAContactData contactAt(final int index) {
        return (OTAContactData) _contacts.elementAt(index);
    }
}
