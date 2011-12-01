/*
 * OrderRecord.java
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

import java.util.Calendar;
import java.util.Date;

import net.rim.device.api.util.Persistable;

/**
 * Represents an order record for a fictional business.
 */
/* package */final class OrderRecord implements Persistable {
    // Members
    // -------------------------------------------------------------------------------------
    private long _date; // The date this ordered was placed on.
    private String _company; // The company from which this order was made.
    private String _product; // The product that was ordered.
    private int _numOrdered; // The number of units ordered.

    /**
     * This constructor simply fills in the data values in this record.
     * 
     * @param date
     *            The date this order was placed on.
     * @param company
     *            The company this order was placed with.
     * @param product
     *            The product that was ordered.
     * @param numOrdered
     *            The number of units ordered.
     */
    OrderRecord(final long date, final String company, final String product,
            final int numOrdered) {
        _date = date;
        _company = company;
        _product = product;
        _numOrdered = numOrdered;
    }

    /**
     * Retrieves the date on this order record.
     * 
     * @return The date on this order record.
     */
    long getDate() {
        return _date;
    }

    /**
     * Sets the date on this order record.
     * 
     * @param date
     *            This order record's new date.
     */
    void setDate(final long date) {
        _date = date;
    }

    /**
     * Retrieves the company on this order record.
     * 
     * @return The company on this order record.
     */
    String getCompany() {
        return _company;
    }

    /**
     * Sets the company on this order record.
     * 
     * @param company
     *            This order record's new company.
     */
    void setCompany(final String company) {
        _company = company;
    }

    /**
     * Retrieves the product on this order record.
     * 
     * @return The product on this order record.
     */
    String getProduct() {
        return _product;
    }

    /**
     * Sets the product on this order record.
     * 
     * @param product
     *            This order record's new product.
     */
    void setProduct(final String product) {
        _product = product;
    }

    /**
     * Retrieves this order record's number of units.
     * 
     * @return The number of units on this order record.
     */
    int getNumOrdered() {
        return _numOrdered;
    }

    /**
     * Sets this order record's number of units.
     * 
     * @param numOrdered
     *            This order record's new number of units.
     */
    void setNumOrdered(final int numOrdered) {
        _numOrdered = numOrdered;
    }

    /**
     * Retrieves a string representation of this order record.
     * 
     * @return The string representation.
     */
    public String toString() {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(_date));

        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int year = calendar.get(Calendar.YEAR);

        final StringBuffer buffer = new StringBuffer();

        buffer.append(day).append('/').append(month).append('/').append(year)
                .append(": ").append(_numOrdered).append(" ").append(_product)
                .append("s from ").append(_company);

        return buffer.toString();
    }
}
