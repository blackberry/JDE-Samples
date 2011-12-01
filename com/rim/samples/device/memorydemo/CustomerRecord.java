/*
 * CustomerRecord.java
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

import net.rim.device.api.util.Persistable;

/**
 * Represents a customer record for a fictional business.
 */
/* package */final class CustomerRecord implements Persistable {
    // Members
    // -------------------------------------------------------------------------------------
    private String _firstName;
    private String _lastName;
    private long _lastAccessDate;

    /**
     * This constructor just fills in the data values in this record.
     * 
     * @param firstName
     *            The customer's first name.
     * @param lastName
     *            The customer's last name.
     * @param lastAccessDate
     *            The date this customer record was last accessed.
     */
    public CustomerRecord(final String firstName, final String lastName,
            final long lastAccessDate) {
        _firstName = firstName;
        _lastName = lastName;
        _lastAccessDate = lastAccessDate;
    }

    /**
     * Retrieve's this customer record's first name.
     * 
     * @return The customer's first name.
     */
    public String getFirstName() {
        return _firstName;
    }

    /**
     * Sets this customer record's first name.
     * 
     * @param firstName
     *            The new first name.
     */
    public void setFirstName(final String firstName) {
        _firstName = firstName;
    }

    /**
     * Retrieve's this customer record's last name.
     * 
     * @return The customer's last name.
     */
    public String getLastName() {
        return _lastName;
    }

    /**
     * Sets this customer record's last name.
     * 
     * @param lastName
     *            The new last name.
     */
    public void setLastName(final String lastName) {
        _lastName = lastName;
    }

    /**
     * Retrieves the date of the last time this customer record was accessed.
     * 
     * @return The "last accessed" date.
     */
    public long getLastAccessDate() {
        return _lastAccessDate;
    }

    /**
     * Sets the date of the last time this customer record was accessed.
     * 
     * @param lastAccessDate
     *            The new "last accessed" date.
     */
    public void setLastAccessDate(final long lastAccessDate) {
        _lastAccessDate = lastAccessDate;
    }

    /**
     * Returns a string representation of this customer record.
     * 
     * @return The string representation.
     */
    public String toString() {
        return _lastName + ", " + _firstName;
    }
}
