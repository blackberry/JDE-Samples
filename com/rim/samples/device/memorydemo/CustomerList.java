/*
 * CustomerList.java
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

package com.rim.samples.device.memorydemo;

import java.util.Date;

import net.rim.device.api.lowmemory.LowMemoryManager;
import net.rim.device.api.system.ObjectGroup;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.Comparator;

/**
 * Represents a list of customer records for a fictional business.
 */
/* package */final class CustomerList {
    // Members
    // -------------------------------------------------------------------------------------
    private final PersistentObject _persist;
    private CustomerRecord[] _customerRecords;

    // Statics
    // -------------------------------------------------------------------------------------
    private static CustomerList _instance;
    private static Comparator _nameComparator;
    private static Comparator _dateComparator;

    // Constants
    // -----------------------------------------------------------------------------------
    private static final long PERSIST = 0x93c4a8cbe067fa85L; // com.rim.samples.device.memorydemo.customers

    static {
        _nameComparator = new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final CustomerRecord r1 = (CustomerRecord) o1;
                final CustomerRecord r2 = (CustomerRecord) o2;

                final int lastNameCompare =
                        r1.getLastName().compareTo(r2.getLastName());

                if (lastNameCompare == 0) {
                    return r1.getFirstName().compareTo(r2.getFirstName());
                }

                return lastNameCompare;
            }
        };

        _dateComparator = new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final CustomerRecord r1 = (CustomerRecord) o1;
                final CustomerRecord r2 = (CustomerRecord) o2;

                if (r1.getLastAccessDate() < r2.getLastAccessDate()) {
                    return -1;
                }

                if (r1.getLastAccessDate() > r2.getLastAccessDate()) {
                    return 1;
                }

                return 0;
            }
        };
    }

    /**
     * This constructor ensures that a persistent object is in place to store
     * customer records.
     */
    private CustomerList() {
        _persist = PersistentStore.getPersistentObject(PERSIST);
        _customerRecords = (CustomerRecord[]) _persist.getContents();

        if (_customerRecords == null) {
            _customerRecords = new CustomerRecord[0];
            _persist.setContents(_customerRecords);
            _persist.commit();
        }
    }

    /**
     * Retrieves the single instance of the customer list.
     * 
     * @return The customer list.
     */
    public static CustomerList getInstance() {
        if (_instance == null) {
            _instance = new CustomerList();
        }

        return _instance;
    }

    /**
     * Retrieves the number of customer records stored in the customer list.
     * 
     * @return The number of customer records.
     */
    public int getNumCustomerRecords() {
        return _customerRecords.length;
    }

    /**
     * Retrieves the customer record at the specified index.
     * 
     * @param index
     *            The index of the customer record to retrieve.
     * @return The retrieved customer record.
     */
    public CustomerRecord getCustomerRecordAt(final int index) {
        return _customerRecords[index];
    }

    /**
     * Deletes a customer record from the list.
     * 
     * @param customerRecord
     *            The customer record to delete.
     */
    public synchronized void deleteCustomerRecord(
            final CustomerRecord customerRecord) {
        Arrays.remove(_customerRecords, customerRecord);
    }

    /**
     * Deletes all customer records from the list.
     */
    public synchronized void deleteAllCustomerRecords() {
        _customerRecords = new CustomerRecord[0];
        _persist.setContents(_customerRecords);
    }

    /**
     * Replaces a customer record at a specified index with a new customer
     * record. The new customer record is first encoded into an object group so
     * that it only occupies one persistent object handle, no matter how many
     * objects it actually refers to.
     * 
     * @param index
     *            The index of the customer record to replace.
     * @param newCustomerRecord
     *            The new customer record.
     */
    public synchronized void replaceCustomerRecordAt(final int index,
            final CustomerRecord newCustomerRecord) {
        ObjectGroup.createGroup(newCustomerRecord);
        _customerRecords[index] = newCustomerRecord;
    }

    /**
     * Removes all customer records that have not been accessed since before
     * 'before', and notifies the low memory manager that their storage can be
     * reclaimed.
     * 
     * @param before
     *            The cutoff date for deleting customer records.
     * @return True if any records are deleted; false otherwise.
     */
    public synchronized boolean removeStaleCustomerRecords(final long before) {
        if (_customerRecords.length == 0) {
            return false;
        }

        // Make sure records are in order by date.
        Arrays.sort(_customerRecords, _dateComparator);

        boolean freedData = false;

        final CustomerRecord[] customerRecords = _customerRecords;

        int i = 0;
        while (customerRecords[i].getLastAccessDate() < before) // Skip over all
                                                                // stale
                                                                // records.
        {
            ++i;
        }

        // If some records are to be deleted (i.e., stale), make an array big
        // enough to
        // hold the records that are to be kept, and copy the kept records to
        // that array.
        // Delete the old array completely.
        if (i > 0) {
            final int numRecordsKept = customerRecords.length - i;
            _customerRecords = new CustomerRecord[numRecordsKept];
            _persist.setContents(_customerRecords);

            for (int j = 0; j < numRecordsKept; ++j) {
                _customerRecords[j] = customerRecords[j + i];
                customerRecords[j + i] = null;
            }

            LowMemoryManager.markAsRecoverable(customerRecords);
            freedData = true;

            commit();
        }

        Arrays.sort(_customerRecords, _nameComparator); // Put records back in
                                                        // order by name.

        return freedData;
    }

    /**
     * Commits the customer records to the persistent store.
     */
    public synchronized void commit() {
        _persist.commit();
    }

    /**
     * Populates the customer list with random data, just to fill up memory.
     * Each customer record is given a random first and last name, and a date
     * representing the last time it was accessed in the fictional business's
     * database.
     * 
     * @param totalRecords
     *            The total number of records that exist upon completion of this
     *            method.
     * @param listener
     *            Object that listens for counting and sorting updates.
     */
    public synchronized void populate(final int totalRecords,
            final CountAndSortListener listener) {
        final int numRecordsToAdd = totalRecords - _customerRecords.length;

        for (int i = 0; i < numRecordsToAdd; ++i) {
            listener.counterUpdated(i);

            final String firstName = MemoryDemo.randomString();
            final String lastName = MemoryDemo.randomString();
            final long today = new Date().getTime();
            final long lastAccessDate = MemoryDemo.randomLongBetween(0, today);

            final CustomerRecord customerRecord =
                    new CustomerRecord(firstName, lastName, lastAccessDate);
            addCustomerRecord(customerRecord);
        }

        listener.sortingStarted();

        Arrays.sort(_customerRecords, _nameComparator);

        // Array sorting is done.
        listener.sortingFinished();
    }

    /**
     * Retrieves the number of records to be added to the customer list.
     * 
     */
    public int getNumRecordsToAdd(final int totalRecords) {
        return totalRecords - _customerRecords.length;
    }

    /**
     * Helper method to add a new customer record to the list. The customer
     * record is first encoded into an object group so that it only occupies one
     * persistent object handle, no matter how many objects it actually refers
     * to.
     * 
     * @param customerRecord
     *            The customer record to add.
     */
    private void addCustomerRecord(final CustomerRecord customerRecord) {
        ObjectGroup.createGroup(customerRecord);
        Arrays.add(_customerRecords, customerRecord);
    }
}
