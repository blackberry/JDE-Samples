/*
 * OTAContactCollection.java
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

package com.rim.samples.device.otasyncdemo;

import java.io.EOFException;
import java.util.Vector;

import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.CollectionListener;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.synchronization.OTASyncCapable;
import net.rim.device.api.synchronization.OTASyncParametersProvider;
import net.rim.device.api.synchronization.OTASyncPriorityProvider;
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
 * A Collection that is completely synchable over the air.
 */
public class OTAContactCollection implements OTASyncCapable,
        OTASyncPriorityProvider, OTASyncParametersProvider, SyncConverter,
        SyncCollection, CollectionEventSource {

    private static final long PERSISTENT_KEY = 0xb7abba0f9ef77e29L; // Hash of
                                                                    // com.rim.samples.device.otasyncdemo
    private static final long COLLECTION_KEY = 0x8781a172fb82e0caL; // Hash of
                                                                    // com.rim.samples.device.otasyncdemo.OTAContactCollection

    private static final int FIELDTAG_FIRST_NAME = 1;
    private static final int FIELDTAG_LAST_NAME = 2;
    private static final int FIELDTAG_EMAIL_ADDRESS = 3;
    private static final int FIELDTAG_PHONE_NUMBER = 4;

    private Vector _contacts; // The collection container.
    private PersistentObject _persist; // Persistent object for the collection.
    private Vector _listeners; // Listeners to notify when the collection
                               // changes.

    /**
     * Constructor
     */
    public OTAContactCollection() {
        _persist = PersistentStore.getPersistentObject(PERSISTENT_KEY);
        _contacts = (Vector) _persist.getContents();

        if (_contacts == null) {
            _contacts = new Vector();
            _persist.setContents(_contacts);
            _persist.commit();
        }

        _listeners = new CloneableVector();
    }

    /**
     * Returns an instance of this Collection.
     */
    static OTAContactCollection getInstance() {
        final RuntimeStore rs = RuntimeStore.getRuntimeStore();

        synchronized (rs) {
            OTAContactCollection collection =
                    (OTAContactCollection) rs.get(COLLECTION_KEY);

            if (collection == null) {
                collection = new OTAContactCollection();
                rs.put(COLLECTION_KEY, collection);
            }

            return collection;
        }
    }

    // SyncCollection methods
    // ----------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.SyncCollection#addSyncObject(SyncObject)
     */
    public boolean addSyncObject(final SyncObject object) {
        // Add a contact to the persistent store.
        _contacts.addElement(object);
        _persist.setContents(_contacts);
        _persist.commit();

        // We want to let any collection listeners we have that the collection
        // has been changed.
        final int numListeners = _listeners.size();

        for (int i = 0; i < numListeners; i++) {
            final CollectionListener cl =
                    (CollectionListener) _listeners.elementAt(i);
            cl.elementAdded(this, object);
        }

        return true;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#updateSyncObject(SyncObject,SyncObject)
     */
    public boolean updateSyncObject(final SyncObject oldObject,
            final SyncObject newObject) {
        // Remove an object.
        if (_contacts.contains(oldObject)) {
            final int index = _contacts.indexOf(oldObject);
            _contacts.setElementAt(newObject, index);
        }

        // Persist
        _persist.setContents(_contacts);
        _persist.commit();

        // Notify listeners.
        final int numListeners = _listeners.size();

        for (int i = 0; i < numListeners; i++) {
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
        // Remove the object.
        if (_contacts.contains(object)) {
            _contacts.removeElement(object);
        }

        // Persist
        _persist.setContents(_contacts);
        _persist.commit();

        // Notify listeners.
        final int numListeners = _listeners.size();

        for (int i = 0; i < numListeners; i++) {
            final CollectionListener cl =
                    (CollectionListener) _listeners.elementAt(i);
            cl.elementRemoved(this, object);
        }

        return true;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#removeAllSyncObjects()
     */
    public boolean removeAllSyncObjects() {
        // Clear the Vector.
        _contacts.removeAllElements();
        _persist.setContents(_contacts);
        _persist.commit();

        // Notify listeners.
        final int numListeners = _listeners.size();

        for (int i = 0; i < numListeners; i++) {
            final CollectionListener cl =
                    (CollectionListener) _listeners.elementAt(i);
            cl.reset(this);
        }

        return true;
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
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObject(int)
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
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncVersion()
     */
    public int getSyncVersion() {
        return 0;
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncObjectCount()
     */
    public int getSyncObjectCount() {
        return _contacts.size();
    }

    /**
     * @see net.rim.device.api.synchronization.SyncCollection#getSyncName()
     */
    public String getSyncName() {
        return "Sample Contacts";
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

    // OTASyncPriorityProvider methods
    // ------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.OTASyncPriorityProvider#getSyncPriority()
     */
    public int getSyncPriority() {
        return OTASyncPriorityProvider.MIN_PRIORITY - 1;
    }

    // SyncConverter methods
    // ----------------------------------------------------
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
                final String phone = ((OTAContactData) object).getPhone();

                // In compliance with desktop sync format.
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
                buffer.writeShort(phone.length() + 1);
                buffer.writeByte(FIELDTAG_PHONE_NUMBER);
                buffer.write(phone.getBytes());
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
                    final String fname = new String(bytes).trim();

                    if (fname != null) {
                        contact.setFirst(fname);
                    } else {
                        contact.setFirst("");
                    }
                    break;

                case FIELDTAG_LAST_NAME:
                    data.readFully(bytes);
                    final String lname = new String(bytes).trim();

                    if (lname != null) {
                        contact.setLast(lname);
                    } else {
                        contact.setLast("");
                    }
                    break;

                case FIELDTAG_EMAIL_ADDRESS:
                    data.readFully(bytes);
                    final String email = new String(bytes).trim();

                    if (email != null) {
                        contact.setEmail(email);
                    } else {
                        contact.setEmail("");
                    }
                    break;

                case FIELDTAG_PHONE_NUMBER:
                    data.readFully(bytes);
                    final String phone = new String(bytes).trim();

                    if (phone != null) {
                        contact.setPhone(phone);
                    } else {
                        contact.setPhone("");
                    }
                    break;

                default:
                    data.readFully(bytes);
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

    // OTASyncParametersProvider methods
    // ----------------------------------------
    /**
     * @see net.rim.device.api.synchronization.OTASyncParametersProvider#getUserSystemId()
     */
    public String getUserSystemId() {
        return null;
    }

    /**
     * @see net.rim.device.api.synchronization.OTASyncParametersProvider#getDataSourceName()
     */
    public String getDataSourceName() {
        return "Sample";
    }

    /**
     * @see net.rim.device.api.synchronization.OTASyncParametersProvider#getDatabaseName()
     */
    public String getDatabaseName() {
        return "Sample Contacts";
    }

    // OTASyncCapable methods
    // ---------------------------------------------------
    /**
     * @see net.rim.device.api.synchronization.OTASyncCapable#getSchema()
     */
    public SyncCollectionSchema getSchema() {
        return null;
    }

    // Class-specific methods
    // ---------------------------------------------------
    /**
     * Returns the contact from the contact list at a specific index.
     * 
     * @param index
     *            The index of the contact to retrieve
     * @return The contact from the list at the specific index
     */
    Object getAt(final int index) {
        return _contacts.elementAt(index);
    }
}
