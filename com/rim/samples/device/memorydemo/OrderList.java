/*
 * OrderList.java
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
 * Represents a list of order records for a fictional business.
 */
/* package */final class OrderList {
    // Members
    // -------------------------------------------------------------------------------------
    private final PersistentObject _persist;
    private OrderRecord[] _orderRecords;

    // Statics
    // -------------------------------------------------------------------------------------
    private static OrderList _instance;
    private static Comparator _comparator;

    // Constants
    // -----------------------------------------------------------------------------------
    private static final long PERSIST = 0x9dbb6535e730ff7dL; // com.rim.samples.device.memorydemo.orders
    private static final int MAX_NUM_ORDERED = 100;

    static {
        _comparator = new Comparator() {
            public int compare(final Object o1, final Object o2) {
                final OrderRecord r1 = (OrderRecord) o1;
                final OrderRecord r2 = (OrderRecord) o2;

                if (r1.getDate() < r2.getDate()) {
                    return -1;
                }

                if (r1.getDate() > r2.getDate()) {
                    return 1;
                }

                return 0;
            }
        };
    }

    /**
     * This constructor ensures that a persistent object is in place to store
     * order records.
     */
    private OrderList() {
        _persist = PersistentStore.getPersistentObject(PERSIST);
        _orderRecords = (OrderRecord[]) _persist.getContents();

        if (_orderRecords == null) {
            _orderRecords = new OrderRecord[0];
            _persist.setContents(_orderRecords);
            _persist.commit();
        }
    }

    /**
     * Retrieves the single instance of the order list.
     * 
     * @return The order list.
     */
    public static OrderList getInstance() {
        if (_instance == null) {
            _instance = new OrderList();
        }

        return _instance;
    }

    /**
     * Retrieves the number of order records stored in the order list.
     * 
     * @return The number of order records.
     */
    public int getNumOrderRecords() {
        return _orderRecords.length;
    }

    /**
     * Retrieves the order record at the specified index.
     * 
     * @param index
     *            The index of the order record to retrieve.
     * @return The retrieved order record.
     */
    public OrderRecord getOrderRecordAt(final int index) {
        return _orderRecords[index];
    }

    /**
     * Deletes an order record from the list.
     * 
     * @param orderRecord
     *            The order record to delete.
     */
    public synchronized void deleteOrderRecord(final OrderRecord orderRecord) {
        Arrays.remove(_orderRecords, orderRecord);
    }

    /**
     * Deletes all order records from the list.
     */
    public synchronized void deleteAllOrderRecords() {
        _orderRecords = new OrderRecord[0];
        _persist.setContents(_orderRecords);
    }

    /**
     * Replaces an order record at a specified index with a new order record.
     * The new order record is first encoded into an object group so that it
     * only occupies one persistent object handle, no matter how many objects it
     * actually refers to.
     * 
     * @param index
     *            The index of the order record to replace.
     * @param newOrderRecord
     *            The new order record.
     */
    public synchronized void replaceOrderRecordAt(final int index,
            final OrderRecord newOrderRecord) {
        ObjectGroup.createGroup(newOrderRecord);
        _orderRecords[index] = newOrderRecord;
    }

    /**
     * Removes all orders that occurred before 'before', and notifies the low
     * memory manager that their storage can be reclaimed.
     * 
     * @param before
     *            The cutoff date for deleting order records.
     * @return True if any records are deleted; false otherwise.
     */
    public synchronized boolean removeStaleOrderRecords(final long before) {
        if (_orderRecords.length == 0) {
            return false;
        }

        Arrays.sort(_orderRecords, _comparator); // Make sure records are in
                                                 // order by date.

        boolean freedData = false;

        final OrderRecord[] orderRecords = _orderRecords;

        int i = 0;
        while (orderRecords[i].getDate() < before) // Skip over all stale
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
            final int numRecordsKept = orderRecords.length - i;
            _orderRecords = new OrderRecord[numRecordsKept];
            _persist.setContents(_orderRecords);

            for (int j = 0; j < numRecordsKept; ++j) {
                _orderRecords[j] = orderRecords[j + i];
                orderRecords[j + i] = null;
            }

            LowMemoryManager.markAsRecoverable(orderRecords);

            freedData = true;

            commit();
        }

        return freedData;
    }

    /**
     * Commits the order records to the persistent store.
     */
    public synchronized void commit() {
        _persist.commit();
    }

    /**
     * Populates this order list with order records, each consisting of a random
     * company, product, and number of products ordered.
     * 
     * @param totalRecords
     *            The number of records that should be in the list upon
     *            completion of this method.
     * @param listener
     *            Object that listens for counting and sorting updates.
     */
    public synchronized void populate(final int totalRecords,
            final CountAndSortListener listener) {
        final int numRecordsToAdd = totalRecords - _orderRecords.length;

        for (int i = 0; i < numRecordsToAdd; ++i) {
            listener.counterUpdated(i);

            final long today = new Date().getTime();
            final long date = MemoryDemo.randomLongBetween(0, today);
            final String company = MemoryDemo.randomString();
            final String product = MemoryDemo.randomString();
            final int numOrdered =
                    MemoryDemo.randomIntBetween(1, MAX_NUM_ORDERED);

            final OrderRecord orderRecord =
                    new OrderRecord(date, company, product, numOrdered);
            addOrderRecord(orderRecord);
        }

        listener.sortingStarted();

        Arrays.sort(_orderRecords, _comparator);

        // Array sorting is done.
        listener.sortingFinished();
    }

    /**
     * Retrieves the number of records to be added to the order list.
     * 
     */
    public int getNumRecordsToAdd(final int totalRecords) {
        return totalRecords - _orderRecords.length;
    }

    /**
     * Helper method to add a new order record to the list. The order record is
     * first encoded into an object group so that it only occupies one
     * persistent object handle, no matter how many objects it actually refers
     * to.
     * 
     * @param orderRecord
     *            The order record to add.
     */
    private void addOrderRecord(final OrderRecord orderRecord) {
        ObjectGroup.createGroup(orderRecord);
        Arrays.add(_orderRecords, orderRecord);
    }
}
