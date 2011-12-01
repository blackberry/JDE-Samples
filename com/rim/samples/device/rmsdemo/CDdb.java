/*
 * CDdb.java
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

package com.rim.samples.device.rmsdemo;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

/**
 * This class manages an RMS of Compact Disc titles
 * 
 */
public final class CDdb {
    RecordStore _rs;

    /**
     * Constructs a new CDdb, creator
     * 
     * @param name
     *            Name of the RMS
     * @exception RecordStoreException
     *                General record store exception
     * @exception java.io.IOException
     *                Thrown if the record store could not be opened
     */
    public CDdb(final String name) throws RecordStoreException,
            java.io.IOException {
        _rs =
                RecordStore.openRecordStore(name, true,
                        RecordStore.AUTHMODE_ANY, false);
    }

    /**
     * Constructor a new CDdb object, consumer
     * 
     * @param recordStoreName
     *            Name of the RMS
     * @param vendorName
     *            MIDlet suite vendor
     * @param suiteName
     *            MIDlet suite name
     * @exception RecordStoreException
     *                General record store exception
     * @exception java.io.IOException
     *                Thrown if the record store could not be opened
     */
    public CDdb(final String recordStoreName, final String vendorName,
            final String suiteName) throws RecordStoreException,
            java.io.IOException {
        _rs =
                RecordStore.openRecordStore(recordStoreName, vendorName,
                        suiteName);
    }

    /**
     * Adds a CD to the RecordStore
     * 
     * @param artist
     *            The name of the artist of the CD
     * @param title
     *            The title of the CD
     * @return RecordID of new CD
     * @exception java.io.IOException
     *                Thrown if an error occurs while writing to the record
     *                store
     * @exception RecordStoreNotOpenException
     *                Thrown if the record store is not open
     * @exception RecordStoreException
     *                Thrown if there is a record store error
     */
    public synchronized int add(final String artist, final String title)
            throws java.io.IOException, RecordStoreNotOpenException,
            RecordStoreException {
        final CD cd = new CD(artist, title);
        final byte[] data = cd.toByteArray();

        return _rs.addRecord(data, 0, data.length);
    }

    /**
     * Edits a CD in the RecordStore
     * 
     * @param artist
     *            The name of the artist of the CD
     * @param title
     *            The title of the CD
     * @exception java.io.IOException
     *                Thrown if an error occurs while writing to the record
     *                store
     * @exception RecordStoreNotOpenException
     *                Thrown if the record store is not open
     * @exception RecordStoreException
     *                Thrown if there is a record store error
     */
    public synchronized void edit(final int index, final String artist,
            final String title) throws java.io.IOException,
            RecordStoreNotOpenException, RecordStoreException {
        final CD cd = new CD(artist, title);
        final byte[] data = cd.toByteArray();
        _rs.setRecord(index, data, 0, data.length);
    }

    /**
     * Returns the CD of a given recordID with the RMS
     * 
     * @param recordID
     *            RecordID of CD to retrieve
     * @return The CD corresponding to the recordID
     * @exception RecordStoreNotOpenException
     *                Thrown if the record store is not open
     * @exception InvalidRecordIDException
     *                Thrown if the recordID is invalid
     * @exception RecordStoreException
     *                Thrown if there is a record store error
     * @exception java.io.IOException
     *                Thrown if there is an I/O error when retrieving the record
     *                from the store
     */
    public CD getCD(final int recordID) throws RecordStoreNotOpenException,
            InvalidRecordIDException, RecordStoreException, java.io.IOException {
        final byte[] data = _rs.getRecord(recordID);

        return new CD(data);
    }

    /**
     * Delete a CD from the RecordStore
     * 
     * @param recordId
     *            RecordID within the RMS
     * @exception RecordStoreNotOpenException
     *                Thrown if the record store is not open
     * @exception InvalidRecordIDException
     *                Thrown if the recordID is invalid
     * @exception RecordStoreException
     *                Thrown if there is a record store error
     */
    public synchronized void delete(final int recordId)
            throws RecordStoreNotOpenException, InvalidRecordIDException,
            RecordStoreException {
        _rs.deleteRecord(recordId);
    }

    /**
     * Generates an enumeration for the RMS
     * 
     * @return An enumeration object for the RMS
     * @exception RecordStoreNotOpenException
     *                Thrown if the record store is not open
     */
    RecordEnumeration enumerate() throws RecordStoreNotOpenException {
        return _rs.enumerateRecords(null, null, true);
    }
}
