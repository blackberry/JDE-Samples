/*
 * AddressBook.java
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

package com.rim.samples.device.objectgroupingdemo;

import java.util.Vector;

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RuntimeStore;

/**
 * This class represents the AddressBook implementation where one can add,
 * remove, update and traverse the different address book records stored in the
 * address book.
 */
public class AddressBook {
    private static final long PERSIST = 0x72ea07b090aac4a0L; // com.rim.samples.device.objectgroupingdemo
    private static final long ADDRESS_BOOK = 0xdc33b15c18be898fL; // com.rim.samples.device.objectgroupingdemo.AddressBook.ADDRESS_BOOK

    private final PersistentObject _persist; // Reference to the
                                             // PersistentObject for our address
                                             // book
    private Vector _records; // The Vector of address book records that make up
                             // the address book

    /**
     * Simple constructor for the class that will initialize the _records Vector
     * using the data stored in the persistent object. This method is marked
     * private because no other class should be able to instantiate the
     * AddressBook.
     */
    public AddressBook() {
        _persist = PersistentStore.getPersistentObject(PERSIST);
        _records = (Vector) _persist.getContents();

        if (_records == null) {
            _records = new Vector();
            _persist.setContents(_records);
        }
    }

    /**
     * Returns the singleton instance of the AddressBook
     * 
     * @return The singleton instance of the AddressBook
     */
    static AddressBook getInstance() {
        final RuntimeStore rs = RuntimeStore.getRuntimeStore();

        synchronized (rs) {
            AddressBook addressBook = (AddressBook) rs.get(ADDRESS_BOOK);

            if (addressBook == null) {
                addressBook = new AddressBook();
                rs.put(ADDRESS_BOOK, addressBook);
            }

            return addressBook;
        }
    }

    /**
     * Adds the address book record
     * 
     * @param record
     *            The address book record to add Note: This method does not
     *            perform any duplicate detection.
     */
    void add(final AddressBookRecord record) {
        if (record == null) {
            throw new IllegalArgumentException();
        }

        // Be sure to group the record before adding it to the address book
        ObjectGroup.createGroup(record);
        _records.addElement(record);
        _persist.commit();
    }

    /**
     * Updates the old record in the address book with the contents specified in
     * the new record
     * 
     * @param oldRecord
     *            The record to update in the address book
     * @param newRecord
     *            The record to use for the data to update the oldRecord
     */
    void update(final AddressBookRecord oldRecord,
            final AddressBookRecord newRecord) {
        if (oldRecord == null || newRecord == null) {
            throw new IllegalArgumentException();
        }

        // Ensure that the oldRecord is actually contained in our address book
        final int index = _records.indexOf(oldRecord);
        if (index == -1) {
            // Item not found
            throw new IllegalArgumentException();
        }

        // Ungroup the old record
        final AddressBookRecord ungroupedRecord =
                (AddressBookRecord) ObjectGroup.expandGroup(_records
                        .elementAt(index));

        ungroupedRecord.setTitle(newRecord.getTitle());
        ungroupedRecord.setFirstName(newRecord.getFirstName());
        ungroupedRecord.setLastName(newRecord.getLastName());

        ObjectGroup.createGroup(ungroupedRecord);
        _records.setElementAt(ungroupedRecord, index);
        _persist.commit();
    }

    /**
     * Removes the address book record from the address book
     * 
     * @param record
     *            The record to remove
     */
    void remove(final AddressBookRecord record) {
        if (record == null) {
            throw new IllegalArgumentException();
        }

        _records.removeElement(record);
        _persist.commit();
    }

    /**
     * Removes all records from the address book
     */
    void removeAll() {
        _records.removeAllElements();
        _persist.commit();
    }

    /**
     * Returns the address book record specified by the index
     * 
     * @param index
     *            The index into the list of address book records
     * @return The address book record specified by the index if that
     *         corresponds to a valid index
     * @throws IllegalArgumentException
     *             Thrown if the index is invalid
     */
    AddressBookRecord getRecord(final int index) {
        if (index < 0 || index >= _records.size()) {
            throw new IllegalArgumentException();
        }

        return (AddressBookRecord) _records.elementAt(index);
    }

    /**
     * Returns the size of the address book
     * 
     * @return The number of records currently stored in the address book
     */
    int size() {
        return _records.size();
    }
}
