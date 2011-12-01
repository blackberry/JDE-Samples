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

import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RuntimeStore;
import net.rim.device.api.util.Arrays;

/**
 * This class represents the AddressBook implementation where one can add,
 * remove, update and traverse the different address book records stored in the
 * address book.
 */
public class AddressBook {
    private static final long PERSIST = 0xcf76f65979a526eaL; // com.rim.samples.device.objectgroupingdemo.AddressBook.PERSIST
    private static final long ADDRESS_BOOK = 0xdc33b15c18be898fL; // com.rim.samples.device.objectgroupingdemo.AddressBook.ADDRESS_BOOK

    private final PersistentObject _persist; // Refeence to the PersistentObject
                                             // for our address book.
    private AddressBookRecord[] _records; // The array of address book records
                                          // that make up the address book.

    /**
     * Simple constructor for the class that will initialize the _records array
     * using the data stored in the persistent object. This method is marked
     * private because no other class should be able to instantiate the
     * AddressBook.
     */
    private AddressBook() {
        _persist = PersistentStore.getPersistentObject(PERSIST);
        _records = (AddressBookRecord[]) _persist.getContents();

        if (_records == null) {
            _records = new AddressBookRecord[0];
            _persist.setContents(_records);
        }
    }

    /**
     * Returns the singleton instance of the AddressBook.
     * 
     * @return the singleton instance of the AddressBook.
     */
    public static AddressBook getInstance() {
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
     * Adds the address book record.
     * 
     * @param record
     *            the address book record to add. Note: This method does not
     *            perform any duplicate detection.
     */
    public void add(final AddressBookRecord record) {
        if (record == null) {
            throw new IllegalArgumentException();
        }

        // Be sure to group the record before adding it to the address book.
        ObjectGroup.createGroup(record);
        Arrays.add(_records, record);
        _persist.commit();
    }

    /**
     * Updates the oldRecord in the address book with the contents specified in
     * the newRecord.
     * 
     * @param oldRecord
     *            the record to update in the address book.
     * @param newRecord
     *            the record to use for the data to update the oldRecord.
     */
    public void update(final AddressBookRecord oldRecord,
            final AddressBookRecord newRecord) {
        if (oldRecord == null || newRecord == null) {
            throw new IllegalArgumentException();
        }

        // The obvious implementation for updating an address book record is
        // to remove the oldRecord and add the new record. The following two
        // lines would accomplish this task.
        //
        // Arrays.remove( _records, oldRecord );
        // Arrays.add( _records, newRecord );
        //
        // However, this is both inefficient (traverse the array, resize the
        // array twice, etc)
        // and doesn't help demonstrate grouping and ungrouping. So, in this
        // implementation
        // we will simply modify the title, first name and last name in the
        // specific
        // old record with the data from the new record.

        // Ensure that the oldRecord is actually contained in our address book.
        final int index = Arrays.getIndex(_records, oldRecord);
        if (index == -1) {
            // Item not found.
            throw new IllegalArgumentException();
        }

        // Ungroup the old record.
        final AddressBookRecord ungroupedRecord =
                (AddressBookRecord) ObjectGroup.expandGroup(_records[index]);

        ungroupedRecord.setTitle(newRecord.getTitle());
        ungroupedRecord.setFirstName(newRecord.getFirstName());
        ungroupedRecord.setLastName(newRecord.getLastName());

        ObjectGroup.createGroup(ungroupedRecord);
        _records[index] = ungroupedRecord;
        _persist.commit();
    }

    /**
     * Removes the address book reecord from the address book.
     * 
     * @param record
     *            the record to remove.
     */
    public void remove(final AddressBookRecord record) {
        if (record == null) {
            throw new IllegalArgumentException();
        }

        Arrays.remove(_records, record);
        _persist.commit();
    }

    /**
     * Returns the address book record specified by the index.
     * 
     * @param index
     *            the index into the list of address book records.
     * @return the address book record specified by the index if that
     *         corresponds to a valid index.
     * @throws IllegalArgumentException
     *             if the index is invalid.
     */
    public AddressBookRecord getRecord(final int index) {
        if (index < 0 || index >= _records.length) {
            throw new IllegalArgumentException();
        }

        return _records[index];
    }

    /**
     * Returns the size of the address book.
     * 
     * @return the number of records currently stored in the address book.
     */
    public int size() {
        return _records.length;
    }
}
